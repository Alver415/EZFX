package com.ezfx.controls.editor.introspective;

import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.skin.EditorSkin;

public class DelegatingEditorSkin<E extends Editor<T> & DelegatingEditor<T>, T> extends EditorSkin<E, T> {

	public DelegatingEditorSkin(E control) {
		super(control);
		control.delegateProperty().subscribe(delegate -> {
			if (delegate == null) getChildren().clear();
			else getChildren().setAll(delegate);
		});
	}
}
