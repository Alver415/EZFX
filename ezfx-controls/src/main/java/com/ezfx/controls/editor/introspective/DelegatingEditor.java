package com.ezfx.controls.editor.introspective;

import com.ezfx.controls.editor.Editor;
import javafx.beans.property.Property;

public interface DelegatingEditor<T> {

	Property<Editor<T>> delegateProperty();

	default Editor<T> getDelegate() {
		return this.delegateProperty().getValue();
	}

	default void setDelegate(Editor<T> value) {
		this.delegateProperty().setValue(value);
	}
}
