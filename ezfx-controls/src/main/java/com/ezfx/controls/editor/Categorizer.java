package com.ezfx.controls.editor;

import javafx.collections.ObservableMap;

public interface Categorizer {

	<T, R> ObservableMap<Category, R> categorize(T target);
}
