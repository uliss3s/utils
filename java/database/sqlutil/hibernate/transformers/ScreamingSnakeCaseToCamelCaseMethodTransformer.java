package br.gov.am.sefaz.util.sqlutil.hibernate.transformers;

import br.gov.am.sefaz.util.sqlutil.parsers.ValueParser;
import org.hibernate.transform.ResultTransformer;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;

import static br.gov.am.sefaz.util.sqlutil.util.StringConverter.screamingSnakeCaseToCamelCase;

/**
 * <p>Custom Hibernate Transformer that populates a target class with the values returned from the query.</p>
 * <p>The mapping is done on-the-fly. The "setter" method names, in camel case, should be equivalent to the column names in screaming snake case. Ex:.</p>
 * <p><strong>set</strong>TableColumn equals TABLE_COLUMN<br>
 * <strong>set</strong>Column equals COLUMN <br></p>
 */
public class ScreamingSnakeCaseToCamelCaseMethodTransformer implements ResultTransformer {

    /**
     * Target Class to be populated
     */
    private Class<?> targetClass;
    /**
     * Map containing the mapped set methods
     */
    private Map<Integer, Method> setMethods;

    /**
     *
     */
    private boolean inheritanceMapping;

    /**
     * Constructor method of the transformer
     * @param targetClass Class&lt;?&gt; to be populated
     */
    public ScreamingSnakeCaseToCamelCaseMethodTransformer(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    /**
     * Option that enables the mapping of super class attributes
     */
    public ScreamingSnakeCaseToCamelCaseMethodTransformer withInheritanceMapping() {
        this.inheritanceMapping = true;
        return this;
    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        Object instance = null;
        try {
            instance = targetClass.newInstance();

            if (setMethods == null) {
                setMethods = new HashMap<>();

                getSetMethods(targetClass, aliases);
            }

            for (Integer index : setMethods.keySet()) {
                if (tuple[index] != null) {
                    Method method = setMethods.get(index);
                    method.invoke(instance, parseValue(tuple[index], method));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return instance;
    }

    /**
     * Map methods from super class
     * @param targetClass current child class
     * @param declaredMethods array of methods from child class
     * @return array with methods from both chield and parent class
     */
    private Method[] mapSuperClassMethods(Class<?> targetClass, Method[] declaredMethods) {

        Class<?> superClass = targetClass.getSuperclass();
        if (superClass != null && !superClass.equals(Object.class)) {

            declaredMethods = Stream.concat(Arrays.stream(declaredMethods), Arrays.stream(superClass.getDeclaredMethods()))
                    .toArray(Method[]::new);
        }

        if (superClass.getSuperclass() != null && !superClass.getSuperclass().equals(Object.class)) {
            declaredMethods = mapSuperClassMethods(superClass, declaredMethods);
        }

        return declaredMethods;
    }

    @Override
    public List transformList(List collection) {
        return collection;
    }

    /**
     * Converts the value returned from the query to the method param type
     * @param value Object column value
     * @param targetMethod Method target method
     * @return Object parsed value
     * @throws Exception
     */
    private Object parseValue(Object value, Method targetMethod) throws Exception {
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

    private void getSetMethods(Class<?> targetClass, String[] aliases) {
        Method[] declaredMethods = targetClass.getDeclaredMethods();

        if (inheritanceMapping) {
            declaredMethods = mapSuperClassMethods(targetClass, declaredMethods);
        }

        for (int i = 0; i < aliases.length; i++) {
            // Append the "SET_" prefix on the column name and convert to camel case
            String setMethodName = screamingSnakeCaseToCamelCase("SET_" + aliases[i], 1);
            // Search for the existence of the method on the target class
            Optional<Method> methodOptional = Arrays.stream(declaredMethods)
                    .filter(m -> m.getName().equals(setMethodName))
                    .findAny();

            if (!methodOptional.isPresent()) {
                continue;
            }

            Method method = methodOptional.get();

            setMethods.put(i, method);
        }
    }
}
