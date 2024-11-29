package com.ezfx.controls.editor;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Control;
import javafx.scene.image.Image;

public class EditorAction extends Control {


	private final StringProperty name = new SimpleStringProperty(this, "name");

	public StringProperty nameProperty() {
		return this.name;
	}

	public String getName() {
		return this.nameProperty().get();
	}

	public void setName(String value) {
		this.nameProperty().set(value);
	}

	private final ObjectProperty<Image> icon = new SimpleObjectProperty<>(this, "icon");

	public ObjectProperty<Image> iconProperty() {
		return this.icon;
	}

	public Image getIcon() {
		return this.iconProperty().get();
	}

	public void setIcon(Image value) {
		this.iconProperty().set(value);
	}

	private final ObjectProperty<Runnable> action = new SimpleObjectProperty<>(this, "action");

	public ObjectProperty<Runnable> actionProperty() {
		return this.action;
	}

	public Runnable getAction() {
		return this.actionProperty().get();
	}

	public void setAction(Runnable value) {
		this.actionProperty().set(value);
	}
}
