package blackbee.swarm.parsinghelper;

import blackbee.common.collections.generic.IKeyValuePair;
import blackbee.swarm.core.web.HttpHeader;
import blackbee.swarm.core.web.HttpHeaderKey;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dpozinen
 */
public final class HeaderBuilder {
	private final HttpHeader header;
	private static final Map<String, HttpHeaderKey> constantHeaders = new HashMap<String, HttpHeaderKey>(){{
		put(HttpHeaderKey.Cookie.toString().toLowerCase(), HttpHeaderKey.Cookie);
		put(HttpHeaderKey.Host.toString().toLowerCase(), HttpHeaderKey.Host);
		put(HttpHeaderKey.XRequestedWith.toString().toLowerCase(), HttpHeaderKey.XRequestedWith);
		put(HttpHeaderKey.Referer.toString().toLowerCase(), HttpHeaderKey.Referer);
		put(HttpHeaderKey.ContentType.toString().toLowerCase(), HttpHeaderKey.ContentType);
		put(HttpHeaderKey.Accept.toString().toLowerCase(), HttpHeaderKey.Accept);
		put(HttpHeaderKey.AcceptEncoding.toString().toLowerCase(), HttpHeaderKey.AcceptEncoding);
		put(HttpHeaderKey.AcceptLanguage.toString().toLowerCase(), HttpHeaderKey.AcceptLanguage);
		put(HttpHeaderKey.UserAgent.toString().toLowerCase(), HttpHeaderKey.UserAgent);
		put(HttpHeaderKey.Connection.toString().toLowerCase(), HttpHeaderKey.Connection);
		put(HttpHeaderKey.Origin.toString().toLowerCase(), HttpHeaderKey.Origin);
		put(HttpHeaderKey.CacheControl.toString().toLowerCase(), HttpHeaderKey.CacheControl);
	}};

	private static HttpHeaderKey get(String k) {
		k = k.toLowerCase();
		return constantHeaders.containsKey(k) ? constantHeaders.get(k) : new HttpHeaderKey(k);
	}

	public HeaderBuilder() {
		this.header = new HttpHeader();
	}

	public HeaderBuilder(HttpHeader h) {
		this.header = h;
	}

	public HeaderBuilder add(HttpHeaderKey k, String v) {
		header.add(k, v);
		return this;
	}

	public HeaderBuilder add(String k, String v) {
		return add(get(k), v);
	}

	public HeaderBuilder remove(HttpHeaderKey k) {
		if (header.contains(k))
			header.remove(k);
		return this;
	}

	public HeaderBuilder remove(String k) {
		return remove(get(k));
	}

	public HeaderBuilder set(HttpHeaderKey k, String v) {
		if (header.contains(k))
			header.set(k, v);
		return this;
	}

	public HeaderBuilder set(String k, String v) {
		return set(get(k), v);
	}

	public HeaderBuilder setOrAdd(HttpHeaderKey k, String v) {
		if (header.contains(k))
			header.set(k, v);
		else
			header.add(k, v);
		return this;
	}

	public HeaderBuilder setOrAdd(String k, String v) {
		return setOrAdd(get(k), v);
	}

	public HeaderBuilder userAgent(String v) {
		return setOrAdd(HttpHeaderKey.UserAgent, v);
	}

	public HeaderBuilder referer(String v) {
		return setOrAdd(HttpHeaderKey.Referer, v);
	}

	public HeaderBuilder cookie(String v) {
		return setOrAdd(HttpHeaderKey.Cookie, v);
	}

	public HttpHeader build() {
		return header;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for ( IKeyValuePair<HttpHeaderKey, String> kv : header )
			s.append(kv.getKey()).append(":").append(kv.getValue()).append("\n");
		return s.toString();
	}
}
