package ${PACKAGE_NAME};

import java.util.ArrayList;
import java.util.List;

import blackbee.swarm.core.parsing.html.IHtmlElementFilter;
import blackbee.swarm.core.swarm.parsers.framework.BaseNavigationStep;
import blackbee.swarm.core.swarm.parsers.framework.INavigationStep;
import blackbee.swarm.core.swarm.parsers.framework.NavigationStepException;
import blackbee.swarm.core.swarm.parsers.helpers.UIdCreationHelper;
import blackbee.swarm.core.swarm.resultmodel.IResultEntrySet;
import blackbee.swarm.core.web.Uri;
import blackbee.swarm.parsinghelper.Literals;
import blackbee.swarm.parsinghelper.ParserUtil;
import blackbee.swarm.parsinghelper.UriHelper;

import org.apache.commons.lang3.StringUtils;

/**
 *
#parse("author.java") 
 */
@SuppressWarnings({ "rawtypes", "serial" })
class ${NAME}NavigationStep extends BaseNavigationStep
{
	private static final String			PRODUCTS_PATH	    = "PATH";	// todo

	private static final int			PRODUCTS_PER_PAGE	= 48;		// todo

	private static final int			PAGINATION_START	= 2;		// todo

	private final IHtmlElementFilter	documentFilter;

	private final UriWrapper			pageUri;

	private final ${NAME}Helper		    helper;

	${NAME}NavigationStep(${NAME}RouterStep parent)
	{
		super(UIdCreationHelper.createUIdByStringAndClass(parent.getUId(), ${NAME}NavigationStep.class), Boolean.FALSE);
		this.documentFilter = parent.getDocumentFilter();
		this.pageUri = new UriWrapper(parent.getResponse().getUri());
		this.helper = parent.getHelper();
	}

	static boolean isResponsible(${NAME}RouterStep parent)
	{
		return !parent.getDocumentFilter().filterByDataPath(PRODUCTS_PATH).getIsEmpty();
	}

	@Override
	public INavigationStep[] run(IResultEntrySet results) throws NavigationStepException
	{
		List<INavigationStep> nextSteps = new ArrayList<>();

		if ( !pageUri.containsKey(Literals.PAGE) )
		{
			nextSteps.addAll(createPaginationSteps());
		}
        
        nextSteps.addAll(createProductSteps());

		return ParserUtil.navigationStepListToArray(nextSteps);
	}

	private List<INavigationStep> createPaginationSteps()
	{
		List<INavigationStep> nextSteps = new ArrayList<>();
		String path = "PATH"; // todo
		String resultCountStr = ParserUtil.extractTextFromHtmlElement(documentFilter, path, 0).replaceAll("\\D", "");

		if ( StringUtils.isNotBlank(resultCountStr) )
		{
			int resultCount = Integer.parseInt(resultCountStr);
			int resultPages = (int) Math.ceil((double) resultCount / PRODUCTS_PER_PAGE);

			for ( int i = PAGINATION_START; i <= resultPages; i++ )
			{ // make sure ProductsPerPage is also set here to PRODUCTS_PER_PAGE
				Uri nextPageUri = new UriWrapper(pageUri).setOrAdd(Literals.PAGE, i).uri();
				nextSteps.add(helper.defaultStep(nextPageUri));
			}
		}

		return nextSteps;
	}

    private List<INavigationStep> createProductSteps()
    {
        List<INavigationStep> nextSteps = new ArrayList<>();
        for ( String url : ParserUtil.extractAllElementsFromHtmlElement(documentFilter, PRODUCTS_PATH, Literals.HREF) )
        {
            nextSteps.add(helper.defaultStep(url));
        }
        return nextSteps;
     }

}
