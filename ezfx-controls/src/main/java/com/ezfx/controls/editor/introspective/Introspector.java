package com.ezfx.controls.editor.introspective;

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

}
