package com.ezfx.app.editor;

import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.introspective.IntrospectingPropertiesEditor;
import com.ezfx.controls.nodetree.NodeTreeItem;
import com.ezfx.controls.nodetree.NodeTreeView;
import com.ezfx.controls.viewport.Viewport;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.Skin;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.StackPane;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SceneEditor extends ObjectEditor<Node> {

	private final NodeTreeView treeView = new NodeTreeView();
	private final Viewport viewport = new Viewport();

	public SceneEditor() {
		treeView.rootProperty().bind(target.map(NodeTreeItem::new));
		viewport.contentProperty().bind(target.map(t -> t instanceof Parent p ? p : new StackPane(t)).orElse(new StackPane()));

		ExecutorService targetThreadExecutor = Executors.newSingleThreadExecutor();
		EventStreams.valuesOf(treeView.getSelectionModel().selectedItemProperty())
				.threadBridgeFromFx(targetThreadExecutor)
				.map(TreeItem::getValue)
				.map(IntrospectingPropertiesEditor::new)
				.threadBridgeToFx(targetThreadExecutor)
				.subscribe(this::setEditor);

		treeView.rootProperty().subscribe(treeView.getSelectionModel()::select);
	}

	public NodeTreeView getTreeView() {
		return treeView;
	}

	public Viewport getViewport() {
		return viewport;
	}

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

	private final ObjectProperty<Editor<Node>> editor = new SimpleObjectProperty<>(this, "editor");

	public ObjectProperty<Editor<Node>> editorProperty() {
		return this.editor;
	}

	public Editor<Node> getEditor() {
		return this.editorProperty().get();
	}

	public void setEditor(Editor<Node> value) {
		this.editorProperty().set(value);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new SceneEditorSkin(this);
	}

}