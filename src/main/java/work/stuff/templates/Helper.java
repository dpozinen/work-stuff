package ${PACKAGE_NAME};

import java.io.Serializable;

import blackbee.swarm.core.swarm.IQuery;
import blackbee.swarm.core.swarm.WebRequestSettings;
import blackbee.swarm.core.swarm.parsers.Country;
import blackbee.swarm.core.web.Uri;
import blackbee.swarm.parsinghelper.ModiHelper;
import blackbee.swarm.parsinghelper.ParserUtil;

/**
 *
#parse("author.java") 
 */
@SuppressWarnings({ "rawtypes", "serial" })
class ${NAME}Helper implements Serializable
{
    private static final String BASE_URL = "https://example.com"; // todo

    private static final String SEARCH_TEMPLATE = "https://example.com/search?q=%s&ProdPerPage="; // todo

	${NAME}Helper(IQuery query)
	{ // if no query info is needed, remove the query param
	}

	${NAME}Helper(${NAME}Helper other)
	{ // if theres nothing to copy, remove

	}

	INavigationStep defaultStep(String url) 
	{
	    WebRequestSettings settings = new UriWrapper(url, BASE_URL).settings();
		return new ${NAME}RouterStep(settings, this);
	}

    INavigationStep defaultStep(UriWrapper uri) 
	{
		return new ${NAME}RouterStep(uri.settings(), this);
	}

	INavigationStep searchStep(String keyword)
	{
	    String url = String.format(SEARCH_TEMPLATE, keyword);
	    WebRequestSettings settings = new UriWrapper(url).settings();
		return new ${NAME}RouterStep(settings, this);
	}

}
