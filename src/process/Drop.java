package process;

import org.json.JSONException;

import preprocess.Constant;
import preprocess.Error;
import preprocess.PreProcess;
import preprocess.Util;

/**
 * ����drop
 * 
 * @author WSL
 */
public class Drop {
	private static String[] arr = null;
	private static String sql;

	public static void Check(String[] arrs) {
		arr = arrs;
		sql = Util.arrayToString(arrs);
		if (arr.length >= 2) {
			switch (arr[1]) {
			case "database":
				dropDatabase();
				break;
			case "table":
				dropTable();
				break;
			case "user":
				dropUser();
				break;
			case "view":
				dropView();
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
	 * ɾ����ͼ
	 */
	private static void dropView() {
		// ����﷨
		if (arr.length != 3) {
			Util.showInTextArea(sql, Error.COMMAND_ERROR);
			return;
		}
		// ����Ƿ�ѡ�����ݿ�
		if (Constant.currentdatabase == null) {
			Util.showInTextArea(sql, Error.NO_DATABASE_SELECTED);
			return;
		}
		// �����ͼ�Ƿ����
		try {
			if (!Constant.currentdatabase.getJSONObject("view").has(arr[2])) {
				Util.showInTextArea(sql, Error.VIEW_NOT_EXIST + " : " + arr[2]);
				return;
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		// ���Ȩ��
		if (!CheckPermission.checkDropViewPermission()) {
			Util.showInTextArea(sql, Error.ACCESS_DENIED);
			return;
		}
		try {
			Constant.currentdatabase.getJSONObject("view").remove(arr[2]);
			Util.writeData(Constant.PATH_DICTIONARY, Constant.DICTIONARY.toString());
			Util.showInTextArea(sql, "ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ɾ�����ݿ�
	 */
	public static void dropDatabase() {
		try {
			// ����﷨
			if (arr.length != 3) {
				Util.showInTextArea(sql, Error.COMMAND_ERROR);
				return;
			}
			// ������ݿ��Ƿ����
			if (!Constant.DICTIONARY.has(arr[2])) {
				Util.showInTextArea(sql, Error.DATABASE_NOT_EXIST + " : " + arr[2]);
				return;
			}
			// ���Ȩ��
			if (!CheckPermission.checkDropDatabasePermission()) {
				Util.showInTextArea(sql, Error.ACCESS_DENIED);
				return;
			}
			// ִ��
			if (arr[2].equals(Constant.databasename)) {
				Constant.databasename = null;
				Constant.currentdatabase = null;
			}
			Constant.DICTIONARY.remove(arr[2]);
			Util.writeData(Constant.PATH_DICTIONARY, Constant.DICTIONARY.toString());
			Util.deletePath(Constant.PATH_ROOT + arr[2]);
			Util.showInTextArea(sql, "ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ɾ����
	 */
	public static void dropTable() {
		// ����﷨
		if (arr.length != 3) {
			Util.showInTextArea(sql, Error.COMMAND_ERROR);
			return;
		}
		// ����Ƿ�ѡ�����ݿ�
		if (Constant.currentdatabase == null) {
			Util.showInTextArea(sql, Error.NO_DATABASE_SELECTED);
			return;
		}
		// �����Ƿ����
		try {
			if (!Constant.currentdatabase.getJSONObject("table").has(arr[2])) {
				Util.showInTextArea(sql, Error.TABLE_NOT_EXIST + " : " + arr[2]);
				return;
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		// ���Ȩ��
		if (!CheckPermission.checkDropTablePermission()) {
			Util.showInTextArea(sql, Error.ACCESS_DENIED);
			return;
		}
		try {
			Constant.currentdatabase.getJSONObject("table").remove(arr[2]);
			Util.writeData(Constant.PATH_DICTIONARY, Constant.DICTIONARY.toString());
			Util.deleteFile(Constant.PATH_ROOT + Constant.databasename + "/" + arr[2] + ".sql");
			Util.showInTextArea(sql, "ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ɾ���û�
	 */
	public static void dropUser() {
		// ����﷨
		if (arr.length != 3) {
			Util.showInTextArea(sql, Error.COMMAND_ERROR);
			return;
		}
		// ����Ƿ����
		if (!Constant.USERS.has(arr[2])) {
			Util.showInTextArea(sql, Error.USER_EXIST + " : " + arr[2]);
			return;
		}
		// ���Ȩ��
		if (!CheckPermission.checkDropUserPermission()) {
			Util.showInTextArea(sql, Error.ACCESS_DENIED);
			return;
		}
		try {
			if (Constant.username.equals(arr[2])) {
				Constant.username = null;
				Constant.currentuser = null;
				PreProcess.islogin = false;
			}
			Constant.USERS.remove(arr[2]);
			Util.writeData(Constant.PATH_USERS, Constant.USERS.toString());
			Util.showInTextArea(sql, "ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
