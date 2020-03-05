package blackbee.swarm.parsinghelper.filter;

import blackbee.swarm.parsinghelper.JsonUtils;
import blackbee.swarm.util.JsonPathWrapper;
import com.jayway.jsonpath.internal.JsonContext;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @author dpozinen
 */
public class Script {

	private String content;
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
		return JsonUtils.parse(json.read(path));
	}

	public Script scriptAfter(String after) {
		this.content = StringUtils.defaultString(StringUtils.substringAfter(content, after));
		return this;
	}

	public Script scriptBefore(String before) {
		this.content = StringUtils.defaultString(StringUtils.substringBefore(content, before));
		return this;
	}

	public Script scriptBeforeLast(String before) {
		this.content = StringUtils.defaultString(StringUtils.substringBeforeLast(content, before));
		return this;
	}

	public Script scriptBetween(String open, String close) {
		this.content = StringUtils.defaultString(StringUtils.substringBetween(content, open, close));
		return this;
	}

	public Script addStart(String add) {
		this.content = add + content;
		return this;
	}

	public Script addEnd(String add) {
		this.content = content + add;
		return this;
	}

	public Script unescapeJson() {
		this.content = StringEscapeUtils.unescapeJson(content);
		return this;
	}

	public Script replace(String target, String replacement) {
		this.content = StringUtils.replace(content, target, replacement);
		return this;
	}

	public Script replacePattern(String regex, String replacement) {
		this.content = StringUtils.replacePattern(this.content, regex, replacement);
		return this;
	}

	public Script remove(String target) {
		this.content = StringUtils.remove(content, target);
		return this;
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
