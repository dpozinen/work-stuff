package ${PACKAGE_NAME};

import java.util.ArrayList;
import java.util.List;

import blackbee.swarm.core.parsing.html.IHtmlElementFilter;
import blackbee.swarm.core.swarm.WebRequestSettings;
import blackbee.swarm.core.swarm.parsers.framework.BaseWebRequestStep;
import blackbee.swarm.core.swarm.parsers.framework.INavigationStep;
import blackbee.swarm.core.swarm.parsers.framework.IWebRequestStep;
import blackbee.swarm.core.swarm.parsers.framework.WebRequestStepException;
import blackbee.swarm.core.swarm.resultmodel.IResultEntrySet;
import blackbee.swarm.parsinghelper.ParserUtil;

/**
 *
#parse("author.java") 
 */
@SuppressWarnings({ "rawtypes", "serial" })
class ${NAME}RouterStep extends BaseWebRequestStep
{
    private final ${NAME}Helper	helper;

	private IHtmlElementFilter		documentFilter;

	${NAME}RouterStep(WebRequestSettings settings, ${NAME}Helper helper)
	{
		this(settings, Boolean.FALSE, helper);
	}

	private ${NAME}RouterStep(WebRequestSettings settings, boolean override, ${NAME}Helper helper)
	{
		super(settings, override);
		this.helper = new ${NAME}Helper(helper);
	}

	@Override
	protected IWebRequestStep createCopy(boolean override) throws WebRequestStepException
	{
		return new ${NAME}RouterStep(getWebRequestSettings(), override, helper);
	}

	@Override
	protected INavigationStep[] runCore(IResultEntrySet results)
	{
		List<INavigationStep> nextSteps = new ArrayList<>();
		documentFilter = createDocumentFilter();

		if ( ${NAME}ProductStep.isResponsible(this) )
		{
			nextSteps.add(new ${NAME}ProductStep(this));
		}
		else if ( ${NAME}NavigationStep.isResponsible(this) )
		{
			nextSteps.add(new ${NAME}NavigationStep(this));
		}
		else if ( ${NAME}FccNavigationStep.isResponsible(this) )
		{
			nextSteps.add(new ${NAME}FccStep(this));
		}

		return ParserUtil.navigationStepListToArray(nextSteps);
	}

	IHtmlElementFilter getDocumentFilter()
	{
		return documentFilter;
	}

	${NAME}Helper getHelper()
	{
		return new ${NAME}Helper(helper);
	}
}
