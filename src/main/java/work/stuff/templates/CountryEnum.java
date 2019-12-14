package ${PACKAGE_NAME};

import blackbee.swarm.core.swarm.parsers.Country;
import blackbee.swarm.parsinghelper.CountryEnumHelper;

/**
#parse("author.java")
 */

enum ${NAME}Country implements CountryEnumHelper.CountryAware
{
	DEU(Country.Germany, "base_url") //TODO
	;

	private final Country	country;

	private final String	baseUrl;

	private ${NAME}Country(Country country, String baseUrl)
	{
		this.country = country;
		this.baseUrl = baseUrl;
	}

	@Override
	public Country getCountry()
	{
		return country;
	}

	String getBaseUrl()
	{
		return baseUrl;
	}
}
