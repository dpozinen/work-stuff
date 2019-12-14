package ${PACKAGE_NAME};

import java.util.ArrayList;
import java.util.List;

import blackbee.swarm.core.parsing.html.IHtmlElementFilter;
import blackbee.swarm.core.swarm.parsers.framework.BaseNavigationStep;
import blackbee.swarm.core.swarm.parsers.framework.INavigationStep;
import blackbee.swarm.core.swarm.parsers.framework.NavigationStepException;
import blackbee.swarm.core.swarm.parsers.helpers.UIdCreationHelper;
import blackbee.swarm.core.swarm.resultmodel.IResultEntrySet;
import blackbee.swarm.parsinghelper.Literals;
import blackbee.swarm.parsinghelper.ModiHelper;
import blackbee.swarm.parsinghelper.ParserUtil;
import blackbee.swarm.parsinghelper.UriHelper;

/**
 *
#parse("author.java") 
 */
@SuppressWarnings({ "rawtypes", "serial" })
class ${NAME}FccStep extends BaseNavigationStep
{
	private static final String			CATEGORIES_PATH	= "PATH";	// todo

	private final IHtmlElementFilter	documentFilter;

	private final ${NAME}Helper		helper;

	${NAME}FccStep(${NAME}RouterStep parent)
	{
		super(UIdCreationHelper.createUIdByStringAndClass(parent.getUId(), ${NAME}FccStep.class), Boolean.FALSE);
		this.documentFilter = parent.getDocumentFilter();
		this.helper = parent.getHelper();
	}

	static boolean isResponsible(${NAME}RouterStep parent)
	{
		boolean isMainPage = UriHelper.isBaseUrl(parent.getResponse().getUri());
		boolean containsCategories = !parent.getDocumentFilter().filterByDataPath(CATEGORIES_PATH).getIsEmpty();

		return ModiHelper.isFullCataglogCrawlMode(parent.getQuery()) && isMainPage && containsCategories;
	}

	@Override
	public INavigationStep[] run(IResultEntrySet iResultEntrySet) throws NavigationStepException
	{
		return createCategorySteps();
	}

	private INavigationStep[] createCategorySteps() 
	{	
		List<INavigationStep> nextSteps = new ArrayList<>();
	
		for ( String link : ParserUtil.extractAllElementsFromHtmlElement(documentFilter, CATEGORIES_PATH, Literals.HREF) )
		{
			nextSteps.add(helper.defaultStep(link));
		}
	
		return ParserUtil.navigationStepListToArray(nextSteps);
	}

}
