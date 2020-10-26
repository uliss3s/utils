package br.gov.am.sefaz.util.sqlutil.parsers;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.function.Function;

public class DateParser implements Function<Object,Date> {

    @Override
    public Date apply(Object value) {
        if (value instanceof Date) {
            return (Date) value;
        } else if (value instanceof Timestamp) {
            return new Date(((Timestamp) value).getTime());
        } else if (value instanceof LocalDate) {
            return Date.from(((LocalDate) value).atStartOfDay(ZoneId.systemDefault()).toInstant());
        } else if (value instanceof LocalDateTime) {
            return Date.from(((LocalDateTime) value).atZone(ZoneId.systemDefault()).toInstant());
        } else if (value instanceof String) {
            if (((String) value).matches("\\d{4}-\\d{2}-\\d{2}")) { // ISO8601
                return Date.from(LocalDate.parse((String) value).atStartOfDay(ZoneId.systemDefault()).toInstant());
            } else {
                throw new RuntimeException("ISO8601 date format expected: yyyy-MM-dd");
            }
        }

        return null;
    }
}