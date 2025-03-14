package com.ezfx.base.utils;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class ObservableConstant<T> implements ObservableValue<T> {

	public static final ObservableValue<Object> NONE = new ObservableConstant<>(null);
	public static final ObservableValue<Boolean> TRUE = new ObservableConstant<>(true);
	public static final ObservableValue<Boolean> FALSE = new ObservableConstant<>(false);

	private final T value;

	private ObservableConstant(T value) {
		this.value = value;
	}

	public static <T> ObservableConstant<T> constant(T value) {
		return CACHE.of(value);
	}

	@SuppressWarnings("unchecked")
	public static <T> ObservableValue<T> none() {
		return (ObservableValue<T>) NONE;
	}

	@Override
	public T getValue() {
		return value;
	}

	@Override
	public void addListener(ChangeListener<? super T> listener) {
		//Do Nothing
	}

	@Override
	public void removeListener(ChangeListener<? super T> listener) {
		//Do Nothing
	}

	@Override
	public void addListener(InvalidationListener listener) {
		//Do Nothing
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		//Do Nothing
	}

	private static final ICache CACHE = CachedProxy.wrap(new Cache(), ICache.class);

	private static class Cache implements ICache {
		public <T> ObservableConstant<T> of(T value) {
			return new ObservableConstant<>(value);
		}
	}

	private interface ICache {
		<T> ObservableConstant<T> of(T value);

	}
}
