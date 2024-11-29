package com.ezfx.controls.editor.skin;

import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.EditorWrapper;
import com.ezfx.controls.editor.MultiEditor;
import javafx.collections.FXCollections;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiEditorSkin<E extends Editor<T> & MultiEditor<T>, T> extends EditorSkin<E, T> {

	private static final Logger log = LoggerFactory.getLogger(MultiEditorSkin.class);

	public MultiEditorSkin(E control) {
		super(control);

		VBox vBox = new VBox();
		getChildren().setAll(vBox);

		control.editorsProperty()
				.map(editors -> editors.stream().map(EditorWrapper::new).toList())
				.orElse(FXCollections.observableArrayList())
				.subscribe(editors -> vBox.getChildren().setAll(editors));
	}
}
