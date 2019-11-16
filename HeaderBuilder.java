public final class HeaderBuilder {
    private HttpHeader header;

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
        header.add(new HttpHeaderKey(k), v);
        return this;
    }

    public HeaderBuilder remove(HttpHeaderKey k) {
        if (header.contains(k))
            header.remove(k, v);
        return this;
    }

    public HeaderBuilder set(HttpHeaderKey k, String v) {
        if (header.contains(k))
            header.set(k, v);
        return this;
    }

    public HttpHeader build() {
        return header;
    }

    public String toString() {
        return header.toString();
    }
}