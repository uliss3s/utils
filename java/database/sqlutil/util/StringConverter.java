package br.gov.am.sefaz.util.sqlutil.util;

public class StringConverter {
    /**
     * Converts string from screaming snake case to camel case
     * @param name field/class name
     * @param type 0: class, 1:field
     * @return
     */
    public static String screamingSnakeCaseToCamelCase(String name, int type) {
        String[] strings = name.toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            String string = strings[i];
            if (type == 1 && i == 0) {
                sb.append(string);
            } else {
                sb.append(String.valueOf(string.charAt(0)).toUpperCase() + string.substring(1));
            }
        }

        return sb.toString();
    }
}
