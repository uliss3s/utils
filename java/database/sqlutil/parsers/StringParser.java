package br.gov.am.sefaz.util.sqlutil.parsers;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.function.Function;

public class StringParser implements Function<Object,String> {

    /**
     * Override default charset with System.setProperty("StringParser.charset", "CHARSET")
     */
    public static final String DEFAULT_CHARSET = "UTF-8";

    @Override
    public String apply(Object value) {
        String charset = System.getProperty("StringParser.charset");

        if (charset == null) {
            charset = DEFAULT_CHARSET;
        }

        if (value instanceof String) {
            return (String) value;
        } else if (value instanceof int[]) {
            return Arrays.toString((int[]) value);
        } else if (value instanceof long[]) {
            return Arrays.toString((long[]) value);
        } else if (value instanceof byte[]) {
            return new String((byte[]) value, Charset.forName(charset));
        } else if (value instanceof Object[]) {
            return Arrays.toString((Object[]) value);
        } else {
            return value.toString();
        }
    }
}