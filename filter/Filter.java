package blackbee.swarm.parsinghelper.filter;

import blackbee.common.data.Key;
import blackbee.swarm.core.parsing.html.*;
import blackbee.swarm.core.swarm.parsers.framework.BaseWebRequestStep;
import blackbee.swarm.core.web.Uri;
import blackbee.swarm.parsinghelper.BasicParsingHelper;
import blackbee.swarm.parsinghelper.PricingHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static java.util.Objects.requireNonNull;

/**
 * This is a class that provides control of HTML documents. It is iterable and
 * is based off of multiple years of gathering useful features, that have been
 * added in a new more convenient way. At it's core is the
 * {@link IHtmlElementFilter} that this class is built on top of.
 * <p>
 * Glossary: - filter - the instance of this class, containing some objects that
 * can be filtered through - element/s - the html element/s directly under the
 * filter, these elements will always become filters as well, once returned from
 * withing the class. In other words instance/s of {@link IHtmlElementFilter},
 * not to be confused with {@link Filter} - path - the path in jsoup format
 *
 * @author dpozinen
 */
public class Filter implements Iterable<Filter> {

	private static final Filter EMPTY = new Filter();
	private final IHtmlElementFilter filter;
	private final boolean innerEmpty;

	/**
	 * Should only be used to construct an empty instance of {@link Filter}
	 */
	private Filter() {
		innerEmpty = true;
		filter = null;
	}

	private Filter(IHtmlElementFilter o) {
		filter = o;
		innerEmpty = false;
	}

	/**
	 * Creates an insatnce of {@link Filter} based on the provided
	 * {@link IHtmlElementFilter}
	 */
	public static Filter fromFilter(IHtmlElementFilter o) {
		return new Filter(o);
	}

	/**
	 * Creates an insatnce of {@link Filter} based on the provided
	 * {@link IHtmlElement}
	 */
	public static Filter fromElement(IHtmlElement e) {
		return new Filter(new HtmlElementFilter(e));
	}

	/**
	 * Provides a way to construct a filter from a custom string. Uses UTF-8
	 * encoding.
	 *
	 * @param s the html code that the filter will be created from
	 */
	public static Filter fromString(String s, Uri uri, IHtmlParserProvider provider) {
		IHtmlDocument doc = provider.parseWithJSoup(s, uri, "UTF-8");
		return new Filter(new HtmlElementFilter(doc));
	}

	/**
	 * @param charset the charset that will be used for the filter creation
	 */
	public static Filter fromString(String s, String url, String charset, IHtmlParserProvider provider) {
		IHtmlDocument doc = provider.parseWithJSoup(s, new Uri(url), charset);
		return new Filter(new HtmlElementFilter(doc));
	}

	/**
	 * Used to create a filter on the {@link BaseWebRequestStep} from the response.
	 *
	 * @param parent the response/BaseWebRequestStep/router step
	 * @return a filter from the response received
	 */
	public static Filter fromResponse(BaseWebRequestStep<?, ?> parent) {
		return new Filter(parent.createDocumentFilter());
	}

	/**
	 * Used to create a filter on the {@link BaseWebRequestStep} from the response with the provided charset
	 *
	 * @param parent the response/BaseWebRequestStep/router step
	 * @param charset the charset to be used (UTF-8, Windows-1251 etc)
	 * @return a filter from the response received
	 */
	public static Filter fromResponse(BaseWebRequestStep<?, ?> parent, String charset) {
		String content = parent.getResponse().getContent().Content;
		Uri uri = parent.getResponse().getUri();
		IHtmlDocument doc = parent.getHtmlParserProvider().parseWithJSoup(content, uri, charset);
		return new Filter(new HtmlElementFilter(doc));
	}

	// #filter

	/**
	 * Filters the document by the provided path.
	 *
	 * @param path a valid path in jsoup syntax
	 * @return the filter that is created from the resulting path filtering
	 */
	public Filter filter(String path) {
		return isEmpty() ? EMPTY : new Filter(requireNonNull(filter).filterByDataPath(path));
	}

