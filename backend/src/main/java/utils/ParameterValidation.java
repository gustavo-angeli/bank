package utils;

public class ParameterValidation {
    @SafeVarargs
    public static <T> void nullOrEmptyParam(T... obj) {
        for (T t : obj) {
            if (t == null) {
                throw new NullPointerException("Null value");
            }
            if (t.toString().trim().isEmpty()) {
                throw new IllegalArgumentException("Empty or Blank value");
            }
        }
    }

    @SafeVarargs
    public static <T> void nullOrEmptyParamCustomMessage(String message, T... obj) {
        for (T t : obj) {
            if (t == null) {
                throw new NullPointerException(message);
            }
            if (t.toString().trim().isEmpty()) {
                throw new IllegalArgumentException(message);
            }
        }
    }
}
