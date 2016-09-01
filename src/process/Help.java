package process;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import preprocess.Constant;
import preprocess.Error;
import preprocess.Util;
import ui.MainFrame;

/**
 * help����
 * 
 * @author W_SL
 *
 */
@SuppressWarnings("all")
public class Help {
	private static String[] arr = null;
	private static String sql;

	public static void Check(String[] arrs) {
		arr = arrs;
		sql = Util.arrayToString(arrs);
		if (arr.length >= 2) {
			switch (arr[1]) {
			case "database":
				showDatabase();
				break;
			case "table":
				showTable();
				break;
			case "view":
				showView();
				break;
			default:
				Util.showInTextArea(sql, Error.COMMAND_ERROR);
				break;
			}
		} else {
			Util.showInTextArea(sql, Error.COMMAND_ERROR);
		}
	}

	// �����ͼ�Ķ������
	private static void showView() {
		try {
			// �﷨����
			if (arr.length != 3) {
				Util.showInTextArea(sql, Error.COMMAND_ERROR);
				return;
			}
			// ���Ȩ��
			if (!Constant.username.equals("root")) {
				Util.showInTextArea(sql, Error.ACCESS_DENIED);
				return;
			}
			// ����Ƿ�ѡ�����ݿ�
			if (Constant.databasename == null) {
				Util.showInTextArea(sql, Error.NO_DATABASE_SELECTED);
				return;
			}
			// ��ͼ�Ƿ����
			if (!Constant.currentdatabase.getJSONObject("view").has(arr[2])) {
				Util.showInTextArea(sql, Error.VIEW_NOT_EXIST + " �� " + arr[2]);
				return;
			}
			String content = Constant.currentdatabase.getJSONObject("view").getJSONObject(arr[2]).getString("content") + "\n";
			JSONArray natures = Constant.currentdatabase.getJSONObject("view").getJSONObject(arr[2]).getJSONArray("items");
			content += "nature\t\t" + "type\t\t" + "limit" + "\n";
			for(int i = 0; i < natures.length(); i++){
				JSONObject temp = natures.getJSONObject(i);
				content += temp.getString("nature") + "\t\t" + temp.getString("type");
				if(temp.has("limit")){
					content  += "\t\t" + temp.getString("limit");
				}
				content += "\n";
			}
			Util.showInTextArea(sql, "content" + " : " + content.substring(0, content.length() - 1));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ����������ݱ���ͼ����������Ϣ��ͬʱ��ʾ���������
	 */
	public static void showDatabase() {
		// ����﷨
		if (arr.length != 2) {
			Util.showInTextArea(sql, Error.COMMAND_ERROR);
			return;
		}
		// ����Ƿ�ѡ�����ݿ�
		if (Constant.databasename == null) {
			Util.showInTextArea(sql, Error.NO_DATABASE_SELECTED);
			return;
		}
		try {
			// ִ��
			String content;
			if (Constant.username.equals("root")) {
				Iterator<String> it = Constant.currentdatabase.getJSONObject("view").keys();
				content = "all view : \n";
				int i = 0;
				while (it.hasNext()) {
					content += it.next() + "\n";
					i++;
				}
				content += "total �� " + i + "\n";
				content += "all table : \n";
				i = 0;
				it = Constant.currentdatabase.getJSONObject("table").keys();
				while (it.hasNext()) {
					content += it.next() + "\n";
					i++;
				}
				content += "total �� " + i;
			} else {
				Iterator<String> it = Constant.currentuser.getJSONObject(Constant.databasename).keys();
				int i = 0;
				content = "all table : \n";
				while (it.hasNext()) {
					content += it.next() + "\n";
					i++;
				}
				content += "total : " + i;
			}
			Util.showInTextArea(sql, content);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ������ݱ����������Ե���ϸ��Ϣ
	 */
	public static void showTable() {
		try {
			// ����﷨
			if (arr.length != 3) {
				Util.showInTextArea(sql, Error.COMMAND_ERROR);
				return;
			}
			// �Ƿ�ѡ�����ݿ�
			if (Constant.currentdatabase == null) {
				Util.showInTextArea(sql, Error.NO_DATABASE_SELECTED);
				return;
			}
			// ���Ƿ����
			if (!Constant.currentdatabase.getJSONObject("table").has(arr[2])) {
				Util.showInTextArea(sql, Error.TABLE_NOT_EXIST + " : " + arr[2]);
				return;
			}

			// ���Ȩ��
			if (!Constant.username.equals("root")) {
				if (!Constant.currentuser.getJSONObject(Constant.databasename).has(arr[2])) {
					Util.showInTextArea(sql, Error.ACCESS_DENIED);
					return;
				}
			}
			JSONArray table = Constant.currentdatabase.getJSONObject("table").getJSONObject(arr[2])
					.getJSONArray("items");
			String content = arr[2] + "\nnature\ttype\tlimit\n";
			for (int i = 0; i < table.length(); i++) {
				JSONObject temp = table.getJSONObject(i);
				content += temp.getString("nature") + "\t";
				content += temp.getString("type");
				if (temp.has("limit")) {
					content += "\t" + temp.getString("limit");
				}
				content += "\n";
			}
			Util.showInTextArea(sql, content.substring(0, content.length() - 1));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
