package com.ezfx.controls.editor.skin;

import com.ezfx.controls.editor.Editor;
import javafx.beans.property.Property;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TextField;

import java.util.Collection;

public class EditorSkin<E extends Editor<T>, T> extends SkinBase<E> {

	private final E editor;
	public EditorSkin(E editor) {
		super(editor);
		this.editor = editor;

		TextField text = new TextField();
		text.setDisable(true);
		text.textProperty().bind(editor.property().map(String::valueOf));
		getChildren().setAll(text);
	}

	protected E editor(){
		return editor;
	}
	protected Property<T> property(){
		return editor.property();
	}

	protected void setChildren(Collection<? extends Node> children){
		getChildren().setAll(children);
	}
	protected void setChildren(Node... children){
		getChildren().setAll(children);
	}
}
