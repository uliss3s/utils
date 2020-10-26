package br.gov.am.sefaz.util.sqlutil.parsers;

import java.math.BigDecimal;
import java.util.function.Function;

public class IntegerParser implements Function<Object,Integer> {

    @Override
    public Integer apply(Object value) {
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof BigDecimal) {
            return ((BigDecimal) value).intValue();
        } else if (value instanceof Long) {
            return ((Long) value).intValue();
        } else if (value instanceof String) {
            return Integer.valueOf((String) value);
        }
        return null;
    }
}