package com.ezfx.controls.editor;

import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.collections.ObservableList;

public interface MultiEditor<T> extends Editor<T> {

	ListProperty<Editor<?>> editorsProperty();

	default ObservableList<Editor<?>> getEditors() {
		return this.editorsProperty().get();
	}

	default void setEditors(ObservableList<Editor<?>> value) {
		this.editorsProperty().set(value);
	}

}
