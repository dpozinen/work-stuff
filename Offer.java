package blackbee.swarm.parsinghelper;

import blackbee.common.crawling.data.OfferKey;
import blackbee.swarm.core.swarm.IQuery;
import blackbee.swarm.core.swarm.resultmodel.IResultEntry;
import blackbee.swarm.core.swarm.resultmodel.IResultEntrySet;
import blackbee.swarm.util.JsonPathWrapper;
import com.jayway.jsonpath.DocumentContext;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author dpozinen
 */

public final class Offer implements Comparable<Offer>, Serializable
{
	private static final long                serialVersionUID = -8874043410881127494L;

	/**
	 * A storage for all the newly created offerKeys and their initial string representations.
	 * If a provided key string in one of {@link #add(String, String)} or {@link #addNormalized(String, String)} or {@link #addTableKey} is present in this map,
	 * the value found by that string is used and no cleaning or creating is performed.
	 * This optimization is important for the Electrolux parsers, where table info is usually extracted as new offerKeys,
	 * but that data is mostly repetitive
	 */
	private static OfferKeyStore     		 customKeyStorage = new OfferKeyStore();
	private        Map<OfferKey, Object>     objectStorage;
	private        Map<OfferKey, String>     stringStorage;
	private        Map<OfferKey, Integer>    intStorage;
	private        Map<OfferKey, BigDecimal> decimalStorage;
	/**
	 * Flag indicating if storage functionality is on or off
	 */
	private        boolean                   store;
	private final  IResultEntry              entry;
	private final  String                    entryId;
	/**
	 * A flag that indicates whether the entryId provided to the constructor was blank.
	 * Is used to determine if the entry should be added to results or not.
	 * If it is set to true, this means that the entry id was generated by the class itself and was used as a placeholder for the blank id.
	 * Entries like these should not be added to the {@link IResultEntrySet} since they are not valid.
	 * This behaviour provides the option for logging and removes blank checks before creating an entry.
	 */
	private final  boolean                   isIdBlank;

	/**
	 * Always creates an instance of {@link IResultEntry}, but sets the {@link #isIdBlank} to true if the provided id is empty.
	 * Logs the debug link to {@link System#out} if the id was blank.
	 *
	 * @param results the {@link IResultEntrySet} to create an entry from
	 * @param entryId the entry id that will be used for creation
	 * @param debugLink a link that is going to be provided in a log message if the {@param id} is empty
	 * @see #isIdBlank
	 */
	private Offer(IResultEntrySet results, String entryId, UriWrapper debugLink) {
		this(results, entryId, debugLink, ParserUtil.RESULT_DATA_NODE_PATH);
	}

	private Offer(IResultEntrySet results, String entryId, UriWrapper debugLink, String collection) {
		String id;
		if (StringUtils.isEmpty(entryId)) {
			id = String.valueOf(ThreadLocalRandom.current().nextInt());
			logEmptyId(debugLink);
		} else id = entryId;
		this.isIdBlank = entryId == null || entryId.isEmpty();
		this.entry = results.createResultEntry(id, collection);
		this.entryId = id;
	}

	/**
	 * Creates an instance of {@link Offer}
	 * Static factory method with a link to be debug added to log in case the provided id is empty.
	 *
	 * @param id id to be used as EntryId and ArticleId
	 * @param debugLink link that is going to be provided in a log message if the {@code id} is empty
	 * @return a new instance of entry with ArticleId already set to it
	 */
	public static Offer simpleEntry(IResultEntrySet results, String id, UriWrapper debugLink) {
		return new Offer(results, id, debugLink).articleId(id);
	}

	/**
	 * Same as {@link #simpleEntry(IResultEntrySet, String, UriWrapper)} but without debugLink
	 */
	public static Offer simpleEntry(IResultEntrySet results, String id) {
		return new Offer(results, id, null).articleId(id);
	}

	/**
	 * Same as {@link #createAdded(IResultEntrySet, String, UriWrapper)} but without debugLink
	 */
	public static Offer createAdded(IResultEntrySet results, String id) {
		return new Offer(results, id, null).articleId(id).save(results);
	}

	/**
	 * Same as {@link #simpleEntry(IResultEntrySet, String, UriWrapper)} plus adds the created entry to the results immediately
	 */
	public static Offer createAdded(IResultEntrySet results, String id, UriWrapper debugLink) {
		return new Offer(results, id, debugLink).articleId(id).save(results);
	}

