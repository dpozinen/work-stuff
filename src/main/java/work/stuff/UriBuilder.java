package blackbee.swarm.parsinghelper;

import blackbee.common.collections.generic.KeyValuePair;
import blackbee.swarm.core.web.IUriSegment;
import blackbee.swarm.core.web.Uri;
import blackbee.swarm.core.web.UriPathSegment;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for {@link Uri} built on the fluent design pattern.
 * Introduces some additional features and safety measures that the {@link Uri} class lacks.
 * @author dpozinen
 */
public final class UriWrapper
{
	private Uri uri;

	public UriWrapper(String url) {
		//noinspection deprecation
		uri = new Uri(url);
	}

	public UriWrapper(Uri uri) {
		this.uri = new Uri(uri);
	}

	public UriWrapper(String url, String host) {
		uri = new Uri(url, host);
	}

	public UriWrapper add(String k, String v) {
		uri.addParameter(k, v);
		return this;
	}

	public UriWrapper add(String k, int v) {
		return add(k, String.valueOf(v));
	}

	public UriWrapper set(String k, String v) {
		if (containsKey(k))
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
			if ( segment.toString().replace("/", "").equals(s) )
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

	@Override
	public String toString() {
		return uri.toString();
	}

	private boolean uriParametersContains(String s, boolean comparingKeys) {
		for ( KeyValuePair<String, String> parameter : uri.getParameters() ) {
			if ( comparingKeys && parameter.getKey().equals(s) )
				return true;
			else if ( !comparingKeys && parameter.getValue().equals(s) )
				return true;
		}
		return false;
	}

	private boolean goodKV(String k, String v) {
		return StringUtils.isNotBlank(k) && v != null;
	}

	public UriWrapper removeSegment(String s) {
		return containsSegment(s) ? removeSegmentInternal(s) : this;
	}

	public UriWrapper removeSegmentsMatches(String regex) {
		List<String> toDelete = new ArrayList<>();
		for ( IUriSegment segment : uri.getUriPathSegments() )
			if ( segment.toString().matches(regex) )
				toDelete.add(segment.toString());

		for ( String d : toDelete )
			removeSegment(d);
		return this;
	}

	private UriWrapper removeSegmentInternal(String s) {
		uri.getUriPathSegments().remove(new UriPathSegment(s));
		return this;
	}

	public UriWrapper removeValueMatches(String regex) {
		return this;
	}

	public UriWrapper removeKeyMatches(String regex) {
		return this;
	}

	private UriWrapper removeParamMatches()
	{
//		for ( KeyValuePair<String, String> p : uri.getParameters() )
//			if ( p.getValue().equals(v) )
//				k = p.getKey();
//
//		if ( k != null )
//			uri.removeParameter(k);
		return this;
	}
}
