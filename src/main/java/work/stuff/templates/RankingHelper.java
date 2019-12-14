package main.java.work.stuff.templates;

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
 */
public abstract class RankingHelper<T extends RankingHelper<T>> implements Serializable
{
	private final boolean isRankingMode;
	private String overviewUrl;
	private Integer rank;

	public RankingHelper(IQuery query) {
		this.isRankingMode = ModiHelper.isRankingMode(query);
	}

	public RankingHelper(RankingHelper other) {
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

	public int getRank() {
		return rank;
	}

	public String getOverviewUrl() {
		return overviewUrl;
	}

}