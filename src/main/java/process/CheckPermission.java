package process;

import org.json.JSONException;
import preprocess.Constant;

/**
 * 检查权限
 *
 * @author WSL
 */
public class CheckPermission {

    /**
     * 改变数据库权限
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
     * 创建数据库权限
     *
     * @return
     */
    public static boolean checkCreateDatabasePermission() {
        if (Constant.username.equals("root"))
            return true;
        return false;
    }

    /**
     * 创建数据表权限
     *
     * @return
     */
    public static boolean checkCreateTablePermission() {
        if (Constant.username.equals("root"))
            return true;
        return false;
    }

    /**
     * 创建视图权限
     *
     * @return
     */
    public static boolean checkCreateViewPermission() {
        if (Constant.username.equals("root"))
            return true;
        return false;
    }

    /**
     * 添加用户权限
     *
     * @return
     */
    public static boolean checkCreateUserPermission() {
        if (Constant.username.equals("root"))
            return true;
        return false;
    }

    /**
     * 删除数据库权限
     *
     * @return
     */
    public static boolean checkDropDatabasePermission() {
        if (Constant.username.equals("root"))
            return true;
        return false;
    }

    /**
     * 删除用户权限
     *
     * @return
     */
    public static boolean checkDropUserPermission() {
        if (Constant.username.equals("root"))
            return true;
        return false;
    }

    /**
     * 删除表权限
     *
     * @return
     */
    public static boolean checkDropTablePermission() {
        if (Constant.username.equals("root"))
            return true;
        return false;
    }

    /**
     * 删除视图权限
     *
     * @return
     */
    public static boolean checkDropViewPermission() {
        if (Constant.username.equals("root"))
            return true;
        return false;
    }

    /**
     * 给用户加权限权限
     *
     * @return
     */
    public static boolean checkGrantAndRevokePermission() {
        if (Constant.username.equals("root"))
            return true;
        return false;
    }

    /**
     * 插入、删除、修改、选择权限
     *
     * @param table
     * @return
     */
    public static boolean checkPermission(String table, boolean isview, String type) {
        if (Constant.username.equals("root"))
            return true;
        try {
            if (isview)
                return false;
            if (Constant.currentuser.has(Constant.databasename)) {
                if (Constant.currentuser.getJSONObject(Constant.databasename).has(table)) {
                    if (Constant.currentuser.getJSONObject(Constant.databasename).getJSONObject(table).has(type)) {
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
