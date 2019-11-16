public class JsonUtils {

    private JsonUtils() {
        throw new AssertionError();
    }

    public static List<JsonContext> contextListFromContext(List<?> list, String path) {
        // todo
    }

    public static Predicate fieldIs(String field, Object value) {
        return Filter.filter(Criteria.where(field).is(value));
    }

    public static Predicate fieldContains(String field, Object value) {
        return Filter.filter(Criteria.where(field).contains(value));
    }

    public static boolean contextContainsField(JsonContext c, String field) {
        c.read("$..[?]", Filter.filter(Criteria.where(field).exists(true));)
    }

    public static Predicate fieldContainsIgnoreCase(String field, Object value) {
        // todo
    }

    public static boolean isEmpty(JsonContext c) {
        return c != null && !c.jsonString().equals("{}");
    }
}