package com.ezfx.controls.item;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class FXItemBase<T, C extends FXItem<?, ?>> implements FXItem<T, C> {
	protected final FXItemFactory factory;
	protected final T item;
	protected final ObservableList<C> children = FXCollections.observableArrayList();
	protected final Property<Boolean> visible = new SimpleBooleanProperty();

	protected FXItemBase(FXItemFactory factory, T item) {
		this.factory = factory;
		this.item = item;
	}

	@Override
	public T get() {
		return item;
	}

	@Override
	public ObservableList<? extends C> getChildren() {
		return children;
	}

	public String getId() {
		return null;
	}

	public List<String> getStyleClass() {
		return List.of();
	}

	public Property<Boolean> visibleProperty() {
		return visible;
	}
}