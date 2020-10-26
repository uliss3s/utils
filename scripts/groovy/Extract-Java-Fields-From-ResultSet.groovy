/**
 * Data extractor script for Intellij or DataGrip
 * Author: Ulisses Ricardo (urssilva@hotmail.com)
 * Tested with Oracle 11g and Intellij 2020.2
 */

NEWLINE = System.getProperty("line.separator")

ALL_COLUMNS.each { column ->
    def name = column.properties.column.properties.name;
    def type = column.properties.column.properties.typeName
    def scale = column.properties.column.properties.scale
    def size = column.properties.column.properties.size

    def javaType = column.properties.column.properties.javaClassName

    def sizeScaleInfo = "";

    if (type == "NUMBER") {
        if (scale > 0) {
            javaType = "java.math.BigDecimal";
        } else {
            if (size <= 9) {
                javaType = "java.lang.Integer";
            } else if (size <= 18) {
                javaType = "java.lang.Long";
            } else {
                javaType = "java.math.BigInteger";
                sizeScaleInfo = "//($size,$scale)"
            }
        }
    } else if (type == "TIMESTAMP") {
        javaType = "java.time.LocalDateTime";
    }

    def camelCaseField = parseScreamingSneakCaseToCamelCase(name, 1)

    def output = "private $javaType $camelCaseField; $sizeScaleInfo$NEWLINE"

    OUT.append(output)
}

def parseScreamingSneakCaseToCamelCase(name, type) {
    if (type == 0) { // Class
        fieldNameArray = name.toLowerCase().split("_")
        for (i = 0; i < fieldNameArray.length; i++) {
            fieldNameEl = fieldNameArray[i]
            fieldNameArray[i] = fieldNameEl.capitalize()
        }
        fieldNameArray.join();
    } else if (type == 1) { // Field
        fieldNameArray = name.toLowerCase().split("_")
        for (i = 0; i < fieldNameArray.length; i++) {
            if (i == 0) {
                continue
            }
            fieldNameEl = fieldNameArray[i]
            fieldNameArray[i] = fieldNameEl.capitalize()
        }
        fieldNameArray.join()
    }
}
