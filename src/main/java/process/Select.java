package process;

import org.json.JSONException;
import preprocess.Constant;
import preprocess.Error;
import preprocess.Util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 处理select
 *
 * @author WSL
 */
public class Select {
    public static List<String[]> alldata = null;
    public static String sql = null;
    public static String natures = null;
    public static String table = null;
    public static String view = null;
    public static String condition = null;
    public static String order = "asc";
    public static String order_nature = null;
    public static String func = null;// 聚集函数
    public static Pattern pattern = null;
    public static Matcher matcher = null;
    public static boolean isview = false;

    public static void Check(String[] arrs) {
        natures = null;
        table = null;
        condition = null;
        alldata = null;
        func = null;
        isview = false;
        view = null;
        order = "asc";
        order_nature = null;
        sql = Util.arrayToString(arrs);
        // 初步检查语法
        if (!checkSelectGrammer(sql)) {
            Util.showInTextArea(sql, Error.COMMAND_ERROR);
            return;
        }
        // 检查是否选中数据库
        if (Constant.databasename == null) {
            Util.showInTextArea(sql, Error.NO_DATABASE_SELECTED);
            return;
        }
        // 检查表是否存在
        try {
            if (!Constant.currentdatabase.getJSONObject("table").has(table)) {
                if (!Constant.currentdatabase.getJSONObject("view").has(table)) {
                    Util.showInTextArea(sql, Error.TABLE_NOT_EXIST + " : " + table);
                    return;
                } else {
                    isview = true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // 检查权限
        if (!CheckPermission.checkPermission(table, isview, "select")) {
            Util.showInTextArea(sql, Error.ACCESS_DENIED);
            return;
        }
        if (isview) {
            try {
                view = table;
                table = Constant.currentdatabase.getJSONObject("view").getJSONObject(table).getString("table");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (condition == null) {
            alldata = Util.readDataFromTable(table);
        } else {
            alldata = GetResultWithCheck.getAllResult(sql, table, view, condition, false, isview);
        }
        // 获得最终的数据
        if (alldata == null) {
            return;
        }
        // 匹配是否有聚集函数,提取出natures
        matchNaturesGrammer();
        //排序
        if (order_nature != null) {
            if (!sort()) {
                return;
            }
        }
        // 获得投影数据
        if (!natures.equals("*")) {
            if (!getResultWithNatures(natures)) {
                return;
            }
        } else {//是* ，判断是不是视图
            if (isview) {
                String[] arr = Util.getNaturesArray(view, isview);
                String content = arr[0];
                for (int i = 1; i < arr.length; i++) {
                    content += "," + arr[i];
                }
                if (!getResultWithNatures(content)) {
                    return;
                }
            }
        }
        if (func != null) {// 处理聚集函数
            dealFunc();
            return;
        }
        String tt = table;
        if (isview)
            tt = view;
        String head = "";
        if (natures.equals("*"))
            head = Util.getNaturesString(tt, isview);
        else {
            String[] temp = natures.split(",");
            head = temp[0];
            for (int i = 1; i < temp.length; i++) {
                head += "\t\t" + temp[i].trim();
            }
        }
        String result = Util.parseListToOutput(alldata);
        Util.showInTextArea(sql, head + "\n" + result + "total : " + alldata.size());
    }

    /**
     * 比较字符串
     *
     * @param s1
     * @param s2
     * @return
     */
    public static int compareString(String s1, String s2) {
        s1 = s1.substring(1, s1.length() - 1);
        s2 = s2.substring(1, s2.length() - 1);
        int i = 0;
        for (i = 0; i < s1.length() && i < s2.length(); i++) {
            if (s1.charAt(i) != s2.charAt(i))
                return s1.charAt(i) - s2.charAt(i);
        }
        return s1.length() - s2.length();
    }

    /**
     * 对数据冒泡排序
     */
    private static boolean sort() {
        String ttt = table;
        if (isview)
            ttt = view;
        int pos = 0;
        if ((pos = Util.getNaturePosition(ttt, order_nature, isview)) != -1) {
            String type = Util.getNatureType(ttt, order_nature, isview);
            String temp[] = null;
            String[][] arr = Util.parseListToArray(alldata);
            for (int i = arr.length - 1; i > 0; --i) {
                for (int j = 0; j < i; ++j) {
                    if (order.equals("asc")) {
                        if (type.equals("int") || type.equals("double")) {
                            if (Double.parseDouble(arr[j + 1][pos]) < Double.parseDouble(arr[j][pos])) {
                                temp = arr[j];
                                arr[j] = arr[j + 1];
                                arr[j + 1] = temp;
                            }
                        } else {
                            if (compareString(arr[j + 1][pos], arr[j][pos]) < 0) {
                                temp = arr[j];
                                arr[j] = arr[j + 1];
                                arr[j + 1] = temp;
                            }
                        }
                    } else {
                        if (type.equals("int") || type.equals("double")) {
                            if (Double.parseDouble(arr[j + 1][pos]) > Double.parseDouble(arr[j][pos])) {
                                temp = arr[j];
                                arr[j] = arr[j + 1];
                                arr[j + 1] = temp;
                            }
                        } else {
                            if (compareString(arr[j + 1][pos], arr[j][pos]) > 0) {
                                temp = arr[j];
                                arr[j] = arr[j + 1];
                                arr[j + 1] = temp;
                            }
                        }
                    }
                }
            }
            alldata = Util.parseArrayToLiat(arr);
            return true;
        } else {
            Util.showInTextArea(sql, Error.ATTR_NOT_EXIST + " : " + order_nature);
        }
        return false;
    }

    public static void dealFunc() {
        String tt = table;
        if (isview)
            tt = view;
        switch (func) {
            case "count":
                Util.showInTextArea(sql, "count(" + natures + ")\n" + alldata.size());
                break;
            case "max":
                if (Util.getNatureType(tt, natures, isview).equals("int")
                        || Util.getNatureType(tt, natures, isview).equals("double")) {
                    if (alldata.size() > 0) {
                        double max = 0;
                        max = Double.parseDouble(alldata.get(0)[0]);
                        for (int i = 1; i < alldata.size(); i++) {
                            if (max <= Double.parseDouble(alldata.get(i)[0])) {
                                max = Double.parseDouble(alldata.get(i)[0]);
                            }
                        }
                        Util.showInTextArea(sql, "max(" + natures + ")\n" + String.valueOf(max));
                    } else {
                        Util.showInTextArea(sql, "max(" + natures + ")\n" + "null");
                    }
                } else {
                    Util.showInTextArea(sql, Error.DATATYPE_NOT_MATCH + " : " + natures);
                }
                break;
            case "min":
                if (Util.getNatureType(tt, natures, isview).equals("int")
                        || Util.getNatureType(tt, natures, isview).equals("double")) {
                    if (alldata.size() > 0) {
                        double min = 0;
                        min = Double.parseDouble(alldata.get(0)[0]);
                        for (int i = 1; i < alldata.size(); i++) {
                            if (min >= Double.parseDouble(alldata.get(i)[0])) {
                                min = Double.parseDouble(alldata.get(i)[0]);
                            }
                        }
                        Util.showInTextArea(sql, "min(" + natures + ")\n" + String.valueOf(min));
                    } else {
                        Util.showInTextArea(sql, "min(" + natures + ")\n" + "null");
                    }
                } else {
                    Util.showInTextArea(sql, Error.DATATYPE_NOT_MATCH + " : " + natures);
                }
                break;
            case "avg":
                if (Util.getNatureType(tt, natures, isview).equals("int")
                        || Util.getNatureType(tt, natures, isview).equals("double")) {
                    if (alldata.size() > 0) {
                        double sum = 0;
                        for (int i = 0; i < alldata.size(); i++) {
                            sum += Double.parseDouble(alldata.get(i)[0]);
                        }
                        Util.showInTextArea(sql, "avg(" + natures + ")\n" + String.valueOf(sum / alldata.size()));
                    } else {
                        Util.showInTextArea(sql, "avg(" + natures + ")\n" + "null");
                    }
                } else {
                    Util.showInTextArea(sql, Error.DATATYPE_NOT_MATCH + " : " + natures);
                }
                break;
            case "sum":
                if (Util.getNatureType(tt, natures, isview).equals("int")
                        || Util.getNatureType(tt, natures, isview).equals("double")) {
                    if (alldata.size() > 0) {
                        double sum = 0;
                        for (int i = 0; i < alldata.size(); i++) {
                            sum += Double.parseDouble(alldata.get(i)[0]);
                        }
                        Util.showInTextArea(sql, "sum(" + natures + ")\n" + String.valueOf(sum));
                    } else {
                        Util.showInTextArea(sql, "sum(" + natures + ")\n" + "null");
                    }
                } else {
                    Util.showInTextArea(sql, Error.DATATYPE_NOT_MATCH + " : " + natures);
                }
                break;
        }
    }

    /**
     * 获得结果
     *
     * @return
     */
    public static boolean getResultWithNatures(String anatures) {
        // 获得所有的属性
        String[] natures_array = anatures.split(",");
        for (int i = 0; i < natures_array.length; i++)
            natures_array[i] = natures_array[i].trim();
        List<String> natures_list;
        if (isview)
            natures_list = Util.getNaturesList(view, isview);
        else
            natures_list = Util.getNaturesList(table, isview);
        // 检查所有的属性是否存在
        String result = Util.checkAllNatureExsit(natures_list, natures_array);
        if (result != null) {
            Util.showInTextArea(sql, Error.ATTR_NOT_EXIST + " : " + result);
            return false;
        }
        // 获得所有属性的位置
        int[] pos = new int[natures_array.length];
        for (int i = 0; i < natures_array.length; i++)
            pos[i] = Util.getNaturePosition(table, natures_array[i], false);
        for (int i = 0; i < alldata.size(); i++) {
            String[] temp = alldata.get(i);
            String[] newv = new String[pos.length];
            alldata.remove(i);
            for (int j = 0; j < pos.length; j++) {
                newv[j] = temp[pos[j]];
            }
            alldata.add(i, newv);
        }
        return true;
    }

    /**
     * 检查语法，获得数据表名
     */
    public static boolean checkSelectGrammer(String ssql) {
        String match = "select (.+) from ([a-z0-9_]+) where (.+) order by (.+)( desc|asc)";
        pattern = Pattern.compile(match);
        matcher = pattern.matcher(ssql);
        if (matcher.matches()) {
            natures = matcher.group(1).trim();
            table = matcher.group(2).trim();
            condition = matcher.group(3).trim();
            order_nature = matcher.group(4).trim();
            if (matcher.group(5) != null)
                order = matcher.group(5).trim();
            return true;
        }
        match = "select (.+) from ([a-z0-9_]+) order by (.+)( desc|asc)";
        pattern = Pattern.compile(match);
        matcher = pattern.matcher(ssql);
        if (matcher.matches()) {
            natures = matcher.group(1).trim();
            table = matcher.group(2).trim();
            order_nature = matcher.group(3).trim();
            if (matcher.group(4) != null)
                order = matcher.group(4).trim();
            return true;
        }
        match = "select (.+) from ([a-z0-9_]+) where (.+)";
        pattern = Pattern.compile(match);
        matcher = pattern.matcher(ssql);
        if (matcher.matches()) {
            natures = matcher.group(1).trim();
            table = matcher.group(2).trim();
            condition = matcher.group(3).trim();
            return true;
        }
        match = "select (.+) from ([a-z0-9_]+)";
        pattern = Pattern.compile(match);
        matcher = pattern.matcher(ssql);
        if (matcher.matches()) {
            natures = matcher.group(1).trim();
            table = matcher.group(2).trim();
            return true;
        }
        return false;
    }

    /**
     * 匹配属性的语法
     *
     * @return
     */
    public static void matchNaturesGrammer() {
        String match = "(count|max|avg|sum|min) ?\\( ?(.+) ?\\)";
        pattern = Pattern.compile(match);
        matcher = pattern.matcher(natures);
        if (matcher.matches()) {
            func = matcher.group(1).trim();
            natures = matcher.group(2).trim();
        }
    }
}
