package com.ezfx.controls.editor;

import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TextField;

import java.util.Collection;

public class EditorSkinBase<E extends Control & Editor<T>, T> extends SkinBase<E> {

	protected final E editor;

	public EditorSkinBase(E editor) {
		super(editor);
		this.editor = editor;
	}

	@Override
	public void install(){
		if (!getChildren().isEmpty()) return;
		TextField text = new TextField();
		text.setDisable(true);
		text.textProperty().bind(valueProperty().map(String::valueOf).orElse("null"));
		getChildren().setAll(text);
	}

	@Override
	public void dispose(){
		super.dispose();
	}

	public Property<T> valueProperty() {
		return editor.valueProperty();
	}

	public T getValue() {
		return valueProperty().getValue();
	}

	public void setValue(T value) {
		valueProperty().setValue(value);
	}

	public final ObservableList<String> getStylesheets() {
		return editor.getStylesheets();
	}

	public final ObservableList<String> getStyleClass() {
		return editor.getStyleClass();
	}

	protected void setChildren(Node... children) {
		getChildren().setAll(children);
	}

	protected void setChildren(Collection<? extends Node> children) {
		getChildren().setAll(children);
	}

}
