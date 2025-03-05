package com.ezfx.controls.popup;

import com.ezfx.controls.info.NodeInfo;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Window;

public class OverlayPopup extends PopupControl {

	public OverlayPopup(Window owner) {
		setOwner(owner);
		show(owner);
		setVisible(true);
		bridge.visibleProperty().bind(visible);
		showingProperty().subscribe(this::setVisible);

		setHideOnEscape(false);
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

			control.boundsProperty()
					.orElse(DEFAULT_BOUNDING_BOX)
					.subscribe(this::updateBounds);

			getChildren().setAll(pane);
		}

		private void updateBounds(Bounds screenBounds) {
			control.setX(screenBounds.getMinX());
			control.setY(screenBounds.getMinY());
			control.setWidth(screenBounds.getWidth());
			control.setHeight(screenBounds.getHeight());
			Bounds localBounds = root.screenToLocal(screenBounds);
			localBounds = localBounds == null ? DEFAULT_BOUNDING_BOX : localBounds;
			pane.setLayoutX(localBounds.getMinX());
			pane.setLayoutY(localBounds.getMinY());
			pane.setPrefWidth(localBounds.getWidth());
			pane.setPrefHeight(localBounds.getHeight());
		}

		private String boundsToString(Bounds bounds) {
			return "x=%.2f, y=%.2f, w=%.2f, h=%.2f".formatted(
					bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
		}

	}
	@Override
	protected Skin<?> createDefaultSkin() {
		return new OverlayPopupSkin(this);
	}


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

	private final Property<Window> owner = new SimpleObjectProperty<>(this, "owner");

	public Property<Window> ownerProperty() {
		return this.owner;
	}

	public Window getOwner() {
		return this.ownerProperty().getValue();
	}

	public void setOwner(Window value) {
		this.ownerProperty().setValue(value);
	}

	private final BooleanProperty visible = new SimpleBooleanProperty(this, "visible");

	public BooleanProperty visibleProperty() {
		return this.visible;
	}

	public Boolean getVisible() {
		return this.visibleProperty().getValue();
	}

	public void setVisible(Boolean value) {
		this.visibleProperty().setValue(value);
	}

}
