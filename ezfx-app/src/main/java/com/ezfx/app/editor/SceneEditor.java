package com.ezfx.app.editor;

import com.ezfx.controls.editor.ObjectEditor;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Skin;

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