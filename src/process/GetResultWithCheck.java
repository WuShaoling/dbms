package process;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import beans.ItemCondition;
import preprocess.Constant;
import preprocess.Error;
import preprocess.Util;

/**
 * 
 * @author WSL
 *
 */
public class GetResultWithCheck {
	private static List<String[]> aalldata = null; // alldataȡ��
	private static List<String[]> alldata = null;
	private static Pattern pattern = null;
	private static Matcher matcher = null;
	private static String match_and = "([a-z0-9_]+ ?(>|<|=|!=) ?.+)( and [a-z0-9_]+ ?(>|<|=|!=) ?.+){1,}";
	private static String match_or = "([a-z0-9_]+ ?(>|<|=|!=) ?.+)( or [a-z0-9_]+ ?(>|<|=|!=) ?.+){1,}";
	private static String match_single = "([a-z0-9_]+ ?(>|<|=|!=) ?.+)";
	public static int flag = 0;

	/**
	 * ���ƥ��Ľ����
	 * 
	 * @param sql
	 * @param table
	 * @param condition
	 * @param delete
	 * @return
	 */
	public static List<String[]> getAllResult(String sql, String table, String view, String condition, boolean delete,
			boolean isview) {
		// ���where�Ӿ������
		flag = 0;
		alldata = Util.readDataFromTable(table);
		aalldata = new LinkedList<String[]>();
		List<ItemCondition> list = getMulitCondition(sql, condition, table, view, isview);
		if (list == null) {
			return null;
		}
		if (flag == 1) {
			getDataWithAndCondition(list, table);
		} else if (flag == 2) {
			getDataWithOrCondition(list, table);
		}
		if (delete)
			return aalldata;
		return alldata;
	}

	/**
	 * ��ò�ȷ��where���������� ����ȷ�� and or ���� û��andû��or
	 * 
	 * @param sql
	 * @param condition
	 * @param table
	 * @return
	 */
	public static List<ItemCondition> getMulitCondition(String sql, String condition, String table, String view, boolean isview) {
		alldata = Util.readDataFromTable(table);
		aalldata = new LinkedList<String[]>();
		List<ItemCondition> list;
		flag = 0;
		if (((list = getSingleCondition(match_and, "and", condition)) != null)) {
			flag = 1;
		} else if ((list = getSingleCondition(match_or, "or", condition)) != null) {
			flag = 2;
		} else if ((list = getSingleCondition(match_single, null, condition)) != null) {
			flag = 1;
		}
		// ���ݲ�ƥ��
		if (flag == 0) {
			Util.showInTextArea(sql, Error.COMMAND_ERROR);
			return null;
		}
		// �������Ͳ�ƥ��
		String tt = table;
		if(isview)
			tt = view;
		if (!checkAllConditions(sql, list, tt, isview)) {
			return null;
		}
		return list;
	}

	/**
	 * �����ַ������ݽ���ƥ�䲢�ִʻ�����ݣ����ݶ��ţ�and�� or�ִ�
	 * 
	 * @param match
	 *            ƥ��ı��ʽ
	 * @param split
	 *            �ָ���
	 * @param condition
	 *            ԭ�ַ���
	 * @return
	 */
	public static List<ItemCondition> getSingleCondition(String match, String split, String content) {
		pattern = Pattern.compile(match);
		matcher = pattern.matcher(content);
		if (matcher.matches()) {
			List<ItemCondition> list = new LinkedList<ItemCondition>();
			String items[] = { content };
			if (split != null) {
				items = content.split(split);
			}
			for (int i = 0; i < items.length; i++) {
				items[i] = items[i].trim();
				pattern = Pattern.compile("([a-z0-9_]+) ?(>|<|=|!=) ?(.+)");
				matcher = pattern.matcher(items[i]);
				if (matcher.matches()) {
					list.add(new ItemCondition(matcher.group(1).trim(), matcher.group(2).trim(),
							matcher.group(3).trim()));
				}
			}
			return list;
		}
		return null;
	}

