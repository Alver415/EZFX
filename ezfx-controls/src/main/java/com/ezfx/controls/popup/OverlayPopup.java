package com.ezfx.controls.popup;

import com.ezfx.controls.info.NodeInfo;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PopupControl;
import javafx.scene.control.Skin;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class OverlayPopup extends PopupControl {

	private final Property<String> title = new SimpleObjectProperty<>(this, "title");

	public Property<String> titleProperty() {
		return this.title;
	}

	public String getTitle() {
		return this.titleProperty().getValue();
	}

	public void setTitle(String value) {
		this.titleProperty().setValue(value);
	}

	private final Property<Node> target = new SimpleObjectProperty<>(this, "target");

	public Property<Node> targetProperty() {
		return this.target;
	}

	public Node getTarget() {
		return this.targetProperty().getValue();
	}

	public void setTarget(Node value) {
		this.targetProperty().setValue(value);
	}

	private final Property<Bounds> bounds = new SimpleObjectProperty<>(this, "bounds");

	public Property<Bounds> boundsProperty() {
		return this.bounds;
	}

	public Bounds getBounds() {
		return this.boundsProperty().getValue();
	}

	public void setBounds(Bounds value) {
		this.boundsProperty().setValue(value);
	}

	private final Property<Background> background = new SimpleObjectProperty<>(this, "background");

	public Property<Background> backgroundProperty() {
		return this.background;
	}

	public Background getBackground() {
		return this.backgroundProperty().getValue();
	}

	public void setBackground(Background value) {
		this.backgroundProperty().setValue(value);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new OverlayPopupSkin(this);
	}

	public static class OverlayPopupSkin extends PopupControlSkinBase<OverlayPopup> {

		private static final Bounds DEFAULT_BOUNDING_BOX = new BoundingBox(0,0,0,0);
		private final Pane pane;
		private final NodeInfo info;
		private final Label coordinates;

		protected OverlayPopupSkin(OverlayPopup control) {
			super(control);
			this.pane = new Pane();
			this.info = new NodeInfo();
			this.coordinates = new Label();

			pane.backgroundProperty().bind(control.backgroundProperty());
			pane.getChildren().setAll(info, coordinates);

			info.subjectProperty().bind(control.targetProperty());
			info.translateYProperty().bind(info.heightProperty().multiply(-1));
			info.setPrefWidth(-1);
			info.setPadding(new Insets(2, 4, 0, 4));
			CornerRadii topRadii = new CornerRadii(5d, 5d, 0d, 0d, false);
			info.setBackground(new Background(
					new BackgroundFill(Color.LIGHTGRAY, topRadii, Insets.EMPTY),
					new BackgroundFill(Color.DARKGRAY, topRadii, new Insets(1,1,0,1)),
					new BackgroundFill(Color.WHITE, topRadii, new Insets(2,2,0,2))));

			coordinates.textProperty().bind(control.boundsProperty().map(this::boundsToString));
			coordinates.translateYProperty().bind(pane.heightProperty());
			coordinates.setPrefWidth(-1);
			coordinates.setPadding(new Insets(0, 4, 2, 4));
			CornerRadii bottomRadii = new CornerRadii(0d, 0d, 5d, 5d, false);
			coordinates.setBackground(new Background(
					new BackgroundFill(Color.LIGHTGRAY, bottomRadii, Insets.EMPTY),
					new BackgroundFill(Color.DARKGRAY, bottomRadii, new Insets(0,1,1,1)),
					new BackgroundFill(Color.WHITE, bottomRadii, new Insets(0,2,2,2))));

			ObservableValue<Bounds> screenBounds = control.boundsProperty().orElse(DEFAULT_BOUNDING_BOX);
			screenBounds.map(Bounds::getMinX).subscribe(control::setX);
			screenBounds.map(Bounds::getMinY).subscribe(control::setY);

			ObservableValue<Bounds> localBounds = screenBounds.map(root::screenToLocal).orElse(DEFAULT_BOUNDING_BOX);
			pane.translateXProperty().bind(localBounds.map(Bounds::getMinX));
			pane.translateYProperty().bind(localBounds.map(Bounds::getMinY));
			pane.prefWidthProperty().bind(localBounds.map(Bounds::getWidth));
			pane.prefHeightProperty().bind(localBounds.map(Bounds::getHeight));

			getChildren().setAll(pane);
		}

		private String boundsToString(Bounds bounds) {
			return "x=%.2f, y=%.2f, w=%.2f, h=%.2f".formatted(
					bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
		}
	}
}
