package com.ezfx.controls.item;

import javafx.beans.property.Property;
import javafx.collections.ObservableList;

import java.util.List;

public interface FXItem<T, C extends FXItem<?, ?>> {

	T get();

	ObservableList<? extends C> getChildren();

	String getId();

	List<String> getStyleClass();

	Property<Boolean> visibleProperty();

}
