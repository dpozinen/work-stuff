package main.java.work.stuff.parser.entries; /* Dariy */

class OfferKey {

	public static OfferKey Price = new OfferKey("Price");
	public static OfferKey NetPrice = new OfferKey("NetPrice");
	public static OfferKey NetInitialPrice = new OfferKey("NetInitialPrice");
	public static OfferKey InitialPrice = new OfferKey("InitialPrice");
	public static OfferKey Description = new OfferKey("Description");
	final String k;

	public static OfferKey ShopName = new OfferKey("ShopName");
	public static OfferKey ImageUrl = new OfferKey("ImageUrl");
	public static OfferKey Color = new OfferKey("Color");
	public static OfferKey ColorCode = new OfferKey("ColorCode");
	public static OfferKey Stock = new OfferKey("Stock");
	public static OfferKey ShippinNo = new OfferKey("ShippinNo");
	public static OfferKey Shipping = new OfferKey("Shipping");
	public static OfferKey Collection = new OfferKey("Collection");
	public static OfferKey DeliveryTime = new OfferKey("DeliveryTime");
	public static OfferKey Rating = new OfferKey("Rating");
	public static OfferKey RatingCount = new OfferKey("RatingCount")  ;
	public static OfferKey Currency = new OfferKey("Currency");
	static OfferKey ArticleId = new OfferKey("ArticleId");
	public static OfferKey ShopUrl = new OfferKey("ShopUrl");

	public static OfferKey Source = new OfferKey("Source");

	OfferKey() {
		this.k = "k";
	}

	OfferKey(String k) {
		this.k = k;
	}

	public static OfferKey create(String key) {
		return ArticleId;
	}

	@Override public String toString() {
		return k;
	}

}
