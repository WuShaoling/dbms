package process;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;

import preprocess.Constant;
import preprocess.Error;
import preprocess.Util;

/**
 * ����delete
 * 
 * @author W_SL
 *
 */
public class Delete {
	private static List<String[]> alldata = null;
	private static String sql = null;
	private static String table = null;
	private static String condition = null;
	private static Pattern pattern = null;
	private static Matcher matcher = null;
	private static boolean isview = false;
	private static String view = "";

	public static void Check(String[] arrs) {
		sql = Util.arrayToString(arrs);
		condition = null;
		table = null;
		alldata = null;
		view = "";
		// ����Ƿ�ѡ�����ݿ�
		if (Constant.currentdatabase == null) {
			Util.showInTextArea(sql, Error.NO_DATABASE_SELECTED);
			return;
		}
		// ����﷨
		if (!checkDeleteGrammer()) {
			Util.showInTextArea(sql, Error.COMMAND_ERROR);
			return;
		}
		// �����Ƿ����
		try {
			if (!Constant.currentdatabase.getJSONObject("table").has(table)) {
				if (!Constant.currentdatabase.getJSONObject("view").has(table)) {
					Util.showInTextArea(sql, Error.TABLE_NOT_EXIST + " : " + table);
					return;
				}else{
					isview = true;
					view = table;
					table = Constant.currentdatabase.getJSONObject("view").getJSONObject(table).getString("table");
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// ���Ȩ��
		if (!CheckPermission.checkPermission(table, isview, "delete")) {
			Util.showInTextArea(sql, Error.ACCESS_DENIED);
			return;
		}
		// �����һ�����������where��
		if (condition == null) {
			deleteWithoutCondition();
		} else {
			deleteWithCondition();
		}
	}

	/**
	 * ���������Ȼ�����ݣ�ѡ�����ݣ�Щ����
	 */
	private static void deleteWithCondition() {
		alldata = GetResultWithCheck.getAllResult(sql, table, view, condition, true, isview);
		if (alldata == null) {
			return;
		}
		try {
			int size = Constant.currentdatabase.getJSONObject("table").getJSONObject(table).getInt("size");
			Util.showInTextArea(sql, "ok, " + String.valueOf(size - alldata.size()) + " rows deleted!");
			Util.updateTableSize(table, alldata.size());
			Util.writeData(Constant.PATH_ROOT + Constant.databasename + "/" + table + ".sql",
					Util.parseListToTableFormat(alldata));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * û��������ɾ�����е����� ,��ɾ���ļ����ٴ����ļ�
	 */
	private static void deleteWithoutCondition() {
		Util.deleteFile(Constant.PATH_ROOT + "/" + Constant.databasename + "/" + table + ".sql");
		Util.creatFile(Constant.PATH_ROOT + "/" + Constant.databasename + "/" + table + ".sql");
		try {
			Util.showInTextArea(sql,
					"ok, " + Constant.currentdatabase.getJSONObject("table").getJSONObject(table).getInt("size")
							+ " rows deleted!");
			Util.updateTableSize(table, 0);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ���ɾ���﷨
	 * 
	 * @return
	 */
	public static boolean checkDeleteGrammer() {
		String match = "delete from ([a-z0-9_]+) where (.+)";
		pattern = Pattern.compile(match);
		matcher = pattern.matcher(sql);
		if (matcher.matches()) {
			table = matcher.group(1);
			condition = matcher.group(2);
			return true;
		} else {
			match = "delete from ([a-z0-9_]+)";
			pattern = Pattern.compile(match);
			matcher = pattern.matcher(sql);
			if (matcher.matches()) {
				table = matcher.group(1);
				return true;
			}
		}
		return false;
	}
}
