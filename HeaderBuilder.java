public final class HeaderWrapper {
    private HttpHeader header;

    public HeaderWrapper() {
        this.header = new HttpHeader();
    }

    public HeaderWrapper(HttpHeader h) {
        this.header = h;
    }

    public HeaderWrapper add(HttpHeaderKey k, String v) {
        header.add(k, v);
        return this;
    }

    public HeaderWrapper add(String k, String v) {
        header.add(new HttpHeaderKey(k), v);
        return this;
    }

    public HeaderWrapper remove(HttpHeaderKey k) {
        if (header.contains(k))
            header.remove(k, v);
        return this;
    }

    public HeaderWrapper set(HttpHeaderKey k, String v) {
        if (header.contains(k))
            header.set(k, v);
        return this;
    }

    public HeaderWrapper setOrAdd(HttpHeaderKey k, String v) {
        if (header.contains(k))
            header.set(k, v);
        else
            header.add(k, v);
        return this;
    }

    public HeaderWrapper referer(String v) {
        return setOrAdd(HttpHeaderKey.Referer, v);
    }

    public HttpHeader build() {
        return header;
    }

    public String toString() {
        return header.toString();
    }
}