	/**
	 * ���where�Ӿ��е�ÿһ�����������Ƿ���ȷ�������Ƿ�ƥ�� user = 123 and name = 123
	 * 
	 * @param list
	 * @return
	 */
	public static boolean checkAllConditions(String sql, List<ItemCondition> list, String table, boolean isview) {
		try {
			JSONArray items;
			if (isview) {
				items = Constant.currentdatabase.getJSONObject("view").getJSONObject(table).getJSONArray("items");
			} else {
				items = Constant.currentdatabase.getJSONObject("table").getJSONObject(table).getJSONArray("items");
			}
			Iterator<ItemCondition> it = list.iterator();
			while (it.hasNext()) {
				ItemCondition temp = it.next();
				boolean match = false;
				for (int j = 0; j < items.length(); j++) {
					JSONObject item = items.getJSONObject(j);
					if (temp.getNature().equals(item.getString("nature"))) {// ����ƥ�䣬��ƥ����������
						if (item.getString("type").equals("varchar")) {
							if (!(temp.getOperation().equals("=") || temp.getOperation().equals("!="))) {
								Util.showInTextArea(sql, Error.DATATYPE_NOT_MATCH + " : " + temp.getNature());
								return false;
							}
						} else if (item.getString("type").equals("int")) {
							try {
								Integer.valueOf(temp.getValue());
							} catch (Exception e) {
								Util.showInTextArea(sql, Error.DATATYPE_NOT_MATCH + " : " + temp.getNature());
								return false;
							}
						} else {
							try {
								Double.valueOf(temp.getValue());
							} catch (Exception e) {
								Util.showInTextArea(sql, Error.DATATYPE_NOT_MATCH + " : " + temp.getNature());
								return false;
							}
						}
						match = true;
						break;
					}
				}
				if (!match) {
					Util.showInTextArea(sql, Error.ATTR_NOT_EXIST + " : " + temp.getNature());
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * ÿһ������ƥ��and
	 */
	public static void getDataWithAndCondition(List<ItemCondition> list, String table) {
		Iterator<String[]> it = alldata.iterator();
		while (it.hasNext()) {
			String[] arr = it.next();
			if (arr.length > 0) {
				for (int j = 0; j < list.size(); j++) {
					ItemCondition temp = list.get(j);
					int pos = Util.getNaturePosition(table, temp.getNature(), false);
					if (temp.getOperation().equals("=")) {
						if (!arr[pos].equals(temp.getValue())) {
							aalldata.add(arr);
							it.remove();
							break;
						}
					} else if (temp.getOperation().equals("!=")) {
						if (arr[pos].equals(temp.getValue())) {
							aalldata.add(arr);
							it.remove();
							break;
						}
					} else if (temp.getOperation().equals(">")) {
						if (Double.valueOf(arr[pos]) <= Double.valueOf(temp.getValue())) {
							aalldata.add(arr);
							it.remove();
							break;
						}
					} else if (temp.getOperation().equals("<")) {
						if (Double.valueOf(arr[pos]) >= Double.valueOf(temp.getValue())) {
							aalldata.add(arr);
							it.remove();
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * ÿһ������ƥ��or
	 */
	public static void getDataWithOrCondition(List<ItemCondition> list, String table) {
		Iterator<String[]> it = alldata.iterator();
		while (it.hasNext()) {
			String[] arr = it.next();
			if (arr.length > 0) {
				boolean is = false;
				for (int j = 0; j < list.size(); j++) {
					ItemCondition temp = list.get(j);
					int pos = Util.getNaturePosition(table, temp.getNature(), false);
					if (temp.getOperation().equals("=")) {
						if (arr[pos].equals(temp.getValue())) {
							is = true;
							break;
						}
					} else if (temp.getOperation().equals("!=")) {
						if (!arr[pos].equals(temp.getValue())) {
							is = true;
							break;
						}
					} else if (temp.getOperation().equals(">")) {
						if (Double.valueOf(arr[pos]) > Double.valueOf(temp.getValue())) {
							is = true;
							break;
						}
					} else if (temp.getOperation().equals("<")) {
						if (Double.valueOf(arr[pos]) < Double.valueOf(temp.getValue())) {
							is = true;
							break;
						}
					}
				}
				if (!is) {
					aalldata.add(arr);
					it.remove();
				}
			}
		}
	}
}
