package process;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import preprocess.Constant;
import preprocess.Error;
import preprocess.Util;

/**
 * ����insert
 * 
 * @author WSL
 *
 */
public class Insert {
	private static String[] arr = null;
	private static String[] values = null;
	private static String table = null;
	private static String sql = null;
	private static boolean isview = false;

	public static void Check(String[] arrs) {
		values = null;
		arr = arrs;
		isview = false;
		sql = Util.arrayToString(arrs);
		String sql = Util.arrayToString(arr);
		// ����Ƿ�ѡ�����ݿ�
		if (Constant.currentdatabase == null) {
			Util.showInTextArea(sql, Error.NO_DATABASE_SELECTED);
			return;
		}
		// ����﷨
		if (!checkInsertGrammer(sql)) {
			Util.showInTextArea(sql, Error.COMMAND_ERROR);
			return;
		}
		// �����Ǵ���
		try {
			if (!Constant.currentdatabase.getJSONObject("table").has(table)) {
				if (!Constant.currentdatabase.getJSONObject("table").has(table)) {
					Util.showInTextArea(sql, Error.TABLE_NOT_EXIST + " : " + table);
					return;
				}else{
					isview = true;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}
		// ���Ȩ��
		if (!CheckPermission.checkPermission(table, isview, "insert")) {
			Util.showInTextArea(sql, Error.ACCESS_DENIED);
			return;
		}
		// ��������Ƿ�ƥ��
		if (!checkDataMatch()) {
			return;
		}
		//����Ƿ���Լ�����Ƿ����
		String[] allnature = Util.getNaturesArray(table, isview);
		if(!Util.checkLimit(sql, table, allnature, values, false)){
			return;
		}
		// �����
		insertIntoDB(sql);
		Util.showInTextArea(sql, "ok, insert 1 record to " + arr[2]);
	}

	/**
	 * �������ݱ�����
	 */
	public static void insertIntoDB(String sql) {
		String str = values[0];
		for (int i = 1; i < values.length; i++) {
			str += Constant.SPLIT + values[i];
		}
		try {
			int size = Constant.currentdatabase.getJSONObject("table").getJSONObject(table).getInt("size");
			Util.insertDataToTable(str + "\r\n", table);
			Util.updateTableSize(table, size + 1);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��������Ƿ�ƥ��
	 * 
	 * @return
	 */
	public static boolean checkDataMatch() {
		try {
			JSONArray items = Constant.currentdatabase.getJSONObject("table").getJSONObject(table)
					.getJSONArray("items");
			if (items.length() != values.length) {
				Util.showInTextArea(sql, Error.DATATYPE_NOT_MATCH);
				return false;
			}
			for (int i = 0; i < values.length; i++) {
				JSONObject item = items.getJSONObject(i);
				if ((values[i].charAt(0) == '"' && values[i].charAt(values[i].length() - 1) == '"')
						|| (values[i].charAt(0) == '\'' && values[i].charAt(values[i].length() - 1) == '\'')) {
					if (!item.get("type").equals("varchar")) {
						Util.showInTextArea(sql,
								Error.DATATYPE_NOT_MATCH + " : " + item.get("nature") + " is " + item.get("type"));
						return false;
					}
				} else {
					try {
						Double.parseDouble(values[i]);
						if ((values[i].contains(".")) && (!item.get("type").equals("double"))) {
							Util.showInTextArea(sql,
									Error.DATATYPE_NOT_MATCH + " : " + item.get("nature") + " is " + item.get("type"));
							return false;
						}
					} catch (Exception e) {
						Util.showInTextArea(sql,
								Error.DATATYPE_NOT_MATCH + " : " + item.get("nature") + " is " + item.get("type"));
						return false;
					}
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * ����﷨
	 * 
	 * @return
	 */
	public static boolean checkInsertGrammer(String sql) {
		try {
			Pattern pattern = Pattern.compile("insert into (.{1,}) values ?\\((.{1,})\\)");
			Matcher matcher = pattern.matcher(sql);
			if (matcher.matches()) {
				table = matcher.group(1);
				String temp_value = matcher.group(2);
				pattern = Pattern.compile("( ?.{1,} ?, ?)+");
				matcher = pattern.matcher(temp_value + ",");
				if (matcher.matches()) {
					values = temp_value.split(",");
					for (int i = 0; i < values.length; i++) {
						values[i] = values[i].trim();
					}
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
