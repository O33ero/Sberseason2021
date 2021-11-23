import org.json.JSONArray;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonStringHandler {

    private static final String jsObjPattern = "\"([\\w]+)\"[ ]*:[ ]*(([\\d\\.\\-]+)|(\".*?\")|(true|false|null)|(\\[.*?\\])|(\\{.*?\\}))";


    public static void handleString(String str) {
        Pattern pat = Pattern.compile(jsObjPattern);
        Matcher mat = pat.matcher(str);

        while(mat.find()) {
            String key = mat.group(1);
            String value = mat.group(2);

            String tempSTR = isString(value); // Строка
            if(tempSTR != null) {
                KeyCounter.handleKey(key, tempSTR);
                continue;
            }

            Boolean tempBLN = isBoolean(value); // Логическая
            if(tempBLN != null) {
                KeyCounter.handleKey(key, tempBLN);
                continue;
            }

            Integer tempINT = isInteger(value); // Целое
            if(tempINT != null) {
                KeyCounter.handleKey(key, tempINT);
                continue;
            }

            Float tempFLT = isFloat(value); // Дробное
            if(tempFLT != null) {
                KeyCounter.handleKey(key, tempFLT);
                continue;
            }

            boolean tempNLL = isNull(value); // Null
            if(tempNLL == true ) {
                KeyCounter.handleKey(key, null);
                continue;
            }

            JSONArray tempARR = isArray(value); // json-массив
            if(tempARR != null) {
                KeyCounter.handleKey(key, tempARR);
                JsonHandler.handleJson(tempARR);
                continue;
            }

            JSONObject tempOBJ = isObject(value); // json-объект
            if(tempOBJ != null) {
                KeyCounter.handleKey(key, tempOBJ);
                JsonHandler.handleJson(tempOBJ);
            }
        }
    }


    private static String isString(String str) {
        StringBuilder buffer = new StringBuilder(str);
        if(buffer.charAt(0) == '\"' && buffer.charAt(buffer.length() - 1) == '\"') {
            buffer.deleteCharAt(buffer.length() - 1).deleteCharAt(0);
            return buffer.toString();
        }
        else
            return null;
    }

    private static Boolean isBoolean(String str) {
        if(str.equals("true")) return true;
        else if(str.equals("false")) return false;
        else return null;
    }

    private static Integer isInteger(String str) {
        try{
            return  Integer.parseInt(str);
        }
        catch (Exception e) {
            return null;
        }
    }

    private static Float isFloat(String str) {
        try{
            return Float.parseFloat(str);
        }
        catch (Exception e) {
            return null;
        }
    }

    private static boolean isNull(String str) {
        return str.equals("null");
    }

    private static JSONArray isArray(String str) {
        try {
            return new JSONArray(str);
        }
        catch (Exception e) {
            return null;
        }
    }

    private static JSONObject isObject(String str) {
        try {
            return new JSONObject(str);
        }
        catch (Exception e) {
            return null;
        }
    }
}
