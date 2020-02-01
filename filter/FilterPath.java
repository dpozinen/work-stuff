package blackbee.swarm.parsinghelper.filter;

import blackbee.common.data.Key;
import blackbee.swarm.core.parsing.html.IHtmlElement;
import blackbee.swarm.core.parsing.html.IHtmlNode;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

class FilterPath {

	private final Condition condition;

	private final Filter filter;

	final List<String> paths = new ArrayList<>();

	FilterPath(Condition condition, Filter filter) {
		this.condition = condition;
		this.filter = filter;
	}

	FilterPath find() {
		StringBuilder path = new StringBuilder();

		for (Filter f : filter)
			deepFind(f, path);

		return this;
	}

	private void deepFind(Filter filter, StringBuilder path) {
		StringBuilder copy = new StringBuilder(path);
		addFilterToPath(filter, copy);

		if (filter.matches(condition))
			paths.add(copy.toString());
		if (filter.size() > 1)
			for (Filter f : filter)
				deepFind(f, copy);
		else if (filter.isNotEmpty() && requireNonNull(filter).iHtmlFilter().get(0).getHasChildElements())
			for (IHtmlElement f : filter.iHtmlFilter().get(0).getChildElements())
				deepFind(Filter.fromElement(f), copy);
	}

//	TODO rewrite using Filter#allAttributesPaired()
	private void addFilterToPath(Filter filter, StringBuilder copy) {
		if (filter.isNotEmpty()) {
			IHtmlElement element = filter.iHtmlFilter().get(0);

			if (element == null || element.getAttributes() == null) return;

			IHtmlNode.IHtmlNodeAttributeCollection attributes = element.getAttributes();

			if (attributes.getKeys().length == 0) {
				if (copy.length() == 0)
					copy.append("***");
				else
					copy.append(".").append(element.getTagName());
				return;
			}

			Key[] keys = attributes.getKeys();
			Object[] values = attributes.getValues();

			if (keys.length == values.length)
				appendTags(copy, element, keys, values);
			else
				copy.append(".***");
		}
	}

	private void appendTags(StringBuilder copy, IHtmlElement element, Key[] keys, Object[] values) {
		copy.append(".").append(element.getTagName()).append("[");

		for (int i = 0; i < keys.length; i++) {
			String key = String.valueOf(keys[i]);
			String val = String.valueOf(values[i]);

			String tag = StringUtils.isEmpty(val) ? String.format("%s:* ", key) : String.format("%s:'%s' ", key, val);

			copy.append(tag).append("& ");
			if (i == 4) break;
		}
		copy.setLength(copy.length() - 3);
		copy.append("]");
	}

}
