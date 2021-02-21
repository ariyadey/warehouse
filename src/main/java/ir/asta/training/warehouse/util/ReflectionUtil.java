package ir.asta.training.warehouse.util;

import java.lang.reflect.Field;

public class ReflectionUtil {

    private ReflectionUtil() {
    }

    public static <T> boolean hasNullField(T t) {
        boolean result = false;
        for (Field field : t.getClass().getDeclaredFields()) {
            boolean accessibleByDefault = field.isAccessible();
            if (!accessibleByDefault) {
                field.setAccessible(true);
            }

            try {
                if (field.get(t) == null) {
                    result = true;
                    if (!accessibleByDefault) {
                        field.setAccessible(false);
                    }
                    break;
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException();
            }
        }
        return result;
    }
}
