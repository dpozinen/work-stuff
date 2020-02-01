package blackbee.swarm.parsinghelper.filter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * An abstract class that provides a way to implement a set of actions to be taken by the {@link Filter}
 * when extracting data using {@link Filter#mergeBy(Merger, Merger)}. This was created due to a recurring issue,
 * when we need to create a map from unrelated elements.
 * This way we can combine/merge two different lists of values into a single map.
 *
 * For example, suppose we have a list of (weird) product containers:
 * <p>
 * <pre>
 * {@code <div class="products">}
 * {@code       <div class="item" id="3874749"></div> }
 * {@code       <a href="/de/google-pixel-3.html"></a>}
 * {@code </div>}
 * </pre>
 * We want a map of product id to link. This would be achieved by the following:
 * <pre>
 * doc.filter("***.div[class:'products']")
 *    .merge(Merger.attributes("***.a", "href"), Merger.attributes("***.div", "id"))
 * </pre>
 * Static imports are recommended for readability:
 * <pre>
 * doc.filter("***.div[class:'products']")
 *    .merge(attributes("***.a", "href"), attributes("***.div", "id"))
 * </pre>
 *
 * @author dpozinen
 */
public abstract class Merger<T> {

	public abstract List<T> extract(Filter f);

	public static Merger<String> text(final String path) {
		return new Merger<String>() {
			@Override public List<String> extract(Filter f) {
				return f.filter(path).allText(false);
			}
		};
	}

	public static Merger<String> textWithChildren(final String path) {
		return new Merger<String>() {
			@Override public List<String> extract(Filter f) {
				return f.filter(path).allText(true);
			}
		};
	}

	public static Merger<String> attributes(final String path, final String name) {
		return new Merger<String>() {
			@Override public List<String> extract(Filter f) {
				return f.filter(path).allAttributes(name);
			}
		};
	}

	public static Merger<BigDecimal> numbers(final String path) {
		return new Merger<BigDecimal>() {
			@Override public List<BigDecimal> extract(Filter f) {
				List<BigDecimal> numbers = new ArrayList<>();
				for (Filter filter : f.filter(path)) numbers.add(filter.number());
				return numbers;
			}
		};
	}

	public static Merger<BigDecimal> attributesAsNumbers(final String path, final String name) {
		return new Merger<BigDecimal>() {
			@Override public List<BigDecimal> extract(Filter f) {
				List<BigDecimal> numbers = new ArrayList<>();
				for (Filter filter : f.filter(path)) numbers.add(filter.attributeAsNumber(name, 0));
				return numbers;
			}
		};
	}

	public static Merger<Filter> filters(final String path) {
		return new Merger<Filter>() {
			@Override public List<Filter> extract(Filter f) {
				List<Filter> filters = new ArrayList<>();
				for (Filter filter : f.filter(path)) filters.add(filter);
				return filters;
			}
		};
	}
}
