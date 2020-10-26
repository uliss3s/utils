package br.gov.am.sefaz.util.sqlutil.parsers;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.function.Function;

public class LocalDateParser implements Function<Object,LocalDate> {

    @Override
    public LocalDate apply(Object value) {
        if (value instanceof LocalDate) {
            return (LocalDate) value;
        } else if (value instanceof LocalDateTime) {
            return ((LocalDateTime) value).toLocalDate();
        } else if (value instanceof Timestamp) {
            return ((Timestamp) value).toLocalDateTime().toLocalDate();
        } else if (value instanceof java.sql.Date) {
            return ((java.sql.Date) value).toLocalDate();
        } else if (value instanceof java.util.Date) {
            return ((java.util.Date) value).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } else if (value instanceof String) {
            if (((String) value).matches("\\d{4}-\\d{2}-\\d{2}")) { // ISO8601
                return LocalDate.parse((String) value);
            } else {
                throw new RuntimeException("ISO8601 date format expected: yyyy-MM-dd");
            }
        }

        return null;
    }
}