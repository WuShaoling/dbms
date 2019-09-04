package process;

import org.json.JSONException;
import preprocess.Constant;
import preprocess.Error;
import preprocess.Util;

import java.util.Iterator;

/**
 * help命令
 *
 * @author WSL
 */
@SuppressWarnings("all")
public class Show {
    private static String[] arr = null;
    private static String sql;

    public static void Check(String[] arrs) {
        arr = arrs;
        sql = Util.arrayToString(arrs);
        if (arr.length >= 2) {
            switch (arr[1]) {
                case "databases":
                    showDatabase();
                    break;
                case "tables":
                    showTable();
                    break;
                case "views":
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

    /**
     * 输出视图
     */
    private static void showView() {
        // 检查语法
        if (arr.length != 2) {
            Util.showInTextArea(sql, Error.COMMAND_ERROR);
            return;
        }
        // 是否选中数据库
        if (Constant.currentdatabase == null) {
            Util.showInTextArea(sql, Error.NO_DATABASE_SELECTED);
            return;
        }
        // 执行
        try {
            Iterator<String> iterator;
            if (Constant.username.equals("root")) {
                iterator = Constant.currentdatabase.getJSONObject("view").keys();
                int len = 0;
                String tbs = "";
                while (iterator.hasNext()) {
                    tbs += (String) iterator.next() + "\n";
                    len++;
                }
                Util.showInTextArea(sql, tbs + "total : " + len);
            } else {
                Util.showInTextArea(sql, Error.ACCESS_DENIED);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 输出所有数据库
     */
    public static void showDatabase() {
        // 语法错误
        if (arr.length != 2) {
            Util.showInTextArea(sql, Error.COMMAND_ERROR);
            return;
        }
        // 执行
        try {
            Iterator<String> iterator;
            if (!Constant.username.equals("root")) {
                iterator = Constant.USERS.getJSONObject(Constant.username).keys();
            } else {
                iterator = Constant.DICTIONARY.keys();
            }
            int len = 0;
            String dbs = "";
            while (iterator.hasNext()) {
                dbs += (String) iterator.next() + "\n";
                len++;
            }
            Util.showInTextArea(sql, dbs + "total : " + len);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 输出所有数据表
     */
    public static void showTable() {
        // 检查语法
        if (arr.length != 2) {
            Util.showInTextArea(sql, Error.COMMAND_ERROR);
            return;
        }
        // 是否选中数据库
        if (Constant.currentdatabase == null) {
            Util.showInTextArea(sql, Error.NO_DATABASE_SELECTED);
            return;
        }
        // 执行
        try {
            Iterator<String> iterator;
            if (!Constant.username.equals("root"))
                iterator = Constant.USERS.getJSONObject(Constant.username).getJSONObject(Constant.databasename).keys();
            else
                iterator = Constant.currentdatabase.getJSONObject("table").keys();
            int len = 0;
            String tbs = "";
            while (iterator.hasNext()) {
                tbs += (String) iterator.next() + "\n";
                len++;
            }
            Util.showInTextArea(sql, tbs + "total : " + len);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
