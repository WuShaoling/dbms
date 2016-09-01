package process;

import preprocess.Constant;
import preprocess.Error;
import preprocess.Util;

/**
 * �ı����ݿ�
 * 
 * @author WSL
 *
 */
public class Use {
	/**
	 * �ı����ݿ�
	 */
	public static void Check(String[] arr) {
		// ����﷨
		String sql = Util.arrayToString(arr);
		if (arr.length != 2) {
			Util.showInTextArea(sql, Error.COMMAND_ERROR);
			return;
		}
		// ������ݿ����
		if (!Constant.DICTIONARY.has(arr[1])) {
			Util.showInTextArea(sql, Error.DATABASE_NOT_EXIST + " : " + arr[1]);
			return;
		}
		// ���Ȩ��
		if (!CheckPermission.checkUsePermission(arr[1])) {
			Util.showInTextArea(sql, Error.ACCESS_DENIED);
			return;
		}
		// ִ��
		try {
			Constant.databasename = arr[1];
			Constant.currentdatabase = Constant.DICTIONARY.getJSONObject(arr[1]);
			Util.showInTextArea(sql, "Database changed!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
