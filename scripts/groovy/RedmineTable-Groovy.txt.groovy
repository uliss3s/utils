/*
 * Data extractor script for Intellij or DataGrip
 * Author: Ulisses Ricardo (urssilva@hotmail.com)
 *
 * Generate tables in the Redmine compatible format: https://www.redmine.org/projects/redmine/wiki/RedmineTextFormattingTextile#Tables
 */

SEPARATOR = "|"
NEWLINE   = System.getProperty("line.separator")

def printColumns = { columns ->
  columns.eachWithIndex { col, idx ->
    OUT.append(idx == 0 ? SEPARATOR : "")
      .append("_.")
      .append(col.name())
      .append(idx != columns.size() - 1 ? SEPARATOR : SEPARATOR + NEWLINE)
  }
}

def printRow = { values, valueToString ->
  values.eachWithIndex { value, idx ->
    def str = valueToString(value)
    def q = str.contains(SEPARATOR) || str.contains(NEWLINE)
    OUT.append(idx == 0 ? SEPARATOR : "")
      .append(str)
      .append(idx != values.size() - 1 ? SEPARATOR : SEPARATOR + NEWLINE)
  }
}

if (!TRANSPOSED) {
  printColumns(COLUMNS)
  ROWS.each { row -> printRow(COLUMNS, { FORMATTER.format(row, it) }) }
}
else {
  OUT.append("")
}
