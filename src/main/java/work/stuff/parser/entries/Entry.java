package main.java.work.stuff.parser.entries;

import net.minidev.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author dpozinen
 */
@SuppressWarnings("unused")
public final class Entry
//		implements Iterable<Map.Entry<OfferKey, Object>>
{

	private static Map<String, OfferKey> customKeyStorage = new HashMap<>();
	private Map<OfferKey, String> stringStorage;
	private Map<OfferKey, Integer> intStorage;
	private Map<OfferKey, BigDecimal> decimalStorage;
	private Map<OfferKey, Class<?>> classStore;
	private boolean store;
	private final IResultEntry entry;
	private final String entryId;
	private final boolean isIdBlank;

	private Entry(IResultEntrySet results, String entryId, UriWrapper debugLink) {
		String id;
		if (entryId == null || entryId.isEmpty()) {
			logEmptyId(debugLink);
			id = String.valueOf(ThreadLocalRandom.current().nextInt());
		} else id = entryId;
		this.isIdBlank = entryId == null || entryId.isEmpty();
		this.entry = results.createResultEntry(id);
		this.entryId = id;
	}

	public static Entry simpleEntry(IResultEntrySet results, String id, UriWrapper debugLink) {
		return new Entry(results, id, debugLink).articleId(id);
	}

	public static Entry simpleEntry(IResultEntrySet results, String id) {
		return new Entry(results, id, null).articleId(id);
	}

	public static Entry createAdded(IResultEntrySet results, String id) {
		return new Entry(results, id, null).articleId(id).save(results);
	}

	public static Entry createAdded(IResultEntrySet results, String id, UriWrapper uri) {
		return new Entry(results, id, uri).articleId(id).save(results);
	}

	public static Entry copyOf(IResultEntrySet results, String id, Entry other) {
		Entry entry = simpleEntry(results, id);
		if (other.store) {
			entry.store = true;
			other.stringStorage.remove(OfferKey.ArticleId);
			entry.stringStorage = new HashMap<>(other.stringStorage);
			entry.decimalStorage = new HashMap<>(other.decimalStorage);
			entry.intStorage = new HashMap<>(other.intStorage);
			entry.store(OfferKey.ArticleId, id);
			addAllToEntry(entry, other);
		}
		return entry;
	}

	private static void addAllToEntry(Entry target, Entry other) {
		for (Map.Entry<OfferKey, String> e : other.stringStorage.entrySet()) target.entry.addEntry(e.getKey(), e.getValue());
		for (Map.Entry<OfferKey, Integer> e : other.intStorage.entrySet()) target.entry.addEntry(e.getKey(), e.getValue());
		for (Map.Entry<OfferKey, BigDecimal> e : other.decimalStorage.entrySet()) target.entry.addEntry(e.getKey(), e.getValue());
	}

	public Entry save(IResultEntrySet results) {
		if (!isIdBlank) results.add(entry);
		return this;
	}

	public Entry enableStorage() {
		store = true;
		stringStorage = new HashMap<>(); intStorage = new HashMap<>(); decimalStorage = new HashMap<>();
		return store(OfferKey.ArticleId, entryId);
	}

	public Entry disableStorage() {
		store = false;
		stringStorage.clear(); intStorage.clear(); decimalStorage.clear();
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

	public Entry currency(String currency) {
		entry.addEntry(OfferKey.Currency, currency);
		return store(OfferKey.Currency, currency);
	}

	public Entry currency(IQuery query) {
		String currency = ParserUtil.getCurrency(query);
		entry.addEntry(OfferKey.Currency, currency);
		return store(OfferKey.Currency, currency);
	}

	public Entry rating(BigDecimal rating) {
		if (rating != null && rating.compareTo(BigDecimal.ZERO) != 0)
			entry.addEntry(OfferKey.Rating, rating.setScale(1, RoundingMode.HALF_UP));
		return store(OfferKey.Rating, rating);
	}

	public Entry ratingCount(BigDecimal count) {
		if (count != null && count.intValue() > 0)
			entry.addEntry(OfferKey.RatingCount, count.intValue());
		return store(OfferKey.RatingCount, count);
	}

	// custom makers

	public Entry add(OfferKey key, String value) {
		entry.addEntry(key, value);
		return store(key, value);
	}

	public Entry add(String key, String value) {
		OfferKey k = customKeyStorage.containsKey(key) ? customKeyStorage.get(key) : createAndSaveKey(key, false);
		entry.addEntry(k, value);
		return store(k, value);
	}

	public Entry addNormalized(String key, String value) { // todo: normalize text
		OfferKey k = customKeyStorage.containsKey(key) ? customKeyStorage.get(key) : createAndSaveKey(key, false);
		entry.addEntry(k, value);
		return store(k, value);
	}

	public Entry addNormalized(OfferKey key, String value) { // todo: normalize text
		entry.addEntry(key, value);
		return store(key, value);
	}

	public Entry addTableKey(String key, String value) { // todo: normalize key & text
		OfferKey k = customKeyStorage.containsKey(key) ? customKeyStorage.get(key) : createAndSaveKey(key, true);
		entry.addEntry(k, value);
		return store(k, value);
	}

	private OfferKey createAndSaveKey(String key, boolean isTableKey) {
		String finalKey = isTableKey ? "TD_" + key : key;
		OfferKey offerKey = ParserUtil.createCleanOfferKeyName(finalKey);
		customKeyStorage.put(key, offerKey);
		return offerKey;
	}

	@Override public String toString() {
		JSONObject o = new JSONObject();
		if (store) {
			o.putAll(makeKeysString(intStorage));
			o.putAll(makeKeysString(decimalStorage));
			o.putAll(makeKeysString(stringStorage));
		} else o.put("EntryId", entryId);
		return o.toJSONString();
	}

	private Map<String, ?> makeKeysString(Map<?, ?> map) {
		Map<String, Object> ret = new HashMap<>();
		for (Map.Entry<?, ?> e : map.entrySet())
			ret.put(String.valueOf(e.getKey()), e.getValue());
		return ret;
	}

	// privates

	private Entry store(OfferKey key, String id) {
		if (store) stringStorage.put(key, id);
		return this;
	}

	private Entry store(OfferKey key, BigDecimal id) {
		if (store) decimalStorage.put(key, id);
		return this;
	}

	private Entry store(OfferKey key, Integer id) {
		if (store) intStorage.put(key, id);
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T> T getOther(OfferKey key) {
		return (T) stringStorage.get(key);
	}

	public Integer getInt(OfferKey key) {
		return intStorage.get(key);
	}

	public String getString(OfferKey key) {
		return stringStorage.get(key);
	}

	public BigDecimal getDecimal(OfferKey key) {
		return decimalStorage.get(key);
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
