package com.ezfx.controls.item;

import com.ezfx.base.utils.CachedProxy;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

public interface FXItemFactory {

	FXItemFactory CACHED = CachedProxy.wrap(new FXItemFactoryImpl(), FXItemFactory.class);

	<T> FXItem<T, ?> create(T object);

	<A extends Application> FXApplicationItem<A> create(A application);

	<W extends Window> FXWindowItem<W> create(W window);

	<S extends Stage> FXStageItem<S> create(S stage);

	<S extends Scene> FXSceneItem<S> create(S scene);

	<N extends Node> FXNodeItem<N> create(N node);

	<P extends Parent> FXParentItem<P> create(P parent);
}
