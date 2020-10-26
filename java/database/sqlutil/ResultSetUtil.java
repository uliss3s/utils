package br.gov.am.sefaz.util.sqlutil;

import br.gov.am.sefaz.util.sqlutil.parsers.ValueParser;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

import static br.gov.am.sefaz.util.sqlutil.util.StringConverter.screamingSnakeCaseToCamelCase;

/**
 * Classe utilitária para conversões diretamente do java.sql.ResultSet
 */
public class ResultSetUtil {

    private static Map<String, Set<String>> mappings = new HashMap<>();
    private static Map<Class<?>, Map<String,Method>> classMappings = new HashMap<>();

    /**
     * Gera HashMap a partir ResultSet
     * @param rs ResultSet
     * @param mappingId Identificação do mapeamento. <br>Os mapeamentos são salvos em cache.
     * @return Instância de HashMap
     * @throws SQLException
     */
    public static Map<String, Object> toMap(ResultSet rs, String mappingId) throws SQLException {
        if (mappings.get(mappingId) == null) {
            Set<String> columns = getQueryColumnsList(rs);
            mappings.put(mappingId, columns);
        }

        Set<String> columns = mappings.get(mappingId);

        Map<String, Object> map = new HashMap<>();
        for (String coluna: columns) {
            Object value = rs.getObject(coluna);
            map.put(coluna, value);
        }

        return map;
    }

    /**
     * Gera Object[] a partir ResultSet
     * @param rs ResultSet
     * @param mappingId Identificação do mapeamento. <br>Os mapeamentos são salvos em cache.
     * @return Instância de Object[]
     * @throws SQLException
     */
    public static Object[] toArray(ResultSet rs, String mappingId) throws SQLException {
        if (mappings.get(mappingId) == null) {
            Set<String> columns = getQueryColumnsList(rs);
            mappings.put(mappingId, columns);
        }

        Set<String> columns = mappings.get(mappingId);

        Object[] values = new Object[columns.size()];
        for (int i = 0; i < columns.size(); i++) {
            values[i] = rs.getObject(i+1);
        }

        return values;
    }

    /**
     * <p>Gera instância da classe a partir do ResultSet.</p>
     * <p>A conversão é feita a partir da seguinte convenção: Os nomes dos métodos "setter" da classe "targetClass",
     * em Camel Case, devem ser equivalentes ao nome das colunas, em SCREAMING SNAKE CASE. Ex.:</p>
     * <p><strong>set</strong>TableColumn equivale à TABLE_COLUMN<br>
     * <strong>set</strong>Column equivale à COLUMN <br></p>
     * @param rs ResultSet
     * @param targetClass Classe da instância desejada
     * @return Instancia da classe targetClass
     * @throws SQLException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static <T> T toObject(ResultSet rs, Class<?> targetClass) throws Exception {
        if (classMappings.get(targetClass) == null) {
            Set<String> columns = getQueryColumnsList(rs);

            Method[] declaredMethods = targetClass.getDeclaredMethods();

            Map<String,Method> setMethods = new HashMap<>();
            for (String column : columns) {
                // Append the "SET_" prefix on the column name and convert to camel case
                String setMethodName = screamingSnakeCaseToCamelCase("SET_" + column, 1);
                // Search for the existence of the method on the target class
                Optional<Method> methodOptional = Arrays.stream(declaredMethods)
                        .filter(m -> m.getName().equals(setMethodName))
                        .findAny();

                if (!methodOptional.isPresent()) {
                    continue;
                }

                Method method = methodOptional.get();

                setMethods.put(column, method);
            }

            classMappings.put(targetClass, setMethods);
        }

        Map<String, Method> methods = classMappings.get(targetClass);

        Object instance = targetClass.newInstance();

        for (String column : methods.keySet()) {
            Method method = methods.get(column);
            method.invoke(instance, parseValue(rs.getObject(column), method));
        }

        return (T) instance;
    }

    /**
     * <p>Gera um ArrayList a partir do ResultSet</p>
     * <p>Os objetos carregados seguem a regra descrita no método {@link ResultSetUtil#toObject(ResultSet, Class)}</b></p>
     * @param rs ResultSet
     * @param targetClass Classe da instância desejada
     * @return ArrayList do tipo definido no par&acirc;metro targetClass
     * @throws Exception
     */
    public static <T> List<T> toList(ResultSet rs, Class<T> targetClass) throws Exception {
        List<T> lista = new ArrayList<>();

        if (rs != null) {
            while (rs.next()) {
                lista.add(toObject(rs, targetClass));
            }
        }

        return lista;
    }

    private static Set<String> getQueryColumnsList(ResultSet rs) throws SQLException {
        Set<String> colunasRetornadas = new HashSet<>();

        ResultSetMetaData rsmd = rs.getMetaData();

        int qtdColunas = rsmd.getColumnCount();
        for (int i=1;i<=qtdColunas;i++) {
            colunasRetornadas.add(rsmd.getColumnName(i));
        }

        return colunasRetornadas;
    }

    /**
     * Converts the value returned from the query to the method param type
     * @param value Object column value
     * @param targetMethod Method target method
     * @return Object parsed value
     * @throws Exception
     */
    private static Object parseValue(Object value, Method targetMethod) throws Exception {
        Class<?> targetType = targetMethod.getParameterTypes()[0];

        if (value != null) {
            try {
                return ValueParser.parse(value, targetType);
            } catch (Exception e) {
                throw new Exception(String.format("Error parsing value. Value '%s' of type '%s', target method '%s' with parameter type '%s'",
                        value, targetType.getName(), targetMethod.getName(), targetType.getName()), e);
            }
        }
        return null;
    }
}