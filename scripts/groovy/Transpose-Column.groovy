/**
 * Data extractor script for Intellij or DataGrip
 * Author: Ulisses Ricardo (urssilva@hotmail.com)
 * Tested with Oracle 11g and Intellij 2020.2
 */

def valores = []

COLUMNS.each { c ->
    def type = c.properties.column.properties.typeName
    ROWS.each { r ->
        if (type.matches("CHAR|VARCHAR|VARCHAR2")) {
            valores.add("'" + r.value(c) + "'")
        } else {
            valores.add(r.value(c))
        }
    }
}

OUT.append(valores.join(","))