package com.ezfx.base.introspector;

import javafx.beans.property.Property;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
public class StandardIntrospector implements Introspector {

	private static final Logger log = LoggerFactory.getLogger(StandardIntrospector.class);

	//TODO: Initialize off of application thread, ideally in parallel during startup.
	private static final Reflections reflections = new Reflections(new ConfigurationBuilder()
			.forPackages("com") // TODO: Scan all packages instead of just 'com'
			.addScanners(Scanners.values()));

	@Override
	public List<PropertyInfo> getDeclaredPropertyInfo(Type type) {
		return type instanceof Class<?> clazz ?
				_getPropertyInfo(type, clazz.getDeclaredMethods()) :
				List.of();
	}

	@Override
	public List<PropertyInfo> getPropertyInfo(Type type) {
		return type instanceof Class<?> clazz ?
				_getPropertyInfo(type, clazz.getMethods()) :
				List.of();
	}

	private List<PropertyInfo> _getPropertyInfo(Type type, Method[] methods) {
		if (type == null) {
			return List.of();
		}
		List<PropertyInfo> propertyInfoList = new ArrayList<>();

		Map<String, Method> setMethods = new HashMap<>();
		Map<String, Method> getMethods = new HashMap<>();
		Map<String, Method> propertyMethods = new HashMap<>();

		for (Method method : methods) {
			String methodName = method.getName();
			boolean endsWithProperty = methodName.endsWith("Property");
			boolean assignableFromProperty = Property.class.isAssignableFrom(method.getReturnType());
			if (endsWithProperty && assignableFromProperty) {
				String rootName = methodName.substring(0, methodName.length() - "Property".length());
				propertyMethods.put(Introspector.decapitalize(rootName), method);
			}
			boolean startsWithSet = methodName.startsWith("set");
			boolean singleParameter = method.getParameterCount() == 1;
			if (startsWithSet && singleParameter) {
				String rootName = methodName.substring("set".length());
				setMethods.put(Introspector.decapitalize(rootName), method);
			}
			boolean startsWithGet = methodName.startsWith("get");
			boolean startsWithIs = methodName.startsWith("is");
			boolean hasReturnType = method.getReturnType() != Void.class;
			boolean zeroParameters = method.getParameterCount() == 0;
			if (hasReturnType && zeroParameters) {
				if (startsWithGet) {
					String rootName = methodName.substring("get".length());
					getMethods.put(Introspector.decapitalize(rootName), method);
				} else if (startsWithIs) {
					String rootName = methodName.substring("is".length());
					getMethods.put(Introspector.decapitalize(rootName), method);
				}
			}
		}

		for (String name : propertyMethods.keySet()) {
			Method property = propertyMethods.get(name);
			Method setter = setMethods.get(name);
			Method getter = getMethods.get(name);

			Optional<PropertyMetadata> propertyDetails = Optional.ofNullable(property.getAnnotation(PropertyMetadata.class));
			if (propertyDetails.map(PropertyMetadata::ignore).orElse(false)) continue;

			String displayName = propertyDetails.map(PropertyMetadata::displayName)
					.orElse(name);

			Class<?> declaringClass = property.getDeclaringClass();
			String categoryTitle = propertyDetails
					.map(PropertyMetadata::categoryTitle)
					.orElse(declaringClass.getSimpleName());

			int depth = getSuperClassesCount(declaringClass);
			int categoryOrder = propertyDetails
					.map(PropertyMetadata::categoryOrder)
					.orElse(depth);
			Category category = Category.of(categoryTitle, categoryOrder);

			int order = propertyDetails.map(PropertyMetadata::order)
					.orElse(PropertyMetadata.DEFAULT_ORDER);

			if (setter == null && getter == null) {
				log.debug("Ignoring {} because there is neither a get method or a set method.", name);
				continue;
			}

			propertyInfoList.add(new PropertyInfo(
					name, displayName, category, order,
					property, setter, getter));
		}

		return propertyInfoList;
	}

	private static int getSuperClassesCount(Class<?> declaringClass) {
		int depth = 0;
		Class<?> clazz = declaringClass;
		while ((clazz = clazz.getSuperclass()) != null) {
			depth++;
		}
		return depth;
	}