	/**
	 * Filters the document by the provided paths
	 *
	 * @param paths the alternative paths if the first one returns an empty filter
	 * @return the first result of filtering that is not empty, or the last result if all paths return empty filters.
	 * @see #filter(String)
	 */
	public Filter first(String path, String... paths) {
		Filter f = filter(path);

		if (f.isEmpty())
			for (String p : paths) {
				f = filter(p);
				if (f.isNotEmpty())
					return f;
			}
		return f;
	}

	/**
	 * Filters by all the provided paths and matches all the results by the
	 * specified condition.
	 *
	 * @param condition any condition to test the filter against
	 * @param paths     the paths to filter by
	 * @return the first filter that matches the given {@link Condition}
	 */
	public Filter first(Condition condition, String... paths) {
		for (String path : paths) {
			Filter f = filter(path);
			if (f.matches(condition)) return f;
		}
		return EMPTY;
	}

	/**
	 * Filters through the document by path {@code "***.script"} and finds the first filter to
	 *
	 * @param condition any condition to test the filter against, usually {@link Condition#scriptContains} or {@link Condition#scriptStartsWith} since
	 *                  all other conditions don't really make sense
	 * @return the first filter to match the {@link Condition}
	 */
	public Script firstScript(Condition condition) {
		for (Filter f : filter("***.script"))
			if (f.matches(condition)) return f.script();
		return EMPTY.script();
	}

	/**
	 * Checks this filter against the given condition
	 *
	 * @return {@link Condition#test(Filter)}
	 */
	public boolean matches(Condition c) {
		return c.test(this);
	}

	/**
	 * Performs a deep scan of the filter, searching ALL the elements of the filter.
	 *
	 * @param condition the condition to match all filters against
	 * @return the first filter that matches the given condition
	 */
	public Filter first(Condition condition) {
		List<Filter> all = all(condition);
		for (Filter f : all)
			if (f.isNotEmpty())
				return f;
		return EMPTY;
	}

	/**
	 * Performs a deep scan of the filter, searching ALL the elements of the filter.
	 *
	 * Example:
	 * {@code document.findPaths(Condition.textContains("499.00", false));}
	 *
	 * will produce a list with all paths that lead to this value
	 *
	 * @param condition the condition to match all filters against
	 * @return all the paths to reach the filters that matches the conditions
	 */
	public List<String> findPaths(Condition condition) {
		return new FilterPath(condition, this).find().paths;
	}

	/**
	 * Performs a deep scan of the filter, searching ALL the elements of the filter.
	 *
	 * @param condition the condition to match all filters against
	 * @return all the filters that match the given condition
	 */
	public List<Filter> all(Condition condition) {
		List<Filter> ret = new ArrayList<>();
		for (Filter f : this)
			f.deepFilterAll(condition, ret);
		return ret;
	}

	private void deepFilterAll(Condition condition, List<Filter> ret) {
		if (this.matches(condition))
			ret.add(this);
		if (size() > 1)
			for (Filter f : this)
				f.deepFilterAll(condition, ret);
		else
			for (Filter f : children())
				f.deepFilterAll(condition, ret);
	}

	/**
	 * This is a safe way to get the first element of the filter. Basically a
	 * {@code get(0)}
	 *
	 * @return the first filter in the elements of this filter.
	 * @see #get(int)
	 */
	public Filter first() {
		return get(0);
	}

	public Filter last() {
		return get(size() - 1);
	}

	/**
	 * Returns the element at the specified index
	 *
	 * @param i index of the desired element
	 * @return the element at the specified index. If this filter does not contain
	 * any elements, an empty/stub filter is returned.
	 */
	public Filter get(int i) {
		return size() > i ? Filter.fromElement(requireNonNull(filter).get(i)) : EMPTY;
	}

	// #text

	/**
	 * @return the text of the first element of this filter including children, or
	 * an empty string if the filter is empty
	 */
	public String text() {
		return text(0);
	}

	/**
	 * @param orElse the string to be returned if this filter is empty
	 * @return the text of the first element of this filter, or the specified string
	 * if this filter is empty
	 */
	public String text(String orElse) {
		return text(0, orElse);
	}

