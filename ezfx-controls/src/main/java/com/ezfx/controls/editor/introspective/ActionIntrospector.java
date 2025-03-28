package com.ezfx.controls.editor.introspective;

import javafx.event.ActionEvent;
import org.controlsfx.control.action.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Action Map provides an ability to create an action map of any object.
 * Attempts to convert methods annotated with {@link ActionProxy} to {@link Action}.
 *
 * <h3>Code Example</h3>
 * Here's a very simple example of how to use ActionMap to register a class (in
 * this class it is the application class itself), and to then retrieve actions
 * out of the ActionMap (via the static {@link org.controlsfx.control.action.ActionMap#action(String)} method:
 * <br>
 *
 * <pre>
 * public class ActionMapDemo extends Application {
 *     public ActionMapDemo() {
 *         ActionMap.register(this);
 *         Action action11 = ActionMap.action("action11");
 *         Button actionButton = ActionUtils.createButton(action11);
 *     }
 *
 *     &#64;ActionProxy(text="Action 1.1", graphic="start.png", accelerator="ctrl+shift+T")
 *     private void action11() {
 *         System.out.println( "Action 1.1 is executed");
 *     }
 * }
 * </pre>
 * <p>
 * If you require more control over the creation of the Action objects, you can either set the
 * global ActionFactory by calling ActionMap.setActionFactory() and/or you can use the factory
 * property on individual &#64;ActionProxy declarations to set the factory on a case-by-case basis.
 *
 * @see ActionProxy
 * @see Action
 */
public class ActionIntrospector {

	private static AnnotatedActionFactory actionFactory = new DefaultActionFactory();

	private static final Map<String, AnnotatedAction> actions = new HashMap<>();


	private ActionIntrospector() {
		// no-op
	}


	/**
	 * Returns the action factory used by ActionMap to construct AnnotatedAction instances. By default, this
	 * is an instance of {@link DefaultActionFactory}.
	 */
	public static AnnotatedActionFactory getActionFactory() {
		return actionFactory;
	}

	/**
	 * Sets the action factory used by ActionMap to construct AnnotatedAction instances. This factory can be overridden on
	 * a case-by-case basis by specifying a factory class in {@link ActionProxy#factory()}
	 */
	public static void setActionFactory(AnnotatedActionFactory factory) {
		Objects.requireNonNull(factory);
		actionFactory = factory;
	}


	/**
	 * Attempts to convert target's methods annotated with {@link ActionProxy} to {@link Action}s.
	 * Three types of methods are currently converted: parameter-less methods,
	 * methods with one parameter of type {@link ActionEvent}, and methods with two parameters
	 * ({@link ActionEvent}, {@link Action}).
	 * <p>
	 * Note that this method supports safe re-registration of a given instance or of another instance of the
	 * same class that has already been registered. If another instance of the same class is registered, then
	 * those actions will now be associated with the new instance. The first instance is implicitly unregistered.
	 * <p>
	 * Actions are registered with their id or method name if id is not defined.
	 *
	 * @param target object to work on
	 * @throws IllegalStateException if a method with unsupported parameters is annotated with {@link ActionProxy}.
	 */
	public static void register(Object target) {

		for (Method method : target.getClass().getMethods()) {
			// Only process methods that have the ActionProxy annotation
			Annotation[] annotations = method.getAnnotationsByType(ActionProxy.class);
			if (annotations.length == 0) {
				continue;
			}
			int modifiers = method.getModifiers();
			boolean isPublic = Modifier.isPublic(modifiers);
			boolean isStatic = Modifier.isStatic(modifiers);
			if (!isPublic || isStatic) {
				continue;
			}

			// Only process methods that have
			// a) no parameters OR
			// b) one parameter of type ActionEvent OR
			// c) two parameters (ActionEvent, Action)
			int paramCount = method.getParameterCount();
			Class[] paramTypes = method.getParameterTypes();

			if (paramCount > 2) {
				throw new IllegalArgumentException(String.format("Method %s has too many parameters", method.getName()));
			}

			if (paramCount == 1 && !ActionEvent.class.isAssignableFrom(paramTypes[0])) {
				throw new IllegalArgumentException(String.format("Method %s -- single parameter must be of type ActionEvent", method.getName()));
			}

			if (paramCount == 2 && (!ActionEvent.class.isAssignableFrom(paramTypes[0]) ||
					!Action.class.isAssignableFrom(paramTypes[1]))) {
				throw new IllegalArgumentException(String.format("Method %s -- parameters must be of types (ActionEvent, Action)", method.getName()));
			}

			ActionProxy annotation = (ActionProxy) annotations[0];

			AnnotatedActionFactory factory = determineActionFactory(annotation);
			AnnotatedAction action = factory.createAction(annotation, method, target);

			String id = annotation.id().isEmpty() ? method.getName() : annotation.id();
			actions.put(id, action);
		}
	}


	private static AnnotatedActionFactory determineActionFactory(ActionProxy annotation) {
		// Default to using the global action factory
		AnnotatedActionFactory factory = actionFactory;

		// If an action-factory has been specified on this specific ActionProxy, then
		// instantiate it and use it instead.
		String factoryClassName = annotation.factory();
		if (!factoryClassName.isEmpty()) {
			try {
				Class factoryClass = Class.forName(factoryClassName);
				factory = (AnnotatedActionFactory) factoryClass.newInstance();

			} catch (ClassNotFoundException ex) {
				throw new IllegalArgumentException(String.format("Action proxy refers to non-existant factory class %s", factoryClassName), ex);

			} catch (InstantiationException | IllegalAccessException ex) {
				throw new IllegalStateException(String.format("Unable to instantiate action factory class %s", factoryClassName), ex);
			}
		}

		return factory;
	}


	/**
	 * Removes all the actions associated with target object from the action map.
	 *
	 * @param target object to work on
	 */
	public static void unregister(Object target) {
		if (target != null) {
			Iterator<Map.Entry<String, AnnotatedAction>> entryIter = actions.entrySet().iterator();
			while (entryIter.hasNext()) {
				Map.Entry<String, AnnotatedAction> entry = entryIter.next();

				Object actionTarget = entry.getValue().getTarget();

				if (actionTarget == null || actionTarget == target) {
					entryIter.remove();
				}
			}
		}
	}

	/**
	 * Returns action by its id.
	 *
	 * @param id action id
	 * @return action or null if id was not found
	 */
	public static Action action(String id) {
		return actions.get(id);
	}

	/**
	 * Returns collection of actions by ids. Useful to create {@link ActionGroup}s.
	 * Ids starting with "---" are converted to {@link ActionUtils#ACTION_SEPARATOR}.
	 * Incorrect ids are ignored.
	 *
	 * @param ids action ids
	 * @return collection of actions
	 */
	public static Collection<Action> actions(String... ids) {
		List<Action> result = new ArrayList<>();
		for (String id : ids) {
			if (id.startsWith("---")) result.add(ActionUtils.ACTION_SEPARATOR); //$NON-NLS-1$
			Action action = action(id);
			if (action != null) result.add(action);
		}
		return result;
	}
}

