package com.alfred.parkingalfred.utils;

import java.lang.reflect.Field;

public class ReflectionUtil {

    public static <T> Boolean isComparablePropertyName(T t, String propertyName) {
        try {
            Field field = t.getClass().getDeclaredField(propertyName);
            return Comparable.class.isAssignableFrom(field.getType())
                    || field.getType() == int.class
                    || field.getType() == float.class
                    || field.getType() == double.class
                    || field.getType() == long.class
                    || field.getType() == short.class
                    || field.getType() == byte.class
                    || field.getType() == char.class;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static <T, V> V getPropertyValue(T t, String propertyName) {
        try {
            Field field = t.getClass().getDeclaredField(propertyName);
            field.setAccessible(true);
            return (V) field.get(t);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