	/**
	 * @param i the index of the element
	 * @return the text of the element at the specified index of this filter, or an
	 * empty string if the filter does not contain an element at the
	 * specified index
	 */
	public String text(int i) {
		return text(i, "");
	}

	/**
	 * @param i the index of the element
	 * @return the text of the element at the specified index of this filter, or an
	 * empty string if the filter does not contain an element at the
	 * specified index
	 */
	public String text(int i, boolean includeChildren) {
		return text(i, "", includeChildren);
	}

	/**
	 * @see #text(int)
	 * @see #text(String)
	 */
	public String text(int i, String orElse) {
		return text(i, orElse, true);
	}

	public String text(int i, String orElse, boolean includeChildren) {
		if (size() <= i) return orElse;
		String asText = requireNonNull(filter).get(i).getAsText(includeChildren);
		return asText.isEmpty() ? orElse : asText;
	}

	/**
	 * @return the text of the first element of this filter in normalized form
	 */
	public String textNormalized() {
		return textNormalized(0);
	}

	/**
	 * @see #text(int)
	 * @see #textNormalized()
	 */
	public String textNormalized(int i) {
		return textNormalized(i, true);
	}

	/**
	 * @see #textNormalized(int, boolean)
	 */
	public String textNormalized(int i, boolean includeChildren) {
		return BasicParsingHelper.normalizeText(text(i, "", includeChildren));
	}

	/**
	 * @return the text of all the elements
	 */
	public List<String> allText(boolean includeChildren) {
		List<String> text = new ArrayList<>();
		for (Filter f : this)
			text.add(f.text(0, includeChildren));
		return text;
	}

	/**
	 * @return the text of all the elements in a set
	 */
	public Set<String> allDistinctText(boolean includeChildren) {
		return new LinkedHashSet<>(allText(includeChildren));
	}

	/**
	 * @see #allText
	 * @see #textNormalized()
	 */
	public List<String> allTextNormalized(boolean includeChildren) {
		List<String> text = new ArrayList<>();
		for (Filter f : this)
			text.add(f.textNormalized(0, includeChildren));
		return text;
	}

	/**
	 * @see #allText
	 * @see #textNormalized()
	 */
	public Set<String> allDistinctTextNormalized(boolean includeChildren) {
		return new LinkedHashSet<>(allTextNormalized(includeChildren));
	}

	/**
	 * Joins all extracted text, normalized, to one string joining it using spaces
	 * @see #allTextJoined(boolean, String)
	 */
	public String allTextJoined(boolean includeChildren)
	{
		return StringUtils.join(allTextNormalized(includeChildren), " ");
	}

	/**
	 * Joins all extracted text, normalized, to one string joining it using the provided string
	 */
	public String allTextJoined(boolean includeChildren, String joiner)
	{
		return StringUtils.join(allTextNormalized(includeChildren), joiner);
	}

	// #attrubute

	/**
	 * Returns the attribute of the first element of this filter by the provided
	 * name.
	 *
	 * @param name should be the name of the desired attribute
	 * @return the value by the provided name or an empty string if there was no
	 * value by the name
	 */
	public String attribute(String name) {
		return attribute(name, 0, "");
	}

	/**
	 * Provides a way to customize the return if there was no value by the name
	 *
	 * @param orElse the string returned if no value was found by the name
	 * @return the value by the provided name or the provided alternative
	 * @see #attribute(String)
	 */
	public String attribute(String name, String orElse) {
		return attribute(name, 0, orElse);
	}

	/**
	 * Provides a way to specify the index of the element
	 *
	 * @return the value by the provided name at the specified index or an empty
	 * string if there was no value by the name
	 * @see #attribute(String)
	 */
	public String attribute(String name, int i) {
		return attribute(name, i, "");
	}

	/**
	 * @see #attribute(String, String)
	 * @see #attribute(String, int)
	 */
	public String attribute(String name, int i, String orElse) {
		if (size() <= i || StringUtils.isBlank(name)) return orElse;
		IHtmlNode.IHtmlNodeAttributeCollection attributes = requireNonNull(filter).get(i).getAttributes();
		return attributes.containsKey(name) && !attributes.get(name).isEmpty() ? attributes.get(name) : orElse;
	}

