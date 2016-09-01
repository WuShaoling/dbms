package process;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;

import beans.ItemCondition;
import preprocess.Constant;
import preprocess.Error;
import preprocess.Util;

/**
 * 处理update
 * 
 * @author W_SL
 *
 */
public class Update {

	private static List<ItemCondition> allnatures = null;
	private static List<ItemCondition> allcondition = null;
	private static List<String[]> alldata = null;
	private static String sql = null;
	private static String table = null;
	private static String natures = null;
	private static String condition = null;
	private static Pattern pattern = null;
	private static Matcher matcher = null;
	private static int len = 0;
	private static boolean isview = false;
	private static String view = "";

	public static void Check(String[] arrs) {
		sql = Util.arrayToString(arrs);
		condition = null;
		table = null;
		alldata = null;
		natures = null;
		allnatures = null;
		allcondition = null;
		isview = false;
		view = null;
		len = 0;
		// 检查是否选中数据库
		if (Constant.currentdatabase == null) {
			Util.showInTextArea(sql, Error.NO_DATABASE_SELECTED);
			return;
		}
		// 检查语法
		if (!checkUpdateGrammer()) {
			Util.showInTextArea(sql, Error.COMMAND_ERROR);
			return;
		}
		// 检查表是否存在
		try {
			if (!Constant.currentdatabase.getJSONObject("table").has(table)) {
				if (!Constant.currentdatabase.getJSONObject("view").has(table)) {
					Util.showInTextArea(sql, Error.TABLE_NOT_EXIST);
					return;
				} else {
					isview = true;
					view = table;
					table = Constant.currentdatabase.getJSONObject("view").getJSONObject(table).getString("table");
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}
		// 检查权限
		if (!CheckPermission.checkPermission(table, isview, "update")) {
			Util.showInTextArea(sql, Error.ACCESS_DENIED);
			return;
		}
		// 获得所有数据
		alldata = Util.readDataFromTable(table);
		// 获得where子句
		if (condition != null) {
			if ((allcondition = GetResultWithCheck.getMulitCondition(sql, condition, table, view, isview)) == null) {
				return;
			}
		}
		// 获得要更新的属性
		boolean ismatch = false;
		boolean aa = false;

		String match_comma = "([a-z0-9_]+ ?(=) ?.+)( ?, ?[a-z0-9_]+ ?(=) ?.+){1,}";
		if ((allnatures = GetResultWithCheck.getSingleCondition(match_comma, ",", natures)) != null) {
			String tt = table;
			if (isview)
				tt = view;
			if (GetResultWithCheck.checkAllConditions(sql, allnatures, tt, isview)) {
				ismatch = true;
			} else {
				aa = true;
			}
		} else {
			String tt = table;
			if (isview)
				tt = view;
			String match_single = "([a-z0-9_]+ ?(=) ?.+)";
			if ((allnatures = GetResultWithCheck.getSingleCondition(match_single, null, natures)) != null) {
				if (GetResultWithCheck.checkAllConditions(sql, allnatures, tt, isview)) {
					ismatch = true;
				} else {
					aa = true;
				}
			}
		}
		if (!ismatch) {
			if (!aa)
				Util.showInTextArea(sql, Error.COMMAND_ERROR);
			return;
		}
		String[] arr1 = new String[allnatures.size()];
		String[] arr2 = new String[allnatures.size()];
		Iterator<ItemCondition> it = allnatures.iterator();
		int i = 0;
		while (it.hasNext()) {
			ItemCondition temp = it.next();
			arr1[i] = temp.getNature();
			arr2[i] = temp.getValue();
			i++;
		}
		// 检查完整性约束
		if (!Util.checkLimit(sql, table, arr1, arr2, false)) {
			return;
		}
		executeUpdata();
		// 写回磁盘
		Util.showInTextArea(sql, "ok, " + len + " rows updated");
		Util.writeData(Constant.PATH_ROOT + Constant.databasename + "/" + table + ".sql",
				Util.parseListToTableFormat(alldata));
	}

	/**
	 * 执行更新
	 */
	private static void executeUpdata() {
		if (GetResultWithCheck.flag == 1) {// and语句
			updataWithAndCondition();
		} else if (GetResultWithCheck.flag == 2) {// or语句
			updataWithOrCondition();
		} else {
			for (int i = 0; i < alldata.size(); i++) {
				String[] result = updateSingleRow(alldata.get(i));
				alldata.remove(i);
				alldata.add(i, result);
			}
		}
	}

	/**
	 * 匹配and
	 * 
	 * @param list
	 * @param table
	 */
	public static void updataWithAndCondition() {
		for (int i = 0; i < alldata.size(); i++) {
			String[] arr = alldata.get(i);
			if (arr.length > 0) {
				boolean yes = true;// 符合条件
				for (int j = 0; j < allcondition.size(); j++) {
					ItemCondition temp = allcondition.get(j);
					int pos = Util.getNaturePosition(table, temp.getNature(), false);
					if (temp.getOperation().equals("=")) {
						if (!arr[pos].equals(temp.getValue())) {
							yes = false;
							break;
						}
					} else if (temp.getOperation().equals("!=")) {
						if (arr[pos].equals(temp.getValue())) {
							yes = false;
							break;
						}
					} else if (temp.getOperation().equals(">")) {
						if (Double.valueOf(arr[pos]) <= Double.valueOf(temp.getValue())) {
							yes = false;
							break;
						}
					} else if (temp.getOperation().equals("<")) {
						if (Double.valueOf(arr[pos]) >= Double.valueOf(temp.getValue())) {
							yes = false;
							break;
						}
					}
				}
				if (yes) {
					String[] result = updateSingleRow(alldata.get(i));
					alldata.remove(i);
					alldata.add(i, result);
				}
			}
		}
	}

	/**
	 * 匹配or
	 */
	public static void updataWithOrCondition() {
		for (int i = 0; i < alldata.size(); i++) {
			String[] arr = alldata.get(i);
			if (arr.length > 0) {
				for (int j = 0; j < allcondition.size(); j++) {
					ItemCondition temp = allcondition.get(j);
					int pos = Util.getNaturePosition(table, temp.getNature(), false);
					if (temp.getOperation().equals("=")) {
						if (arr[pos].equals(temp.getValue())) {
							String[] result = updateSingleRow(alldata.get(i));
							alldata.remove(i);
							alldata.add(i, result);
							break;
						}
					} else if (temp.getOperation().equals("!=")) {
						if (!arr[pos].equals(temp.getValue())) {
							String[] result = updateSingleRow(alldata.get(i));
							alldata.remove(i);
							alldata.add(i, result);
							break;
						}
					} else if (temp.getOperation().equals(">")) {
						if (Double.valueOf(arr[pos]) > Double.valueOf(temp.getValue())) {
							String[] result = updateSingleRow(alldata.get(i));
							alldata.remove(i);
							alldata.add(i, result);
							break;
						}
					} else if (temp.getOperation().equals("<")) {
						if (Double.valueOf(arr[pos]) < Double.valueOf(temp.getValue())) {
							String[] result = updateSingleRow(alldata.get(i));
							alldata.remove(i);
							alldata.add(i, result);
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * 更新单条数据
	 * 
	 * @param arr
	 * @return
	 */
	public static String[] updateSingleRow(String[] arr) {
		len++;
		for (int i = 0; i < allnatures.size(); i++) {
			ItemCondition temp = allnatures.get(i);
			int pos = Util.getNaturePosition(table, temp.getNature(), false);
			arr[pos] = temp.getValue();
		}
		return arr;
	}

	/**
	 * 检查更新语法
	 * 
	 * @return
	 */
	public static boolean checkUpdateGrammer() {
		String match = "update ([a-z0-9_]+) set (.+) where (.+)";
		pattern = Pattern.compile(match);
		matcher = pattern.matcher(sql);
		if (matcher.matches()) {
			table = matcher.group(1).trim();
			natures = matcher.group(2).trim();
			condition = matcher.group(3).trim();
			return true;
		} else {
			match = "update ([a-z0-9_]+) set (.+)";
			pattern = Pattern.compile(match);
			matcher = pattern.matcher(sql);
			if (matcher.matches()) {
				table = matcher.group(1).trim();
				natures = matcher.group(2).trim();
				return true;
			}
		}
		return false;
	}
}
