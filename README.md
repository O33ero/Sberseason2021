## Обработчик json-строк

GitHub: https://github.com/O33ero/Sberseason2021

**TODO:**
- **Обязательно**:
  - [x] Apache Flink
  - [x] Вывод количества уникальных значений по ключам
  - [x] Можно задать специальный ключ, как параметры командной строки
  - [x] Вывод всех ключей, если не задан специальной
  - [x] (не)Обработка обрезанных или неправильных json-строк
  - [x] Структура неизвестна  

- **Дополнительно**:
  - [x] Журналирование   
    - Добавлены Logger-ы
  - [x] Обработка ошибок 
    - Неправильные json-ы не обрабатываются
  - [x] Обработка аргументов командной строки 
    - Добавлены некоторые параметры для тестирования
  - [ ] Перезапуск приложения в случае ошибки
  - [x] Работа со вложенными json 
    - Реккурентно обрабатываются внутренние объекты
  - [ ] Работа с окнами во Flink

**Используется:**
1. Java 17
2. Maven
3. Flink Core 1.14.0
4. Flink Java 1.14.0
5. Flink Streaming Java 1.14.0
6. Flink Test utils 1.14.0
7. Flink Clients 1.14.0
8. JSON in java 20210307

**Обязательные параметры:**

Для правильного запуска необходимо добавить флаги для VM:
- --add-opens java.base/java.lang=ALL-UNNAMED
- --add-opens java.base/java.net=ALL-UNNAMED
- --add-opens java.base/java.io=ALL-UNNAMED
  

**Параметры запуска:**

Добавлено 3 аргумента:
- -h, --help - вывод помощи
- -v, --verbose - подробный вывод всего происходящего (Logger)
- -k, --key <STRING> - специальный ключ, по которому нужно провести поиск

**Известные проблемы:**
1. Некорректно определяются разные пары ключ-значения с вложенными объектами
> Пример.
> 
>  | Объект 1 | Объект 2 |       |
>  |------------|------------|---------|
>  | "A" : 1  | "A" : 2  | Разные значения, засчитаются 2 раза |
>  | "A" : 1  | "A" : 1  | Одинаковые значения, засчитаются 1 раз |
>  | `"A" : [1,2,3]`| `"A" : [3,2,1]`| Разные значения, засчитаются 2 раза|
>  | `"A" : [1,2,3]`| `"A" : [1,2,3]`| Одинаковые значения, но засчитаются 2 раза, поскольку разный hashCode|
2. Поскольку используется рекурсивная функция, то программа также ограничена глубиной стека.
3. Используется setParallelism(1), поскольку при многопоточности возможен неправильный подсчет (Есть подозрение, что это можно исправить).
