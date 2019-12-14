package main.java.work.stuff.parser.entries; /* Dariy */

public class Main {

	public static void main(String[] args) {
		Entry added = Entry.createAdded(new IResultEntrySet(), "34");
		String o = added.get(OfferKey.ArticleId);
	}
}
