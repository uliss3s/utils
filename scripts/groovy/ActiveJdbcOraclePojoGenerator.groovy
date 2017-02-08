/**
 * ActiveJDBC POJO Generator script extension for Oracle databases
 * Author: Ulisses Ricardo (urssilva@hotmail.com)
 * IDE: Intellij 2016.2, DataGrip
 * Description: Save the script on the "Extensions" folder.
 * Right Click <TABLE> -> Scripted Extensions -> ActiveJdbcOraclePojoGenerator.groovy
 * The generated code will be copied to the clipboard
 * Tested with Oracle 11g
 */

NEWLINE = System.getProperty("line.separator")
SPACING = "    "
CLASS_CONVERTION = 0
FIELD_CONVERTION = 1
FIELD_TYPE_IDX = 0
IMPORT_IDX = 1

GENERATE_FIELDS = false;

buffer = new StringBuilder()
SELECTION.each {
    if (it.class.canonicalName == "com.intellij.database.psi.DbTableImpl") {
        def importsToAppend = []
        def fieldsToAppend = []
        def gettersToAppend = []
        def settersToAppend = []

        tableName = it.getName();
        className = parseScreamingSneakCaseToCamelCase(tableName, CLASS_CONVERTION)

        columns = it.getDbChildren(com.intellij.database.psi.DbColumn.class, com.intellij.database.model.ObjectKind.COLUMN)
        columns.each { c ->
            columnName = c.name
            columnType = c.getDataType().typeName
            columnPrecision = c.getDataType().getPrecision()
            columnScale = c.getDataType().getScale()
            columnIsNotNull = c.isNotNull()

            fieldJavaType = getJavaType(columnType, columnPrecision, columnScale)
            fieldName = parseScreamingSneakCaseToCamelCase(columnName, FIELD_CONVERTION)

            if (fieldJavaType[IMPORT_IDX] != null) {
                importsToAppend << "import ${fieldJavaType[IMPORT_IDX]};"
            }

            if (GENERATE_FIELDS) {
                fieldsToAppend << "private ${fieldJavaType[FIELD_TYPE_IDX]} ${fieldName};"
            }

            getter = ""
            setter = ""
            if (fieldJavaType[FIELD_TYPE_IDX] == "Object") {
                getter = "public ${fieldJavaType[FIELD_TYPE_IDX]} get${fieldName.capitalize()}() {" + NEWLINE +
                        "${SPACING}return get(\"${columnName}\");" + NEWLINE +
                        "}"

                setter = "public void set${fieldName.capitalize()}(${fieldJavaType[FIELD_TYPE_IDX]} ${fieldName}) {" + NEWLINE +
                        "${SPACING}set(\"${columnName}\", ${fieldName});" + NEWLINE +
                        "}"
            } else if (fieldJavaType[FIELD_TYPE_IDX] == "byte[]") {
                getter = "public ${fieldJavaType[FIELD_TYPE_IDX]} get${fieldName.capitalize()}() {" + NEWLINE +
                        "${SPACING}return getBytes(\"${columnName}\");" + NEWLINE +
                        "}"

                setter = "public void set${fieldName.capitalize()}(${fieldJavaType[FIELD_TYPE_IDX]} ${fieldName}) {" + NEWLINE +
                        "${SPACING}set(\"${columnName}\", ${fieldName});" + NEWLINE +
                        "}"
            } else {
                getter = "public ${fieldJavaType[FIELD_TYPE_IDX]} get${fieldName.capitalize()}() {" + NEWLINE +
                        "${SPACING}return get${fieldJavaType[FIELD_TYPE_IDX]}(\"${columnName}\");" + NEWLINE +
                        "}"

                setter = "public void set${fieldName.capitalize()}(${fieldJavaType[FIELD_TYPE_IDX]} ${fieldName}) {" + NEWLINE +
                        "${SPACING}set${fieldJavaType[FIELD_TYPE_IDX]}(\"${columnName}\", ${fieldName});" + NEWLINE +
                        "}"
            }
            gettersToAppend << getter
            settersToAppend << setter

        }

        buffer.append("import org.javalite.activejdbc.Model;").append(NEWLINE)
        buffer.append("import org.javalite.activejdbc.annotations.Table;").append(NEWLINE)
        importsToAppend.toUnique().each { i ->
            buffer.append(SPACING).append(i).append(NEWLINE)
        }
        buffer.append(NEWLINE)

        buffer.append("@Table(\"${tableName}\")").append(NEWLINE)
        buffer.append("public class ${className} extends Model {").append(NEWLINE)
        buffer.append(NEWLINE)

        if (GENERATE_FIELDS) {
            fieldsToAppend.each { f ->
                buffer.append(SPACING).append(f).append(NEWLINE)
            }
            buffer.append(NEWLINE)
        }

        gettersToAppend.each { g ->
            buffer.append(g).append(NEWLINE)
        }
        buffer.append(NEWLINE)

        settersToAppend.each { s ->
            buffer.append(s).append(NEWLINE)
        }
        buffer.append(NEWLINE)

        buffer.append("}")
    }
}

