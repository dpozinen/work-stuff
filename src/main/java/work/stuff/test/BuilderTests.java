package main.java.work.stuff.test;

import main.java.work.stuff.HeaderBuilder;

class BuilderTests {

	public static void main(String[] args) {
		HeaderBuilder headerBuilder = new HeaderBuilder().add("accept", "whatever accept").referer("referererererer").setOrAdd("key", "value");
		log("added accept, referer and  key", headerBuilder);
		HeaderBuilder headerBuilder1 = new HeaderBuilder().add("sgdfaccept", "some accept").referer("a ref").setOrAdd("k", "v");
		log("added sgdfaccept, referer and k", headerBuilder1);

		headerBuilder.remove("sgdfaccept");
		log("removed sgdfaccept", headerBuilder1);
		headerBuilder1.remove("k");
		log("removed k", headerBuilder1);
	}

	private static void log(String msg, HeaderBuilder h) {
		System.out.printf("%s : %s%n", msg, h);

	}
}
