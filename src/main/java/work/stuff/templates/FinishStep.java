package ${PACKAGE_NAME};

import java.util.LinkedHashMap;
import java.util.Map;

import blackbee.common.crawling.data.OfferKey;
import blackbee.common.data.Key;
import blackbee.common.data.dom.INode;
import blackbee.common.data.storage.IDataContainer;
import blackbee.swarm.core.swarm.parsers.framework.BaseParserStep;
import blackbee.swarm.core.swarm.parsers.framework.FinishStepException;
import blackbee.swarm.core.swarm.parsers.framework.IFinishStep;
import blackbee.swarm.core.swarm.parsers.helpers.UIdCreationHelper;

/**
 *
#parse("author.java") 
 */
@SuppressWarnings({ "serial", "rawtypes" })
public class ${NAME}FinishStep extends BaseParserStep implements IFinishStep
{

	public ${NAME}FinishStep(String uid, boolean override)
	{
		super(UIdCreationHelper.createUIdByStringAndClass(uid, ${NAME}FinishStep.class), override);
	}

	@Override
	public void finish(IDataContainer results, String[] arg1, Key arg2) throws FinishStepException
	{
		Map<String, INode> products = mapBy(OfferKey.EntryId, "products.product", results);

		for ( INode offer : results.filterByDataPath("offers.offer") )
		{
			if ( offer.getAttributes().containsKey(OfferKey.ProductId) )
			{
				String productId = offer.getAttributes().get(OfferKey.ProductId).get(0).toString();
				INode product = products.get(productId);
				if ( product != null )
				{
					product.getAttributes().removeKey(OfferKey.EntryId);
					product.getAttributes().removeKey(OfferKey.ExtractionDate);
					offer.getAttributes().addRange(product.getAttributes());
				}
			}
		}

		results.removeNodes("products.product");
	}
		private Map<String, INode> mapBy(Key key, String collection, IDataContainer results)
	{
		Map<String, INode> productDataMap = new HashMap<>();
		for ( INode entry : results.filterByDataPath(collection) )
		{
			String id = entry.getAttributes().get(key).get(0).toString();
			productDataMap.put(id, entry);
		}
		return productDataMap;
	}
	}