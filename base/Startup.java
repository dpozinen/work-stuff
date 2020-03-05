package blackbee.swarm.parsinghelper.base;

import blackbee.swarm.core.swarm.IQuery;
import blackbee.swarm.core.swarm.parsers.framework.BaseStartupStep;
import blackbee.swarm.core.swarm.parsers.framework.INavigationStep;
import blackbee.swarm.core.swarm.parsers.framework.StartupStepException;
import blackbee.swarm.core.swarm.resultmodel.IResultEntrySet;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"serial", "rawtypes"})
public abstract class Startup<H extends Helper> extends BaseStartupStep implements Step
{
	protected H helper;

	@SuppressWarnings("unchecked")
	public Startup(IQuery query, Class c) {
		super(query, c);
	}

	@Override @Deprecated
	public INavigationStep[] run(IResultEntrySet set) throws StartupStepException {
		List<Step> nextSteps = new ArrayList<>();
		start(nextSteps);
		return nextSteps.toArray(new INavigationStep[0]);
	}

	protected abstract void start(List<Step> nextSteps);
}
