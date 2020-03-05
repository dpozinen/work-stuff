package blackbee.swarm.parsinghelper.base;

import blackbee.swarm.core.swarm.parsers.framework.BaseNavigationStep;
import blackbee.swarm.core.swarm.parsers.framework.INavigationStep;
import blackbee.swarm.core.swarm.parsers.framework.NavigationStepException;
import blackbee.swarm.core.swarm.parsers.helpers.UIdCreationHelper;
import blackbee.swarm.core.swarm.resultmodel.IResultEntrySet;
import blackbee.swarm.parsinghelper.Offer;
import blackbee.swarm.parsinghelper.ParserUtil;
import blackbee.swarm.parsinghelper.UriWrapper;
import blackbee.swarm.parsinghelper.filter.Filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A base class for all Navigation steps to implement. Has the most used fields in it.
 *
 * @param <H> is the helper for your package, which should implement the {@link Helper} interface.
 *           This parameter is here so that there won't be a need to cast all you helpers to implementations and keep the helper in the base class.
 * @author dpozinen
 */
@SuppressWarnings({"rawtypes", "serial"})
public abstract class NavStep<H extends Helper> extends BaseNavigationStep implements Step {

	protected Filter document;

	protected final UriWrapper pageUri;

	protected final H helper;

	public NavStep(Router<H> parent, String uid, boolean override) {
		super(uid, override);
		this.pageUri = parent.getResponseUri();
		this.helper = parent.getHelper();
		this.document = parent.getDocument();
	}

	public NavStep(Router<H> parent, String uid) {
		this(parent, uid, false);
	}

	public NavStep(Router<H> parent, Class<?> c) {
		this(parent, UIdCreationHelper.createUIdByStringAndClass(parent.getUId(), c), false);
	}

	@Override @Deprecated
	public INavigationStep[] run(IResultEntrySet results) throws NavigationStepException {
		List<Step> steps;
		try {
			 steps = extract(results);
		} catch ( RuntimeException e ) {
			throw new NavigationStepException(this, "Url: " + pageUri, e);
		}

		if (!steps.isEmpty()) {
			if (steps.size() == 1 && steps.get(0) == null)
				return ParserUtil.NO_NEXT_STEPS;
			try {
				steps.removeAll(Collections.singletonList(null));
			} catch (UnsupportedOperationException e) {
				steps = new ArrayList<>(steps);
				steps.removeAll(Collections.singletonList(null));
			}
		}

		return steps.toArray(new INavigationStep[0]);
	}

	/**
	 * Is supposed to extract data from the response and possibly create {@link Offer}s from the data.
	 *
	 * @param results used to create {@link Offer} instances
	 * @return the list of steps to execute by the framework. Safe to return null steps, they will be removed.
	 */
	protected abstract List<Step> extract(IResultEntrySet results);

}
