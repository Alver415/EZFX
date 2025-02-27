package com.ezfx.base.utils;

import javafx.beans.binding.Binding;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.transform.Transform;
import javafx.stage.Window;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface ScreenBounds {


	Map<Object, ObservableValue<Bounds>> cache = new ConcurrentHashMap<>();

	static ObservableValue<Bounds> of(Object selected) {
		if (selected instanceof Node node) {
			return ofNode(node);
		} else if (selected instanceof Scene scene) {
			return ofScene(scene);
		} else if (selected instanceof Window window) {
			return ofWindow(window);
		}
		return null;
	}
	static ObservableValue<Bounds> ofWindow(Window window) {
		return cache.computeIfAbsent(window, _ -> {
			Property<Bounds> bounds = new SimpleObjectProperty<>();
			ChangeListener<Number> update = (_, _, _) -> bounds.setValue(new BoundingBox(
					window.getX(), window.getY(), 0,
					window.getWidth(), window.getHeight(), 0));
			window.xProperty().addListener(update);
			window.yProperty().addListener(update);
			window.widthProperty().addListener(update);
			window.heightProperty().addListener(update);
			update.changed(null, null, null);
			return bounds;
		});
	}

	static ObservableValue<Bounds> ofScene(Scene scene) {
		return cache.computeIfAbsent(scene, _ -> {
			Property<Bounds> bounds = new SimpleObjectProperty<>();
			Runnable update = () -> bounds.setValue(scene.getRoot().localToScreen(scene.getRoot().getBoundsInLocal()));
			ChangeListener<Number> l0 = (_, _, _) -> update.run();
			ChangeListener<Bounds> l1 = (_, _, _) -> update.run();
			scene.xProperty().addListener(l0);
			scene.yProperty().addListener(l0);
			scene.widthProperty().addListener(l0);
			scene.heightProperty().addListener(l0);
			scene.windowProperty().flatMap(ScreenBounds::ofWindow).addListener(l1);
			update.run();
			return bounds;
		});
	}

	static ObservableValue<Bounds> ofNode(Node node) {
		return cache.computeIfAbsent(node, _ -> {
			Property<Bounds> bounds = new SimpleObjectProperty<>();
			Runnable update = () -> bounds.setValue(node.localToScreen(node.getBoundsInLocal()));
			ChangeListener<Bounds> l1 = (_, _, _) -> update.run();
			ChangeListener<Transform> l2 = (_, _, _) -> update.run();
			node.boundsInLocalProperty().addListener(l1);
			node.boundsInParentProperty().addListener(l1);
			node.localToSceneTransformProperty().addListener(l2);
			node.localToParentTransformProperty().addListener(l2);
			node.sceneProperty().flatMap(ScreenBounds::ofScene).addListener(l1);
			update.run();
			return bounds;
		});
	}

}
