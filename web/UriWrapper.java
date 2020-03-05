package blackbee.swarm.parsinghelper;

import blackbee.common.collections.generic.CollectionFactory;
import blackbee.common.collections.generic.KeyValuePair;
import blackbee.swarm.core.swarm.WebRequestSettings;
import blackbee.swarm.core.web.HttpHeader;
import blackbee.swarm.core.web.IUriSegment;
import blackbee.swarm.core.web.PostContent;
import blackbee.swarm.core.web.Uri;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Wrapper for {@link Uri} built on the fluent design pattern.
 * Introduces some additional features and safety measures that the {@link Uri} class lacks.
 * @author dpozinen
 */
@SuppressWarnings("unused")
public final class UriWrapper implements Serializable
{

	public static final String SLASH = "/";

	private Uri uri;

	public UriWrapper(String url) {
		// noinspection deprecation
		uri = new Uri(url);
	}

	public UriWrapper(UriWrapper other) {
		uri = new Uri(other.uri);
	}

	public UriWrapper(Uri uri) {
		this.uri = new Uri(uri);
	}

	public UriWrapper(String url, String host) {
		uri = new Uri(url, host);
	}

	public UriWrapper add(String k, String v) {
		if (goodKV(k, v))
			uri.addParameter(k, v);
		return this;
	}

	public UriWrapper add(String k, int v) {
		return add(k, String.valueOf(v));
	}

	public UriWrapper set(String k, String v) {
		if (goodKV(k, v) && containsKey(k))
			uri.setParameter(k, v);
		return this;
	}

	public UriWrapper set(String k, int v) {
		return set(k, String.valueOf(v));
	}

	public UriWrapper setOrAdd(String k, String v) {
		if ( goodKV(k, v) ) {
			if ( containsKey(k) )
				uri.setParameter(k, v);
			else
				uri.addParameter(k, v);
		}
		return this;
	}

	public UriWrapper setOrAdd(String k, int v) {
		return setOrAdd(k, String.valueOf(v));
	}

	public String get(String k) {
		return uri.containsParameter(k) ? uri.getParameter(k) : StringUtils.EMPTY;
	}

	public UriWrapper removeKey(String k) {
		if (containsKey(k))
			uri.removeParameter(k);
		return this;
	}

	public UriWrapper removeValue(int v) {
		return removeValue(String.valueOf(v));
	}

	public UriWrapper removeValue(String v) {
		String k = null;
		if ( containsValue(v) )
			for ( KeyValuePair<String, String> p : uri.getParameters() )
				if ( p.getValue().equals(v) )
					k = p.getKey();

		if ( k != null )
			uri.removeParameter(k);
		return this;
	}

	public UriWrapper clearParams() {
		uri.clearParameters();
		return this;
	}

	public UriWrapper clearAll() {
		uri.removePathSegments(uri.getUriPathSegments());
		return clearParams();
	}

	public boolean containsSegment(String s) {
		for ( IUriSegment segment : uri.getUriPathSegments() )
			if ( segment.toString().replace(SLASH, "").equals(s) )
				return true;
		return false;
	}

	public boolean containsSegmentMatches(String regex) {
		for ( IUriSegment segment : uri.getUriPathSegments() )
			if ( segment.toString().replace(SLASH, "").matches(regex) )
				return true;
		return false;
	}

	public boolean containsKey(String k) {
		return uriParametersContains(k, true);
	}

	public boolean containsValue(String v) {
		return uriParametersContains(v, false);
	}

	public boolean keyIsValue(String k, String v) {
		return goodKV(k, v) && containsKey(k) && uri.getParameter(k).equals(v);
	}

	public UriWrapper https() {
		uri.setProtocol("https://");
		return this;
	}

	public UriWrapper http() {
		uri.setProtocol("http://");
		return this;
	}

	public UriWrapper encode() {
		uri = uri.setEncodeEnabled(true);
		return this;
	}

	public UriWrapper decode() {
		uri = uri.setEncodeEnabled(false);
		return this;
	}

