package com.ezfx.controls.editor.introspective;

import com.ezfx.controls.editor.EditorBase;
import com.ezfx.controls.editor.EditorSkinBase;

public class DelegatingEditorSkin<E extends EditorBase<T> & DelegatingEditor<T>, T> extends EditorSkinBase<E, T> {

	public DelegatingEditorSkin(E control) {
		super(control);
		control.delegateProperty().subscribe(delegate -> {
			if (delegate == null) getChildren().clear();
			else getChildren().setAll(delegate);
		});
	}
}
