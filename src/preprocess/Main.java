package preprocess;

import java.io.File;
import java.io.IOException;

import org.json.JSONObject;

import ui.MainFrame;

/**
 * 程序入口，初始化程序，初始化失败则提示并退出，成功则执行
 * 
 * @author WSL
 */
public class Main {
	public static void createUserDic() {
		File file = new File(Constant.PATH_USERS);
		try {
			file.createNewFile();
			JSONObject root = new JSONObject();
			root.put("password", "123");
			JSONObject user = new JSONObject();
			user.put("root", root);
			Util.writeData(Constant.PATH_USERS, user.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void createDic() {
		File file = new File(Constant.PATH_DICTIONARY);
		try {
			file.createNewFile();
			Util.writeData(Constant.PATH_DICTIONARY, new JSONObject().toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化
	 * 
	 * @return
	 */
	public static boolean init() {
		try {
			File file = new File(Constant.PATH_ROOT);
			if (file.exists()) {
				file = new File(Constant.PATH_DICTIONARY);
				if (!file.exists()) {
					createDic();
				}
				file = new File(Constant.PATH_USERS);
				if (!file.exists()) {
					createUserDic();
				}
			} else {
				file.mkdir();
				createDic();
				createUserDic();
			}
			new MainFrame();
			Constant.USERS = new JSONObject(Util.readData(Constant.PATH_USERS));
			Constant.DICTIONARY = new JSONObject(Util.readData(Constant.PATH_DICTIONARY));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void main(String[] args) throws Exception {
		if (init() == false) {
			MainFrame.output.append("Init failed, system exit!\n");
			return;
		}
	}
}
