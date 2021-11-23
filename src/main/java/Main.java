import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 *  Главное:
 *  1. Корректная обработка вложенных объектов ✔️
 *  2. Исправление обработки несуществующих параметров ✔️
 *  3. Парсинг обрезаных json-строк ✔️
 *  4. Поймать NullPointerException в обработчике параметров ✔
 *  5. Исправить поиск по пустому ключу ✔️
 *
 */

public class Main {

    private final static Set<Object> declaredArgs = new HashSet<>(Arrays.asList("k", "key", "v", "verbose", "h", "help"));
    private final static String helpBox = """
                            Optional:
                                -h, --help             - help box
                                -v, --verbose          - verbose debug info
                                -k, --key <STRING>     - set special key
                            """;

    /**
     * Обработчик параметров. Возвращает {@code false}, если рекомендуется прикратить работу программы.
     * @param args Аргументы
     * @return Результат работы ({@code true} или {@code false})
     */
    private static boolean parseParameters(String[] args) {
        ParameterTool parameters = ParameterTool.fromArgs(args);
        Set<Object> arg = parameters.getProperties().keySet();

        // Неизвестный аргумент
        boolean flag = false;
        for(Object now : arg)
            if(!declaredArgs.contains(now)) { // Если обнаружен неизвестный аргумент
                System.out.println("ERROR: Unexpected argument -" + (String)now);
                flag = true;
            }
        if (flag == true) {
            System.out.println(helpBox);
            return false;
        }


        // Вывод помощи
        if(parameters.has("h") || parameters.has("help")) {
            System.out.println(helpBox);
            return false;
        }

        // Подпробный вывод
        if(parameters.has("v") || parameters.has("verbose")) {
            KeyCounter.verbose = true;
            JsonHandler.verbose = true;
        }

        // Установка специального ключа
        if(parameters.has("k") || parameters.has("key")) {
            KeyCounter.specialKey = parameters.get("k");


            if (KeyCounter.specialKey == null || KeyCounter.specialKey.equals("")) {
                System.out.println("ERROR: Key length should be > 0");
                return false;
            }
            if (KeyCounter.specialKey.equals("__NO_VALUE_KEY"))
                KeyCounter.specialKey = parameters.get("key");

            if (KeyCounter.specialKey == null || KeyCounter.specialKey.equals("")) {
                System.out.println("ERROR: Key length should be > 0");
                return false;
            }
            if (KeyCounter.specialKey.equals("__NO_VALUE_KEY")) {
                System.out.println("ERROR: Expected -k <STRING> or --key <STRING>");
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) throws  Exception{

        /* Обработка параметров */
        if (!parseParameters(args)) return;

        /* Создание и обработка потоков */
        final List<String> lines = Files.readAllLines(Paths.get("./tests/test7.txt")); // Тестовый источник

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStream<String> inStream = env.fromCollection(lines);                                    // Поток json-строк
                                                                                                    // TODO: Главный источник, может быть заменен любым другим
        DataStream<Object> jsonStream = inStream.flatMap(new FlatMapFunction<>() {    // Поток json-объектов
            @Override
            public void flatMap(String s, Collector<Object> out) {
                Object t = JsonHandler.convertString(s);
                if (t != null) out.collect(t);
            }
        });

        jsonStream.flatMap(new FlatMapFunction<>() {                                 // Обработка всех json-объектов
            @Override
            public void flatMap(Object obj, Collector<Object> collector)  {
                if(obj instanceof JSONObject)
                    JsonHandler.handleJson((JSONObject) obj);
                else
                    JsonHandler.handleJson((JSONArray) obj);
            }
        });

        env.execute("job01");

        /* Результат обработки */
        KeyCounter.printResult();

    }
}
