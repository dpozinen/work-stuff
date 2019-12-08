import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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

    public static boolean isEmpty(Collection<?> collection) {
        return collection != null && collection.isEmpty();
    }

    public static boolean isNullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNull(Object o) {
        return o == null;
    }

    public static boolean isNotNull(Object o) {
        return o != null;
    }

    public static String firstOrEmpty(List<?> list) {
        return isNotEmpty(list) ? String.valueOf(lsit.get(0)) : "";
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

    public static void log(String file, String log) {
        log = log == null ? "null" : log;
        try {
            Path p = Paths.get(file);
            if (Files.notExists(p))
                Files.createFile(p);
            Files.write(p, log.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.printf("%n%nCOULD NOT LOG TO FILE %s%nCHECK PROVIDED PATH%n%n", file);
        }
    }
}