CLIPBOARD.set(buffer.toString())

def getJavaType(dataType, dataPrecision, dataScale) {
    if (dataType.matches("CHAR|CHARACTER|LONG|STRING|VARCHAR|VARCHAR2")) {
        ["String", null]
    } else if (dataType.matches("NCHAR|NVARCHAR2")) {
        ["String", null]
    } else if (dataType.matches("NCLOB")) {
        ["String", null]
    } else if (dataType.matches("(RAW)|(LONG RAW)")) {
        ["byte[]", null]
    } else if (dataType.matches("BINARY_INTEGER|NATURAL|NATURALN|PLS_INTEGER|POSITIVE|POSITIVEN|SIGNTYPE|INT|INTEGER|SMALLINT")) {
        ["Integer", null]
    } else if (dataType.matches("DEC|DECIMAL|NUMBER|NUMERIC")) {
        if (dataType.matches("NUMBER|NUMERIC") && dataScale == 0) {
            if (dataPrecision == (Integer.MAX_VALUE - 1)) { // NUMBER WITHOUT SPECIFIED PRECISION. EX: NUMBER, NUMBER(*,0)
                ["BigDecimal", "java.math.BigDecimal"]
            } else if (dataPrecision <= String.valueOf(Integer.MAX_VALUE).length()) {
                ["Integer", null]
            } else if (dataPrecision <= String.valueOf(Long.MAX_VALUE.toString()).length()) {
                ["Long", null]
            } else {
                ["BigDecimal", "java.math.BigDecimal"]
            }
        } else {
            ["BigDecimal", "java.math.BigDecimal"]
        }
    } else if (dataType.matches("DOUBLE PRECISION|FLOAT|REAL")) {
        ["Double", null]
    } else if (dataType.matches("DATE|TIMESTAMP")) {
        ["Timestamp", "java.sql.Timestamp"]
    } else if (dataType.matches("ROWID, UROWID")) {
        ["Object", null]
    } else if (dataType.matches("BOOLEAN")) {
        ["Boolean", null]
    } else if (dataType.matches("CLOB")) {
        ["String", null]
    } else if (dataType.matches("BLOB")) {
        ["byte[]", null]
    } else if (dataType.matches("BFILE")) {
        ["Object", null]
    }
}

def parseScreamingSneakCaseToCamelCase(name, type) {
    if (type == CLASS_CONVERTION) { // Class
        fieldNameArray = name.toLowerCase().split("_");
        for (i=0;i<fieldNameArray.length;i++) {
            fieldNameEl = fieldNameArray[i]
            fieldNameArray[i] = fieldNameEl.capitalize()
        }
        fieldNameArray.join();
    } else if (type == FIELD_CONVERTION) { // Field
        fieldNameArray = name.toLowerCase().split("_");
        for (i=0;i<fieldNameArray.length;i++) {
            if (i==0) {
                continue
            }
            fieldNameEl = fieldNameArray[i]
            fieldNameArray[i] = fieldNameEl.capitalize()
        }
        fieldNameArray.join();
    }
}