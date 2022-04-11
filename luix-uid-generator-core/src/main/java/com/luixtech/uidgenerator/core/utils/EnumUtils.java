package com.luixtech.uidgenerator.core.utils;

import org.apache.commons.lang3.Validate;

/**
 * EnumUtils provides the operations for {@link ValuedEnum} such as Parse, value of...
 */
public abstract class EnumUtils {

    /**
     * Parse the bounded value into ValuedEnum
     *
     * @param clz
     * @param value
     * @return
     */
    public static <T extends ValuedEnum<V>, V> T parse(Class<T> clz, V value) {
        Validate.notNull(clz, "clz can not be null");
        if (value == null) {
            return null;
        }

        for (T t : clz.getEnumConstants()) {
            if (value.equals(t.value())) {
                return t;
            }
        }
        return null;
    }

    /**
     * Null-safe valueOf function
     *
     * @param <T>
     * @param enumType
     * @param name
     * @return
     */
    public static <T extends Enum<T>> T valueOf(Class<T> enumType, String name) {
        if (name == null) {
            return null;
        }
        return Enum.valueOf(enumType, name);
    }
}
