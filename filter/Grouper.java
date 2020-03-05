package blackbee.swarm.parsinghelper.filter;

import java.math.BigDecimal;

/**
 * An abstract class that provides a way to implement a set of actions to be taken by the {@link Filter}
 * when extracting data using {@link Filter#groupBy(Grouper, Grouper)}. This was created due to the endless combinations of
 * Attribute-Text, Text-Attribute, TextAfterFiltering-Attribute, Text-AttributeAfterFiltering etc.
 * This way when a container Filter is reached, we can manipulate the keys and values as we please.
 *
 * For example, suppose we have a list of product containers:
 * <p>
 * <pre>
 * {@code <div class="products">}
 * {@code       <div class="item" id="3874749">}
 * {@code           <a href="/de/google-pixel-3.html"></a>}
 * {@code       </div>}
 * {@code </div>}
 * </pre>
 * We want a map of product id to link. This would be achieved by the following:
 * <pre>
 * doc.filter("***.div[class:'products']")
 *    .groupBy(Grouper.attribute("id"), Grouper.attribute("***.a", "href"))
 * </pre>
 * Static imports are recommended for readability:
 * <pre>
 * doc.filter("***.div[class:'products']").groupBy(attribute("id"), attribute("***.a", "href"))
 * </pre>
 *
 * @author dpozinen
 */
public abstract class Grouper<T> {

	public abstract T extract(Filter f);

	public static Grouper<String> text() {
		return new Grouper<String>() {
			@Override
			public String extract(Filter f) {
				return f.text();
			}
		};
	}

	public static Grouper<String> text(final boolean includeChildren) {
		return new Grouper<String>() {
			@Override
			public String extract(Filter f) {
				return f.text(0, includeChildren);
			}
		};
	}

	public static Grouper<String> text(final String path) {
		return new Grouper<String>() {
			@Override
			public String extract(Filter f) {
				return f.filter(path).text();
			}
		};
	}

	public static Grouper<String> firstText(final String path, final String... paths) {
		return new Grouper<String>() {
			@Override
			public String extract(Filter f) {
				return f.first(path, paths).text();
			}
		};
	}

	public static Grouper<String> text(final String path, final int i) {
		return new Grouper<String>() {
			@Override
			public String extract(Filter f) {
				return f.filter(path).text(i);
			}
		};
	}

	public static Grouper<String> text(final String path, final int i, final boolean includeChild) {
		return new Grouper<String>() {
			@Override
			public String extract(Filter f) {
				return f.filter(path).text(i, includeChild);
			}
		};
	}

	public static Grouper<String> text(final String path, final Condition condition) {
		return new Grouper<String>() {
			@Override
			public String extract(Filter f) {
				return f.filter(path).first(condition).text();
			}
		};
	}

	public static Grouper<String> text(final String path, final Condition condition, final String path2) {
		return new Grouper<String>() {
			@Override
			public String extract(Filter f) {
				return f.filter(path).first(condition).filter(path2).text();
			}
		};
	}

	public static Grouper<String> textNormalized(final String path) {
		return new Grouper<String>() {
			@Override
			public String extract(Filter f) {
				return f.filter(path).textNormalized();
			}
		};
	}

	public static Grouper<String> textNormalized(final String path, final int i) {
		return new Grouper<String>() {
			@Override
			public String extract(Filter f) {
				return f.filter(path).textNormalized(i);
			}
		};
	}

	public static Grouper<String> attribute(final String name) {
		return new Grouper<String>() {
			@Override
			public String extract(Filter f) {
				return f.attribute(name);
			}
		};
	}

	public static Grouper<BigDecimal> number(final String path) {
		return new Grouper<BigDecimal>() {
			@Override
			public BigDecimal extract(Filter f) {
				return f.filter(path).number();
			}
		};
	}

	public static Grouper<BigDecimal> attributeAsNumber(final String name) {
		return new Grouper<BigDecimal>() {
			@Override
			public BigDecimal extract(Filter f) {
				return f.attributeAsNumber(name, 0);
			}
		};
	}

	public static Grouper<String> attribute(final String path, final String name) {
		return new Grouper<String>() {
			@Override
			public String extract(Filter f) {
				return f.filter(path).attribute(name);
			}
		};
	}

	public static Grouper<String> attribute(final String path, final String name, final int i) {
		return new Grouper<String>() {
			@Override
			public String extract(Filter f) {
				return f.filter(path).attribute(name, i);
			}
		};
	}
}
