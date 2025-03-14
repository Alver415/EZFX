package com.ezfx.controls.item;

import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

public interface FXItem<T, C extends FXItem<?, ?>> {

	T get();

	ObservableList<? extends C> getChildren();

	ObservableValue<Image> getThumbnailIcon();

	ObservableValue<String> getPrimaryInfo();

	ObservableValue<String> getSecondaryInfo();

	ObservableValue<String> getTertiaryInfo();

	Property<Boolean> visibleProperty();

}
