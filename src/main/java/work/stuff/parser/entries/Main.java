package main.java.work.stuff.parser.entries; /* Dariy */

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.internal.JsonContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class Main {

	private static final JsonContext context = (JsonContext) JsonPath.parse("{ \"name\": \"some product name\", \"description\": \"this is a product\", \"currency\": \"CHF\", \"id\":  \"123_blue\", \"shipping\": \"Free shipping\", \"colors\": [ { \"id\": \"123_blue\", \"color\": \"blue\", \"availability\": \"In Stock\", \"price\": 123.45, \"initialPrice\" : 1244.78, \"rating\" : 0, \"ratingCount\" : null }, { \"id\": \"123_red\", \"color\": \"red\", \"availability\": \"Out of Stock\", \"price\": 123.45, \"initialPrice\" : 123.45, \"rating\" : 4.6, \"ratingCount\" : 28 }, { \"id\": \"123_yellow\", \"color\": \"yellow\", \"availability\": \"In Stock\", \"price\": 123.45, \"initialPrice\" : 1244.78, \"rating\" : 0, \"ratingCount\" : 0 }, { \"id\": \"123_green\", \"color\": \"green\", \"availability\": \"In Stock\", \"price\": 123.45, \"initialPrice\" : 1244.78, \"rating\" : 4.6, \"ratingCount\" : 23 } ] }");

	public static void main(String[] args) {
		IResultEntrySet results = new IResultEntrySet();

		String id = context.read("$.id");
		String description = context.read("$.description");
		String currency = context.read("$.currency");
		String shipping = context.read("$.shipping");
		Entry parent = Entry.simpleEntry(results, id).enableStorage().description(description).currency(currency).shipping(shipping);

		System.out.println("[");
		List<Map<?,?>> read = context.read("$.colors");
		read.forEach(c -> {
			JsonContext colorContext = (JsonContext) JsonPath.parse(c);
			String cid = colorContext.read("$.id");
			String color = colorContext.read("$.color");
			String availability = colorContext.read("$.availability");
			BigDecimal price = colorContext.read("$.price", BigDecimal.class);
			BigDecimal initialPrice = colorContext.read("$.initialPrice", BigDecimal.class);
			BigDecimal rating = colorContext.read("$.rating", BigDecimal.class);
			BigDecimal ratingCount = colorContext.read("$.ratingCount", BigDecimal.class);

			Entry entry = Entry.copyOf(results, cid, parent).color(color).stock(availability)
															.price(price).initialPrice(initialPrice)
															.rating(rating).ratingCount(ratingCount);
			System.out.println(entry + ",");
		});
		System.out.println("]\n");
	}

	void log(Entry entry, String msg) {

	}

}
