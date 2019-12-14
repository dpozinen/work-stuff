package ${PACKAGE_NAME};

import org.apache.commons.lang3.StringUtils;

import blackbee.swarm.core.swarm.WebRequestSettings;
import blackbee.swarm.core.swarm.parsers.framework.BaseNavigationStep;
import blackbee.swarm.core.swarm.parsers.framework.INavigationStep;
import blackbee.swarm.core.swarm.parsers.framework.NavigationStepException;
import blackbee.swarm.core.swarm.parsers.helpers.UIdCreationHelper;
import blackbee.swarm.core.swarm.resultmodel.IResultEntrySet;
import blackbee.swarm.core.web.HttpHeader;
import blackbee.swarm.core.web.HttpHeaderKey;
import blackbee.swarm.core.web.PostContent;
import blackbee.swarm.core.web.Uri;
import blackbee.swarm.parsers.intersport.IntersportHelper;
import blackbee.swarm.parsinghelper.EncodingHelper;
/*
#parse("author.java") 
*/
@SuppressWarnings({ "rawtypes", "serial" })
class ${NAME}CookieStep extends BaseNavigationStep
{

	private final String			cookies;

	private final ${NAME}Helper	    helper;

	private final UriWrapper		pageUri;

	${NAME}CookieStep(${NAME}RouterStep parent)
	{
		super(UIdCreationHelper.createUIdByStringAndClass(parent.getUId(), ${NAME}CookieStep.class), Boolean.FALSE);
		this.cookies = parent.getResponse().getCookies();
		this.helper = parent.getHelper();
		this.pageUri = new UriWrapper(parent.getResponse().getUri());
	}

	static boolean isResponsible(${NAME}RouterStep parent)
	{
		return StringUtils.containsIgnoreCase(parent.getResponse().getUri().toString(), "cookiewall"); //TODO
	}

	@Override
	public INavigationStep[] run(IResultEntrySet iResultEntrySet) throws NavigationStepException
	{
		helper.setCookies(cookies);

		return new INavigationStep[] { createRequest() };
	}

	private WebRequestSettings createRequest()
	{
		HttpHeader header = new HeaderBuilder().referer(pageUri).cookie(cookies).build();
		WebRequestSettings settings = new UriWrapper("url", helper.getBaseUrl()).settings(header);

		return new ${NAME}RouterStep(settings, helper);
	}
}
