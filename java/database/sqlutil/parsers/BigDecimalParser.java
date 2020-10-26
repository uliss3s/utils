package br.gov.am.sefaz.util.sqlutil.parsers;

import java.math.BigDecimal;
import java.util.function.Function;

public class BigDecimalParser implements Function<Object,BigDecimal> {

    @Override
    public BigDecimal apply(Object value) {
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        } else if (value instanceof Double) {
            return BigDecimal.valueOf((Double) value);
        } else if (value instanceof Long) {
            return BigDecimal.valueOf((Long) value);
        } else if (value instanceof Integer) {
            return BigDecimal.valueOf((Integer) value);
        } else if (value instanceof String) {
            return new BigDecimal((String) value);
        }

        return null;
    }
}