	public boolean checkModifiers(Member member, int... modifiers) {
		return checkModifiers(member.getModifiers(), modifiers);
	}

	public boolean checkModifiers(int modifier, int... modifiers) {
		return Arrays.stream(modifiers)
				.mapToObj(m -> (modifier & m) != 0)
				.reduce((a, b) -> a && b)
				.orElse(true);
	}

	@Override
	public List<Field> getFields(Type type) {
		return type instanceof Class<?> clazz ?
				Arrays.stream(clazz.getFields()).toList() :
				List.of();
	}

	@Override
	public List<Method> getMethods(Type type) {
		return type instanceof Class<?> clazz ?
				Arrays.stream(clazz.getMethods()).toList() :
				List.of();
	}

	@Override
	public <T> List<Constructor<T>> getConstructors(Type type) {
		return type instanceof Class<?> clazz ?
				Stream.concat(Stream.of(clazz), reflections.getSubTypesOf(clazz).stream()
								.filter(c -> Modifier.isPublic(c.getModifiers()))
								.filter(c -> !c.isMemberClass() || Modifier.isStatic(c.getModifiers())))
						.map(Class::getConstructors)
						.flatMap(Arrays::stream)
						.map(constructor -> (Constructor<T>) constructor)
						.toList() :
				List.of();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <S> S getDefaultValueForType(Type type) {
		if (byte.class.equals(type) || Byte.class.equals(type)) {
			return (S) (Byte) (byte) 0;
		} else if (short.class.equals(type) || Short.class.equals(type)) {
			return (S) (Short) (short) 0;
		} else if (int.class.equals(type) || Integer.class.equals(type)) {
			return (S) (Integer) 0;
		} else if (long.class.equals(type) || Long.class.equals(type)) {
			return (S) (Long) 0L;
		} else if (float.class.equals(type) || Float.class.equals(type)) {
			return (S) (Float) 0f;
		} else if (double.class.equals(type) || Double.class.equals(type)) {
			return (S) (Double) 0d;
		} else if (boolean.class.equals(type) || Boolean.class.equals(type)) {
			return (S) (Boolean) false;
		} else if (char.class.equals(type) || Character.class.equals(type)) {
			return (S) (Character) '\u0000';
		} else if (String.class.equals(type)) {
			return (S) "";
		} else if (type instanceof Class<?> clazz && clazz.isEnum()) {
			S[] constants = (S[]) clazz.getEnumConstants();
			return constants.length == 0 ? null : constants[0];
		}
		return null;
	}

	public String getParameterTypeName(Parameter parameter) {
		Type type = parameter.getParameterizedType();
		if (type instanceof ParameterizedType parameterizedType) {
			return getParameterizedTypeName(parameterizedType);
		} else if (type instanceof Class<?> clazz) {
			return clazz.getSimpleName();
		} else {
			return type.getTypeName();
		}
	}

	public String getParameterizedTypeName(ParameterizedType parameterizedType) {
		StringBuilder typeName = new StringBuilder();

		// Raw type (e.g., List, Map)
		typeName.append(((Class<?>) parameterizedType.getRawType()).getSimpleName());

		// Generic arguments (e.g., <String>, <String, List<String>>)
		Type[] typeArguments = parameterizedType.getActualTypeArguments();
		if (typeArguments.length > 0) {
			typeName.append("<");
			for (int i = 0; i < typeArguments.length; i++) {
				if (i > 0) typeName.append(", ");
				if (typeArguments[i] instanceof Class<?>) {
					// Simple type like String
					typeName.append(((Class<?>) typeArguments[i]).getSimpleName());
				} else if (typeArguments[i] instanceof ParameterizedType) {
					// Nested parameterized type like List<String>
					typeName.append(getParameterizedTypeName((ParameterizedType) typeArguments[i]));
				} else {
					typeName.append(typeArguments[i].getTypeName());
				}
			}
			typeName.append(">");
		}

		return typeName.toString();
	}

	/**
	 * Will usually just return arg0, arg1, etc. unless the class was compiled with the '-parameters' flag.
	 */
	@Override
	public String getParameterName(Parameter parameter) {
		return parameter.getName();
	}
}
