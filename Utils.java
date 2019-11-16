import java.util.Collection;
import java.util.Collections;

public final class Utils {

    private Utils() {
        throw new AssertionError();
    }

    public static <T> Collection<T> emptyIfNull(final Collection<T> collection) {
        // todo
        return collection == null ? Collections.<T>empty() : collection;
    }

    public static Collection<String> singletonIfNullOrEmpty(Collection<String> collection) {
        return collection == null || collection.isEmpty() ? Collections.singletonList("") : collection;
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }

    public static boolean isNotNull(Collection<?> collection) {
        return collection != null;
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection != null && collection.isEmpty();
    }

    public static boolean isNullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNull(Collection<?> collection) {
        return collection == null;
    }

    public static String firstOrEmpty(List<String> list) {
        return isNotEmpty(list) ? lsit.get(0) : "";
    }

    public static boolean elContains(String find, Collection<String> collection) {
        if (isNotEmpty(collection))
            for (String s : collection) 
                if (StringUtils.contains(s, find))
                    return true;
        return false;
    }

    public static boolean elContainsIgnoreCase(String find, Collection<String> collection) {
        if (isNotEmpty(collection))
            for (String s : collection) 
                if (StringUtils.containsIgnoreCase(s, find))
                    return true;
        return false;
    }

    public static boolean containsDigits(String s) {
        return s.matches(.*\\d+.*);
    }


}