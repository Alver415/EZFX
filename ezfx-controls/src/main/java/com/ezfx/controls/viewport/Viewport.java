package com.ezfx.controls.viewport;

import javafx.beans.DefaultProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

@DefaultProperty("content")
public class Viewport extends Control {

	public Viewport() {
		this(null);
	}
	public Viewport(Node content) {
		setContent(content);
	}

	public void reset() {
		setContentScale(1);
		setContentPositionX(0);
		setContentPositionY(0);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new ViewportSkin(this);
	}
	private final ObjectProperty<Node> content = new SimpleObjectProperty<>(this, "content");

	public ObjectProperty<Node> contentProperty() {
		return this.content;
	}

	public Node getContent() {
		return this.contentProperty().get();
	}

	public void setContent(Node value) {
		this.contentProperty().set(value);
	}


	private final DoubleProperty contentScale = new SimpleDoubleProperty(this, "contentScale", 1);

	public DoubleProperty contentScaleProperty() {
		return contentScale;
	}

	public double getContentScale() {
		return contentScaleProperty().getValue();
	}

	public void setContentScale(double scale) {
		this.contentScaleProperty().setValue(scale);
	}

	private final DoubleProperty contentPositionX = new SimpleDoubleProperty(this, "contentPositionX", 0);

	public DoubleProperty contentPositionXProperty() {
		return this.contentPositionX;
	}

	public double getContentPositionX() {
		return this.contentPositionXProperty().getValue();
	}

	public void setContentPositionX(double value) {
		this.contentPositionXProperty().setValue(value);
	}

	private final DoubleProperty contentPositionY = new SimpleDoubleProperty(this, "contentPositionY", 0);

	public DoubleProperty contentPositionYProperty() {
		return this.contentPositionY;
	}

	public double getContentPositionY() {
		return this.contentPositionYProperty().getValue();
	}

	public void setContentPositionY(double value) {
		this.contentPositionYProperty().setValue(value);
	}

}
