# work-stuff
Some classes atop of the api provided to us by the framework
Aiming to improve easy of use, provide chaining and introduce a unified approach to all actions.

## Filter

Used to interact with the html dom in an efficient, stream-like way.

Getting a json from a script after a certain string
Before
```java
for (IHtmlElement element : document.filterByDataPath("***.script")) {
  String content = element.getContent(HtmlPrintFlags.Default);
  if (content.startsWith("var json = ")) {
    String json = StringUtils.substringAfter("var json = ");
    return JsonPath.parse(json);
  }
}
```

After 
```java
document.firstScript(Condition.scriptStartsWith("window.state.product")).scriptAfter("=").json();
```
Finding all elements by a path, choosing the one that contains Brand in it, filtering deeper in that element and taking it's attribute.

```java
for (IHtmlElement element : document.filterByDataPath(path)) {
  if (element.getAsText(true).contains("Brand")) {
    IHtmlElementFilter filtered = new HtmlElementFilter(element).filterByDataPath("***.div[class:'class']");
    if (!filtered.getIsEmpty()) {
      IHtmlNode.IHtmlNodeAttributeCollection attributes = filtered.get(0).getAttributes();
      if (attributes.containsKey("href")) {
        attributes.get("href");
      }
    }
  }
}

```
After
```java
document.filter(path)
        .first(Condition.textContains("Brand", true))
        .filter("***.div[class:'class']")
        .attribute("href");
```

## Offer
A wrapper/builder atop of IResultEntry that performs additional data validation and manipulations to change it to the desired form
(no of decimals, values, null values etc). Also to provide a unified approach for storing common attributes of product variants.
Before this Map<Key, Object> had to be used and values had to be cast upon insertion.

```java
for ( JsonContext variant : context )
{
  String articleId = String.format("%s_%s", baseOffer.getString(OfferKey.MPN), extractArticleId(variant));
  Offer.copyOf(results, articleId, baseOffer).price(extractPrice(variant)).save(results);
}
```

Before
```java
IResultEntry entry = results.createResultEntry(id);
entry.addValue(OfferKey.EAN, extractEan());
...
```

After
```java
Offer.createAdded(results, extractArticleId(), pageUri)
     .ean(extractEan())
     .productName(extractProductName())
     .brand(brand)
     .price(price);
```

## UriWrapper and others in the web package
Mostly for chaining and minor improvements, such as being almost exception free,
removing and matching segments, post body entries etc.

## Ranking and Electrolux helpers 
Created for reducing code dublication and are build with the simulated self type idiom to 
provide chaining between base and child classes
