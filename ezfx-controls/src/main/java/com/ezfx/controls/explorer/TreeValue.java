package com.ezfx.controls.explorer;

import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;

public abstract class TreeValue<T, C> {

	private final Property<T> value = new SimpleObjectProperty<>(this, "value");

	public TreeValue(T value) {
		this.value.setValue(value);
	}

	public static <T> TreeValue<T, Object> build(T value) {
		if (value instanceof Application application) {
			TreeValue<?, ?> treeValue = new ApplicationTreeValue(application);
			return (TreeValue<T, Object>) treeValue;
		}
		if (value instanceof Stage stage) {
			TreeValue<?, ?> treeValue = new StageTreeValue(stage);
			return (TreeValue<T, Object>) treeValue;
		}
		if (value instanceof Scene scene) {
			TreeValue<?, ?> treeValue = new SceneTreeValue(scene);
			return (TreeValue<T, Object>) treeValue;
		}
		if (value instanceof Node node) {
			TreeValue<?, ?> treeValue = new NodeTreeValue(node);
			return (TreeValue<T, Object>) treeValue;
		}
		throw new IllegalStateException();
	}

	public Property<T> valueProperty() {
		return this.value;
	}

	public T getValue() {
		return this.valueProperty().getValue();
	}

	public void setValue(T value) {
		this.valueProperty().setValue(value);
	}

	private final ListProperty<C> children = new SimpleListProperty<>(this, "children", FXCollections.observableArrayList());

	public ListProperty<C> childrenProperty() {
		return this.children;
	}

	public ObservableList<C> getChildren() {
		return this.childrenProperty().getValue();
	}

	public void setChildren(ObservableList<C> value) {
		this.childrenProperty().setValue(value);
	}

	protected abstract ObservableValue<Image> observableIcon();

	protected abstract ObservableValue<String> observableJavaType();

	protected abstract ObservableValue<String> observableNodeId();

	protected abstract ObservableValue<String> observableStyleClass();

	protected abstract ObservableValue<Boolean> observableVisibility();


	public static class ApplicationTreeValue extends TreeValue<Application, Stage> {

		public ApplicationTreeValue(Application value) {
			super(value);
			ObservableList<Window> windows = Window.getWindows();
			InvalidationListener listener = _ -> getChildren().setAll(
					windows.stream().filter(a -> a instanceof Stage).map(a -> (Stage) a).toList());
			windows.addListener(listener);
			listener.invalidated(null);

		}

		@Override
		protected ObservableValue<Image> observableIcon() {
			return valueProperty().map(_ -> null);
		}

		@Override
		protected ObservableValue<String> observableJavaType() {
			return valueProperty().map(Application::getClass).map(Class::getSimpleName);
		}

		@Override
		protected ObservableValue<String> observableNodeId() {
			return valueProperty().map(_ -> null);
		}

		@Override
		protected ObservableValue<String> observableStyleClass() {
			return valueProperty().map(_ -> null);
		}

		@Override
		protected ObservableValue<Boolean> observableVisibility() {
			return valueProperty().map(_ -> null);
		}
	}

	public static class StageTreeValue extends TreeValue<Stage, Scene> {

		public StageTreeValue(Stage value) {
			super(value);
			value.sceneProperty().subscribe(scene -> getChildren().setAll(scene));
		}

		@Override
		protected ObservableValue<Image> observableIcon() {
			return valueProperty().map(_ -> null);
		}

		@Override
		protected ObservableValue<String> observableJavaType() {
			return valueProperty().map(Stage::getClass).map(Class::getSimpleName);
		}

		@Override
		protected ObservableValue<String> observableNodeId() {
			return valueProperty().flatMap(Stage::titleProperty);
		}

		@Override
		protected ObservableValue<String> observableStyleClass() {
			return valueProperty().map(_ -> null);
		}

		@Override
		protected ObservableValue<Boolean> observableVisibility() {
			return valueProperty().flatMap(Window::showingProperty);
		}
	}

	public static class SceneTreeValue extends TreeValue<Scene, Parent> {

		public SceneTreeValue(Scene value) {
			super(value);
			value.rootProperty().subscribe(root -> getChildren().setAll(root));
		}

		@Override
		protected ObservableValue<Image> observableIcon() {
			return valueProperty().map(_ -> null);
		}

		@Override
		protected ObservableValue<String> observableJavaType() {
			return valueProperty().map(Scene::getClass).map(Class::getSimpleName);
		}

		@Override
		protected ObservableValue<String> observableNodeId() {
			return valueProperty().map(_ -> null);
		}

		@Override
		protected ObservableValue<String> observableStyleClass() {
			return valueProperty().map(_ -> null);
		}

		@Override
		protected ObservableValue<Boolean> observableVisibility() {
			return valueProperty().map(_ -> null);
		}
	}

	public static class NodeTreeValue extends TreeValue<Node, Node> {

		public NodeTreeValue(Node value) {
			super(value);
			if (value instanceof Parent parent) {
				setChildren(parent.getChildrenUnmodifiable());
			} else if (value instanceof SubScene subScene) {
				subScene.rootProperty().subscribe(root -> getChildren().setAll(root));
			}
		}

		@Override
		protected ObservableValue<Image> observableIcon() {
			return valueProperty().map(_ -> null);
		}

		@Override
		protected ObservableValue<String> observableJavaType() {
			return valueProperty().map(Node::getClass).map(Class::getSimpleName);
		}

		@Override
		protected ObservableValue<String> observableNodeId() {
			return valueProperty().flatMap(Node::idProperty);
		}

		@Override
		protected ObservableValue<String> observableStyleClass() {
			return valueProperty().flatMap(node -> Bindings.createStringBinding(() ->
					String.join(", ", node.getStyleClass()), node.getStyleClass()));
		}

		@Override
		protected ObservableValue<Boolean> observableVisibility() {
			return valueProperty().flatMap(Node::visibleProperty);
		}
	}

}