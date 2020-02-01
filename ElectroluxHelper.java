package blackbee.swarm.parsinghelper;

import blackbee.swarm.core.swarm.IQuery;

import java.io.Serializable;
import java.util.Collection;

/**
 * Abstract helper for most Electrolux needs
 * @author dpozinen
 */
public abstract class ElectroluxHelper<T extends ElectroluxHelper<T>> extends RankingHelper<T> implements Serializable {
	private static final long serialVersionUID = -4601000710860308750L;

	private String articleId;
	private Collection<String> brands;
	private Integer amountOfProducts;
	private String shopUrl;

	public ElectroluxHelper(IQuery query) {
		super(query);
	}

	public ElectroluxHelper(RankingHelper<T> other) {
		super(other);
		ElectroluxHelper<T> electroluxHelper = (ElectroluxHelper<T>) other;
		this.articleId = electroluxHelper.articleId;
		this.brands = electroluxHelper.brands;
		this.amountOfProducts = electroluxHelper.amountOfProducts;
		this.shopUrl = electroluxHelper.shopUrl;
	}

	public String getArticleId() {
		return articleId;
	}

	public T setArticleId(String articleId) {
		this.articleId = articleId;
		return self();
	}

	public Collection<String> getBrands() {
		return brands;
	}

	public T setBrands(Collection<String> brands) {
		if (ParserUtil.isNotEmpty(brands))
			this.brands = brands;
		return self();
	}

	public Integer getAmountOfProducts() {
		return amountOfProducts;
	}

	public T setAmountOfProducts(Integer amountOfProducts) {
		if (amountOfProducts != null)
			this.amountOfProducts = amountOfProducts;
		return self();
	}

	public String getShopUrl() {
		return shopUrl;
	}

	public T setShopUrl(String shopUrl) {
		this.shopUrl = shopUrl;
		return self();
	}

	public T setShopUrl(UriWrapper shopUrl) {
		this.shopUrl = shopUrl.toString();
		return self();
	}
}