	/**
	 * Creates an entry in the specified collection, which is instantly added. Does not set ArticleId unlike other factory methods.
	 * @param id entry id
	 * @param collection the target collection where the entry should be saved
	 * @return an entry created in the specified collection
	 */
	public static Offer addedCollectionEntry(IResultEntrySet results, String id, UriWrapper debugLink, String collection) {
		return new Offer(results, id, debugLink, collection).save(results);
	}

	/**
	 * Creates an entry in the specified collection, which is NOT added.
	 * @see #addedCollectionEntry
	 */
	public static Offer simpleCollectionEntry(IResultEntrySet results, String id, UriWrapper debugLink, String collection) {
		return new Offer(results, id, debugLink, collection);
	}

	/**
	 * Creates a copy of {@code other}. Important to note that the newly created copy will be empty, if {@link #store} is set to false.
	 * In other words {@link #enableStorage()} should have been called on {@code other} for this copy to be filled, because the content is being taken
	 * from the storage maps and not from {@link #entry}. <b> Storage will be turned off for the new entry. <b/>
	 * @param other the entry that the data will be copied from
	 * @return a copy of {@code other}
	 */
	public static Offer copyOf(IResultEntrySet results, String id, Offer other) {
		Offer entry = simpleEntry(results, id);
		if (other.store) {
			entry.store = false;
			other.stringStorage.remove(OfferKey.ArticleId);
			entry.stringStorage = new HashMap<>(other.stringStorage);
			entry.decimalStorage = new HashMap<>(other.decimalStorage);
			entry.intStorage = new HashMap<>(other.intStorage);
			entry.objectStorage = new HashMap<>(other.objectStorage);
			entry.store(OfferKey.ArticleId, id);
			addAllToEntry(entry, other);
		}
		return entry;
	}

	/**
	 * Adds the {@link #entry} to the provided {@link IResultEntrySet}
	 */
	public Offer save(IResultEntrySet results) {
		if (!isIdBlank) results.add(entry);
		return this;
	}

	/**
	 * Enables the storage mechanism provided by the class.
	 * This will save all the added data into {@link Offer} as well as the {@link IResultEntry}.
	 * Storage should only be used for when variant extraction requires a container for common data or for debug purposes.
	 * If you are not planning to call {@link #copyOf(IResultEntrySet, String, Offer)} storage should be off and this method should not be called
	 */
	public Offer enableStorage() {
		if (!store) {
			store = true;
			if (stringStorage == null) { // copies of other will have storage filled
				stringStorage = new HashMap<>(); intStorage = new HashMap<>();
				decimalStorage = new HashMap<>(); objectStorage = new HashMap<>();
			}
			return store(OfferKey.ArticleId, entryId);
		}
		return this;
	}

	/**
	 * Disables the storage. Made public just in case this functionality is ever needed.
	 * Gives an option to clear the stored data.
	 * @param clear flag to indicate if clearing the data is needed
	 * @see #clearStorage()
	 */
	public Offer disableStorage(boolean clear) {
		if (store) {
			store = false;
			return clear ? clearStorage() : this;
		}
		return this;
	}

	/**
	 * Clears the stored values in {@link Offer}. Made public just in case this functionality is ever needed.
	 */
	public Offer clearStorage() {
		if (stringStorage != null) { // if one isn't null -> all aren't
			stringStorage.clear(); intStorage.clear();
			decimalStorage.clear(); objectStorage.clear();
		}
		return this;
	}

	/**
	 * Gives direct access to the underlying {@link IResultEntry} in case it is ever needed.
	 * @return the inner {@link IResultEntry} at the core of the object
	 */
	public IResultEntry entry() {
		return entry;
	}

	// makers

	public Offer articleId(String id) {
		return add(OfferKey.ArticleId, id);
	}

	public Offer source(String source) {
		return add(OfferKey.Source, source);
	}

	public Offer shopName(String shopName) {
		return add(OfferKey.ShopName, shopName);
	}

	public Offer imageUrl(String url) {
		return add(OfferKey.ImageUrl, url);
	}

	public Offer color(String color) {
		return add(OfferKey.Color, color);
	}

	public Offer colorCode(String code) {
		return add(OfferKey.ColorCode, code);
	}

	public Offer deliveryTime(String time) {
		return add(OfferKeyConstants.DELIVERY_TIME, time);
	}

