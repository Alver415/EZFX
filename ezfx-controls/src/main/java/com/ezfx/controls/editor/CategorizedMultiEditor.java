package com.ezfx.controls.editor;

import com.ezfx.base.introspector.Category;
import javafx.beans.property.MapProperty;
import javafx.collections.ObservableMap;

public interface CategorizedMultiEditor<T, S extends Editor<T>> extends MultiEditor<T> {

	MapProperty<Category, S> categorizedEditorsProperty();

	default ObservableMap<Category, S> getCategorizedEditors() {
		return this.categorizedEditorsProperty().get();
	}

	default void setCategorizedEditors(ObservableMap<Category, S> value) {
		this.categorizedEditorsProperty().set(value);
	}

}
