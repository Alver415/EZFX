package com.ezfx.controls.viewport;

import com.ezfx.base.utils.Backgrounds;
import com.ezfx.controls.editor.impl.standard.DoubleEditor;
import com.ezfx.controls.editor.skin.SliderSkin;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
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

			DoubleEditor widthEditor = new DoubleEditor(subScene.widthProperty(), 0, 800);
			SliderSkin widthSkin = new SliderSkin(widthEditor);
			widthEditor.setSkin(widthSkin);
			borderPane.setBottom(widthEditor);

			DoubleEditor heightEditor = new DoubleEditor(subScene.heightProperty(), 0, 800);
			heightEditor.setOrientation(Orientation.VERTICAL);
			SliderSkin heightSkin = new SliderSkin(heightEditor);
			heightEditor.setSkin(heightSkin);
			borderPane.setRight(heightEditor);

			StackPane surface = new StackPane(subScene);
			surface.setBackground(Backgrounds.checkeredBackground());
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


	public void reset() {
		Viewport viewport = getSkinnable();
		viewport.setContentScale(1d);
		viewport.setContentPositionX(0d);
		viewport.setContentPositionY(0d);
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

			scale = event.getDeltaY() < 0 ?
					scale / delta :
					scale * delta;
			scale = Math.min(Math.max(scale, MIN_SCALE), MAX_SCALE);

			double f = (scale / oldScale) - 1;
			double dx = (event.getX() - (target.getBoundsInParent()
					.getWidth() / 2 + target.getBoundsInParent().getMinX()));
			double dy = (event.getY() - (target.getBoundsInParent()
					.getHeight() / 2 + target.getBoundsInParent().getMinY()));

			viewport.setContentScale(scale);
			viewport.setContentPositionX(viewport.getContentPositionX() - (f * dx));
			viewport.setContentPositionY(viewport.getContentPositionY() - (f * dy));

			event.consume();
		}
	}
}
