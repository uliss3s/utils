package br.gov.am.sefaz.util.sqlutil.parsers;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.function.Function;

public class TimestampParser implements Function<Object,Timestamp> {

    @Override
    public Timestamp apply(Object value) {
        if (value instanceof Timestamp) {
            return (Timestamp) value;
        } else if (value instanceof java.sql.Date) {
            return new Timestamp(((java.sql.Date) value).getTime());
        } else if (value instanceof Date) {
            return new Timestamp(((Date) value).getTime());
        } else if (value instanceof LocalDate) {
            return Timestamp.valueOf(((LocalDate) value).atStartOfDay(ZoneId.systemDefault()).toLocalDateTime());
        } else if (value instanceof LocalDateTime) {
            return Timestamp.valueOf((LocalDateTime) value);
        } else if (value instanceof String) {
            return Timestamp.valueOf((String) value);
        }

        return null;
    }
}