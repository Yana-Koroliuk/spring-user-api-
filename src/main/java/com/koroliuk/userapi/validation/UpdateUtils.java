package com.koroliuk.userapi.validation;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class UpdateUtils {

    public static <T> void updateIfChanged(Consumer<T> setter, T newValue, Supplier<T> currentValueSupplier) {
        if (newValue != null) {
            T currentValue = currentValueSupplier.get();
            if (currentValue == null || !currentValue.equals(newValue)) {
                if (newValue instanceof String && ((String) newValue).isBlank()) {
                    throw new IllegalArgumentException("The new value must not be blank");
                }
                setter.accept(newValue);
            }
        }
    }
}
