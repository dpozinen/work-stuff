package blackbee.swarm.parsinghelper.filter;

/**
 * An abstract class providing a way to test filters for specific conditions.
 * Provides predefined implementations for basic cases.
 * This is basically a predicate. <p>
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
 * We want to find the element that has id == 3874749.
 * <pre>
 * doc.filter("***.div[class:'products']").first(Condition.attributeContainsIgnoreCase("id", "389"));
 * </pre>
 * Static imports are recommended for when calls get longer, like when applying several calls:
 * <pre>
 * doc.filter("***.div[class:'products']")
 *    .first(attributeContainsIgnoreCase("id", "389").or(attributeContainsIgnoreCase("name", "pixel 3")));
 * </pre>
 *
 * @author dpozinen
 */
public abstract class Condition {

	/**
	 * The function which will have the condition logic the that filter will be tested
	 * by.
	 * @param f the filter to test
	 * @return depending on the implementation of the function
	 */
	public abstract boolean test(Filter f);

	/**
	 * An ability to chain Condition calls, so that the following is possible
	 * {@code Condition.textIs("text", "is").or(Condition.textIs("text", "IS"))}
	 * @param other other condition to test
	 * @return this.condition || other.condition
	 */
	private Condition or(final Condition other) {
		return null; // TODO find a way?
	}

	/**
	 * @see #or(Condition)
	 */
	private Condition and(final Condition other) {
		return null; // TODO find a way?
	}

	public static Condition attributeContainsIgnoreCase(final String name, final String contains) {
		return new Condition() {
			@Override
			public boolean test(Filter t) {
				return t.attribute(name).toLowerCase().contains(contains.toLowerCase());
			}
		};
	}

	public static Condition textIs(final String is) {
		return new Condition() {
			@Override
			public boolean test(Filter t) {
				return t.text().equals(is);
			}
		};
	}

	public static Condition textIsIgnoreCase(final String is) {
		return new Condition() {
			@Override
			public boolean test(Filter t) {
				return t.text().equalsIgnoreCase(is);
			}
		};
	}

	public static Condition textIsIgnoreCase(final String path, final String is) {
		return new Condition() {
			@Override
			public boolean test(Filter t) {
				return t.filter(path).text().equalsIgnoreCase(is);
			}
		};
	}

	public static Condition textContains(final String contains, final boolean includeChildren) {
		return new Condition() {
			@Override
			public boolean test(Filter t) {
				return t.text(0, includeChildren).contains(contains);
			}
		};
	}

	public static Condition textContainsIgnoreCase(final String contains) {
		return new Condition() {
			@Override
			public boolean test(Filter t) {
				return t.text().toLowerCase().contains(contains.toLowerCase());
			}
		};
	}

	public static Condition textContainsIgnoreCase(final String path, final String contains) {
		return new Condition() {
			@Override
			public boolean test(Filter t) {
				return t.filter(path).text().toLowerCase().contains(contains.toLowerCase());
			}
		};
	}

	public static Condition scriptContains(final String contains) {
		return new Condition() {
			@Override
			public boolean test(Filter t) {
				return t.script().contains(contains);
			}
		};
	}

	public static Condition scriptStartsWith(final String s) {
		return new Condition() {
			@Override
			public boolean test(Filter t) {
				return t.script().startsWith(s);
			}
		};
	}

}