	public Offer mpn(String mpn) {
		return add(OfferKey.MPN, mpn);
	}

	public Offer amountOfProducts(Integer number) {
		return add("AmountOfProducts", number);
	}

	public Offer description(String description) {
		return addNormalized(OfferKey.Description, description);
	}

	public Offer shortDescription(String description) {
		return addNormalized("ShortDescription", description);
	}

	public Offer imageCount(int count) {
		return add("NumberOfImages", count);
	}

	public Offer videoCount(int count) {
		return add("NumberOfVideos", count);
	}

	public Offer stock(String stock) {
		return add(OfferKey.Stock, stock);
	}

	public Offer stock(boolean inStock) {
		String stock = inStock ? StockConstants.IN_STOCK : StockConstants.OUT_OF_STOCK;
		return add(OfferKey.Stock, stock);
	}

	public Offer brand(String brand) {
		return add(OfferKey.Brand, brand);
	}

	public Offer shopUrl(UriWrapper uri) {
		String url = String.valueOf(uri);
		return add(OfferKey.ShopUrl, url);
	}

	public Offer shopUrl(String url) {
		return add(OfferKey.ShopUrl, url);
	}

	public Offer collection(String collection) {
		return add(OfferKeyConstants.COLLECTION, collection);
	}

	public Offer productName(String productName) {
		return add(OfferKey.ProductName, productName);
	}

	public Offer currency(String currency) {
		return add(OfferKey.Currency, currency);
	}

	public Offer currency(IQuery query) {
		String currency = ParserUtil.getCurrency(query);
		return add(OfferKey.Currency, currency);
	}

	public Offer breadCrumbs(String crumbs) {
		return add(OfferKeyConstants.BREADCRUMBS, crumbs);
	}

	public Offer ean(String ean) {
		return add(OfferKey.EAN, ean);
	}

	public Offer breadCrumbs(Collection<String> crumbs) {
		String joined = StringUtils.join(crumbs, OfferKeyConstants.BREADCRUMBS_DELIMITER);
		return add(OfferKeyConstants.BREADCRUMBS, joined);
	}

	public Offer brandIndicator(Collection<String> brands, String brand) {
		if (StringUtils.isNotBlank(brand) && ParserUtil.isNotEmpty(brands)) {
			for (String b : brands)
				if (ParserUtil.equalsIgnoreCaseAndSpaces(b, brand))
					return add(OfferKeyConstants.BRAND_INDICATOR, true);
			return add(OfferKeyConstants.BRAND_INDICATOR, false);
		}
		return add(OfferKeyConstants.BRAND_INDICATOR, (Object) null);
	}

	public <T extends RankingHelper<T>> Offer rankAndOverview(RankingHelper<T> helper) {
		if (helper.isRankingMode) {
			if (helper.getRank() != null)
				add(OfferKeyConstants.RANK, helper.getRank());
			add(OfferKeyConstants.OVERVIEW_URL, helper.getOverviewUrl());
		}
		return this;
	}

	public Offer shipping(String shipping) {
		return add(OfferKey.Shipping, shipping);
	}

	// decimals

	public Offer shipping(BigDecimal shippingNo) {
		String v = shippingNo == null ? null : shippingNo.toString();
		return add(OfferKey.Shipping, v);
	}

	public Offer shippingNo(BigDecimal shippingNo) {
		return add(OfferKey.ShippingNo, shippingNo);
	}

	public Offer shippingNo(BigDecimal price, BigDecimal freeSince, BigDecimal orElse) {
		if (ParserUtil.isNoneNull(price, freeSince, orElse)) {
			if (price.compareTo(freeSince) < 0)
				return shippingNo(orElse);
			else
				return shippingNo(BigDecimal.ZERO);
		}
		return this;
	}

	public Offer shippingAndNo(BigDecimal price, BigDecimal freeSince, BigDecimal orElse) {
		if (ParserUtil.isNoneNull(price, freeSince, orElse)) {
			if (price.compareTo(freeSince) < 0)
				return shipping(orElse).shippingNo(orElse);
			else
				return shipping(BigDecimal.ZERO).shippingNo(BigDecimal.ZERO);
		}
		return this;
	}

	public Offer price(BigDecimal price) {
		return add(OfferKey.Price, price);
	}

	public Offer initialPrice(BigDecimal initialPrice) {
		return add(OfferKey.InitialPrice, initialPrice);
	}

