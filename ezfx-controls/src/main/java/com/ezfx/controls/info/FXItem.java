package com.ezfx.controls.info;

import javafx.application.Application;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.List;

@SuppressWarnings("unchecked")
public interface FXItem<T, C extends FXItem<?, ?>> {

	T get();

	ObservableList<? extends C> getChildren();

	String getId();

	List<String> getStyleClass();

	Property<Boolean> visibleProperty();


	/**
	 * BASE
	 **/
	class FXItemBase<T, C extends FXItem<?, ?>> implements FXItem<T, C> {
		protected final T item;
		protected final ObservableList<C> children = FXCollections.observableArrayList();
		protected final SimpleBooleanProperty visible = new SimpleBooleanProperty();

		protected FXItemBase(T item) {
			this.item = item;
		}

		@Override
		public T get() {
			return item;
		}

		@Override
		public ObservableList<? extends C> getChildren() {
			return children;
		}

		public String getId() {
			return null;
		}

		public List<String> getStyleClass() {
			return List.of();
		}

		public Property<Boolean> visibleProperty() {
			return visible;
		}
	}

	/**
	 * APPLICATION
	 **/
	class FXApplicationItem<T extends Application> extends FXItemBase<T, FXWindowItem<?>> {
		protected FXApplicationItem(T application) {
			super(application);
			for (Window window : Window.getWindows()) {
				children.add(FXItem.create(window));
			}
			Window.getWindows().addListener((ListChangeListener<? super Window>) change -> {
				while (change.next()) {
					for (Window removed : change.getRemoved()) {
						children.removeIf(item -> item.get() == removed);
					}
					for (Window added : change.getAddedSubList()) {
						children.add(FXItem.create(added));
					}
				}
			});
		}
	}

	/**
	 * WINDOW
	 **/
	class FXWindowItem<T extends Window> extends FXItemBase<T, FXSceneItem<?>> {
		protected FXWindowItem(T window) {
			super(window);

			window.sceneProperty()
					.map(FXItem::create)
					.subscribe(scene -> children.setAll(scene));
		}
	}

	/**
	 * STAGE
	 **/
	class FXStageItem<T extends Stage> extends FXWindowItem<T> {
		protected FXStageItem(T stage) {
			super(stage);
		}
	}

	/**
	 * SCENE
	 **/
	class FXSceneItem<T extends Scene> extends FXItemBase<T, FXParentItem<?>> {
		protected FXSceneItem(T scene) {
			super(scene);
			scene.rootProperty()
					.map(FXItem::create)
					.subscribe(root -> children.setAll(root));
		}
	}

	/**
	 * NODE
	 **/
	class FXNodeItem<T extends Node> extends FXItemBase<T, FXNodeItem<?>> {
		protected FXNodeItem(T node) {
			super(node);
		}
	}

	/**
	 * PARENT
	 **/
	class FXParentItem<T extends Parent> extends FXNodeItem<T> {
		protected FXParentItem(T parent) {
			super(parent);
			for (Node child : parent.getChildrenUnmodifiable()){
				children.add(FXItem.create(child));
			}
			parent.getChildrenUnmodifiable().addListener((ListChangeListener<? super Node>) change -> {
				while (change.next()) {
					for (Node removed : change.getRemoved()) {
						children.removeIf(item -> item.get() == removed);
					}
					for (Node added : change.getAddedSubList()) {
						children.add(FXItem.create(added));
					}
				}
			});
		}
	}

	@SuppressWarnings("unchecked")
	static <T> FXItem<T, ?> create(T object) {
		if (object instanceof Application application) {
			return (FXItem<T, ?>) new FXApplicationItem<>(application);
		} else if (object instanceof Stage stage) {
			return (FXItem<T, ?>) new FXStageItem<>(stage);
		} else if (object instanceof Window window) {
			return (FXItem<T, ?>) new FXWindowItem<>(window);
		} else if (object instanceof Scene scene) {
			return (FXItem<T, ?>) new FXSceneItem<>(scene);
		} else if (object instanceof Parent parent) {
			return (FXItem<T, ?>) new FXParentItem<>(parent);
		} else if (object instanceof Node node) {
			return (FXItem<T, ?>) new FXNodeItem<>(node);
		}
		return new FXItemBase<>(object);
	}

	static <A extends Application> FXApplicationItem<A> create(A application) {
		return new FXApplicationItem<>(application);
	}

	static <W extends Window> FXWindowItem<W> create(W window) {
		if (window instanceof Stage stage){
			return (FXWindowItem<W>) create(stage);
		}
		return new FXWindowItem<>(window);
	}

	static <S extends Stage> FXStageItem<S> create(S stage) {
		return new FXStageItem<>(stage);
	}

	static <S extends Scene> FXSceneItem<S> create(S scene) {
		return new FXSceneItem<>(scene);
	}

	static <N extends Node> FXNodeItem<N> create(N node) {
		if (node instanceof Parent parent){
			return (FXNodeItem<N>) create(parent);
		}
		return new FXNodeItem<>(node);
	}

	static <P extends Parent> FXParentItem<P> create(P parent) {
		return new FXParentItem<>(parent);
	}

}
