package br.gov.am.sefaz.util.sqlutil.parsers;

import java.util.function.Function;

public class BooleanParser implements Function<Object,Boolean> {

    @Override
    public Boolean apply(Object value) {
        if (value instanceof Integer) {
            if (((Integer) value).compareTo(1) == 0) {
                return Boolean.TRUE;
            } else if (((Integer) value).compareTo(0) == 0) {
                return Boolean.FALSE;
            }
        } else if (value instanceof String) {
            String valueTest = (String) value;

            if (valueTest.equalsIgnoreCase("y") || valueTest.equalsIgnoreCase("yes")
                    || valueTest.equalsIgnoreCase("true")
                    || valueTest.equalsIgnoreCase("s") || valueTest.equalsIgnoreCase("sim")
                    || valueTest.equalsIgnoreCase("v") || valueTest.equalsIgnoreCase("verdadeiro")
                    || valueTest.equalsIgnoreCase("1")) {
                return Boolean.TRUE;
            } else if (valueTest.equalsIgnoreCase("n") || valueTest.equalsIgnoreCase("no")
                    || valueTest.equalsIgnoreCase("false")
                    || valueTest.equalsIgnoreCase("nao")
                    || valueTest.equalsIgnoreCase("f") || valueTest.equalsIgnoreCase("falso")
                    || valueTest.equalsIgnoreCase("0")) {
                return Boolean.FALSE;
            }
        }

        return null;
    }
}