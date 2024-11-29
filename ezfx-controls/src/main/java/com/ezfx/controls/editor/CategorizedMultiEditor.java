package com.ezfx.controls.editor;

import javafx.beans.property.MapProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public interface CategorizedMultiEditor<T> extends MultiEditor<T> {


	MapProperty<Category, ObservableList<Editor<?>>> categorizedEditorsProperty();

	default ObservableMap<Category, ObservableList<Editor<?>>> getCategorizedEditors() {
		return this.categorizedEditorsProperty().get();
	}

	default void setCategorizedEditors(ObservableMap<Category, ObservableList<Editor<?>>> value) {
		this.categorizedEditorsProperty().set(value);
	}

}
