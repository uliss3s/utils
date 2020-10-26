package br.gov.am.sefaz.util.sqlutil.hibernate.transformers;

import br.gov.am.sefaz.util.sqlutil.parsers.ValueParser;
import org.hibernate.transform.ResultTransformer;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static br.gov.am.sefaz.util.sqlutil.util.StringConverter.screamingSnakeCaseToCamelCase;

/**
 * <p>Custom Hibernate Transformer that populates a target class with the values returned from the query.</p>
 * <p>The mapping is done on-the-fly. The "setter" method names, in camel case, should be equivalent to the column names in screaming snake case. Ex:.</p>
 * <p><strong>set</strong>TableColumn equals TABLE_COLUMN<br>
 * <strong>set</strong>Column equals COLUMN <br></p>
 */
public class ScreamingSnakeCaseToCamelCaseMethodWithClassAttributesSupportTransformer implements ResultTransformer {

    /**
     * Target Class to be populated
     */
    private Class<?> targetClass;
    /**
     * Map containing the mapped set methods
     */
    private Map<Integer, Method> setMethods;

    /**
     * Constructor method of the transformer
     * @param targetClass Class&lt;?&gt; to be populated
     */
    public ScreamingSnakeCaseToCamelCaseMethodWithClassAttributesSupportTransformer(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    /**
     * Sub classes/attributes of the main target class to be populated.
     */
    private Class<?>[] subClasses;

    /**
     * Constructor method of the transformer
     * @param targetClass Class&lt;?&gt; to be populated
     * @param subClasses List of user's classes present as attributes on main class
     */
    public ScreamingSnakeCaseToCamelCaseMethodWithClassAttributesSupportTransformer(Class<?> targetClass, Class<?>... subClasses) {
        this.targetClass = targetClass;
        this.subClasses = subClasses;
    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        Object instance = null;
        try {
            instance = targetClass.newInstance();

            if (setMethods == null) {
                setMethods = new HashMap<>();
                getSetMethods(targetClass, aliases);

                if (subClasses != null) {
                    for (Class<?> subClass : subClasses) {
                        getSetMethods(subClass, aliases);
                    }
                }
            }

            for (Integer index : setMethods.keySet()) {
                if (tuple[index] != null) {
                    Method method = setMethods.get(index);

                    Class<?> declaringClass = method.getDeclaringClass();

                    if (targetClass.equals(declaringClass)) {
                        method.invoke(instance, ValueParser.parse(tuple[index], method.getParameterTypes()[0]));
                    } else {
                        for (Class<?> subClass : subClasses) {
                            if (declaringClass.equals(subClass)) {
                                Method subClassGetMethod = getSubClassGetMethod(subClass, targetClass);

                                if (subClassGetMethod != null) {
                                    Object subClassInstance = subClassGetMethod.invoke(instance);
                                    if (subClassInstance == null) {
                                        subClassInstance = subClass.newInstance();

                                        Method subClassSetMethod = getSubClassSetMethod(subClass, targetClass, subClassGetMethod);
                                        if (subClassSetMethod != null) {
                                            subClassSetMethod.invoke(instance, subClassInstance);
                                        }
                                    }

                                    method.invoke(subClassInstance, ValueParser.parse(tuple[index], method.getParameterTypes()[0]));
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return instance;
    }

    @Override
    public List transformList(List collection) {
        return collection;
    }

    private Method getSubClassGetMethod(Class<?> subClass, Class<?> targetClass) {
        Method[] declaredMethods = targetClass.getDeclaredMethods();

        for (Method declaredMethod : declaredMethods) {
            if (declaredMethod.getReturnType().equals(subClass)) {
                return declaredMethod;
            }
        }

        return null;
    }

    private Method getSubClassSetMethod(Class<?> subClass, Class<?> targetClass, Method subClassGetMethod) {
        Method[] declaredMethods = targetClass.getDeclaredMethods();

        String subClassSetMethodExpectedName = "set" + subClassGetMethod.getName().substring(3);

        for (Method declaredMethod : declaredMethods) {
            Class<?>[] parameterTypes = declaredMethod.getParameterTypes();

            if (parameterTypes != null && parameterTypes.length > 0 && parameterTypes[0].equals(subClass)
                    && declaredMethod.getName().equals(subClassSetMethodExpectedName)) {
                return declaredMethod;
            }
        }

        return null;
    }

    private void getSetMethods(Class<?> targetClass, String[] aliases) {
        Method[] declaredMethods = targetClass.getDeclaredMethods();
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
