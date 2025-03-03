package com.ezfx.controls.popup;

import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.impl.standard.StringEditor;
import com.ezfx.controls.editor.skin.EditorSkin;
import javafx.scene.Node;

public class NodeMiniEditor extends Editor<Node> {

	public static class NodeMiniEditorSkin extends EditorSkin<NodeMiniEditor, Node> {

		private final StringEditor idEditor;
		public NodeMiniEditorSkin(NodeMiniEditor editor) {
			super(editor);
			idEditor = new StringEditor();
			idEditor.valueProperty().subscribe(id -> editor.getValue().setId(id));

			getChildren().setAll(idEditor);
		}
	}
}
