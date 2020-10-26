package br.gov.am.sefaz.util.sqlutil.parsers;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.function.Function;

public class LocalDateTimeParser implements Function<Object,LocalDateTime> {

    @Override
    public LocalDateTime apply(Object value) {
        if (value instanceof LocalDateTime) {
            return (LocalDateTime) value;
        } else if (value instanceof LocalDate) {
            return ((LocalDate) value).atStartOfDay(ZoneId.systemDefault()).toLocalDateTime();
        } else if (value instanceof Timestamp) {
            return ((Timestamp) value).toLocalDateTime();
        } else if (value instanceof java.sql.Date) {
            return ((java.sql.Date) value).toLocalDate().atStartOfDay(ZoneId.systemDefault()).toLocalDateTime();
        } else if (value instanceof Date) {
            return ((Date) value).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } else if (value instanceof String) {
            if (((String) value).matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}")) {
                return LocalDateTime.parse((String) value);
            } else {
                throw new RuntimeException("DateTime format expected: yyyy-MM-ddTHH:mm:ss");
            }
        }

        return null;
    }
}