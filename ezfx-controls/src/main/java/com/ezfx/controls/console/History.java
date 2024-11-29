package com.ezfx.controls.console;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

import java.util.Objects;

public class History<T> {
	protected final ListProperty<T> items =
			new SimpleListProperty<>(this, "items", FXCollections.observableArrayList());

	protected final IntegerProperty index =
			new SimpleIntegerProperty(this, "index", 0);

	public void addItem(T item) {
		Objects.requireNonNull(item);
		items.add(item);
		index.set(items.size());
	}

	public T get() {
		return get(index.get());
	}

	public T get(int index) {
		if (items.isEmpty() || index > items.size() - 1) {
			return null;
		}
		int clamped = Math.max(0, Math.min(index, items.size() - 1));
		return items.get(clamped);
	}

	public int getIndex() {
		return index.get();
	}

	public void setIndex(int newIndex) {
		newIndex = Math.max(0, Math.min(newIndex, items.size()));
		index.set(newIndex);
		get();
	}

	public T step(int delta) {
		setIndex(getIndex() + delta);
		return get();
	}
}