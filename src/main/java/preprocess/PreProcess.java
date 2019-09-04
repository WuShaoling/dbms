package preprocess;

import org.json.JSONObject;
import process.*;

/**
 * 预处理
 *
 * @author WSL
 */
public class PreProcess {

    public static boolean islogin = false;
    private static String sql;
    private static String[] arr;

    /**
     * 输入的语句预处理
     *
     * @param tsql
     */
    public static void preprocessSql(String tsql) {
        try {
            sql = tsql.replaceAll("[\\n\\r\\t]+", " ").trim().toLowerCase().replaceAll(" +", " ");
            arr = sql.split(" ");
            if (arr[0].equals("login")) {
                if (islogin) {
                    Util.showInTextArea(sql, "Please logout first!");
                    return;
                }
                dealLogin();
            } else {
                if (islogin) {
                    switch (arr[0]) {
                        case "exit":
                            Exit(arr);
                            break;
                        case "create":
                            Create.Check(arr);
                            break;
                        case "drop":
                            Drop.Check(arr);
                            break;
                        case "select":
                            Select.Check(arr);
                            break;
                        case "show":
                            Show.Check(arr);
                            break;
                        case "insert":
                            Insert.Check(arr);
                            break;
                        case "update":
                            Update.Check(arr);
                            break;
                        case "delete":
                            Delete.Check(arr);
                            break;
                        case "use":
                            Use.Check(arr);
                            break;
                        case "grant":
                            GrantAndRevoke.Check(arr);
                            break;
                        case "revoke":
                            GrantAndRevoke.Check(arr);
                            break;
                        case "help":
                            Help.Check(arr);
                            break;
                        default:
                            Util.showInTextArea(sql, Error.COMMAND_ERROR);
                            break;
                    }
                } else {
                    Util.showInTextArea(sql, "Please login first!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 登录
     */
    private static void dealLogin() {
        if (arr.length != 3) {
            Util.showInTextArea(sql, Error.COMMAND_ERROR);
            return;
        }
        try {
            if (!Constant.USERS.has(arr[1])) {
                Util.showInTextArea(sql, Error.USER_NOT_EXIST);
                return;
            }
            JSONObject user = Constant.USERS.getJSONObject(arr[1]);
            if (!user.get("password").equals(arr[2])) {
                Util.showInTextArea(sql, Error.PASSWORD_NOT_MATCH);
                return;
            }
            Constant.currentuser = Constant.USERS.getJSONObject(arr[1]);
            Constant.username = arr[1];
            islogin = true;
            Util.showInTextArea(sql, "Login success!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 退出当前用户
     *
     * @param arr
     */
    public static void Exit(String[] arr) {
        if (arr.length != 1) {
            Util.showInTextArea(sql, Error.PASSWORD_NOT_MATCH);
            return;
        }
        Util.showInTextArea(sql, "Bye-Bye!");
        islogin = false;
        Constant.currentuser = null;
        Constant.username = null;
        Constant.currentdatabase = null;
        Constant.databasename = null;
    }
}