	public Offer  initialPrice(BigDecimal initialPrice, BigDecimal price) {
		if (initialPrice != null && price != null) {
			if (price.compareTo(initialPrice) != 0)
				add(OfferKey.InitialPrice, initialPrice);
		} else
			add(OfferKey.InitialPrice, initialPrice);
		return this;
	}

	public Offer netPrice(BigDecimal price) {
		return add(OfferKeyConstants.NET_PRICE, price);
	}

	public Offer netInitialPrice(BigDecimal initialPrice, BigDecimal netPrice) {
		OfferKey netInitialPrice = OfferKey.create("NetInitialPrice");
		if (initialPrice != null && netPrice != null) {
			if (netPrice.compareTo(initialPrice) != 0)
				add(netInitialPrice, initialPrice);
		} else
			add(netInitialPrice, initialPrice);
		return this;
	}

	public Offer rating(BigDecimal rating) {
		if (rating != null && rating.compareTo(BigDecimal.ZERO) != 0)
			add(OfferKey.Rating, rating.setScale(1, RoundingMode.HALF_UP));
		return this;
	}

	public Offer ratingCount(BigDecimal count) {
		if (count != null && count.intValue() > 0)
			add(OfferKey.RatingCount, count.intValue());
		return this;
	}

	// reviews

	public Offer ratingValue(BigDecimal rv) {
		return add("RatingValue", rv);
	}

	public Offer age(String age) {
		return add("Age", age);
	}

	public Offer location(String location) {
		return add("Location", location);
	}

	public Offer reviewContent(String content) {
		return addNormalized("ReviewContent", content);
	}

	public Offer reviewTitle(String title) {
		return addNormalized("ReviewTitle", title);
	}

	public Offer reviewDate(String date) {
		return add("ReviewDate", date);
	}

	public Offer reviewAuthor(String author) {
		return add("ReviewAuthor", author);
	}

	public Offer gender(String gender) {
		return add(OfferKey.Gender, gender);
	}

	public Offer recommended(Boolean val) {
		return add("Recommended", val);
	}

	public Offer verified(Boolean val) {
		return add("Verified", val);
	}

	public Offer usefulnessYes(BigDecimal count) {
		if (count != null)
			return add("UsefulnessYes", count.intValue());
		return this;
	}

	public Offer usefulnessNo(BigDecimal count) {
		if (count != null)
			return add("UsefulnessNo", count.intValue());
		return this;
	}

	// custom makers

	public Offer addElectrolux(ElectroluxHelper<?> helper, String brand) {
		return amountOfProducts(helper.getAmountOfProducts()).rankAndOverview(helper).brandIndicator(helper.getBrands(), brand);
	}

	/**
	 * Fills the offer with the map's contents
	 * @param map any map that has String or OfferKey as Keys
	 * @return this offer with all elements of the map added to it
	 */
	public Offer addAll(Map<?, ?> map) {
		return addAll(map, false);
	}

	/**
	 * Fills the offer with the map's contents
	 * @param map any map that has String or OfferKey as Keys
	 * @return this offer with all elements of the map added to it
	 */
	public Offer addAll(Map<?, ?> map, boolean isTableKey) {
		for (Map.Entry<?, ?> e : map.entrySet()) {
			Object value = e.getValue(); Object key = e.getKey();
			OfferKey k;

			if (isKeyString(key))
				k = customKeyStorage.get((String) key, isTableKey);
			else
				k = (OfferKey) key;

			if (value instanceof String) add(k, (String) value);
			else if (value instanceof Integer) add(k, (Integer) value);
			else if (value instanceof BigDecimal) add(k, (BigDecimal) value);
			else add(k, value);
		}
		return this;
	}

	/**
	 * Added for unusual values. Storage is not supported for this type.
	 */
	public Offer add(OfferKey key, Object value) {
		ResultEntryHelper.addValue(entry, key, value);
		return store(key, value);
	}

	public Offer add(String key, Object value) {
		OfferKey k = customKeyStorage.get(key);
		ResultEntryHelper.addValue(entry, k, value);
		return store(k, value);
	}

	public Offer add(OfferKey key, String value) {
		entry.addValue(key, value);
		return store(key, value);
	}

	public Offer add(OfferKey key, Integer value) {
		if (value != null) {
			entry.addValue(key, value);
			return store(key, value);
		}
		return this;
	}

