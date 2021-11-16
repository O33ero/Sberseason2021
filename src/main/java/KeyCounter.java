import java.util.*;
import java.util.logging.Logger;

public final class KeyCounter {
    public final static Map<String, HashSet<Object>> keyMap = new HashMap<>();

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
        if(keyMap.containsKey(key)) {
            HashSet<Object> t = keyMap.get(key);
            t.add(value);
            keyMap.replace(key, t);
        }
        else {
            HashSet<Object> t = new HashSet<>();
            t.add(value);
            keyMap.put(key, t);
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