	public Uri uri() {
		return uri;
	}

	public UriWrapper removeSegment(String s) {
		return removeSegmentInternal(s);
	}

	public UriWrapper removeSegmentsMatches(String regex) {
		List<IUriSegment> toDelete = new ArrayList<>();
		for ( IUriSegment segment : uri.getUriPathSegments() )
			if ( segment.toString().replace(SLASH, "").matches(regex) )
				toDelete.add(segment);

		for ( IUriSegment d : toDelete )
			removeSegmentInternal(d);
		return this;
	}

	public WebRequestSettings settings() {
		return new WebRequestSettings(uri);
	}

	public WebRequestSettings settings(HeaderBuilder h) {
		return settings(h.build());
	}

	public WebRequestSettings settings(HttpHeader h) {
		return new WebRequestSettings(uri).setRequestHeader(h);
	}

	public WebRequestSettings settings(PostContent c) {
		return new WebRequestSettings(uri).setPostContent(c);
	}

	public WebRequestSettings settings(PostWrapper c) {
		return settings(c.content());
	}

	public WebRequestSettings settings(PostWrapper c, HeaderBuilder h) {
		return settings(c.content(), h.build());
	}

	public WebRequestSettings settings(PostContent c, HttpHeader h) {
		return settings(c).setRequestHeader(h);
	}

	public UriWrapper removeValuesMatches(String regex) {
		return removeParamMatches(regex, false);
	}

	public UriWrapper removeKeysMatches(String regex) {
		return removeParamMatches(regex, true);
	}

	public UriWrapper addSegment(String segment) {
		String clearSegment = removeSlashesFromSegment(segment);
		UriWrapper copy = new UriWrapper(this).clearParams();

		String newUrl = copy + segment;
		UriWrapper ret = new UriWrapper(newUrl);

		for ( KeyValuePair<String, String> parameter : this.uri.getParameters() )
			ret.add(parameter.getKey(), parameter.getValue());

		this.uri = ret.uri;
		return this;
	}

	private String removeSlashesFromSegment(String segment) {
		return segment.startsWith(SLASH) ? StringUtils.removeStart(segment, SLASH) : StringUtils.removeEnd(segment, SLASH);
	}

	private UriWrapper removeParamMatches(String regex, boolean matchingKeys) {
		List<String> toDelete = new ArrayList<>();
		for ( KeyValuePair<String, String> parameter : uri.getParameters() )
			if ( matchingKeys && parameter.getKey().matches(regex) )
				toDelete.add(parameter.getKey());
			else if ( !matchingKeys && parameter.getValue().matches(regex) )
				toDelete.add(parameter.getKey());

		for ( String s : toDelete )
			uri.removeParameter(s);
		return this;
	}

	private boolean uriParametersContains(String s, boolean comparingKeys) {
		for ( KeyValuePair<String, String> parameter : uri.getParameters() )
			if ( comparingKeys && parameter.getKey().equals(s) )
				return true;
			else if ( !comparingKeys && parameter.getValue().equals(s) )
				return true;
		return false;
	}

	private UriWrapper removeSegmentInternal(String s) {
		IUriSegment del = null;
		for ( IUriSegment segment : uri.getUriPathSegments() )
			if ( segment.toString().replace("/", "").equals(s) ) {
				del = segment;
				break;
			}

		return del == null ? this : removeSegmentInternal(del);
	}

	private UriWrapper removeSegmentInternal(IUriSegment t) {
		uri.removePathSegments(CollectionFactory.createDefaultList(IUriSegment.class, new IUriSegment[] { t }));
		return this;
	}

	private boolean goodKV(String k, String v) {
		return StringUtils.isNotBlank(k) && v != null;
	}

	@Override public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UriWrapper that = (UriWrapper) o;
		return uri.equals(that.uri);
	}

	@Override public int hashCode()  {
		return Objects.hashCode(uri);
	}

	@Override public String toString() {
		return uri.toString();
	}

	private void lol() {
		{
			{;}
				 {;}
			{;}
		}
	}
	
	
}
