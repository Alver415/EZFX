package com.ezfx.base.utils;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.transform.Transform;
import javafx.stage.Screen;
import javafx.stage.Window;

import java.util.List;

public class ScreenBounds implements IScreenBounds{

	public static final IScreenBounds CACHED = CachedProxy.wrap(new ScreenBounds(), IScreenBounds.class);

	private ScreenBounds() {
	}

	public ObservableValue<Bounds> of(Object selected) {
		if (selected instanceof Node node) {
			return ofNode(node);
		} else if (selected instanceof Scene scene) {
			return ofScene(scene);
		} else if (selected instanceof Window window) {
			return ofWindow(window);
		}
		return null;
	}

	public ObservableValue<Bounds> ofScreens() {
		List<Rectangle2D> list = Screen.getScreens().stream().map(Screen::getBounds).toList();
		double minX = list.stream().mapToDouble(Rectangle2D::getMinX).min().orElse(0);
		double maxX = list.stream().mapToDouble(Rectangle2D::getMaxX).max().orElse(0);
		double minY = list.stream().mapToDouble(Rectangle2D::getMinY).min().orElse(0);
		double maxY = list.stream().mapToDouble(Rectangle2D::getMaxY).max().orElse(0);
		double width = maxX - minX;
		double height = maxY - minY;
		Property<Bounds> bounds = new SimpleObjectProperty<>();
		bounds.setValue(new BoundingBox(minX, minY, width, height));
		return bounds;
	}

	public ObservableValue<Bounds> ofScreen(Screen screen) {
		Property<Bounds> bounds = new SimpleObjectProperty<>();
		bounds.setValue(new BoundingBox(
				screen.getBounds().getMinX(),
				screen.getBounds().getMinY(),
				screen.getBounds().getWidth(),
				screen.getBounds().getHeight()
		));
		return bounds;
	}

	public ObservableValue<Bounds> ofWindow(Window window) {
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
	}

	public ObservableValue<Bounds> ofScene(Scene scene) {
		Property<Bounds> bounds = new SimpleObjectProperty<>();
		Runnable update = () -> bounds.setValue(scene.getRoot().localToScreen(scene.getRoot().getBoundsInLocal()));
		ChangeListener<Number> l0 = (_, _, _) -> update.run();
		ChangeListener<Bounds> l1 = (_, _, _) -> update.run();
		scene.xProperty().addListener(l0);
		scene.yProperty().addListener(l0);
		scene.widthProperty().addListener(l0);
		scene.heightProperty().addListener(l0);
		scene.windowProperty().flatMap(this::ofWindow).addListener(l1);
		update.run();
		return bounds;
	}

	public ObservableValue<Bounds> ofNode(Node node) {
		Property<Bounds> bounds = new SimpleObjectProperty<>();
		Runnable update = () -> bounds.setValue(node.localToScreen(node.getLayoutBounds()));
		ChangeListener<Bounds> l1 = (_, _, _) -> update.run();
		ChangeListener<Transform> l2 = (_, _, _) -> update.run();
		node.boundsInLocalProperty().addListener(l1);
		node.boundsInParentProperty().addListener(l1);
		node.localToSceneTransformProperty().addListener(l2);
		node.localToParentTransformProperty().addListener(l2);
		node.sceneProperty().flatMap(this::ofScene).addListener(l1);
		update.run();
		return bounds;
	}
}
