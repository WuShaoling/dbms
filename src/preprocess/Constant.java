package preprocess;

import org.json.JSONObject;

/**
 * ����
 * 
 * @author WSL
 *
 */
public class Constant {
	public static char SPLIT = 8;// �ָ���

	public static JSONObject currentuser = null;// ��ǰ�û�
	public static JSONObject currentdatabase = null;// ��ǰ���ݿ�
	public static String username = null; // ��ǰ�û���
	public static String databasename = null;// ��ǰ���ݿ���

	// �����ֵ�
	public static JSONObject USERS = null; // ���ֵ䣬�û��ֵ�
	public static JSONObject DICTIONARY = null;// �ֵ�

	// ·��
	public static String PATH_ROOT = "./"; // ��Ŀ¼
	public static final String PATH_USERS = PATH_ROOT + "users.sql"; // �û��ļ�Ŀ¼
	public static final String PATH_DICTIONARY = PATH_ROOT + "dictionary.sql"; // ���ݿ��ֵ��ļ�Ŀ¼
}
