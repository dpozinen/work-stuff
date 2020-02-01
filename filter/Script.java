package blackbee.swarm.parsinghelper.filter;

import blackbee.swarm.parsinghelper.JsonUtils;
import blackbee.swarm.util.JsonPathWrapper;
import com.jayway.jsonpath.internal.JsonContext;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @author dpozinen
 */
public class Script {

	private final String content;
	private static final JsonContext EMPTY_JSON = JsonPathWrapper.parse("{}");
	static final Script EMPTY = new Script("");

	Script(String content) {
		this.content = StringUtils.defaultString(content);
	}

	public JsonContext json() {
		return content.isEmpty() ? EMPTY_JSON : JsonUtils.parse(content);
	}

	public JsonContext subJson(String path) {
		JsonContext json = json();
		if (JsonUtils.contextIsEmpty(json)) return json;
		return (JsonContext) JsonPathWrapper.parse(json.read(path));
	}

	public Script scriptAfter(String after) {
		String script = StringUtils.substringAfter(content, after);
		return new Script(script);
	}

	public Script scriptBefore(String before) {
		String script = StringUtils.substringBefore(content, before);
		return new Script(script);
	}

	public Script scriptBetween(String open, String close) {
		String script = StringUtils.substringBetween(content, open, close);
		return new Script(script);
	}

	public Script replace(String target, String replacement) {
		return new Script(StringUtils.replace(content, target, replacement));
	}

	public Script replacePattern(String regex, String replacement) {
		return new Script(StringUtils.replacePattern(content, regex, replacement));
	}

	public Script remove(String target) {
		return new Script(StringUtils.remove(content, target));
	}

	public String content() {
		return content;
	}

	public boolean contains(String s) {
		return content.contains(s);
	}

	public boolean startsWith(String s) {
		return content.startsWith(s);
	}

	public boolean endsWith(String s) {
		return content.endsWith(s);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		if (o.getClass().equals(String.class)) {
			return Objects.equals(this.content, o);
		} else {
			Script script = (Script) o;
			return Objects.equals(this.content, script.content);
		}
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(content);
	}

	public boolean isNotEmpty()
	{
		return !content.isEmpty();
	}

	public boolean isEmpty()
	{
		return content.isEmpty();
	}
}
