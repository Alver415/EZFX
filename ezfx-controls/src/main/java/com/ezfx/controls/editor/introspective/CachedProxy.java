package com.ezfx.controls.editor.introspective;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CachedProxy<T> implements InvocationHandler {

	private static final Logger log = LoggerFactory.getLogger(CachedProxy.class);
	private final T target;

	public CachedProxy(T target) {
		this.target = target;
	}

	public static <T> T wrap(T target, Class<?>... interfaces) {
		//noinspection unchecked
		return (T) Proxy.newProxyInstance(
				target.getClass().getClassLoader(),
				interfaces,
				new CachedProxy<>(target));
	}

	private final Map<Method, Map<List<Object>, Object>> cachedResults = new ConcurrentHashMap<>();
	private final List<Object> NULL = List.of();

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		List<Object> argList = args == null ? NULL : Arrays.asList(args);
		return cachedResults
				.computeIfAbsent(method, _ -> new ConcurrentHashMap<>())
				.computeIfAbsent(argList, _ -> invoke(method, args));
	}

	private Object invoke(Method method, Object[] args) {
		log.debug("%s.%s(%s)".formatted(
				target.getClass().getSimpleName(),
				method.getName(),
				Arrays.toString(args)));
		try {
			return method.invoke(target, args);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

}
