import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class KeyCounter {
    public final static Map<String, HashSet<Object>> keyMap = new LinkedHashMap<>();

    private static final Logger logger = Logger.getLogger("KeyCounter");

    public static String specialKey = null;
    public static boolean verbose = false;

    /**
     * Обработка ключа. Ключ добавляется в карту, а значение в множество значений.
     * Количество значений в множестве есть количество уникальных значения для ключа.
     * @param key ключ
     * @param value значение ключа
     */
    public static void handleKey(String key, Object value) {

        HashSet<Object> valueSet = keyMap.get(key);

        if(valueSet != null) {
            if(value instanceof JSONArray) { // Если значение JSONArray
                JSONArray jsArray = (JSONArray) value;
                for(Object now : valueSet) {
                    if(now instanceof JSONArray) {
                        if (jsArray.similar(now)) return; // Если есть такая пара ключ-значение уже есть, то ничего не делаем
                    }
                }
            }
            else if (value instanceof JSONObject) { // Если значение JSONObject
                JSONObject jsObject = (JSONObject) value;
                for(Object now : keyMap.get(key)) {
                    if(now instanceof JSONObject) {
                        if(jsObject.similar(now)) return; // Если уже есть такая пара ключ-значение, то ничего не делаем
                    }
                }
            }
        }


        if(keyMap.containsKey(key) && valueSet != null) {
            valueSet.add(value);
            keyMap.replace(key, valueSet);
        }
        else {
            valueSet = new HashSet<Object>();
            valueSet.add(value);
            keyMap.put(key, valueSet);
            if(verbose) logger.log(Level.INFO, "Added new key: key = {0}", key);
        }

    }


    /**
     * Вывод результатов посчета.
     * Если задан ключ, то выведет резултат только для него (даже если множество значений ключа пустое).
     * В ином случа выведет все возможные ключи, или не выведет ничего если по каким то причинам нету ни одного ключа.
     */
    public static void printResult() {
        if(KeyCounter.specialKey != null) {
             System.out.println("key: " + specialKey + " = " + keyMap.getOrDefault(specialKey, new HashSet<>()).size());
        }
        else {
            for(String key : keyMap.keySet()) {
                System.out.println("key: " + key + " = " + keyMap.get(key).size());
            }
        }
    }
}
