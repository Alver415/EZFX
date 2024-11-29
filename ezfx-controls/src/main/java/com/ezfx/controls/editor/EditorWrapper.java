package com.ezfx.controls.editor;

import com.ezfx.controls.editor.skin.EditorWrapperSkin;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class EditorWrapper<T, E extends Editor<T>> extends Control {

	public EditorWrapper(String name) {
		this(new SimpleStringProperty(name));
	}
	public EditorWrapper(Property<String> name) {
		this(name, null);
	}

	public EditorWrapper(E editor) {
		this(editor.property().getName(), editor);
	}

	public EditorWrapper(String name, E editor) {
		this(new SimpleStringProperty(name), editor);
	}

	public EditorWrapper(Property<String> name, E editor) {
		nameProperty().bind(name);
		setEditor(editor);
		setFocusTraversable(false);
		actionsProperty().bind(editorProperty().flatMap(Editor::actionsProperty));
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new EditorWrapperSkin<>(this);
	}

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

	private final ObjectProperty<E> editor = new SimpleObjectProperty<>(this, "editor");

	public ObjectProperty<E> editorProperty() {
		return this.editor;
	}

	public E getEditor() {
		return this.editorProperty().get();
	}

	public void setEditor(E value) {
		this.editorProperty().set(value);
	}

	private final ListProperty<EditorAction> actions = new SimpleListProperty<>(this, "actions", FXCollections.observableArrayList());

	public ListProperty<EditorAction> actionsProperty() {
		return this.actions;
	}

	public ObservableList<EditorAction> getActions() {
		return this.actionsProperty().get();
	}

	public void setActions(ObservableList<EditorAction> value) {
		this.actionsProperty().set(value);
	}



}
