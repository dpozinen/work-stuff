package main.java.work.stuff.templates;

import java.io.Serializable;

public abstract class RankingHelper<T extends RankingHelper<T>> implements Serializable
{
	private String overviewUrl;
	private int rank;

	protected abstract T self();

	public T setRank(int rank) {
		this.rank = rank;
		return self();
	}

	public T setOverviewUrl(String overviewUrl) {
		this.overviewUrl = overviewUrl;
		return self();
	}

	public int getRank() {
		return rank;
	}

	public String getOverviewUrl() {
		return overviewUrl;
	}

	public static OfferKey createTableOfferKey(String k) {
		return "TD_" + ParserUtil.getCleanedOfferKeyName(k);
	}

}