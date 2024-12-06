package com.ezfx.controls.editor.introspective;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class CachingIntrospector
		//TODO: Create a dynamic proxy instead of writing this out all by hand.
//		implements Introspector
{

	private static final Logger log = LoggerFactory.getLogger(CachingIntrospector.class);

	private final Introspector delegate;
	private final Map<Class<?>, List<PropertyInfo>> propertyInfoCache = new HashMap<>();
	private final Map<Class<?>, List<Field>> fieldsCache = new HashMap<>();
	private final Map<Class<?>, List<Method>> methodsCache = new HashMap<>();
	private final Map<Class<?>, List<? extends Constructor<?>>> constructorsCache = new HashMap<>();


	public CachingIntrospector(Introspector delegate) {
		this.delegate = delegate;
	}

	public <T> List<PropertyInfo> getPropertyInfo(Class<T> type) {
		return propertyInfoCache.computeIfAbsent(type, delegate::getPropertyInfo);
	}

	public <T> List<Field> getFields(Class<T> type) {
		return fieldsCache.computeIfAbsent(type, _ -> delegate.getFields(type));
	}

	public <T> List<Method> getMethods(Class<T> type) {
		return methodsCache.computeIfAbsent(type, _ -> delegate.getMethods(type));
	}

	public <T> List<Constructor<T>> getConstructors(Class<T> type) {
		return (List<Constructor<T>>) constructorsCache.computeIfAbsent(type, _ -> delegate.getConstructors(type));
	}
}
