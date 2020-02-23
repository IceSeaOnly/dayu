package site.binghai.lib.utils;

public class CompareUtils {
    public static <T> boolean inAny(T source, T... elements) {
        if (elements == null || elements.length == 0) { return false; }
        for (T element : elements) {
            if (element.equals(source)) { return true; }
        }
        return false;
    }

    public static <T> boolean notInAny(T source, T... elements) {
        return !inAny(source, elements);
    }

}
