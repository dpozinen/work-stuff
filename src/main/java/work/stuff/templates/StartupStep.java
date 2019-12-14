package ${PACKAGE_NAME};

import java.util.ArrayList;
import java.util.List;

import blackbee.swarm.core.swarm.IQuery;
import blackbee.swarm.core.swarm.parsers.Country;
import blackbee.swarm.core.swarm.parsers.ParserInfo;
import blackbee.swarm.core.swarm.parsers.framework.BaseStartupStep;
import blackbee.swarm.core.swarm.parsers.framework.INavigationStep;
import blackbee.swarm.core.swarm.parsers.framework.StartupStepException;
import blackbee.swarm.core.swarm.resultmodel.IResultEntrySet;
import blackbee.swarm.core.web.Uri;
import blackbee.swarm.parsinghelper.ModiHelper;
import blackbee.swarm.parsinghelper.ParserUtil;

/**
 *
#parse("author.java") 
 */
@SuppressWarnings({ "rawtypes", "serial" })
@ParserInfo(names = { "newparser" }) // todo
public class ${NAME}StartupStep extends BaseStartupStep
{
    private final ${NAME}Helper helper;

    private static final Country[]	ACCEPTED_COUNTRIES	= {};

    private static final String[]	ACCEPTED_MODES		= { ModiHelper.LOAD_URL, ModiHelper.SEARCH, ModiHelper.FULL_CATALOG_CRAWL };

	public ${NAME}StartupStep(IQuery query)
	{
		super(query);
		ModiHelper.isQueryCompliantToExpectations(query, ACCEPTED_COUNTRIES, ACCEPTED_MODES);
		this.helper = new ${NAME}Helper(query);
	}


	@Override
	public INavigationStep[] run(IResultEntrySet results) throws StartupStepException
	{
		List<INavigationStep> nextSteps = new ArrayList<>();

		if ( ModiHelper.isLoadUrlMode(getQuery()) )
		{
			nextSteps.add(helper.defaultStep(ModiHelper.getUrl(getQuery)));
		}
		else if ( ModiHelper.isSearchMode(getQuery()) )
		{
			nextSteps.add(helper.searchStep(getQuery().getKeyword()));
		}
		else if ( ModiHelper.isFullCataglogCrawlMode(getQuery()) )
		{
			nextSteps.add(helper.defaultStep(helper.BASE_URL));
		}

		return ParserUtil.navigationStepListToArray(nextSteps);
	}
}
