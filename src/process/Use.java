package process;

import preprocess.Constant;
import preprocess.Error;
import preprocess.Util;

/**
 * 改变数据库
 * 
 * @author WSL
 *
 */
public class Use {
	/**
	 * 改变数据库
	 */
	public static void Check(String[] arr) {
		// 检查语法
		String sql = Util.arrayToString(arr);
		if (arr.length != 2) {
			Util.showInTextArea(sql, Error.COMMAND_ERROR);
			return;
		}
		// 检查数据库存在
		if (!Constant.DICTIONARY.has(arr[1])) {
			Util.showInTextArea(sql, Error.DATABASE_NOT_EXIST + " : " + arr[1]);
			return;
		}
		// 检查权限
		if (!CheckPermission.checkUsePermission(arr[1])) {
			Util.showInTextArea(sql, Error.ACCESS_DENIED);
			return;
		}
		// 执行
		try {
			Constant.databasename = arr[1];
			Constant.currentdatabase = Constant.DICTIONARY.getJSONObject(arr[1]);
			Util.showInTextArea(sql, "Database changed!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
