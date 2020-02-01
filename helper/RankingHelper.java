package blackbee.swarm.parsinghelper;

import blackbee.swarm.core.swarm.IQuery;

import java.io.Serializable;

/**
 * A ranking helper, that contains {@link #overviewUrl} and {@link #rank} with setters and getters for them.
 *
 * Designed to be inherited by helpers that have require ranking mode functionality.
 * Uses the simulated self type idiom to provide method chaining between base and child classes. So that the following
 * would work and the method chain would not be interrupted
 * <br>
 * {@code new TestHelper(otherHelper).setOverviewUrl("nav url").setRank(1).defaultStep("product url"); }
 * <br>
 * @param <T> Basically the type of the child class.
 * @author dpozinen
 */
public abstract class RankingHelper<T extends RankingHelper<T>> implements Serializable {
	private static final long serialVersionUID = -6746755046026082319L;

	final boolean isRankingMode;
	private String overviewUrl;
	private Integer rank;

	public RankingHelper(IQuery query) {
		this.isRankingMode = ModiHelper.isRankingMode(query);
		this.rank = 0;
		this.overviewUrl = "";
	}

	public RankingHelper(RankingHelper<T> other) {
		this.isRankingMode = other.isRankingMode;
		this.rank = other.rank;
		this.overviewUrl = other.overviewUrl;
	}

	/**
	 * Should be used instead of {@code this} in all base classes to provide support for chaining between base and child class
	 * @return the instance of the current class
	 */
	protected abstract T self();

	public T setRank(Integer rank) {
		if (isRankingMode)
			this.rank = rank;
		return self();
	}

	public T setOverviewUrl(String overviewUrl) {
		if (isRankingMode)
			this.overviewUrl = overviewUrl;
		return self();
	}

	public T setOverviewUrl(UriWrapper overviewUrl) {
		if (isRankingMode)
			this.overviewUrl = String.valueOf(overviewUrl);
		return self();
	}

	/**
	 * Calculates and sets the appropriate rank value for the provided page and the number of products per page
	 * The value set here is the rank of the last product on the previous page. This is done to account for the call
	 * of {@link #incrementRank()} before creating a product step. So this will work as follows:
	 * <blockquote><pre>
	 * for ( String url : extractProducts() )
	 * 		nextSteps.add(helper.incrementRank().defaultStep(url));
	 *
	 * for ( int p = PAGINATION_START; p <= resultPages; p++ )
	 * 		nextSteps.add(helper.setOverviewUrl(url).calcPageRank(p, PPP).navigationStep(url));
	 *
	 * </pre></blockquote>
	 * Note that we do not have any counters and do not access the rank value explicitly. This requires having the value of {@link #rank} to be one fewer,
	 * so that {@link #incrementRank()} will work correctly on the next page.
	 * Also note that the products are extracted before the pagination. This is not mandatory but ensures correct rank on first page. Same can be achieved with just a
	 * helper copy on pagination creation.
	 * @param page the page number
	 * @return this helper
	 */
	public T calcPageRank(int page, int productsPerPage) {
		int pageMultiplier = page > 0 ? page - 1 : page;
		this.rank = pageMultiplier * productsPerPage;
		return self();
	}

	public T incrementRank() {
		if (isRankingMode) rank++;
		return self();
	}

	public Integer getRank() {
		return rank;
	}

	public String getOverviewUrl() {
		return overviewUrl;
	}

}