	public Offer add(OfferKey key, BigDecimal value) {
		entry.addValue(key, value);
		return store(key, value);
	}

	public Offer add(String key, BigDecimal value) {
		OfferKey k = customKeyStorage.get(key);
		entry.addValue(k, value);
		return store(k, value);
	}

	/**
	 * Adds the provided key and value, creating an {@link OfferKey} out of the key.
	 * Stores the created key in the {@link #customKeyStorage} for use if it is going to be passed again
	 */
	public Offer add(String key, String value) {
		OfferKey k = customKeyStorage.get(key);
		entry.addValue(k, value);
		return store(k, value);
	}

	/**
	 * @see #add(String, String)
	 */
	public Offer add(String key, Integer value) {
		OfferKey k = customKeyStorage.get(key);
		if (value != null) {
			entry.addValue(k, value);
			return store(k, value);
		}
		return this;
	}

	/**
	 * Same as {@link #add(String, String)} but the value will be normalized
	 */
	public Offer addNormalized(String key, String value) {
		OfferKey k = customKeyStorage.get(key);
		String normValue = BasicParsingHelper.normalizeText(value);
		entry.addValue(k, normValue);
		return store(k, normValue);
	}

	/**
	 * @see #addNormalized(String, String)
	 */
	public Offer addNormalized(OfferKey key, String value) {
		String normValue = BasicParsingHelper.normalizeText(value);
		entry.addValue(key, normValue);
		return store(key, normValue);
	}

	/**
	 * Creates a key that will be of the format for table data extraction, i.e. TABLE HEAD -> TD_TableHead
	 * @see #add(String, String)
	 */
	public Offer addTableKey(String key, String value) {
		OfferKey k = customKeyStorage.get(key);
		return addNormalized(k, value);
	}

	// #removes

	/**
	 * Creates a copy of this offer, after removing the specified OfferKey.
	 * This offer should not be added to results before removing, so use {@link #simpleEntry}. Storage should be enabled.
	 * <br><br>
	 * Please note that the return of this method should be assigned to a variable, simply calling<br>
	 * {@code someOffer.remove("Price", results);} <br> will have no effect on {@code someOffer}.<br> Correct use would be <br>
	 * {@code someOffer = someOffer.remove("Price", results);} <br>
	 *
	 * @param key     the key to remove
	 * @param results the results, which will be used to create an instance of {@link IResultEntry}
	 * @return a new offer without the specified key, OR this offer if storage was off or the key was not found.
	 * Storage will be enabled on the returned offer.
	 * @see #remove(String, IResultEntrySet)
	 */
	public Offer remove(OfferKey key, IResultEntrySet results)
	{
		if (intStorage != null) return removeFromFirst(key, results);
		return this;
	}

	/**
	 *
	 * @see #remove(OfferKey, IResultEntrySet)
	 */
	public Offer remove(String key, IResultEntrySet results) {
		if (intStorage != null) return removeFromFirst(customKeyStorage.get(key), results);
		return this;
	}

	private Offer removeFromFirst(OfferKey key, IResultEntrySet results) {
		Offer tempCopy = copyOf(results, entryId, this).enableStorage();
		if (tempCopy.intStorage.remove(key) != null)
			return copyOf(results, entryId, tempCopy).enableStorage();
		if (tempCopy.stringStorage.remove(key) != null)
			return copyOf(results, entryId, tempCopy).enableStorage();
		if (tempCopy.decimalStorage.remove(key) != null)
			return copyOf(results, entryId, tempCopy).enableStorage();
		if (tempCopy.objectStorage.remove(key) != null)
			return copyOf(results, entryId, tempCopy).enableStorage();
		return this;
	}

	// getters

	public <V> V getOther(OfferKey key) {
		return objectStorage != null ? (V) objectStorage.get(key) : null;
	}

	public <V> V getOther(String key) {
		OfferKey k = customKeyStorage.get(key);
		return objectStorage != null ? (V) objectStorage.get(k) : null;
	}

	public Integer getInt(OfferKey key) {
		return intStorage != null ? intStorage.get(key) : null;
	}

	public String getString(OfferKey key) {
		return intStorage != null ? stringStorage.get(key) : "";
	}

	public BigDecimal getDecimal(OfferKey key) {
		return intStorage != null ? decimalStorage.get(key) : null;
	}

	// Overrides

