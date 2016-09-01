package preprocess;

import org.json.JSONObject;

/**
 * 常量
 * 
 * @author WSL
 *
 */
public class Constant {
	public static char SPLIT = 8;// 分隔符

	public static JSONObject currentuser = null;// 当前用户
	public static JSONObject currentdatabase = null;// 当前数据库
	public static String username = null; // 当前用户名
	public static String databasename = null;// 当前数据库名

	// 数据字典
	public static JSONObject USERS = null; // 子字典，用户字典
	public static JSONObject DICTIONARY = null;// 字典

	// 路径
	public static String PATH_ROOT = "./"; // 根目录
	public static final String PATH_USERS = PATH_ROOT + "users.sql"; // 用户文件目录
	public static final String PATH_DICTIONARY = PATH_ROOT + "dictionary.sql"; // 数据库字典文件目录
}