	/**
	 * @return the number present in the specified attribute
	 */
	public BigDecimal attributeAsNumber(String name, int i) {
		return PricingHelper.extractNumber(attribute(name, i));
	}

	/**
	 * @param name the name that indicates which attributes are of interest
	 */
	public List<String> allAttributes(String name) {
		List<String> attributes = new ArrayList<>();
		if (StringUtils.isNotBlank(name) && isNotEmpty())
			for (IHtmlElement element : requireNonNull(filter))
				if (element.getAttributes().containsKey(name))
					attributes.add(element.getAttributes().get(name));
		return attributes;
	}

	/**
	 * Collects all attributes into a map, so if several attributes have the same keys, only the last one encountered will be present.
	 *
	 * @return all attributes of all html elements in the this filter.
	 * @see #allAttributesPaired()
	 */
	public Map<String, String> allAttributesMapped() {
		Map<String, String> attributes = new HashMap<>();

		for (Pair<String, String> attribute : collectAllAttributes())
			attributes.put(attribute.getLeft(), attribute.getRight());
		return attributes;
	}

	/**
	 * Collects all attributes into a list of pairs, so that if several have the same keys, they will al be present.
	 * If the desired behaviour is to have only one of each key use {@link #allAttributesMapped()}
	 * @return all attributes of all html elements in the this filter.
	 */
	public List<Pair<String, String>> allAttributesPaired() {
		return collectAllAttributes();
	}

	private List<Pair<String, String>> collectAllAttributes() {
		List<Pair<String, String>> attributes = new ArrayList<>();
		if (isNotEmpty())
			for (IHtmlElement element : requireNonNull(filter)) {
				Key[] keys = element.getAttributes().getKeys();
				Object[] values = element.getAttributes().getValues();

				if (keys.length == values.length)
					for (int i = 0; i < keys.length; i++)
						attributes.add(Pair.of(String.valueOf(keys[i]), String.valueOf(values[i])));
			}
		return attributes;
	}

	/**
	 * @param name the name that indicates which attributes are of interest
	 */
	public Set<String> allDistinctAttributes(String name) {
		return new LinkedHashSet<>(allAttributes(name));
	}

	// #number

	/**
	 * Extracts a number from the first element
	 *
	 * @see #number(int)
	 */
	public BigDecimal number() {
		return PricingHelper.extractNumber(text());
	}

	/**
	 * Extracts the number from the text of the specified element in this filter,
	 * applying {@link PricingHelper#extractNumber}
	 *
	 * @return the number with scale of 2 and {@link RoundingMode#HALF_UP}
	 */
	public BigDecimal number(int i) {
		return PricingHelper.extractNumber(text(i));
	}

	/**
	 * Extracts number with specified scale and RoundingMode
	 *
	 * @see #number(int)
	 */
	public BigDecimal number(int i, int scale, RoundingMode m) {
		return PricingHelper.extractNumber(text(i), scale, m);
	}

	/**
	 * Extracts the number from the text of the specified element in this filter, first removes what matches the regex, then
	 * applies {@link PricingHelper#extractNumber}
	 *
	 * @return the number with scale of 2 and {@link RoundingMode#HALF_UP}
	 */
	public BigDecimal numberCleaned(int i, String regex) {
		String text = text(i).replaceAll(regex, "");
		return PricingHelper.extractNumber(text);
	}

	// #script

	public Script script() {
		return script(0);
	}

	public Script script(int i) {
		if (size() <= i) return Script.EMPTY;
		String script = requireNonNull(filter).get(i).getContent(HtmlPrintFlags.Default);
		return script.isEmpty() ? Script.EMPTY : new Script(script);
	}

	// #children

	public List<Filter> children() {
		List<Filter> children = new ArrayList<>();

		if (isNotEmpty() && requireNonNull(filter).get(0).getHasChildElements())
			for (IHtmlElement f : filter.get(0).getChildElements())
				children.add(Filter.fromElement(f));

		return children;
	}

	// #extra

	/**
	 * @return number of top level elements of this filter
	 */
	public int size() {
		return isEmpty() ? 0 : requireNonNull(filter).getCount();
	}

