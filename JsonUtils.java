package blackbee.swarm.parsinghelper;

import blackbee.swarm.util.JsonPathWrapper;
import com.jayway.jsonpath.Criteria;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.internal.JsonContext;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonUtils {

	public static final JsonContext EMPTY_JSON = parse("{}");

	private JsonUtils()
	{
		throw new AssertionError();
	}

	public static List<JsonContext> createJsonList(JsonContext c, String path, Predicate... filters) {
		List<?> variantMapList = c.read(path, filters);
		List<JsonContext> list = new ArrayList<>();
		if (ParserUtil.isNotEmpty(variantMapList))
			for ( Object map : variantMapList )
				list.add((JsonContext) JsonPathWrapper.parse(map));
		return list;
	}

	public static Predicate fieldIs(String field, Object value) {
		return Filter.filter(Criteria.where(field).is(value));
	}

	public static Predicate fieldIsIgnoreCase(final String field, final String value) {
		return new Predicate()
		{
			public boolean apply(PredicateContext c)
			{
				Map<?, ?> map = c.item(Map.class);
				if (map != null) {
					String s = String.valueOf(map.get(field));
					return value != null && value.equalsIgnoreCase(s);
				}
				return false;
			}
		};
	}

	public static Predicate fieldIsIgnoreCaseAndSpaces(final String field, final String value) {
		return new Predicate()
		{
			public boolean apply(PredicateContext c)
			{
				Map<?, ?> map = c.item(Map.class);
				if (map != null) {
					String s = String.valueOf(map.get(field)).replaceAll("\\s+", "");
					return value.replaceAll("\\s+", "").equalsIgnoreCase(s);
				}
				return false;
			}
		};
	}

	public static Predicate fieldContains(String field, Object value)
	{
		return Filter.filter(Criteria.where(field).contains(value));
	}

	public static Predicate fieldContainsIgnoreCase(final String field, final String value) {
		return new Predicate()
		{
			@Override public boolean apply(PredicateContext c)
			{
				Map<?, ?> map = c.item(Map.class);
				if (map != null) {
					if (map.get(field) instanceof List) {
						for (Object o : (List<?>) map.get(field)) {
							String s = String.valueOf(o);
							if (value != null && s.toLowerCase().contains(value.toLowerCase()))
								return true;
						}
					} else {
						String s = String.valueOf(map.get(field));
						return value != null && s.toLowerCase().contains(value.toLowerCase());
					}
				}
				return false;
			}
		};
	}

	public static boolean contextContainsField(JsonContext c, String field) {
		List<?> read = c.read("$..[?]", List.class, Filter.filter(Criteria.where(field).exists(true)));
		return ParserUtil.isNotEmpty(read);
	}

	public static boolean contextIsEmpty(JsonContext c)
	{
		return c != null && (c.equals(EMPTY_JSON) || c.jsonString().equals("{}"));
	}

	public static String readString(JsonContext c, String path)
	{
		return StringUtils.defaultString(c.read(path, String.class));
	}

	/**
	 * Reads the first string encountered. Accepts relative paths, unlike {@link #readString}
	 * @return the String value of the first element or empty string if none were found/it was null
	 */
	public static String readFirstString(JsonContext c, String path)
	{
		return ParserUtil.firstOrEmpty(c.read(path, List.class));
	}

	/**
	 * Reads the first string encountered. Accepts relative paths, unlike {@link #readString}
	 * @return the String value of the first element or empty string if none were found/it was null
	 */
	public static String readFirstString(JsonContext c, String path, Predicate... filters)
	{
		return ParserUtil.firstOrEmpty(c.read(path, List.class, filters));
	}

	public static BigDecimal readBigDecimal(JsonContext c, String path)
	{
		return PricingHelper.extractNumber(readString(c, path));
	}

	public static BigDecimal readBigDecimalOrZero(JsonContext c, String path)
	{
		BigDecimal number = PricingHelper.extractNumber(readString(c, path));
		return number == null ? BigDecimal.ZERO : number;
	}

	public static JsonContext parse(String s) {
		try {
			return JsonPathWrapper.parse(s);
		} catch (IllegalArgumentException | InvalidJsonException e) {
			try {
				JsonContext json = ParserUtil.extractJson(s);
				if (contextIsEmpty(json))
					try {
						return parse(JsonPath.parse(s).jsonString());
					} catch (IllegalArgumentException | InvalidJsonException e1) {
						return EMPTY_JSON;
					}
				return parse(json.jsonString());
			} catch (UnsupportedOperationException ex) {
				return EMPTY_JSON;
			}
		}
	}

	/**
	 * Returns the first element from the results. This is used with relative Json Paths for which {@link JsonContext#read} returns a list.
	 * This methods gets that first element saving the generic information provided by the {@link TypeRef<T>}
	 *
	 * @param typeRef the type reference to provide
	 * @param <T> the desired return type
	 * @return the first element from the read results or null if the results were empty
	 */
	public static <T> T firstOrNull(JsonContext context, String path, TypeRef<List<T>> typeRef)
	{
		List<T> read = context.read(path, typeRef);
		return ParserUtil.isNotEmpty(read) ? read.get(0) : null;
	}

	public static JsonContext parse(Object o) {
		return o == null ? JsonPathWrapper.parse("{}") : (JsonContext) JsonPathWrapper.parse(o);
	}
}
