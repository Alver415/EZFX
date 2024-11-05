package com.fsfx.control.explorer;

import com.fsfx.control.editor.EditorControl;
import com.fsfx.control.editor.EditorControlFactory;
import com.fsfx.control.editor.IntrospectingBeanEditor;
import com.fsfx.control.editor.PropertyInfo;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class SceneExplorer extends Control {

	public static void stage(Scene scene) {
		Stage stage = new Stage();
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
		return new SceneExplorerSkin(this);
	}

	public static class SceneExplorerSkin extends SkinBase<SceneExplorer> {

		protected SceneExplorerSkin(SceneExplorer sceneExplorer) {
			super(sceneExplorer);
			BorderPane borderPane = new BorderPane();

			TreeTableView<Node> treeTableView = new TreeTableView<>();
			treeTableView.rootProperty().bind(sceneExplorer.rootProperty().map(NodeTreeItem::new));
			treeTableView.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

			TreeTableColumn<Node, String> nodeColumn = new TreeTableColumn<>("Node");
			nodeColumn.setCellValueFactory(n -> Bindings.createObjectBinding(() -> n)
					.map(TreeTableColumn.CellDataFeatures::getValue)
					.map(TreeItem::getValue)
					.map(Node::getClass)
					.map(Class::getSimpleName));
			treeTableView.getColumns().add(nodeColumn);
			borderPane.setLeft(treeTableView);

			IntrospectingBeanEditor<Node> beanEditor = new IntrospectingBeanEditor<>();

			treeTableView.getSelectionModel().select(0);
			beanEditor.getProperty().bind(treeTableView.selectionModelProperty().flatMap(SelectionModel::selectedItemProperty).map(TreeItem::getValue));
			ScrollPane scrollPane = new ScrollPane(beanEditor);
			scrollPane.setFitToWidth(true);
			scrollPane.setFitToHeight(true);
			borderPane.setCenter(scrollPane);

			getChildren().setAll(borderPane);
		}
	}

	public static class NodeTreeItem extends TreeItem<Node> {
		public NodeTreeItem(Node node) {
			super(node);
			if (node instanceof Parent parent) {
				parent.getChildrenUnmodifiable().addListener((ListChangeListener<? super Node>) change -> {
					while (change.next()) {
						if (change.wasAdded()) {
							for (Node addedChild : change.getAddedSubList()) {
								getChildren().add(new NodeTreeItem(addedChild));
							}
						}
						if (change.wasRemoved()) {
							for (Node removedChild : change.getRemoved()) {
								getChildren().removeIf(treeItem -> treeItem.getValue().equals(removedChild));
							}
						}
					}
				});
				for (Node child : parent.getChildrenUnmodifiable()) {
					getChildren().add(new NodeTreeItem(child));
				}
			}
		}
	}

}
