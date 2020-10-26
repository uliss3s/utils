package br.gov.am.sefaz.util.sqlutil.parsers;

import java.math.BigDecimal;
import java.util.function.Function;

public class DoubleParser implements Function<Object,Double> {

    @Override
    public Double apply(Object value) {
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).doubleValue();
        } else if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof Long) {
            return ((Long) value).doubleValue();
        } else if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        } else if (value instanceof String) {
            return Double.valueOf(((String) value));
        }

        return null;
    }
}