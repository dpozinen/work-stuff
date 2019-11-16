public final class PostBuilder {
    private PostContent content;
    private PostContentFormatter formatter;

    public PostBuilder() {
        this.content = new PostContent();
        this.formatter = content.getFormatter();
    }

    public PostBuilder(PostContent content) {
        this.content = content;
        this.formatter = content.getFormatter();
    }

    private PostBuilder(PostContentFormatter formatter) {
        this.content = new PostContent(formatter);
        this.formatter = formatter;
    }

    public static PostBuilder formattedBy(PostContentFormatter formatter) {
        return new PostBuilder(formatter);
    }

    public static PostBuilder singleEntry(boolean encode) {
        return new PostBuilder(new SingleEntryPostContentFormatter(encode));
    }

    public static PostBuilder fromMap(Map<?, ?> map) {
        // todo
    }

    public PostBuilder add(String k, String v) {
        content.addEntry(k, v);
        return this;
    }

    public PostBuilder set(String k, String v) {
        // todo
        return this;
    }

    public PostBuilder remove(String k) {
        // todo
        return this;
    }

    public PostContent build() {
        return content;
    }

    public String toString() {
        return content.toString();
    }

}