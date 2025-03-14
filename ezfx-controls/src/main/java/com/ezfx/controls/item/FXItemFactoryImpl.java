package com.ezfx.controls.item;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.stage.Stage;
import javafx.stage.Window;

@SuppressWarnings("unchecked")
public class FXItemFactoryImpl implements FXItemFactory {

	@Override
	public <T> FXItem<T, ?> create(T object) {
		if (object instanceof Parent parent) {
			return (FXItem<T, ?>) new FXParentItem<>(this, parent);
		} else if (object instanceof SubScene subScene) {
			return (FXItem<T, ?>) new FXSubSceneItem<>(this, subScene);
		} else if (object instanceof Node node) {
			return (FXItem<T, ?>) new FXNodeItem<>(this, node);
		} else if (object instanceof Stage stage) {
			return (FXItem<T, ?>) new FXStageItem<>(this, stage);
		} else if (object instanceof Window window) {
			return (FXItem<T, ?>) new FXWindowItem<>(this, window);
		} else if (object instanceof Scene scene) {
			return (FXItem<T, ?>) new FXSceneItem<>(this, scene);
		} else if (object instanceof Application application) {
			return (FXItem<T, ?>) new FXApplicationItem<>(this, application);
		}
		return new FXItemBase<>(this, object);
	}


	@Override
	public <A extends Application> FXApplicationItem<A> create(A application) {
		return new FXApplicationItem<>(this, application);
	}

	@Override
	public <W extends Window> FXWindowItem<W> create(W window) {
		if (window instanceof Stage stage) {
			return (FXWindowItem<W>) new FXStageItem<>(this, stage);
		}
		return new FXWindowItem<>(this, window);
	}

	@Override
	public <S extends Stage> FXStageItem<S> create(S stage) {
		return new FXStageItem<>(this, stage);
	}

	@Override
	public <S extends Scene> FXSceneItem<S> create(S scene) {
		return new FXSceneItem<>(this, scene);
	}

	@Override
	public <N extends Node> FXNodeItem<N> create(N node) {
		if (node instanceof Parent parent) {
			return (FXNodeItem<N>) new FXParentItem<>(this, parent);
		} else if (node instanceof SubScene subScene) {
			return (FXNodeItem<N>) new FXSubSceneItem<>(this, subScene);
		}
		return new FXNodeItem<>(this, node);
	}

	@Override
	public <P extends Parent> FXParentItem<P> create(P parent) {
		return new FXParentItem<>(this, parent);
	}

}
