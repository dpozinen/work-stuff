public final class UriWrapper {
    private Uri uri;

    public UriWrapper(String url) {
        this.uri = new Uri(url);
    }

    public UriWrapper(Uri uri) {
        this.uri = new Uri(uri);
    }

    public UriWrapper(String url, String host) {
        this.uri = new Uri(url, host);
    }

    public UriWrapper add(String k, String v) {
        this.uri.addParameter(k, v);
        return this;
    }

    public UriWrapper set(String k, String v) {
        if (this.containsKey(k))
            this.uri.setParameter(k, v);
        return this;
    }

    public UriWrapper remove(String k, String v) {
        // todo
        return this;
    }

    public UriWrapper clear() {
        this.uri.clearParameters();
        return this;
    }

    public UriWrapper containsSegment(String k) {
        // todo
        return this;
    }

    public UriWrapper containsKey(String k) {
        // todo
        return this;
    }

    public UriWrapper containsValue(String v) {
        // todo
        return this;
    }

    public UriWrapper https() {
        this.uri.setProtocol("https");
        return this;
    }

    public UriWrapper http() {
        this.uri.setProtocol("http");
        return this;
    }

    public UriWrapper encode() {
        this.uri = this.uri.setEncodingEnabled(true);
        return this;
    }

    public UriWrapper decode() {
        this.uri = this.uri.setEncodingEnabled(false);
        return this;
    }

    public Uri uri() {
        return this.uri;
    }

    public String toString() {
        return this.uri.toString();
    }
}