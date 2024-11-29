package com.ezfx.spring;

import com.ezfx.spring.annotations.FXMLAutoLoad;
import com.ezfx.spring.util.DelegatingMap;
import com.sun.javafx.fxml.BeanAdapter;
import com.sun.javafx.fxml.ModuleHelper;
import com.sun.javafx.reflect.ReflectUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class SpringFXProxyBuilder<T> extends SpringFXBuilder<T> implements DelegatingMap<String, Object> {

	private final ApplicationContext context;
	private final Map<String, Object> delegateMap = new HashMap<>();


	private final Map<String, Property> propertiesMap;
	private Set<String> propertyNames;

	private static final String SETTER_PREFIX = "set";
	private static final String GETTER_PREFIX = "get";

	public SpringFXProxyBuilder(ApplicationContext context, Class<T> type) {
		super(type);
		this.context = context;
		this.propertiesMap = scanForSetters();
	}

	private final Map<String, Object> userValues = new HashMap<>();

	@Override
	public Object put(String key, Object value) {
		userValues.put(key, value);
		return null; // to behave the same way as ObjectBuilder does
	}

	private final Map<String, Object> containers = new HashMap<>();

	/**
	 * This is used to support read-only collection property. This method must
	 * return a Collection of the appropriate type if 1. the property is
	 * read-only, and 2. the property is a collection. It must return null
	 * otherwise.
	 */
	private Object getTemporaryContainer(String propName) {
		return containers.computeIfAbsent(propName, this::getReadOnlyProperty);
	}

	// Wrapper for ArrayList which we use to store read-only collection
	// properties in
	private static class ArrayListWrapper<T> extends ArrayList<T> {

	}

	// This is used to support read-only collection property.
	private Object getReadOnlyProperty(String propName) {
		// return ArrayListWrapper now and convert it to proper type later
		// during the build - once we know which constructor we will use
		// and what types it accepts
		return new ArrayListWrapper<>();
	}

	@Override
	public Map<String, Object> getDelegateMap() {
		return delegateMap;
	}

	@Override
	public int size() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isEmpty() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsKey(Object key) {
		return (getTemporaryContainer(key.toString()) != null);
	}

	@Override
	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object get(Object key) {
		return getTemporaryContainer(key.toString());
	}

	@Override
	public T build() {
		// adding collection properties to userValues
		this.putAll(containers);

		this.propertyNames = userValues.keySet();

		// constructor with exact match doesn't exist
		Set<String> settersArgs = propertiesMap.keySet();

		// check if all properties can be set by setters
		if (!settersArgs.containsAll(propertyNames)) {
			throw new RuntimeException("Cannot create instance of "
					+ type.getCanonicalName() + " with given set of properties: "
					+ userValues.keySet());
		}

		return createObjectFromContext();
	}

	private T createObjectFromContext() throws RuntimeException {
		T retObj = context.getBean(type);
		if (AnnotationUtils.findAnnotation(type, FXMLAutoLoad.class) != null) {
			SpringFX springFX = context.getBean(SpringFX.class);
			springFX.load(retObj);
		}
		for (String propName : propertyNames) {
			try {
				Property property = propertiesMap.get(propName);
				property.invoke(retObj, getUserValue(propName, property.getType()));
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
		return retObj;
	}

	private Object getUserValue(String key, Class<?> type) {
		Object val = userValues.get(key);
		if (val == null) {
			return null;
		}

		if (type.isAssignableFrom(val.getClass())) {
			return val;
		}

		// we currently don't have proper support for arrays
		// in FXML, so we use lists instead
		// the user provides us with a list and here we convert it to
		// array to pass to the constructor
		if (type.isArray()) {
			try {
				return convertListToArray(val, type);
			} catch (RuntimeException ex) {
				// conversion failed, maybe the ArrayListWrapper is
				// used for storing single value
			}
		}

		if (ArrayListWrapper.class.equals(val.getClass())) {
			// user given value is an ArrayList but the constructor doesn't
			// accept an ArrayList so the ArrayList comes from
			// the getTemporaryContainer method
			// we take the first argument
			List<?> l = (List<?>) val;
			return l.get(0);
		}

		return val;
	}

	private Map<String, Property> scanForSetters() {
		Map<String, Property> strsMap = new HashMap<>();
		Map<String, LinkedList<Method>> methods = getClassMethodCache(type);

		for (String methodName : methods.keySet()) {
			if (methodName.startsWith(SETTER_PREFIX) && methodName.length() > SETTER_PREFIX.length()) {
				String propName = methodName.substring(SETTER_PREFIX.length());
				propName = Character.toLowerCase(propName.charAt(0)) + propName.substring(1);
				List<Method> methodsList = methods.get(methodName);
				for (Method m : methodsList) {
					Class<?> retType = m.getReturnType();
					Class<?>[] argType = m.getParameterTypes();
					if (retType.equals(Void.TYPE) && argType.length == 1) {
						strsMap.put(propName, new Setter(m, argType[0]));
					}
				}
			}
			if (methodName.startsWith(GETTER_PREFIX) && methodName.length() > GETTER_PREFIX.length()) {
				String propName = methodName.substring(GETTER_PREFIX.length());
				propName = Character.toLowerCase(propName.charAt(0)) + propName.substring(1);
				List<Method> methodsList = methods.get(methodName);
				for (Method m : methodsList) {
					Class<?> retType = m.getReturnType();
					Class<?>[] argType = m.getParameterTypes();
					if (Collection.class.isAssignableFrom(retType) && argType.length == 0) {
						strsMap.put(propName, new Getter(m, retType));
					}
				}
			}
		}

		return strsMap;
	}


	private static abstract class Property {
		protected final Method method;
		protected final Class<?> type;

		public Property(Method m, Class<?> t) {
			method = m;
			type = t;
		}

		public Class<?> getType() {
			return type;
		}

		public abstract void invoke(Object obj, Object argStr) throws Exception;
	}

	private static class Setter extends Property {

		public Setter(Method m, Class<?> t) {
			super(m, t);
		}

		public void invoke(Object obj, Object argStr) throws Exception {
			Object[] arg = new Object[]{BeanAdapter.coerce(argStr, type)};
			ModuleHelper.invoke(method, obj, arg);
		}
	}

	private static class Getter extends Property {

		public Getter(Method m, Class<?> t) {
			super(m, t);
		}

		@Override
		@SuppressWarnings("unchecked")
		public void invoke(Object obj, Object argStr) throws Exception {
			// we know that this.method returns collection otherwise it wouldn't be here
			Collection<? super Object> to = (Collection<? super Object>) ModuleHelper.invoke(method, obj, new Object[]{});
			if (argStr instanceof Collection<?> from) {
				to.addAll(from);
			} else {
				to.add(argStr);
			}
		}
	}

	private static HashMap<String, LinkedList<Method>> getClassMethodCache(Class<?> type) {
		HashMap<String, LinkedList<Method>> classMethodCache = new HashMap<>();

		ReflectUtil.checkPackageAccess(type);

		Method[] declaredMethods = type.getMethods();
		for (Method method : declaredMethods) {
			int modifiers = method.getModifiers();

			if (Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers)) {
				classMethodCache.computeIfAbsent(method.getName(), k -> new LinkedList<>())
						.add(method);
			}
		}

		return classMethodCache;
	}

	// Utility method for converting list to array via reflection
	// it assumes that localType is an array
	private static Object[] convertListToArray(Object userValue, Class<?> localType) {
		Class<?> arrayType = localType.getComponentType();
		List<?> l = BeanAdapter.coerce(userValue, List.class);

		return l.toArray((Object[]) Array.newInstance(arrayType, 0));
	}
}
