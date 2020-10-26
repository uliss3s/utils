package br.gov.am.sefaz.util.sqlutil.parsers;

import java.math.BigDecimal;
import java.util.function.Function;

public class LongParser implements Function<Object,Long> {

    @Override
    public Long apply(Object value) {
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).longValue();
        } else if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof Integer) {
            return ((Integer) value).longValue();
        } else if (value instanceof String) {
            return Long.valueOf((String) value);
        }

        return null;
    }
}