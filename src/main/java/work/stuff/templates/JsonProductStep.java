package ${PACKAGE_NAME};

import java.math.BigDecimal;

import blackbee.common.crawling.data.OfferKey;
import blackbee.swarm.core.parsing.html.IHtmlElementFilter;
import blackbee.swarm.core.swarm.parsers.framework.BaseNavigationStep;
import blackbee.swarm.core.swarm.parsers.framework.INavigationStep;
import blackbee.swarm.core.swarm.parsers.framework.NavigationStepException;
import blackbee.swarm.core.swarm.parsers.helpers.UIdCreationHelper;
import blackbee.swarm.core.swarm.resultmodel.IResultEntry;
import blackbee.swarm.core.swarm.resultmodel.IResultEntrySet;
import blackbee.swarm.core.web.Uri;
import blackbee.swarm.parsinghelper.Currency;
import blackbee.swarm.parsinghelper.ParserUtil;
import blackbee.swarm.parsinghelper.PricingHelper;

import org.apache.commons.lang3.StringUtils;

/**
 *
#parse("author.java") 
 */
@SuppressWarnings({ "rawtypes", "serial" })
class ${NAME}ProductStep extends BaseNavigationStep
{
    private static final String			PRODUCT_CONTAINER_PATH	= "PATH";				// todo

	private static final BigDecimal		FREE_SHIPPING_SINCE				= BigDecimal.valueOf(0);// todo

	private static final BigDecimal		SHIPPING_COST					= BigDecimal.valueOf(0);// todo

	private final JsonContext	        context;

	private final UriWrapper			pageUri;
	
	private final ${NAME}Helper helper;

	${NAME}ProductStep(${NAME}RouterStep parent)
	{
		super(UIdCreationHelper.createUIdByStringAndClass(parent.getUId(), ${NAME}ProductStep.class), Boolean.FALSE);
		this.documentFilter = parent.getDocumentFilter();
		this.pageUri = new UriWrapper(parent.getResponse().getUri());
		this.helper = parent.getHelper();
	}

	static boolean isResponsible(${NAME}RouterStep parent)
	{
		return !parent.getDocumentFilter().filterByDataPath(PRODUCT_CONTAINER_PATH).getIsEmpty();
	}

	@Override
	public INavigationStep[] run(IResultEntrySet results) throws NavigationStepException
	{
		String articleId = extractArticleId();

		if ( StringUtils.isNotBlank(articleId) )
		{
			BigDecimal price = extractPrice();
			BigDecimal shippingNo = getShippingNo(price);

			IResultEntry entry = results.createResultEntry(articleId);
			entry.addValue(OfferKey.ArticleId, articleId);
			entry.addValue(OfferKey.EAN, extractEan());
			entry.addValue(OfferKey.Color, extractColor());
			entry.addValue(OfferKey.ProductName, extractProductName());
			entry.addValue(OfferKey.Price, price);
			entry.addValue(OfferKey.InitialPrice, extractInitialPrice());
			entry.addValue(OfferKey.Currency, ParserUtil.getCurrency(getQuery()));
			entry.addValue(OfferKey.ShopUrl, pageUri.toString());
			entry.addValue(OfferKey.Brand, extractBrand());
			entry.addValue(OfferKey.Source, "Source"); //Todo
			entry.addValue(OfferKey.ShopName, "ShopName");// Todo
			entry.addValue(OfferKey.Stock, extractStock());
			entry.addValue(OfferKey.ShippingNo, shippingNo);
			entry.addValue(OfferKey.Shipping, shippingNo == null ? StringUtils.EMPTY : String.valueOf(shippingNo));
			results.add(entry);
		}

		return ParserUtil.NO_NEXT_STEPS;
	}

	private String extractArticleId()
	{
		return JsonUtils.readString(context, "PATH");
	}

	private String extractEan()
	{
		rreturn JsonUtils.readString(context, "PATH");
	}

	private BigDecimal extractPrice()
	{
		return JsonUtils.readBigDecimal(context, "PATH");
	}

	private BigDecimal extractInitialPrice()
	{
		return JsonUtils.readBigDecimal(context, "PATH");
	}

	private String extractColor()
	{
		return JsonUtils.readString(context, "PATH");
	}

	private String extractProductName()
	{
		return JsonUtils.readString(context, "PATH");
	}

	private String extractBrand()
	{
		return JsonUtils.readString(context, "PATH");
	}

	private String extractStock()
	{
		return JsonUtils.readString(context, "PATH");
	}

	private BigDecimal getShippingNo(BigDecimal price)
	{
		if ( price != null )
		{
			return price.compareTo(FREE_SHIPPING_SINCE) < 0 ?  SHIPPING_COST : BigDecimal.ZERO; 
		}
		return null;
	}
}
