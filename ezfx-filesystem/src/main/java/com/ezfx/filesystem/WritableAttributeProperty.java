package com.ezfx.filesystem;

import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableObjectValue;

public abstract class WritableAttributeProperty<T, S>
		extends ReadableAttributeProperty<T, S>
		implements Property<T>, WritableObjectValue<T> {

	public WritableAttributeProperty(FileSystemEntry entry, String name) {
		this(entry, name, null);
	}

	public WritableAttributeProperty(FileSystemEntry entry, String name, T initialValue) {
		super(entry, name, initialValue);
	}

	@Override
	public void set(T newValue) {
		super.set(newValue);
	}

	@Override
	public void setValue(T newValue) {
		super.setValue(newValue);
	}

	@Override
	public void bindBidirectional(Property<T> other) {
		Bindings.bindBidirectional(this, other);
	}

	@Override
	public void unbindBidirectional(Property<T> other) {
		Bindings.unbindBidirectional(this, other);
	}

	@Override
	public void bind(final ObservableValue<? extends T> newObservable) {
		super.bind(newObservable);
	}

	@Override
	public void unbind() {
		super.unbind();
	}

	@Override
	public boolean isBound() {
		return super.isBound();
	}

}
