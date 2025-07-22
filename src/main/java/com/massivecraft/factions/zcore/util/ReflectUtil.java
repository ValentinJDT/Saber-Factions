package com.massivecraft.factions.zcore.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectUtil {

    /**
     * Executes a chain of method calls or properties on a given object instance using reflection.
     *
     * @param <T>        The expected return type of the final method in the chain.
     * @param instance   The object instance on which the method chain will be executed.
     * @param execution  A string representing the chain of method calls, where each method
     *                   is separated by a dot (e.g., "method1().method2()").
     * @return           The result of the final method call in the chain, cast to the specified type.
     * @throws RuntimeException If any error occurs during method execution, such as method not found,
     *                          invocation failure, or invalid method chain format.
     */
    public static <T> T execute(Object instance, String execution) {
        try {
            String[] parts = execution.split("\\.");
            Object result = instance;

            for (String part : parts) {
                // Supprimer les parenthèses si c'est un appel de méthode
                String name = part.contains("(") ? part.substring(0, part.indexOf('(')) : part;

                Class<?> clazz = result.getClass();
                Method method;
                Field field = null;

                try {
                    // Essayer de trouver une méthode
                    method = clazz.getMethod(name);
                    result = method.invoke(result);
                } catch (NoSuchMethodException e) {
                    try {
                        // Si ce n'est pas une méthode, essayer de trouver un champ
                        field = clazz.getDeclaredField(name);
                        field.setAccessible(true); // Permettre l'accès aux champs privés
                        result = field.get(result);
                    } catch (NoSuchFieldException ex) {
                        throw new RuntimeException("Neither method nor field found: " + name, ex);
                    }
                }
            }

            @SuppressWarnings("unchecked")
            T typedResult = (T) result;
            return typedResult;
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute: " + execution, e);
        }
    }

}
