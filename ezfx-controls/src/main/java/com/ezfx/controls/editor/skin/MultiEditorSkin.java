package com.ezfx.controls.editor.skin;

import com.ezfx.controls.editor.EditorBase;
import com.ezfx.controls.editor.EditorSkinBase;
import com.ezfx.controls.editor.EditorView;
import com.ezfx.controls.editor.MultiEditor;
import javafx.collections.FXCollections;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public interface MultiEditorSkin {

	class VerticalEditorSkin<E extends EditorBase<T> & MultiEditor<T>, T> extends EditorSkinBase<E, T> {

		private final VBox vBox;

		public VerticalEditorSkin(E editor) {
			super(editor);

			vBox = new VBox();
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

			hBox = new HBox();
			getChildren().setAll(hBox);

			editor.editorsProperty()
					.map(editors -> editors.stream().map(EditorView::new).toList())
					.orElse(FXCollections.observableArrayList())
					.subscribe(editors -> hBox.getChildren().setAll(editors));
		}
	}
}
