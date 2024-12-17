package com.ezfx.app.editor;

import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.introspective.IntrospectingPropertiesEditor;
import com.ezfx.controls.nodetree.NodeTreeItem;
import com.ezfx.controls.nodetree.NodeTreeView;
import com.ezfx.controls.viewport.Viewport;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Skin;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.StackPane;
import org.reactfx.EventStreams;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SceneEditor extends ObjectEditor<Node> {


	public SceneEditor() {}

	private final ObjectProperty<Node> target = new SimpleObjectProperty<>(this, "target");

	public ObjectProperty<Node> targetProperty() {
		return this.target;
	}

	public Node getTarget() {
		return this.targetProperty().get();
	}

	public void setTarget(Node value) {
		this.targetProperty().set(value);
	}
	@Override
	protected Skin<?> createDefaultSkin() {
		return new SceneEditorSkin(this);
	}

}