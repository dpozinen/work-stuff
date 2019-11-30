import java.util.HashMap;
import java.util.Map;

public final class PostBuilder {
    private PostContent content;
    private PostContentFormatter formatter;
    private Map<String, String> parameters;

    public PostBuilder() {
        this.content = new PostContent();
        this.formatter = content.getFormatter();
        this.parameters = new HashMap<>();
    }

    public static PostBuilder copyOf(PostBuilder other) {
        PostBuilder b = new PostBuilder();
        b.content = other.content;
        b.formatter = other.formatter;
        b.parameters = new HashMap(other.parameters);
        return b;
    }

    private void copyHere(PostBuilder other) {
        this.content = other.content;
        this.formatter = other.formatter;
        this.parameters = new HashMap(other.parameters);
    }

    public PostBuilder(PostContent content) {
        this.content = content;
        this.formatter = content.getFormatter();
        this.parameters = new HashMap<>();
    }

    private PostBuilder(PostContentFormatter formatter) {
        this.content = new PostContent(formatter);
        this.formatter = formatter;
        this.parameters = new HashMap<>();
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

    public static PostBuilder fromMap(Map<?, ?> map, PostContentFormatter formatter) {
        // todo
    }

    public PostBuilder add(String k, String v) {
        content.addEntry(k, v);
        parameters.put(k, v);
        return this;
    }

    public PostBuilder set(String k, String v) {
        parameters.put(k, v);
        PostBuilder b = fromMap(parameters, this.formatter);
        copyHere(b);
        return this;
    }

    public PostBuilder remove(String k) {
        parameters.remove(k);
        PostBuilder b = fromMap(parameters, this.formatter);
        copyHere(b);
        return this;
    }

    public PostContent build() {
        return content;
    }

    public String toString() {
        return content.toString();
    }

}