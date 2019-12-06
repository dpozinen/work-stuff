package blackbee.swarm.parsinghelper;

import blackbee.swarm.core.web.IPostContentFormatter;
import blackbee.swarm.core.web.PostContent;
import blackbee.swarm.core.web.PostContentDefaultFormatter;
import blackbee.swarm.formatter.PostContentSingleValueFormatter;
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

	public PostWrapper() {
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

	private PostWrapper copyHere(PostWrapper other) {
		this.content = other.content;
		this.formatter = other.formatter;
		this.parameters = new HashMap<>(other.parameters);
		return this;
	}

	public PostWrapper(PostContent content) {
		this.content = content;
		this.formatter = content.getFormatter();
		this.parameters = getParamsFromContent(content);
	}

	private static Map<String, String> getParamsFromContent(PostContent content)
	{
		Map<String, String> map = new HashMap<>();
		String[] split = content.toString().split("&");

		for ( String s : split )
		{
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

	public static PostWrapper fromMap(Map<?, ?> map) {
		return fromMap(map, new PostContentDefaultFormatter());
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

	public PostWrapper set(String k, String v) {
		parameters.put(k, v);
		return copyHere(fromMap(parameters, this.formatter));
	}

	public PostWrapper remove(String k) {
		parameters.remove(k);
		return copyHere(fromMap(parameters, this.formatter));
	}

	public PostContent content() {
		return content;
	}

	@Override
	public String toString() {
		return content.toString();
	}

}
