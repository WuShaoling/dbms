package process;

import org.json.JSONException;
import preprocess.Constant;
import preprocess.Error;
import preprocess.PreProcess;
import preprocess.Util;

/**
 * 处理drop
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
     * 删除视图
     */
    private static void dropView() {
        // 检查语法
        if (arr.length != 3) {
            Util.showInTextArea(sql, Error.COMMAND_ERROR);
            return;
        }
        // 检查是否选中数据库
        if (Constant.currentdatabase == null) {
            Util.showInTextArea(sql, Error.NO_DATABASE_SELECTED);
            return;
        }
        // 检查视图是否存在
        try {
            if (!Constant.currentdatabase.getJSONObject("view").has(arr[2])) {
                Util.showInTextArea(sql, Error.VIEW_NOT_EXIST + " : " + arr[2]);
                return;
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        // 检查权限
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
     * 删除数据库
     */
    public static void dropDatabase() {
        try {
            // 检查语法
            if (arr.length != 3) {
                Util.showInTextArea(sql, Error.COMMAND_ERROR);
                return;
            }
            // 检查数据库是否存在
            if (!Constant.DICTIONARY.has(arr[2])) {
                Util.showInTextArea(sql, Error.DATABASE_NOT_EXIST + " : " + arr[2]);
                return;
            }
            // 检查权限
            if (!CheckPermission.checkDropDatabasePermission()) {
                Util.showInTextArea(sql, Error.ACCESS_DENIED);
                return;
            }
            // 执行
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
     * 删除表
     */
    public static void dropTable() {
        // 检查语法
        if (arr.length != 3) {
            Util.showInTextArea(sql, Error.COMMAND_ERROR);
            return;
        }
        // 检查是否选中数据库
        if (Constant.currentdatabase == null) {
            Util.showInTextArea(sql, Error.NO_DATABASE_SELECTED);
            return;
        }
        // 检查表是否存在
        try {
            if (!Constant.currentdatabase.getJSONObject("table").has(arr[2])) {
                Util.showInTextArea(sql, Error.TABLE_NOT_EXIST + " : " + arr[2]);
                return;
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        // 检查权限
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
     * 删除用户
     */
    public static void dropUser() {
        // 检查语法
        if (arr.length != 3) {
            Util.showInTextArea(sql, Error.COMMAND_ERROR);
            return;
        }
        // 检查是否存在
        if (!Constant.USERS.has(arr[2])) {
            Util.showInTextArea(sql, Error.USER_EXIST + " : " + arr[2]);
            return;
        }
        // 检查权限
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
