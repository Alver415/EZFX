package com.ezfx.controls.editor.skin;

import com.ezfx.controls.editor.Editor;
import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TextField;

import java.util.Collection;

public class EditorSkin<E extends Editor<T>, T> extends SkinBase<E> {

	protected final E editor;

	public EditorSkin(E editor) {
		super(editor);
		this.editor = editor;

		TextField text = new TextField();
		text.setDisable(true);
		text.textProperty().bind(valueProperty().map(String::valueOf).orElse("null"));
		getChildren().setAll(text);
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
