
package blackbee.swarm.parsinghelper;

import blackbee.swarm.core.web.IPostContentFormatter;
import blackbee.swarm.core.web.PostContent;
import blackbee.swarm.core.web.PostContentDefaultFormatter;
import blackbee.swarm.formatter.PostContentSingleValueFormatter;
import blackbee.swarm.util.JsonPathWrapper;
import com.jayway.jsonpath.internal.JsonContext;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dpozinen
 */
public final class PostWrapper
{
	private PostContent           content;
	private IPostContentFormatter formatter;
	private Map<String, String>   parameters;

	public static PostWrapper defaultContent() {
		return new PostWrapper();
	}

	private PostWrapper() {
		this.content = new PostContent();
		this.formatter = content.getFormatter();
		this.parameters = new HashMap<>();
	}

	public static PostWrapper copyOf(PostWrapper other) {
		PostWrapper b = new PostWrapper();
		b.content = other.content;
		b.formatter = other.formatter;
		b.parameters = new HashMap<>(other.parameters);
		return b;
	}

	public static PostWrapper copyOf(PostContent other) {
		return other == null ? null : new PostWrapper(other);
	}

	private PostWrapper copyHere(PostWrapper other) {
		this.content = other.content;
		this.formatter = other.formatter;
		this.parameters = new HashMap<>(other.parameters);
		return this;
	}

	private PostWrapper(PostContent content) {
		this.content = content;
		this.formatter = content.getFormatter();
		this.parameters = getParamsFromContent(content);
	}

	private static Map<String, String> getParamsFromContent(PostContent content) {
		Map<String, String> map = new HashMap<>();
		String[] split = content.toString().split("&");

		for ( String s : split ) {
			String k = StringUtils.substringBefore(s, "=");
			String v = StringUtils.substringAfter(s, "=");
			map.put(k, v);
		}
		return map;
	}

	private PostWrapper(IPostContentFormatter formatter) {
		this.content = new PostContent(formatter);
		this.formatter = formatter;
		this.parameters = new HashMap<>();
	}

	public static PostWrapper formattedBy(IPostContentFormatter formatter) {
		return new PostWrapper(formatter);
	}

	public static PostWrapper singleValue(boolean encode) {
		return new PostWrapper(new PostContentSingleValueFormatter(encode));
	}

	public static PostWrapper fromMap(Map<?, ?> map, boolean encode) {
		return fromMap(map, new PostContentDefaultFormatter(encode));
	}

	public static PostWrapper fromMap(Map<?, ?> map, IPostContentFormatter formatter) {
		PostWrapper b = new PostWrapper(formatter);
		for (Map.Entry<?, ?> e : map.entrySet()) {
			String k = String.valueOf(e.getKey());
			String v = String.valueOf(e.getValue());
			b.content.addEntry(k, v);
			b.parameters.put(k, v);
		}
		return b;
	}

	public PostWrapper add(String k, String v) {
		content.addEntry(k, v);
		parameters.put(k, v);
		return this;
	}

	public PostWrapper add(String k, int v) {
		content.addEntry(k, v);
		parameters.put(k, String.valueOf(v));
		return this;
	}

	public PostWrapper set(String k, String v) {
		parameters.put(k, v);
		return copyHere(fromMap(parameters, this.formatter));
	}

	public PostWrapper set(String k, int v) {
		parameters.put(k, String.valueOf(v));
		return copyHere(fromMap(parameters, this.formatter));
	}

	public PostWrapper remove(String k) {
		parameters.remove(k);
		return copyHere(fromMap(parameters, this.formatter));
	}

	public PostWrapper jsonSet(String path, String v) {
		if (parameters.size() == 1) {
			Map.Entry<String, String> next = parameters.entrySet().iterator().next();
			String key = next.getKey();
			JsonContext value = JsonPathWrapper.parse(next.getValue());
			parameters.put(key, value.set(path, v).jsonString());
			return copyHere(fromMap(parameters, this.formatter));
		}
		return this;
	}

	public String get(String k) {
		return parameters.get(k);
	}

	public PostContent content() {
		return content;
	}

	@Override
	public String toString() {
		return content.toString();
	}

}
