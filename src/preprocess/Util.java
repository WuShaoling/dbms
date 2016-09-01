package preprocess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ui.MainFrame;

/**
 * 工具类
 * 
 * @author WSL
 */
public class Util {
	/**
	 * 按字节从头读文件
	 * 
	 * @param path
	 * @return
	 */
	public static String readData(String path) {
		String str = "";
		try {
			FileInputStream in = new FileInputStream(path);
			int len = 0;
			byte[] temp = new byte[1024];
			while ((len = in.read(temp)) != -1) {
				str += new String(temp, 0, len);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 按字节写文件
	 * 
	 * @param path
	 * @param data
	 * @return
	 */
	public static boolean writeData(String path, String data) {
		try {
			FileOutputStream out = new FileOutputStream(path);
			out.write(data.getBytes());
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 数组转字符串
	 * 
	 * @param arr
	 * @return
	 */
	public static String arrayToString(String[] arr) {
		String sql = "";
		for (int i = 0; i < arr.length; i++) {
			sql += arr[i] + " ";
		}
		return sql.trim();
	}

	/**
	 * 删除文件夹
	 * 
	 * @param filepath
	 * @return
	 */
	public static boolean deletePath(String filepath) {
		try {
			File file = new File(filepath);
			File[] files = file.listFiles();
			for (File t : files)
				t.delete();
			file.delete();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 删除文件
	 * 
	 * @param filepath
	 * @return
	 */
	public static boolean deleteFile(String filepath) {
		try {
			File file = new File(filepath);
			file.delete();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 创建文件
	 * 
	 * @param filepath
	 * @return
	 */
	public static boolean creatFile(String filepath) {
		try {
			File file = new File(filepath);
			file.createNewFile();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 插入一条数据到表
	 * 
	 * @param data
	 * @param table
	 */
	public static void insertDataToTable(String data, String table) {
		String path = Constant.PATH_ROOT + "/" + Constant.databasename + "/" + table + ".sql";
		File file = new File(path);
		try {
			RandomAccessFile random = new RandomAccessFile(path, "rw");
			random.seek(file.length());
			random.write(data.getBytes());
			random.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 按格式输出
	 * 
	 * @param sql
	 * @param result
	 */
	public static void showInTextArea(String sql, String result) {
		result = "[" + Constant.databasename + " @ " + Constant.username + "]>" + sql + ";\n" + result + "\n";
		MainFrame.output.append(result);
	}

	/**
	 * 从数据表读取所有的数据
	 * 
	 * @param table
	 * @return
	 */
	public static List<String[]> readDataFromTable(String table) {
		try {
			List<String[]> list = new LinkedList<String[]>();
			String path = Constant.PATH_ROOT + Constant.databasename + "/" + table + ".sql";
			BufferedReader br = new BufferedReader(new FileReader(new File(path)));
			String temp = "";
			while ((temp = br.readLine()) != null) {
				list.add(temp.split(String.valueOf(Constant.SPLIT)));
			}
			br.close();
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获得所有属性
	 * 
	 * @param table
	 * @param isview
	 * @return
	 */
	public static List<String> getNaturesList(String table, boolean isview) {
		try {
			List<String> list = new LinkedList<String>();
			JSONArray arr;
			if (isview) {
				arr = Constant.currentdatabase.getJSONObject("view").getJSONObject(table).getJSONArray("items");
			} else {
				arr = Constant.currentdatabase.getJSONObject("table").getJSONObject(table).getJSONArray("items");
			}
			for (int i = 0; i < arr.length(); i++) {
				list.add(arr.getJSONObject(i).getString("nature"));
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获得所有属性
	 * 
	 * @param table
	 * @return
	 */
	public static String getNaturesString(String table, boolean isview) {
		try {
			String str = "";
			JSONArray arr;
			if (isview) {
				arr = Constant.currentdatabase.getJSONObject("view").getJSONObject(table).getJSONArray("items");
			} else {
				arr = Constant.currentdatabase.getJSONObject("table").getJSONObject(table).getJSONArray("items");
			}
			for (int i = 0; i < arr.length(); i++) {
				str += arr.getJSONObject(i).getString("nature") + "\t\t";
			}
			return str;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获得所有属性
	 * 
	 * @param table
	 * @return
	 */
	public static String[] getNaturesArray(String table, boolean isview) {
		try {
			JSONArray arr = null;
			if (isview) {
				arr = Constant.currentdatabase.getJSONObject("view").getJSONObject(table).getJSONArray("items");
			} else {
				arr = Constant.currentdatabase.getJSONObject("table").getJSONObject(table).getJSONArray("items");
			}
			String[] result = new String[arr.length()];
			for (int i = 0; i < arr.length(); i++) {
				result[i] = arr.getJSONObject(i).getString("nature");
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获得属性的位置
	 * 
	 * @param table
	 * @param nature
	 * @return
	 */
	public static int getNaturePosition(String table, String nature, boolean isview) {
		try {
			List<String> list = getNaturesList(table, isview);
			return list.indexOf(nature);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 更新表的长度
	 * 
	 * @param table
	 * @param len
	 */
	public static void updateTableSize(String table, int len) {
		try {
			Constant.currentdatabase.getJSONObject("table").getJSONObject(table).put("size", len);
			Util.writeData(Constant.PATH_DICTIONARY, Constant.DICTIONARY.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将链表转换成数据表格式
	 * 
	 * @param list
	 * @return
	 */
	public static String parseListToTableFormat(List<String[]> list) {
		String content = "";
		Iterator<String[]> it = list.iterator();
		while (it.hasNext()) {
			String[] array = it.next();
			if (array.length > 0) {
				content += array[0];
				for (int j = 1; j < array.length; j++) {
					content += Constant.SPLIT + array[j];
				}
				content += "\r\n";
			}
		}
		return content;
	}

	/**
	 * 将链表装换成输出格式
	 */
	public static String parseListToOutput(List<String[]> list) {
		String content = "";
		Iterator<String[]> it = list.iterator();
		while (it.hasNext()) {
			String[] array = it.next();
			if (array.length > 0) {
				content += array[0];
				for (int j = 1; j < array.length; j++) {
					content += "\t\t" + array[j];
				}
				content += "\n";
			}
		}
		return content;
	}

	/**
	 * 将数组转成输出格式
	 * 
	 * @param array
	 * @return
	 */
	public static String parseArrayToOutput(String[][] array) {
		String content = "";
		for (int i = 0; i < array.length; i++) {
			if (array[i].length > 0) {
				content += array[i][0];
				for (int j = 1; j < array[i].length; j++) {
					content += "\t\t" + array[i][j];
				}
				content += "\n";
			}
		}
		return content;
	}

	/**
	 * 检测所有的属性是否存在
	 * 
	 * @param list
	 * @param array
	 * @return
	 */
	public static String checkAllNatureExsit(List<String> list, String[] array) {
		for (int i = 0; i < array.length; i++) {
			if (!list.contains(array[i])) {
				return array[i];
			}
		}
		return null;
	}

	/**
	 * 根据位置获得属性的名字
	 * 
	 * @param table
	 * @param pos
	 * @return
	 */
	public static String getNatureName(String table, int pos) {
		try {
			if (pos != -1) {
				JSONArray arr = Constant.currentdatabase.getJSONObject("table").getJSONObject(table)
						.getJSONArray("items");
				if (pos <= arr.length())
					return arr.getJSONObject(pos).getString("nature");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获得属性的数据类型
	 * 
	 * @param table
	 * @param nature
	 * @param isview
	 * @return
	 */
	public static String getNatureType(String table, String nature, boolean isview) {
		try {
			int pos = getNaturePosition(table, nature, isview);
			if (pos != -1) {
				JSONArray arr;
				if (isview)
					arr = Constant.currentdatabase.getJSONObject("view").getJSONObject(table).getJSONArray("items");
				else
					arr = Constant.currentdatabase.getJSONObject("table").getJSONObject(table).getJSONArray("items");
				return arr.getJSONObject(pos).getString("type");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 查某个属性的值在表中是否存在
	 * 
	 * @param table
	 * @param pos
	 * @param value
	 * @return
	 */
	public static boolean checkIsDataExit(String table, int pos, String value) {
		List<String[]> list = readDataFromTable(table);
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i)[pos].equals(value))
				return true;
		}
		return false;
	}

	/**
	 * 获得某个属性的约束条件
	 * 
	 * @param table
	 * @param pos
	 * @return
	 */
	public static String getLimit(String table, int pos) {
		try {
			if (pos != -1) {
				JSONArray arr = Constant.currentdatabase.getJSONObject("table").getJSONObject(table)
						.getJSONArray("items");
				if (arr.getJSONObject(pos).has("limit"))
					return arr.getJSONObject(pos).getString("limit");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 检查约束
	 * 
	 * @param sql
	 * @param table
	 * @param natures
	 * @param value
	 * @param isview
	 * @return
	 */
	public static boolean checkLimit(String sql, String table, String[] natures, String[] value, boolean isview) {
		try {
			JSONArray items = Constant.currentdatabase.getJSONObject("table").getJSONObject(table)
					.getJSONArray("items");
			Vector<Integer> poss = new Vector<Integer>();
			Vector<String> limit = new Vector<String>();
			for (int i = 0; i < items.length(); i++) {
				JSONObject temp = items.getJSONObject(i);
				for (int j = 0; j < natures.length; j++) {
					if (temp.getString("nature").equals(natures[j])) {
						if (temp.has("limit")) {
							String limi = temp.getString("limit");
							if (limi.equals("unique") || limi.equals("primary key")) {
								poss.add(i);
								limit.add(limi);
							}
						}
					}
				}
			}
			if (poss.size() != 0) {
				List<String[]> list = readDataFromTable(table);
				Iterator<String[]> it = list.iterator();
				while (it.hasNext()) {
					String[] temp = it.next();
					for (int t = 0; t < poss.size(); t++) {
						if (temp[poss.get(t)].equals(value[t])) {
							Util.showInTextArea(sql,
									Error.DATA_EXIST + " : " + items.getJSONObject(poss.get(t)).getString("nature")
											+ " is " + limit.get(t) + " and " + value[t] + " exsit in table " + table);
							return false;
						}
					}
				}
			}
			return true;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 链表转数组
	 * @param list
	 * @return
	 */
	public static String[][] parseListToArray(List<String[]> list) {
		Iterator<String[]> it = list.iterator();
		int i = 0;
		String[][] arr = new String[list.size()][];
		while (it.hasNext()) {
			arr[i++] = it.next();
		}
		return arr;
	}

	/**
	 * 数组转链表
	 * @param array
	 * @return
	 */
	public static List<String[]> parseArrayToLiat(String[][] array) {
		List<String[]> list = new LinkedList<String[]>();
		for (int i = 0; i < array.length; i++) {
			list.add(array[i]);
		}
		return list;
	}
}
