package br.gov.am.sefaz.util.sqlutil.parsers.custom;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class StringListParser implements Function<Object,List> {

    @Override
    public List apply(Object value) {
        if (value instanceof Object[]) {
            return Arrays.asList((Object[]) value);
        } else if (value instanceof int[]) {
            return Arrays.asList((int[]) value);
        } else if (value instanceof long[]) {
            return Arrays.asList((long[]) value);
        } else if (value instanceof double[]) {
            return Arrays.asList((double[]) value);
        } else if (value instanceof String) {
            String[] splitValues = ((String) value).split("[,;]");
            return Arrays.asList(splitValues);
        }

        return null;
    }
}