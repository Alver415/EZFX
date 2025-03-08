package com.ezfx.controls.editor.introspective;

import com.ezfx.controls.editor.EditorBase;
import javafx.beans.property.Property;

public interface DelegatingEditor<T> {

	Property<EditorBase<T>> delegateProperty();

	default EditorBase<T> getDelegate() {
		return this.delegateProperty().getValue();
	}

	default void setDelegate(EditorBase<T> value) {
		this.delegateProperty().setValue(value);
	}
}
