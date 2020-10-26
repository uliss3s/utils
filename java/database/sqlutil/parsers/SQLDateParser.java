package br.gov.am.sefaz.util.sqlutil.parsers;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Function;

public class SQLDateParser implements Function<Object,Date> {

    @Override
    public Date apply(Object value) {
        if (value instanceof Timestamp) {
            return new Date(((Timestamp) value).getTime());
        } else if (value instanceof Date || value instanceof java.util.Date) {
            return (Date) value;
        } else if (value instanceof LocalDate) {
            return Date.valueOf((LocalDate) value);
        } else if (value instanceof LocalDateTime) {
            return Date.valueOf(((LocalDateTime) value).toLocalDate());
        } else if (value instanceof String) {
            if (((String) value).matches("\\d{4}-(\\d{2}|\\d)-(\\d{2}|\\d)")) {
                return Date.valueOf(((String) value));
            } else {
                throw new RuntimeException("DateTime format expected: yyyy-MM-dd");
            }
        }

        return null;
    }
}