	/**
	 * @return the underlying url tied to this filter
	 */
	public String url() {
		return isNotEmpty() ? requireNonNull(filter).get(0).getUri().toString() : "";
	}

	/**
	 * @return true if there are NO elements in this filter
	 */
	public boolean isEmpty() {
		return innerEmpty || requireNonNull(filter).getIsEmpty();
	}

	/**
	 * @return true if there are SOME elements in this filter
	 */
	public boolean isNotEmpty() {
		return !isEmpty();
	}

	/**
	 * Provides a way to group elements into a map similar to Java streams #groupBy().
	 * Iterates through this filter and applies each of the grouper's extraction logic on each iteration.
	 * So for making a basic table extraction the following code would work: </p>
	 * {@code document.filter("***.tr").groupBy(Grouper.text("***.th"), Grouper.text("***.td"));}
	 * or if both key and value are in the same tag: </p>
	 * {@code document.filter("***.tr").groupBy(Grouper.text("***.td", 0), Grouper.text("***.td", 1));}
	 *
	 * @param key the {@link Grouper} that will be used for generating the keys of the map
	 * @param val the {@link .Grouper} that will be used for generating the values of the map
	 * @see Grouper
	 */
	public <K, V> Map<K, V> groupBy(Grouper<K> key, Grouper<V> val) {
		Map<K, V> map = new HashMap<>();

		for (Filter row : this) {
			K k = key.extract(row);
			V v = val.extract(row);

			map.put(k, v);
		}
		return map;
	}

	/**
	 * Combines unrelated lists of values into one map if the extracted sizes of the lists are equal
	 *
	 * @param key the {@link Merger} that will be used for generating the keys of the map
	 * @param val the {@link Merger} that will be used for generating the values of the map
	 * @return empty map if the value and key lists are unequal in size, otherwise the map of key to values from the extracted lists
	 * @see Merger
	 */
	public <K, V> Map<K, V> mergeBy(Merger<K> key, Merger<V> val) {
		Map<K, V> map = new HashMap<>();
		List<K> k = key.extract(this);
		List<V> v = val.extract(this);

		if (k.size() == v.size())
			for (int i = 0; i < k.size(); i++)
				map.put(k.get(i), v.get(i));

		return map;
	}

	/**
	 * Combines unrelated lists of values into one map if the extracted sizes of the lists are equal
	 *
	 * @param key the {@link Merger} that will be used for generating the keys of the pair
	 * @param val the {@link Merger} that will be used for generating the values of the pair
	 * @return empty list if the value and key lists are unequal in size, otherwise the list of Pairs of keys to values from the extracted lists
	 * @see Merger
	 */
	public <K, V> List<Pair<K, V>> pairBy(Merger<K> key, Merger<V> val) {
		List<Pair<K, V>> pairs = new ArrayList<>();
		List<K> k = key.extract(this);
		List<V> v = val.extract(this);

		if (k.size() == v.size())
			for (int i = 0; i < k.size(); i++)
				pairs.add(Pair.of(k.get(i), v.get(i)));

		return pairs;
	}

	/**
	 * @see #pairBy(Merger, Merger)
	 * @see #groupBy(Grouper, Grouper)
	 */
	public <K, V> List<Pair<K, V>> pairBy(Grouper<K> key, Grouper<V> val) {
		List<Pair<K, V>> pairs = new ArrayList<>();

		for (Filter row : this) {
			K k = key.extract(row);
			V v = val.extract(row);

			pairs.add(Pair.of(k, v));
		}
		return pairs;
	}

	/**
	 * Provides an iterator for this filter's top level elements
	 */
	@Override
	public Iterator<Filter> iterator() {
		List<Filter> l = new ArrayList<>();
		if (isNotEmpty())
			for (IHtmlElement element : requireNonNull(filter))
				l.add(Filter.fromElement(element));
		return l.iterator();
	}

	/**
	 * This may be null if {@link #innerEmpty} is true
	 *
	 * @return the core of the class - {@link IHtmlElementFilter}
	 */
	public IHtmlElementFilter iHtmlFilter() {
		return filter;
	}
}
