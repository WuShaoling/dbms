package process;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import preprocess.Constant;
import preprocess.Error;
import preprocess.Util;

/**
 * 添加和删除权限
 * 
 * @author WSL
 *
 */
public class GrantAndRevoke {
	private static String[] users = null; // 临时存储用户
	private static String[] tables = null; // 临时存储表
	private static List<String> permissions = null;
	private static String sql = null;

	public static void Check(String[] arr) {
		users = null;
		tables = null;
		permissions = null;
		sql = Util.arrayToString(arr);
		// 检查是否选中数据库
		if (Constant.currentdatabase == null) {
			Util.showInTextArea(sql, Error.NO_DATABASE_SELECTED);
			return;
		}
		// 检查语法并修改临时数组的值
		String tt = null;
		if (arr[0].equals("grant")) {
			if ((tt = checkGrammar(sql, "grant (.{1,}) on table (.*) to (.{1,})")) != null) {
				Util.showInTextArea(sql, tt);
				return;
			}
		} else {
			if ((tt = checkGrammar(sql, "revoke (.{1,}) on table (.*) from (.{1,})")) != null) {
				Util.showInTextArea(sql, tt);
				return;
			}
		}
		// 检查所有的表是否都存在
		if (!checkTablesExsit()) {
			return;
		}
		// 检查所有的用户是否都存在
		if (!checkUsersExsit()) {
			return;
		}
		// 检查权限
		if (!CheckPermission.checkGrantAndRevokePermission()) {
			Util.showInTextArea(sql, Error.ACCESS_DENIED);
			return;
		}
		if (arr[0].equals("grant")) { // 增加权限
			grantPermission();
		} else { // 删除权限
			revokePermission();
		}
	}

	/**
	 * 检查分配权限语法，语法正确给用户和表数组赋值
	 * 
	 * @param sql
	 * @return
	 */
	public static String checkGrammar(String sql, String match) {
		Pattern pattern = Pattern.compile(match);
		Matcher matcher = pattern.matcher(sql);
		if (matcher.matches()) {
			permissions = new LinkedList<String>();
			String group2 = matcher.group(2);
			String group3 = matcher.group(3);
			if (matcher.group(1).trim().equals("all privileges")) {
				permissions.add("insert");
				permissions.add("delete");
				permissions.add("update");
				permissions.add("select");
			} else {
				String[] array = matcher.group(1).split(",");
				for (int i = 0; i < array.length; i++) {
					String tt = array[i].trim();
					if (!(tt.equals("select") || tt.equals("delete") || tt.equals("update") || tt.equals("insert"))) {
						permissions = null;
						return "Failed, " + tt + " is not exsit";
					} else {
						permissions.add(tt);
					}
				}
			}
			pattern = Pattern.compile("( ?.{1,} ?, ?)+");
			matcher = pattern.matcher(group2 + ",");
			if (matcher.matches()) {
				pattern = Pattern.compile("( ?.{1,} ?, ?)+");
				matcher = pattern.matcher(group3 + ",");
				if (matcher.matches()) {
					tables = group2.split(",");
					users = group3.split(",");
					for (int i = 0; i < tables.length; i++)
						tables[i] = tables[i].trim();
					for (int i = 0; i < users.length; i++)
						users[i] = users[i].trim();
					return null;
				}
			}
		}
		return Error.COMMAND_ERROR;
	}

	/**
	 * 检查所有表是否存在
	 * 
	 * @return
	 */
	public static boolean checkTablesExsit() {
		for (String table : tables) {
			try {
				if (!Constant.currentdatabase.getJSONObject("table").has(table)) {
					Util.showInTextArea(sql, Error.TABLE_NOT_EXIST + " : " + table);
					return false;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	/**
	 * 检查所有的用户是否存在
	 * 
	 * @return
	 */
	public static boolean checkUsersExsit() {
		for (String user : users) {
			if (!Constant.USERS.has(user)) {
				Util.showInTextArea(sql, Error.USER_NOT_EXIST + " : " + user);
				return false;
			}
		}
		return true;
	}

	/**
	 * 添加权限
	 */
	public static void grantPermission() {
		try {
			for (String user : users) {
				// 如果当前用户的当前数据库不存在，创建数据库，创建所有的表
				if (!Constant.USERS.getJSONObject(user).has(Constant.databasename)) {
					JSONObject db = new JSONObject();
					for (int i = 0; i < tables.length; i++) {
						JSONObject temp = new JSONObject();
						for (int j = 0; j < permissions.size(); j++)
							temp.put(permissions.get(j), 1);
						db.put(tables[i], temp);
					}
					Constant.USERS.getJSONObject(user).put(Constant.databasename, db);
				} else {// 有数据库
					JSONObject db = Constant.USERS.getJSONObject(user).getJSONObject(Constant.databasename);
					for (String table : tables) {
						JSONObject temp_table;
						if (!db.has(table)) {// 没有表
							temp_table = new JSONObject();
							for (int j = 0; j < permissions.size(); j++)
								temp_table.put(permissions.get(j), 1);
							db.put(table, temp_table);
						} else {
							temp_table = db.getJSONObject(table);
							for (int j = 0; j < permissions.size(); j++)
								temp_table.put(permissions.get(j), 1);
						}
					}
				}
			}
			Util.writeData(Constant.PATH_USERS, Constant.USERS.toString());
			Util.showInTextArea(sql, "ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除权限
	 */
	public static void revokePermission() {
		try {
			for (String user : users) {
				if (Constant.USERS.getJSONObject(user).has(Constant.databasename)) {
					JSONObject db = Constant.USERS.getJSONObject(user).getJSONObject(Constant.databasename);
					for (String table : tables) {
						if (db.has(table)) {
							JSONObject temp_table = db.getJSONObject(table);
							for (int i = 0; i < permissions.size(); i++) {
								if (temp_table.has(permissions.get(i))) {
									temp_table.remove(permissions.get(i));
								}
							}
						}
					}
				}
			}
			Util.writeData(Constant.PATH_USERS, Constant.USERS.toString());
			Util.showInTextArea(sql, "ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
