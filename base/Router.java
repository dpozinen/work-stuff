package blackbee.swarm.parsinghelper.base;

import blackbee.swarm.core.swarm.WebRequestSettings;
import blackbee.swarm.core.swarm.parsers.framework.BaseWebRequestStep;
import blackbee.swarm.core.swarm.parsers.framework.INavigationStep;
import blackbee.swarm.core.swarm.parsers.framework.IWebRequestStep;
import blackbee.swarm.core.swarm.parsers.framework.WebRequestStepException;
import blackbee.swarm.core.swarm.resultmodel.IResultEntrySet;
import blackbee.swarm.core.web.IProxy;
import blackbee.swarm.parsinghelper.JsonUtils;
import blackbee.swarm.parsinghelper.UriWrapper;
import blackbee.swarm.parsinghelper.filter.Filter;
import com.jayway.jsonpath.internal.JsonContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a base class for all Router steps to extend.
 * It provides several useful getters and default fields, that all Routers have.
 *
 * @param <H> is the helper for your package, which should implement the {@link Helper} interface.
 *          This parameter is here so that there won't be a need to cast all you helpers to implementations and keep the helper in the base class.
 * @author dpozinen
 */
@SuppressWarnings({"rawtypes", "serial"})
public abstract class Router<H extends Helper> extends BaseWebRequestStep implements Step {

	private static final Map<Class<?>, Method> isResponsibles = new HashMap<>();
	private static final Map<Class<?>, Constructor<?>> constructors = new HashMap<>();

	protected H helper;

	protected Filter document;

	public Router(WebRequestSettings settings, boolean override) {
		super(settings, override);
	}

	public Router(WebRequestSettings settings) {
		this(settings, false);
	}

	public Router(String uid, WebRequestSettings settings) {
		super(uid, settings, false);
	}

	@Override @Deprecated
	protected IWebRequestStep createCopy(boolean retry) throws WebRequestStepException {
		return copy(retry);
	}

	@Override @Deprecated
	protected INavigationStep[] runCore(IResultEntrySet results) throws WebRequestStepException {
		document = Filter.fromResponse(this);
		List<Step> nextSteps = new ArrayList<>();
		mapResponse(nextSteps);
		return nextSteps.toArray(new INavigationStep[0]);
	}

	/**
	 * Should create a copy of this step.
	 * @param retry whether this is a retry, provided by the framework
	 * @return a copy of this router
	 */
	protected abstract Router<H> copy(boolean retry);

	/**
	 * Provides the steps for mapping in {@link #mapResponse(List)}
	 * @return the list of classes to be checked by {@link #mapResponse(List)}. These classes should be steps and have an {@code isResponsible(Router)} method,
	 * or the creation and checking won't work. This method is not abstract, because you may choose to override {@link #mapResponse(List)} instead.
	 * @throws IllegalStateException when this method is not overridden
	 */
	private List<? extends Class<?>> getSteps() {
		throw throwEmptyStep();
	}

	/**
	 * A method that is used for checking the response and mapping it to the desired {@link NavStep}
	 * The default implementation is designed around {@link #getSteps()}
	 * @param nextSteps and empty list for the steps to invoke
	 * @see #getSteps()
	 */
	protected void mapResponse(List<Step> nextSteps) {
		List<? extends Class<?>> steps = getSteps();

		refillResponsibles(steps);
		for (Map.Entry<Class<?>, Method> c : isResponsibles.entrySet()) {
			boolean isResponsible = safeInvoke(c);
			String s = formLog(c, isResponsible);
			if (isResponsible && addStep(c, nextSteps)) break;
		}
	}

	/**
	 * Adds the desired step to the nextSteps.
	 * @param entry the entry with the class to be created
	 * @param nextSteps the list to add the class to
	 * @return false if the step was not created due to some issues
	 * @see #safeCreate(Class)
	 */
	private boolean addStep(Map.Entry<Class<?>, Method> entry, List<Step> nextSteps) {
		Step step = safeCreate(entry.getKey());
		if (step != null) nextSteps.add(step);
		return false;
	}

	private IllegalStateException throwEmptyStep() {
		return new IllegalStateException("NO STEPS PROVIDED AND METHOD IS NOT OVERRIDDEN");
	}

	private String formLog(Map.Entry<Class<?>, Method> c, boolean isResponsible) {
		return c.getKey().getSimpleName() + (isResponsible ? "IS" : "IS NOT") + "responsible";
	}

	private void refillResponsibles(List<? extends Class<?>> steps) {
		if (!isResponsibles.keySet().containsAll(steps)) {
			isResponsibles.clear();
			for (Class<?> step : steps)
				try {
					isResponsibles.put(step, step.getDeclaredMethod("isResponsible", Router.class));
				} catch (NoSuchMethodException e) { e.printStackTrace(); }
		}
	}

	private Step safeCreate(Class<?> c) {
		Constructor<?> constructor = constructors.get(c);
		if ( constructor == null )
			try {
				constructor = c.getConstructor(Router.class);
				constructors.put(c, constructor);
			} catch ( NoSuchMethodException e ) {
				logErr(c);
				return null;
			}

		try {
			return (Step) constructor.newInstance(this);
		} catch ( IllegalAccessException e ) {
			constructor.setAccessible(true);
			return safeCreate(c); // TODO debug if this is an endless loop
		} catch ( InstantiationException | InvocationTargetException e ) {
			logErr(c);
		}
		return null;
	}

	private boolean safeInvoke(Map.Entry<Class<?>, Method> c) {
		try {
			return (Boolean) c.getValue().invoke(null, this);
		} catch (InvocationTargetException e) {
			logErr(c.getValue());
		} catch ( IllegalAccessException e ) {
			c.getValue().setAccessible(true);
			return safeInvoke(c);
		}
		return false;
	}

	private void logErr(Class<?> c) {
		System.err.printf("%n%nCOULD NOT CREATE INSTANCE OF CLASS %s%n%n", c.getSimpleName());
	}

	private void logErr(Method method) {
		System.err.printf("%n%nCOULD NOT INVOKE METHOD %s%n%n", method.getName());
	}

	public final Filter getDocument() {
		return document;
	}

	public H getHelper() {
		return helper;
	}

	public final IProxy getStepProxy() {
		return getResponse().getWebRequest().getProxy();
	}

	public final String getResponseContent() {
		return getResponse().getContent().Content;
	}

	public final UriWrapper getRequestUri() {
		return new UriWrapper(getWebRequestSettings().getUri());
	}

	public final UriWrapper getResponseUri() {
		return new UriWrapper(getResponse().getUri());
	}

	public final JsonContext getJsonResponse() {
		return JsonUtils.parse(getResponseContent());
	}

	@Override
	public String toString() {
		return String.format("{Url = %s}", getRequestUri());
	}
}
