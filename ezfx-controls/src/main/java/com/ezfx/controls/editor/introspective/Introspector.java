package com.ezfx.controls.editor.introspective;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

public interface Introspector {
	<T> T getDefaultValueForType(Class<T> type);

	<T> List<PropertyInfo> getPropertyInfo(Class<T> type);

	<T> List<Field> getFields(Class<T> type);
	<T> List<Method> getMethods(Class<T> type);
	<T> List<Constructor<T>> getConstructors(Class<T> type);

	String getParameterName(Parameter parameter);
	String getParameterTypeName(Parameter parameter);

}
