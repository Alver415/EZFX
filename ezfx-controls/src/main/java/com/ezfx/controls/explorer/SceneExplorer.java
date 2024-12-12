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
import com.ezfx.controls.nodetree.NodeTreeTableView;
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


	public static class SceneExplorerTreeTableSkin extends SkinBase<SceneExplorer> {

		protected SceneExplorerTreeTableSkin(SceneExplorer sceneExplorer) {
			super(sceneExplorer);

			NodeTreeTableView treeView = new NodeTreeTableView();
			treeView.rootProperty().bind(sceneExplorer.rootProperty().map(NodeTreeItem::new));
			treeView.getSelectionModel().select(0);

			TreeTableColumn<Node, BooleanEditor> visible = new TreeTableColumn<>("Visible");
			visible.setCellValueFactory(cdf -> cdf.getValue().valueProperty().map(Node::visibleProperty).map(BooleanEditor::new));
			treeView.getColumns().add(visible);

			Editor<Node> beanEditor = new IntrospectingPropertiesEditor<>();
			beanEditor.property().bind(treeView.selectionModelProperty().flatMap(SelectionModel::selectedItemProperty).map(TreeItem::getValue));
			ScrollPane scrollPane = new ScrollPane(beanEditor);
			scrollPane.setFitToWidth(true);
			scrollPane.setFitToHeight(true);

			SplitPane splitPane = new SplitPane(treeView, scrollPane);
			getChildren().setAll(splitPane);

			ContextMenu contextMenu = new ContextMenu();
			Introspector introspector = new StandardIntrospector();
			EditorFactory editorFactory = new EditorFactory();
			Node target = sceneExplorer.getRoot();
			for (PropertyInfo propertyInfo : introspector.getPropertyInfo(target.getClass())) {
				MenuItem menuItem = new MenuItem(propertyInfo.displayName());
				menuItem.setOnAction(a -> {
					TreeTableColumn<Node, Editor<?>> editorColumn = new TreeTableColumn<>(propertyInfo.displayName());
					editorColumn.setCellValueFactory(cdf -> cdf.getValue().valueProperty()
							.map(node -> buildEditor(propertyInfo, node, introspector, editorFactory)));
					treeView.getColumns().add(editorColumn);
				});
				contextMenu.getItems().add(menuItem);
			}
			treeView.setContextMenu(contextMenu);
		}

		private static <T> Editor<T> buildEditor(PropertyInfo propertyInfo, Node node, Introspector introspector, EditorFactory editorFactory) {
			try {
				//noinspection unchecked
				Property<T> subProperty = (Property<T>) propertyInfo.property().invoke(node);
				return editorFactory.buildEditor(propertyInfo, subProperty);
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
	}


}
