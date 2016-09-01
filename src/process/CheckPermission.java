package process;

import org.json.JSONException;

import preprocess.Constant;

/**
 * ���Ȩ��
 * 
 * @author WSL
 *
 */
public class CheckPermission {

	/**
	 * �ı����ݿ�Ȩ��
	 * 
	 * @return
	 */
	public static boolean checkUsePermission(String database) {
		if (Constant.username.equals("root"))
			return true;
		try {
			if (Constant.currentuser.has(database))
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * �������ݿ�Ȩ��
	 * 
	 * @return
	 */
	public static boolean checkCreateDatabasePermission() {
		if (Constant.username.equals("root"))
			return true;
		return false;
	}

	/**
	 * �������ݱ�Ȩ��
	 * 
	 * @return
	 */
	public static boolean checkCreateTablePermission() {
		if (Constant.username.equals("root"))
			return true;
		return false;
	}
	
	/**
	 * ������ͼȨ��
	 * 
	 * @return
	 */
	public static boolean checkCreateViewPermission() {
		if (Constant.username.equals("root"))
			return true;
		return false;
	}

	/**
	 * ����û�Ȩ��
	 * 
	 * @return
	 */
	public static boolean checkCreateUserPermission() {
		if (Constant.username.equals("root"))
			return true;
		return false;
	}

	/**
	 * ɾ�����ݿ�Ȩ��
	 * 
	 * @return
	 */
	public static boolean checkDropDatabasePermission() {
		if (Constant.username.equals("root"))
			return true;
		return false;
	}

	/**
	 * ɾ���û�Ȩ��
	 * 
	 * @return
	 */
	public static boolean checkDropUserPermission() {
		if (Constant.username.equals("root"))
			return true;
		return false;
	}

	/**
	 * ɾ����Ȩ��
	 * 
	 * @return
	 */
	public static boolean checkDropTablePermission() {
		if (Constant.username.equals("root"))
			return true;
		return false;
	}

	/**
	 * ɾ����ͼȨ��
	 * 
	 * @return
	 */
	public static boolean checkDropViewPermission() {
		if (Constant.username.equals("root"))
			return true;
		return false;
	}

	/**
	 * ���û���Ȩ��Ȩ��
	 * 
	 * @return
	 */
	public static boolean checkGrantAndRevokePermission() {
		if (Constant.username.equals("root"))
			return true;
		return false;
	}

	/**
	 * ���롢ɾ�����޸ġ�ѡ��Ȩ��
	 * 
	 * @param table
	 * @return
	 */
	public static boolean checkPermission(String table, boolean isview, String type) {
		if (Constant.username.equals("root"))
			return true;
		try {
			if(isview)
				return false;
			if (Constant.currentuser.has(Constant.databasename)) {
				if (Constant.currentuser.getJSONObject(Constant.databasename).has(table)) {
					if(Constant.currentuser.getJSONObject(Constant.databasename).getJSONObject(table).has(type)){
						return true;
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
}
