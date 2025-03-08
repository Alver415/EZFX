package com.ezfx.controls.popup;

import com.ezfx.controls.editor.EditorBase;
import com.ezfx.controls.editor.impl.standard.StringEditor;
import com.ezfx.controls.editor.EditorSkinBase;
import javafx.scene.Node;

public class NodeMiniEditor extends EditorBase<Node> {

	public static class NodeMiniEditorSkin extends EditorSkinBase<NodeMiniEditor, Node> {

		private final StringEditor idEditor;
		public NodeMiniEditorSkin(NodeMiniEditor editor) {
			super(editor);
			idEditor = new StringEditor();
			idEditor.valueProperty().subscribe(id -> editor.getValue().setId(id));

			getChildren().setAll(idEditor);
		}
	}
}
