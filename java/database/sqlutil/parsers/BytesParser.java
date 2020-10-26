package br.gov.am.sefaz.util.sqlutil.parsers;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.function.Function;

public class BytesParser implements Function<Object,byte[]> {

    @Override
    public byte[] apply(Object value) {
        if (value instanceof byte[]) {
            return (byte[]) value;
        } else if (value instanceof Blob) {
            try {
                int blobLength = (int) ((Blob) value).length();
                byte[] blobBytes = ((Blob) value).getBytes(1, blobLength);
                ((Blob) value).free();
                return blobBytes;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("Object type not implemented for this parser");
        }
    }
}