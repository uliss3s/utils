/**
 * Data extractor script for Intellij or DataGrip
 * Author: Ulisses Ricardo (urssilva@hotmail.com)
 * Tested with Oracle 11g and Intellij 2020.2
 */

SEP = " AND "
QUOTE     = "\'"
STRING_PREFIX = DIALECT.getDbms().isMicrosoft() ? "N" : ""
NEWLINE = System.getProperty("line.separator")

def record(columns, dataRow) {
    OUT.append("SELECT * FROM ")
    if (TABLE == null) {
        OUT.append("MY_TABLE")
    } else {
        OUT.append(TABLE.getParent().getName()).append(".").append(TABLE.getName())
    }

    OUT.append(" WHERE 1 = 1")

    columns.eachWithIndex { column, idx ->
        OUT.append(" AND ").append(column.name()).append(" = ");

        def value = dataRow.value(column)
        def skipQuote = value.toString().isNumber() || value == null
        def stringValue = value != null ? FORMATTER.format(dataRow, column) : "NULL"

        if (skipQuote) {
            OUT.append(stringValue);
        } else {
            OUT.append(QUOTE).append(stringValue).append(QUOTE);
        }
    }

    OUT.append(";").append(NEWLINE)
}

ROWS.each { row -> record(COLUMNS, row) }