	@Override public int hashCode() {
		return entryId.hashCode();
	}

	@Override public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Offer entry = (Offer) o;
		return entryId.equals(entry.entryId);
	}

	/**
	 * uses the {@link #entryId} for comparing.
	 * @param o entry for comparing
	 * @throws NullPointerException if the provided object is null
	 */
	@Override public int compareTo(Offer o) {
		return o.entryId.compareTo(entryId);
	}

	/**
	 * Provides a json view for the {@link Offer}. Will return only the entry id if {@link #store} is false
	 * @return a jsonString of the current entry
	 */
	@Override public String toString() {
		if (store) {
			DocumentContext json = JsonPathWrapper.parse(new HashMap<>(intStorage))
				.put("$", "decimals", decimalStorage)
				.put("$", "strings", stringStorage)
				.put("$", "other", objectStorage);
			return json.jsonString();
		} else {
			JSONObject o = new JSONObject();
			o.put("EntryId", entryId);
			return o.toJSONString();
		}
	}

	// privates

	private boolean isKeyString(Object key) {
		if (key instanceof String)
			return true;
		else if (key instanceof OfferKey)
			return false;
		else
			throw new IllegalArgumentException("Key can either be String or OfferKey");
	}

	private Offer store(OfferKey key, String id) {
		if (store) stringStorage.put(key, id);
		return this;
	}

	private Offer store(OfferKey key, BigDecimal id) {
		if (store) decimalStorage.put(key, id);
		return this;
	}

	private Offer store(OfferKey key, Integer id) {
		if (store) intStorage.put(key, id);
		return this;
	}

	private Offer store(OfferKey key, Object id) {
		if (store) objectStorage.put(key, id);
		return this;
	}

	private static void addAllToEntry(Offer target, Offer other) {
		for (Map.Entry<OfferKey, String> e : other.stringStorage.entrySet()) target.entry.addValue(e.getKey(), e.getValue());
		for (Map.Entry<OfferKey, Integer> e : other.intStorage.entrySet()) ResultEntryHelper.addValue(target.entry, e.getKey(), e.getValue());
		for (Map.Entry<OfferKey, BigDecimal> e : other.decimalStorage.entrySet()) target.entry.addValue(e.getKey(), e.getValue());
		for (Map.Entry<OfferKey, Object> e : other.objectStorage.entrySet()) ResultEntryHelper.addValue(target.entry, e.getKey(), e.getValue());
	}

	/**
	 * Prints instances when the provided entry id for construction was empty
	 * @param debugLink a link at which the entry was created
	 */
	private static void logEmptyId(UriWrapper debugLink) {
		if (debugLink == null)
			System.out.println("LOG: entry was not created due to empty id. No further info available");
		else
			System.out.printf("LOG: entry was not created due to empty id. Found on page: %s", debugLink);
	}

	private static class OfferKeyStore {
		private final Map<Key, OfferKey> store = new HashMap<>();

		OfferKey get(String key) {
			return get(key, false);
		}

		OfferKey get(String key, Boolean isTdKey) {
			Key k = new Key(key, isTdKey);
			return store.containsKey(k) ? store.get(k) : createAndSaveKey(k);
		}

		private OfferKey createAndSaveKey(Key k) {
			String cleanKey = k.key.replaceAll("[^\\p{L}_\\d]", " ");
			cleanKey = WordUtils.capitalize(cleanKey, ' ').replace(StringUtils.SPACE, StringUtils.EMPTY);

			String finalKey = k.isTdKey ? "TD_" + cleanKey : cleanKey;
			OfferKey offerKey = OfferKey.create(finalKey);

			store.put(k, offerKey);
			return offerKey;
		}

		private static class Key {
			private final Boolean isTdKey;
			private final String key;

			private Key(String key, boolean isTdKey) {
				this.isTdKey = isTdKey;
				this.key = key;
			}

			@Override
			public boolean equals(Object o) {
				if (this == o) return true;
				if (o == null || getClass() != o.getClass()) return false;
				Key key1 = (Key) o;
				return isTdKey == key1.isTdKey && Objects.equals(key, key1.key);
			}

			@Override
			public int hashCode() {
				return Objects.hash(isTdKey, key);
			}

			@Override
			public String toString()
			{
				return "Key{" + "isTdKey=" + isTdKey + ", key='" + key + '\'' + '}';
			}
		}
	}

}
