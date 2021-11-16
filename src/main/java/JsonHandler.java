import org.json.*;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class JsonHandler {
    private final static Logger logger = Logger.getLogger("JsonHandler");

    /**
     * Конвертирует json-строку в json-объект или json-массив и возвращет его.
     * @param str json-строка
     * @return Object или {@code null}, если не удалось конвертировать строку
     */
    public static Object convertString(String str) {
        try {
            JSONArray t = new JSONArray(str);
            if(KeyCounter.verbose) logger.log(Level.INFO, "Detected JSONArray.");
            return t;                                   // Пробуем распаковать как массив
        }
        catch (JSONException e0) {
            try {
                JSONObject t = new JSONObject(str);
                if(KeyCounter.verbose) logger.log(Level.INFO, "Detected JSONObject.");
                return new JSONObject(str);             // Пробуем распаковать как объект
            }
            catch (JSONException e1) {
                if(KeyCounter.verbose) {
                    logger.log(Level.WARNING, e1.getMessage());
                    logger.log(Level.INFO,  "Cannot convert string to json. str = \"{0}\"", str);
                }
                return null;                            // Не удалось распокавать
            }
        }
    }

    /**
     * Обработчик json-объекта. Все ключи передаются {@link KeyCounter}, который добавляет их в карту.
     * Если задан специальный ключ, то обработка идет относительно него.
     * @param obj json-объект
     */
    public static void handleJson(JSONObject obj) {
        Iterator<String> iter = obj.keys();

        while(iter.hasNext()) {
            String k = iter.next();

            if (KeyCounter.specialKey == null || k.equals(KeyCounter.specialKey))   // Если специального ключа нет или ключ равен специальному ключу, то добавить ключ в карту
                KeyCounter.handleKey(k, obj.get(k));

            if(obj.get(k) instanceof JSONObject) {                                  // Рекурсовно проверяем вложенные json-объекты
                if(KeyCounter.verbose) logger.log(Level.INFO, "Detected inner JSONObject");
                handleJson((JSONObject) obj.get(k));
            }

            if(obj.get(k) instanceof JSONArray) {
                if(KeyCounter.verbose) logger.log(Level.INFO, "Detected inner JSONArray.");
                handleJson((JSONArray) obj.get(k));
            }
        }
    }

    /**
     * Обработчик json-массива. Если элемент массива является json-объектом или json-массивом, то он передается
     * соответсвующему обработчику.
     * @param arr json-массив
     */
    public static void handleJson(JSONArray arr) {
        for(Object now : arr) {                                                     // Рекурсивно прокручиваем каждый элемант json-массива
            if(now instanceof JSONObject)
                handleJson((JSONObject) now);

            if(now instanceof JSONArray)
                handleJson((JSONArray) now);
        }
    }

}
