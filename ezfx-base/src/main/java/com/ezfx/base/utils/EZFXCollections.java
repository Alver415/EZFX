package com.ezfx.base.utils;

import com.ezfx.base.observable.ObservableObjectArray;
import com.ezfx.base.observable.ObservableObjectArrayImpl;

import java.lang.reflect.Array;
import java.util.Objects;

@SuppressWarnings("unchecked")
public interface EZFXCollections {

	static <E> ObservableObjectArray<E> observableObjectArray(Class<E> componentType) {
		return observableObjectArray(componentType, (E[]) Array.newInstance(componentType, 0));
	}

	static <E> ObservableObjectArray<E> observableObjectArray(Class<E> componentType, E[] array) {
		Objects.requireNonNull(array);
		return new ObservableObjectArrayImpl<>(componentType, array);
	}
}
