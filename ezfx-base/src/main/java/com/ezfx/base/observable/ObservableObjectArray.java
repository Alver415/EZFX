package com.ezfx.base.observable;

import javafx.collections.ObservableArray;


public interface ObservableObjectArray<T> extends ObservableArray<ObservableObjectArray<T>> {

	void copyTo(int srcIndex, T[] dest, int destIndex, int length);

	void copyTo(int srcIndex, ObservableObjectArray<T> dest, int destIndex, int length);

	T get(int index);

	void addAll(T... elements);

	void addAll(ObservableObjectArray<T> src);

	void addAll(T[] src, int srcIndex, int length);

	void addAll(ObservableObjectArray<T> src, int srcIndex, int length);

	void setAll(T... elements);

	void setAll(T[] src, int srcIndex, int length);

	void setAll(ObservableObjectArray<T> src);

	void setAll(ObservableObjectArray<T> src, int srcIndex, int length);

	void set(int destIndex, T[] src, int srcIndex, int length);

	void set(int destIndex, ObservableObjectArray<T> src, int srcIndex, int length);

	void set(int index, T value);

	T[] toArray(T[] dest);

	T[] toArray(int srcIndex, T[] dest, int length);

}
