package com.ezfx.controls.editor.skin;

import com.ezfx.controls.editor.*;
import javafx.collections.FXCollections;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public interface MultiEditorSkin {

	//TODO: Remove code duplication between Vertical and Horizontal. Simplify.
	class VerticalEditorSkin<E extends EditorBase<T> & MultiEditor<T>, T> extends EditorSkinBase<E, T> {

		private final VBox vBox;

		public VerticalEditorSkin(E editor) {
			super(editor);

			vBox = new VBox(2);
			getChildren().setAll(vBox);

			editor.editorsProperty()
					.map(editors -> editors.stream().map(EditorView::new).toList())
					.orElse(FXCollections.observableArrayList())
					.subscribe(editors -> vBox.getChildren().setAll(editors));
		}
	}

	class HorizontalEditorSkin<E extends EditorBase<T> & MultiEditor<T>, T> extends EditorSkinBase<E, T> implements MultiEditorSkin {

		private final HBox hBox;

		public HorizontalEditorSkin(E editor) {
			super(editor);

			hBox = new HBox(2);
			getChildren().setAll(hBox);

			editor.editorsProperty()
					.map(editors -> editors.stream().map(EditorView::new).toList())
					.orElse(FXCollections.observableArrayList())
					.subscribe(editors -> hBox.getChildren().setAll(editors));
		}
	}
}
