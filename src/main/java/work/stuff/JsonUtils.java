package src.main.java.work.stuff;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonUtils {

    private static final TypeRef<List<Map<?,?>>> ListOfMaps = new TypeRef<List<Map<?,?>>>(){};

    private JsonUtils() {
        throw new AssertionError();
    }

    public static List<JsonContext> createJsonList(JsonContext c, String path) {
        List<JsonContext> list = new ArrayList<>();
        for (Map.Entry<?, ?> e : c.read(path, ListOfMaps)) {
            list.add(JsonPathWrapper.parse(e));
        }
        return list;
    }

    public static Predicate fieldIs(String field, Object value) {
        return Filter.filter(Criteria.where(field).is(value));
    }

    public static Predicate fieldIsIgnoreCase(String field, String value) {
        return new Predicate() {
            public boolean apply(PredicateContext c) {
                Map<?,?> map = c.item(Map.class);
                if (map != null) {
                    String s = String.valueOf(map.get(field));
                    return value != null && value.equalsIgnoreCase(s);                    
                }
                return false;
            }
        };
    }

    public static Predicate fieldIsIgnoreCaseAndSpaces(String field, String value) {
        return new Predicate() {
            public boolean apply(PredicateContext c) {
                Map<?,?> map = c.item(Map.class);
                if (map != null) {
                    String s = String.valueOf(map.get(field));
                    return BasicParsingHelper.noramlizeText(value).equalsIgnoreCase(BasicParsingHelper.noramlizeText(s));                    
                }
                return false;
            }
        };
    }

    public static Predicate fieldContains(String field, Object value) {
        return Filter.filter(Criteria.where(field).contains(value));
    }

    public static boolean contextContainsField(JsonContext c, String field) {
        c.read("$..[?]", Filter.filter(Criteria.where(field).exists(true)));
    }

    public String readString(JsonContext c, String path) {
        return StringUtils.defaultString(c.read(path, String.class));
    }

    public BigDecimal readBigDecimal(JsonContext c, String path) {
        return PricingHelper.extractNumber(readString(c, path));
    }

    public static Predicate fieldContainsIgnoreCase(String field, String value) {
        return new Predicate() {
            public boolean apply(PredicateContext c) {
                Map<?,?> map = c.item(Map.class);
                if (map != null) {
                    String s = String.valueOf(map.get(field));
                    return value != null && value.toLowerCase().contains(s.toLowerCase());                    
                }
                return false;
            }
        };
    }

    public static boolean contextIsEmpty(JsonContext c) {
        return c != null && !c.jsonString().equals("{}");
    }
}