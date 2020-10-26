package br.gov.am.sefaz.util.sqlutil.parsers;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ValueParser {

    /**
     * Parser implementation map
     */
    private static final Map<Class, Function> defaultParsers = new HashMap<>(
            Stream.of(
                    new AbstractMap.SimpleEntry<Class,Function>(java.math.BigDecimal.class, new BigDecimalParser()),
                    new AbstractMap.SimpleEntry<Class,Function>(java.lang.Double.class, new DoubleParser()),
                    new AbstractMap.SimpleEntry<Class,Function>(Long.class, new LongParser()),
                    new AbstractMap.SimpleEntry<Class,Function>(Integer.class, new IntegerParser()),
                    new AbstractMap.SimpleEntry<Class,Function>(String.class, new StringParser()),
                    new AbstractMap.SimpleEntry<Class,Function>(java.util.Date.class, new DateParser()),
                    new AbstractMap.SimpleEntry<Class,Function>(java.sql.Date.class, new SQLDateParser()),
                    new AbstractMap.SimpleEntry<Class,Function>(java.sql.Timestamp.class, new TimestampParser()),
                    new AbstractMap.SimpleEntry<Class,Function>(java.time.LocalDate.class, new LocalDateParser()),
                    new AbstractMap.SimpleEntry<Class,Function>(java.time.LocalDateTime.class, new LocalDateTimeParser()),
                    new AbstractMap.SimpleEntry<Class,Function>(byte[].class, new BytesParser())

            ).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue))
    );

    private static ThreadLocal<Map<Class, Function>> customParsersThreadLocal = ThreadLocal.withInitial(HashMap::new);

    public static Object parse(final Object value, final Class targetType) throws Exception {
        if (value != null) {

            Optional<Function> parser = ValueParser.customParsersThreadLocal.get().entrySet().stream()
                    .filter(p -> p.getKey().equals(targetType))
                    .map(Map.Entry::getValue)
                    .findFirst();

            if (!parser.isPresent()) {
                parser = ValueParser.defaultParsers.entrySet().stream()
                    .filter(p -> p.getKey().equals(targetType))
                    .map(Map.Entry::getValue)
                    .findFirst();
            }

            if (parser.isPresent()) {
                try {
                    Object result = parser.get().apply(value);

                    if (result == null) {
                        throw new Exception(String.format("Error parsing value. Invalid/Not implemented conversion for value type '%s'",
                                value.getClass().getTypeName()));
                    } else {
                        return result;
                    }

                } catch (Exception e) {
                    throw new Exception(String.format("Error parsing value '%s' of type '%s' to type '%s'",
                            value, value.getClass().getTypeName(), targetType.getName()), e);
                }
            } else {
                throw new Exception(String.format("Error parsing value. Invalid/Not implemented conversion for target type '%s'",
                        targetType.getName()));
            }

        }
        return null;
    }

    public static Map<Class, Function> getDefaultParsers() {
        return defaultParsers;
    }

    public static void addCustomParser(Class targetType, Function parserFunction) {
        ValueParser.customParsersThreadLocal.get().put(targetType, parserFunction);
    }
}