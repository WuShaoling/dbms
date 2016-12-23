package process;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import preprocess.Constant;
import preprocess.Error;
import preprocess.Util;

/**
 * Create语句
 * 
 * @author WSL
 *
 */
public class Create {
	private static String[] arr = null;
	private static String sql = null;
	private static Pattern pattern;
	private static Matcher matcher;
	private static String table = null;
	private static String view = null;
	private static String view_content = null;;

	public static void Check(String[] arrs) {
		pattern = null;
		matcher = null;
		table = null;
		view = null;
		view_content = null;
		arr = arrs;
		sql = Util.arrayToString(arr);
		if (arr.length >= 3) {
			switch (arr[1]) {
			case "database":
				createDatabase();
				break;
			case "table":
				createTable();
				break;
			case "user":
				createUser();
				break;
			case "view":
				createView();
				break;
			default:
				Util.showInTextArea(sql, Error.COMMAND_ERROR);
				break;
			}
		} else {
			Util.showInTextArea(sql, Error.COMMAND_ERROR);
		}
	}

	/**
	 * 创建数据库
	 */
	public static void createDatabase() {
		try {
			// 检查语法
			if (!sql.matches("create database [a-z_][a-z0-9_]{0,99}")) {
				Util.showInTextArea(sql, Error.COMMAND_ERROR);
				return;
			}
			// 检查权限
			if (!CheckPermission.checkCreateDatabasePermission()) {
				Util.showInTextArea(sql, Error.ACCESS_DENIED);
				return;
			}
			// 数据库是否已存在
			if (Constant.DICTIONARY.has(arr[2])) {
				Util.showInTextArea(sql, Error.DATABASE_EXSIT + " : " + arr[2]);
				return;
			}
			// 执行
			JSONObject temp = new JSONObject();
			temp.put("table", new JSONObject());
			temp.put("view", new JSONObject());
			Constant.DICTIONARY.put(arr[2], temp);
			Util.writeData(Constant.PATH_DICTIONARY, Constant.DICTIONARY.toString());
			File file = new File(Constant.PATH_ROOT, arr[2]);
			if (!file.exists())
				file.mkdir();
			Util.showInTextArea(sql, "ok, a database create success");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 创建视图 create view v1 as select name, age from student
	 */
	public static void createView() {
		try {
			// 是否选中数据库
			if (Constant.currentdatabase == null) {
				Util.showInTextArea(sql, Error.NO_DATABASE_SELECTED);
				return;
			}
			// 检查权限
			if (!CheckPermission.checkCreateViewPermission()) {
				Util.showInTextArea(sql, Error.ACCESS_DENIED);
				return;
			}
			// 初步检查语法
			if (!checkCreateViewGrammar()) {
				Util.showInTextArea(sql, Error.COMMAND_ERROR);
				return;
			}
			// 查重
			if (Constant.currentdatabase.getJSONObject("view").has(view)) {
				Util.showInTextArea(sql, Error.VIEW_EXIST + " : " + view);
				return;
			}
			// 执行
			JSONObject temp_view = new JSONObject();
			temp_view.put("content", view_content);
			JSONArray array = new JSONArray();
			String natures[] = Select.natures.split(",");
			JSONArray tablenatures = Constant.currentdatabase.getJSONObject("table").getJSONObject(Select.table)
					.getJSONArray("items");
			for (int i = 0; i < tablenatures.length(); i++) {
				for (int j = 0; j < natures.length; j++) {
					if (tablenatures.getJSONObject(i).getString("nature").equals(natures[j].trim())) {
						array.put(tablenatures.getJSONObject(i));
					}
				}
			}
			JSONObject views = new JSONObject();
			views.put("content", view_content);
			views.put("items", array);
			views.put("table", Select.table);
			Constant.currentdatabase.getJSONObject("view").put(view, views);
			Util.writeData(Constant.PATH_DICTIONARY, Constant.DICTIONARY.toString());
			Util.showInTextArea(sql, "ok, a view create success");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 创建表
	 */
	public static void createTable() {
		try {
			// 是否选中数据库
			if (Constant.currentdatabase == null) {
				Util.showInTextArea(sql, Error.NO_DATABASE_SELECTED);
				return;
			}
			// 检查权限
			if (!CheckPermission.checkCreateTablePermission()) {
				Util.showInTextArea(sql, Error.ACCESS_DENIED);
				return;
			}
			// 检查语法
			if (!checkCreateTableGrammar()) {
				Util.showInTextArea(sql, Error.COMMAND_ERROR);
				return;
			}
			// 查重
			if (Constant.currentdatabase.getJSONObject("table").has(table)) {
				Util.showInTextArea(sql, Error.TABLE_EXIST + " : " + table);
				return;
			}
			String str_natures = sql.substring(sql.indexOf("(") + 1, sql.lastIndexOf(")")).trim();
			String[] natures = str_natures.split(",");
			JSONArray items = new JSONArray();
			for (int i = 0; i < natures.length; i++) {
				String[] elem = natures[i].trim().split(" ");
				JSONObject temp = new JSONObject();
				temp.put("nature", elem[0].trim());
				temp.put("type", elem[1].trim());
				if (elem.length == 3) {
					temp.put("limit", elem[2].trim());
				} else if (elem.length == 4) {
					temp.put("limit", elem[2].trim() + " " + elem[3].trim());
				}
				items.put(temp);
			}
			JSONObject table1 = new JSONObject();
			table1.put("size", 0);
			table1.put("items", items);
			Constant.currentdatabase.getJSONObject("table").put(table, table1);
			Util.writeData(Constant.PATH_DICTIONARY, Constant.DICTIONARY.toString());
			Util.creatFile(Constant.PATH_ROOT + Constant.databasename + "/" + table + ".sql");
			Util.showInTextArea(sql, "ok, a table create success");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 创建用户
	 */
	public static void createUser() {
		try {
			// 检查语法
			if (!sql.matches("create user [a-z_][a-z0-9_]{0,99} \\w{1,100}")) {
				Util.showInTextArea(sql, Error.COMMAND_ERROR);
				return;
			}
			// 检查权限
			if (!CheckPermission.checkCreateUserPermission()) {
				Util.showInTextArea(sql, Error.ACCESS_DENIED);
				return;
			}
			// 是否已存在
			if (Constant.USERS.has(arr[2])) {
				Util.showInTextArea(sql, Error.USER_EXIST + " : " + arr[2]);
				return;
			}
			JSONObject u = new JSONObject();
			u.put("password", arr[3]);
			Constant.USERS.put(arr[2], u);
			Util.writeData(Constant.PATH_USERS, Constant.USERS.toString());
			Util.showInTextArea(sql, "ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 创建数据表语法
	 * 
	 * @return
	 */
	public static boolean checkCreateTableGrammar() {
		final String matchCreateTable = "create table [a-z_][a-z0-9_]{0,99} ?\\( ?( ?[a-z_][a-z0-9_]{0,99} (( ?int ?)|( ?do"
				+ "uble ?)|( ?varchar ?))(( ?primary key ?)|( ?not null ?)|( unique ?))?)( ?, ?[a-z_][a-z0-9_]{0,99} (( ?i"
				+ "nt ?)|( ?double ?)|( ?varchar ?))(( ?primary key ?)|( ?not null ?)|( unique ?))?){0,} ?\\)";
		pattern = Pattern.compile(matchCreateTable);
		matcher = pattern.matcher(sql);
		if (matcher.matches()) {
			table = sql.substring(13, sql.indexOf("(")).trim();
			return true;
		}
		return false;
	}

	/**
	 * 检查创建视图语法
	 * 
	 * @return
	 */
	private static boolean checkCreateViewGrammar() {
		String match = "create view ([a-z_][a-z0-9_]{0,99}) as (.+)";
		pattern = Pattern.compile(match);
		matcher = pattern.matcher(sql);
		if (matcher.matches()) {
			view = matcher.group(1);
			view_content = matcher.group(2);
			// 检查select中的语法
			if (!Select.checkSelectGrammer(view_content)) {
				Util.showInTextArea(sql, Error.COMMAND_ERROR);
				return false;
			}
			// 获得所有的属性
			String[] natures_array = Select.natures.split(",");
			for (int i = 0; i < natures_array.length; i++)
				natures_array[i] = natures_array[i].trim();
			List<String> natures_list = Util.getNaturesList(Select.table, false);
			// 检查所有的属性是否存在
			String result = Util.checkAllNatureExsit(natures_list, natures_array);
			if (result != null) {
				Util.showInTextArea(sql, Error.ATTR_NOT_EXIST + " : " + result);
				return false;
			}
			return true;
		}
		return false;
	}
}
