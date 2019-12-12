package main.java.work.stuff.test;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.internal.JsonContext;
import main.java.work.stuff.JsonUtils;

import java.util.List;

class JsonUtilsTest {

	private static final JsonContext context = (JsonContext) JsonPath.parse("[ { \"glossary\": { \"title\": \"example glossary\", \"GlossDiv\": { \"title\": \"S\", \"GlossList\": { \"GlossEntry\": { \"ID\": \"SGML\", \"SortAs\": \"SGML\", \"GlossTerm\": \"Standard Generalized Markup Language\", \"Acronym\": \"SGML\", \"Abbrev\": \"ISO 8879:1986\", \"GlossDef\": { \"para\": \"\", \"GlossSeeAlso\": [ \"ABC\", \"DEF\" ] }, \"GlossSee\": \"markup\" } } } } }, { \"glossary\": { \"title\": \"not glossary\", \"GlossDiv\": { \"title\": \"S\", \"GlossList\": { \"GlossEntry\": { \"ID\": \"SGML1\", \"SortAs\": \"SGML\", \"GlossTerm\": \"Standard Generalized Markup Language\", \"Acronym\": \"SGML\", \"Abbrev\": \"ISO 8879:1986\", \"GlossDef\": { \"para\": \"A meta-markup language, used to create markup languages such as DocBook.\", \"GlossSeeAlso\": [ \"GML\", \"XML\" ] }, \"GlossSee\": \"gfghdf\" } } } } } ]");

	public static void main(String[] args) {
		{
			log(JsonUtils.createJsonList(context, "$").toString(), "created json list from root");
			log(JsonUtils.createJsonList(context, "$..GlossList").toString(), "created json list from deep scan for GlossList");
		}
		{
			List<?> para = context.read("$..[?]", List.class, JsonUtils.fieldIs("para", ""));
			log(para, "deep scan for json objects where <para> is <empty>");
			List<?> id = context.read("$..[?]", List.class, JsonUtils.fieldIs("ID", "SGML"));
			log(id, "deep scan for json objects where <ID> is <SGML>");
		}
		{
			List<?> para = context.read("$..[?]", List.class, JsonUtils.fieldIsIgnoreCase("Acronym", "sGml"));
			log(para, "deep scan for json objects where <Acronym> is <sGml>, ignore case");
			List<?> id = context.read("$..[?]", List.class, JsonUtils.fieldIsIgnoreCase("ID", "SGmL"));
			log(id, "deep scan for json objects where <ID> is <SGmL>, ignore case");
		}
		{
			List<?> para = context.read("$..[?]", List.class, JsonUtils.fieldIsIgnoreCaseAndSpaces("Acronym", " sG \t\tml "));
			log(para, "deep scan for json objects where <Acronym> is < sG \t\tml >, ignore case and whitespaces");
			List<?> id = context.read("$..[?]", List.class, JsonUtils.fieldIsIgnoreCaseAndSpaces("ID", " SG  \n\t\r m  L "));
			log(id, "deep scan for json objects where <ID> is <SGmL>, ignore case and whitespaces");
		}
		{
			List<?> para = context.read("$..[?]", List.class, JsonUtils.fieldContains("GlossSee", "gf"));
			log(para, "deep scan for json objects where <GlossSee> contains <gf>");
			List<?> id = context.read("$..[?]", List.class, JsonUtils.fieldContains("GlossSeeAlso", "GML"));
			log(id, "deep scan for json objects where <GlossSeeAlso> contains <GML>");
		}
		{
			List<?> para = context.read("$..[?]", List.class, JsonUtils.fieldContainsIgnoreCase("para", "LaNgUaGe"));
			log(para, "deep scan for json objects where <para> contains <LaNgUaGe>, ignore case");
			List<?> id = context.read("$..[?]", List.class, JsonUtils.fieldContainsIgnoreCase("GlossSeeAlso", "gml"));
			log(id, "deep scan for json objects where <GlossSeeAlso> contains <gml>, ignore case");
		}
		{
			log(String.valueOf(JsonUtils.contextIsEmpty(context)), "check if context is empty");
			log(String.valueOf(JsonUtils.contextIsEmpty((JsonContext) JsonPath.parse("{}"))), "check if {} is empty");
		}
		{
			log(String.valueOf(JsonUtils.contextContainsField(context, "lkjfdhg")), "check if context contains field <lkjfdhg>");
			log(String.valueOf(JsonUtils.contextContainsField(context, "GlossSee")), "check if context contains field <GlossSee>");
		}
	}

	private static void log(String json, String msg) {
		System.out.printf("%s : %s%n%n", msg, json);
		System.out.println("----------------------------");
	}

	private static void log(List<?> json, String msg) {
		System.out.printf("%s%n", msg);
		for (Object o : json) System.out.println(JsonPath.parse(o).jsonString());
		System.out.println("----------------------------");
	}

}
