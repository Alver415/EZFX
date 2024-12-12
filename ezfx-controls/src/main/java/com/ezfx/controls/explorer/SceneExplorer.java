package com.ezfx.controls.explorer;

import com.ezfx.base.utils.Screens;
import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.EditorFactory;
import com.ezfx.controls.editor.impl.standard.BooleanEditor;
import com.ezfx.controls.editor.impl.standard.StringEditor;
import com.ezfx.controls.editor.introspective.IntrospectingPropertiesEditor;
import com.ezfx.controls.editor.introspective.Introspector;
import com.ezfx.controls.editor.introspective.PropertyInfo;
import com.ezfx.controls.editor.introspective.StandardIntrospector;
import com.ezfx.controls.misc.FilterableTreeItem;
import com.ezfx.controls.nodetree.NodeTreeItem;
import com.ezfx.controls.nodetree.NodeTreeView;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.lang.reflect.InvocationTargetException;

public class SceneExplorer extends Control {

	public static void stage(Scene scene) {
		stage(scene, 2);
	}

	public static void stage(Scene scene, int screenIndex) {
		Stage stage = new Stage();
		Screens.setScreen(stage, screenIndex);

		SceneExplorer sceneExplorer = new SceneExplorer(scene);
		sceneExplorer.setPrefSize(1200, 800);

		stage.setScene(new Scene(sceneExplorer));
		stage.setTitle("Scene Explorer");
		stage.show();
	}

	public SceneExplorer(Scene scene) {
		rootProperty().bind(scene.rootProperty());
	}

	private final ObjectProperty<Node> root = new SimpleObjectProperty<>(this, "root");

	public ObjectProperty<Node> rootProperty() {
		return this.root;
	}

	public Node getRoot() {
		return this.rootProperty().get();
	}

	public void setRoot(Node value) {
		this.rootProperty().set(value);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new SceneExplorerTreeSkin(this);
	}

	public static class SceneExplorerTreeSkin extends SkinBase<SceneExplorer> {

		protected SceneExplorerTreeSkin(SceneExplorer sceneExplorer) {
			super(sceneExplorer);

			StringEditor filterEditor = new StringEditor();
			Property<String> filterProperty = filterEditor.property();
			NodeTreeView treeView = new NodeTreeView();
			treeView.rootProperty().bind(sceneExplorer.rootProperty().map(NodeTreeItem::new));
			treeView.rootProperty().subscribe(treeRoot -> {
				if (treeRoot instanceof FilterableTreeItem<Node> root) {
					root.predicateProperty().bind(Bindings.createObjectBinding(() -> node -> {
						if (filterProperty.getValue() == null) return true;
						String filterText = filterProperty.getValue().toLowerCase();

						if (filterText.startsWith("#")) {
							if (node.getId() != null && node.getId().toLowerCase().contains(filterText.substring(1))) {
								return true;
							}
						} else if (filterText.startsWith(".")) {
							for (String styleClass : node.getStyleClass()) {
								if (styleClass.toLowerCase().contains(filterText.substring(1))) {
									return true;
								}
							}
						} else {
							if (node.getClass().getSimpleName().toLowerCase().contains(filterText)) {
								return true;
							}
						}
						return false;
					}, filterProperty));
				}
			});

			Editor<Node> beanEditor = new IntrospectingPropertiesEditor<>();
			treeView.getSelectionModel().select(0);
			beanEditor.property().bind(treeView.selectionModelProperty().flatMap(SelectionModel::selectedItemProperty).map(TreeItem::getValue));
			ScrollPane right = new ScrollPane(beanEditor);
			right.setFitToWidth(true);
			right.setFitToHeight(true);

			VBox left = new VBox(filterEditor, treeView);
			VBox.setVgrow(treeView, Priority.ALWAYS);
			SplitPane splitPane = new SplitPane(left, right);
			getChildren().setAll(splitPane);
		}
	}
}
