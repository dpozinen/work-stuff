package main.java.work.stuff.parser.entries;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dpozinen
 */
@SuppressWarnings("unused")
public final class Entry
//		implements Iterable<Map.Entry<OfferKey, Object>>
{

	private static Map<String, OfferKey> customKeyStorage = new HashMap<>();
	private Map<OfferKey, Object> storage;
	private Map<OfferKey, Class<?>> classStore;
	private boolean store;
	private final IResultEntry entry;
	private final String articleId;

	private Entry(IResultEntrySet results, String entryId, UriWrapper debugLink) {
		if (entryId == null || entryId.isEmpty()) logEmptyId(debugLink);
		this.entry = results.createResultEntry(entryId);
		this.articleId = entryId;
	}

	public static Entry simpleEntry(IResultEntrySet results, String id, UriWrapper debugLink) {
		return new Entry(results, id, debugLink).articleId(id);
	}

	public static Entry simpleEntry(IResultEntrySet results, String id) {
		return new Entry(results, id, null).articleId(id);
	}

	public static Entry createAdded(IResultEntrySet results, String id) {
		Entry e = new Entry(results, id, null).articleId(id);
		results.add(e.entry);
		return e;
	}

	public static Entry createAdded(IResultEntrySet results, String id, UriWrapper uri) {
		Entry e = new Entry(results, id, uri).articleId(id);
		results.add(e.entry);
		return e;
	}

	private Entry enableStorage() {
		storage = new HashMap<>();
		storage.put(OfferKey.ArticleId, articleId);
		return this;
	}

	// makers

	public Entry articleId(String id) {
		entry.addEntry(OfferKey.ArticleId, id);
		return store(OfferKey.ArticleId, id);
	}

	public Entry source(String source) {
		entry.addEntry(OfferKey.Source, source);
		return store(OfferKey.Source, source);
	}

	public Entry shopName(String shopName) {
		entry.addEntry(OfferKey.ShopName, shopName);
		return store(OfferKey.ShopName, shopName);
	}

	public Entry imageUrl(String url) {
		entry.addEntry(OfferKey.ImageUrl, url);
		return store(OfferKey.ImageUrl, url);
	}

	public Entry color(String color) {
		entry.addEntry(OfferKey.Color, color);
		return store(OfferKey.Color, color);
	}

	public Entry colorCode(String code) {
		entry.addEntry(OfferKey.ColorCode, code);
		return store(OfferKey.ColorCode, code);
	}

	public Entry deliveryTime(String time) {
		entry.addEntry(OfferKey.DeliveryTime, time);
		return store(OfferKey.DeliveryTime, time);
	}

	public Entry stock(String stock) {
		entry.addEntry(OfferKey.Stock, stock);
		return store(OfferKey.Stock, stock);
	}

	public Entry shippingNo(String source) { // todo
		entry.addEntry(OfferKey.ShippinNo, source);
		return store(OfferKey.ShippinNo, source);
	}

	public Entry shipping(String source) { // todo
		entry.addEntry(OfferKey.Shipping, source);
		return store(OfferKey.Shipping, source);
	}

	public Entry shipping(BigDecimal shippingNo) {
		String v = shippingNo == null ? null : shippingNo.toString();
		entry.addEntry(OfferKey.Shipping, v);
		return store(OfferKey.Shipping, v);
	}

	public Entry shopUrl(UriWrapper uri) {
		String url = String.valueOf(uri);
		entry.addEntry(OfferKey.ShopUrl, url);
		return store(OfferKey.ShopUrl, url);
	}

	public Entry collection(String collection) {
		entry.addEntry(OfferKey.Collection, collection);
		return store(OfferKey.Collection, collection);
	}

	// custom makers

	public Entry add(OfferKey key, String value) {
		entry.addEntry(key, value);
		return store(key, value);
	}

	public Entry add(String key, String value) {
		OfferKey k = customKeyStorage.containsKey(key) ? customKeyStorage.get(key) : OfferKey.create(key);
		entry.addEntry(k, value);
		return store(k, value);
	}

	public Entry addNormalized(String key, String value) { // todo: normalize text
		OfferKey k = customKeyStorage.containsKey(key) ? customKeyStorage.get(key) : OfferKey.create(key);
		entry.addEntry(k, value);
		return store(k, value);
	}

	public Entry addNormalized(OfferKey key, String value) { // todo: normalize text
		entry.addEntry(key, value);
		return store(key, value);
	}

	public Entry addTableKey(String key, String value) { // todo: normalize key
		String offerKey = String.valueOf(key);
		OfferKey k = customKeyStorage.containsKey(offerKey) ? customKeyStorage.get(offerKey) : OfferKey.create(offerKey);
		entry.addEntry(k, value);
		return store(k, value);
	}

	// privates

	private Entry store(OfferKey key, String id) {
		if (store) storage.put(key, id);
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(OfferKey key) {
		return (T) storage.get(key);
	}

	private static void logEmptyId(UriWrapper debugLink) {
		if (debugLink == null)
			System.out.println("LOG: entry was not created due to empty id. No further info available");
		else
			System.out.printf("LOG: entry was not created due to empty id. Found on page: %s", debugLink);
	}

//	@Override public Iterator<Map.Entry<OfferKey, Object>> iterator() {
//		return storage.entrySet().iterator();
//	}

}
