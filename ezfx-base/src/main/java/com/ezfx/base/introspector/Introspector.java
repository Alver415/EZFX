package com.ezfx.base.introspector;

import java.lang.reflect.*;
import java.util.List;

public interface Introspector {
	<T> T getDefaultValueForType(Type type);

	List<PropertyInfo> getDeclaredPropertyInfo(Type type);
	List<PropertyInfo> getPropertyInfo(Type type);

	List<Field> getFields(Type type);
	List<Method> getMethods(Type type);
	<T> List<Constructor<T>> getConstructors(Type type);

	String getParameterName(Parameter parameter);
	String getParameterTypeName(Parameter parameter);

	static String decapitalize(String name) {
		if (name == null || name.isEmpty()) {
			return name;
		}
		if (name.length() > 1 && Character.isUpperCase(name.charAt(1)) &&
				Character.isUpperCase(name.charAt(0))){
			return name;
		}
		char[] chars = name.toCharArray();
		chars[0] = Character.toLowerCase(chars[0]);
		return new String(chars);
	}
}
