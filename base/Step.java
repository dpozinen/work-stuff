package blackbee.swarm.parsinghelper.base;

import blackbee.swarm.core.swarm.parsers.framework.INavigationStep;

/**
 * The interface that {@link NavStep} and {@link Router} implement.
 * Removes the confusion between INavigationStep, BaseNavigationStep, IWebRequestSteps etc.
 * Everything is a Step - NavSteps and RouterSteps.
 * @author dpozinen
 */
public interface Step extends INavigationStep { }
