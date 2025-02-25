package com.ezfx.controls.viewport;

import com.ezfx.base.utils.Backgrounds;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SubScene;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import static com.ezfx.controls.utils.Clips.rectangle;

public class ViewportSkin extends SkinBase<Viewport> {

	public ViewportSkin(Viewport viewport) {
		super(viewport);

		viewport.contentProperty()
				.map(content -> content instanceof Parent parent ? parent : new StackPane(content))
				.orElse(new StackPane())
				.subscribe(root -> {
					SubScene subScene = new SubScene(root, 600, 600);
					subScene.setFill(Color.TRANSPARENT.interpolate(Color.RED, 0.5d));
					setSubScene(subScene);
				});

		subSceneProperty().subscribe(subScene -> {
			BorderPane borderPane = new BorderPane();

			StackPane surface = new StackPane(subScene);
			surface.setBackground(Backgrounds.CHECKERED);
			surface.setClip(rectangle(surface));
			borderPane.setCenter(surface);

			Gestures gestures = new Gestures(viewport, subScene);
			surface.addEventFilter(MouseEvent.MOUSE_PRESSED, gestures::onMousePressedEventHandler);
			surface.addEventFilter(MouseEvent.MOUSE_DRAGGED, gestures::onMouseDraggedEventHandler);
			surface.addEventFilter(ScrollEvent.ANY, gestures::onScrollEventHandler);

			subScene.scaleXProperty().bind(viewport.contentScaleProperty());
			subScene.scaleYProperty().bind(viewport.contentScaleProperty());
			subScene.translateXProperty().bind(viewport.contentPositionXProperty());
			subScene.translateYProperty().bind(viewport.contentPositionYProperty());

			getChildren().setAll(borderPane);
		});
	}


	private final ObjectProperty<SubScene> subScene = new SimpleObjectProperty<>(this, "subScene");

	public ObjectProperty<SubScene> subSceneProperty() {
		return this.subScene;
	}

	public SubScene getSubScene() {
		return this.subSceneProperty().get();
	}

	public void setSubScene(SubScene value) {
		this.subSceneProperty().set(value);
	}

	private static class DragContext {
		double mouseAnchorX;
		double mouseAnchorY;
		double translateAnchorX;
		double translateAnchorY;
	}

	private static class Gestures {

		private static final double MAX_SCALE = 5.0d;
		private static final double MIN_SCALE = 0.2d;

		private final DragContext dragContext = new DragContext();
		private final Viewport viewport;
		private final Node target;

		public Gestures(Viewport viewport, Node target) {
			this.viewport = viewport;
			this.target = target;
		}

		private static boolean notCtrlClick(MouseEvent event) {
			return !event.isPrimaryButtonDown() || !event.isAltDown();
		}

		private void onMousePressedEventHandler(MouseEvent event) {
			if (notCtrlClick(event)) return;
			dragContext.mouseAnchorX = event.getX();
			dragContext.mouseAnchorY = event.getY();
			dragContext.translateAnchorX = viewport.getContentPositionX();
			dragContext.translateAnchorY = viewport.getContentPositionY();

		}

		private void onMouseDraggedEventHandler(MouseEvent event) {
			if (notCtrlClick(event)) return;
			viewport.setContentPositionX(dragContext.translateAnchorX - dragContext.mouseAnchorX + event.getX());
			viewport.setContentPositionY(dragContext.translateAnchorY - dragContext.mouseAnchorY + event.getY());
			event.consume();
		}

		private void onScrollEventHandler(ScrollEvent event) {
			double delta = 1.2;
			double scale = viewport.getContentScale();
			double oldScale = scale;

			Bounds bounds = target.getBoundsInParent();
			double width = bounds.getWidth();
			double height = bounds.getHeight();
			double minX = bounds.getMinX();
			double minY = bounds.getMinY();
			double x = event.getX();
			double y = event.getY();

			scale = event.getDeltaY() < 0 ? scale / delta : scale * delta;
			scale = Math.clamp(scale, MIN_SCALE, MAX_SCALE);

			double f = (scale / oldScale) - 1;

			double dx = (x - (width / 2 + minX));
			double dy = (y - (height / 2 + minY));

			viewport.setContentScale(scale);
			viewport.setContentPositionX(viewport.getContentPositionX() - (f * dx));
			viewport.setContentPositionY(viewport.getContentPositionY() - (f * dy));

			event.consume();
		}
	}
}
