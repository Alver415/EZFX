package com.ezfx.controls.editor;

import com.ezfx.controls.editor.skin.EditorViewSkin;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import org.controlsfx.control.action.Action;

public class EditorView<T, E extends Editor<T>> extends Control {

	public EditorView(String name) {
		this(new SimpleStringProperty(name));
	}
	public EditorView(Property<String> name) {
		this(name, null);
	}

	public EditorView(E editor) {
		this(editor.valueProperty().getName(), editor);
	}

	public EditorView(String name, E editor) {
		this(new SimpleStringProperty(name), editor);
	}

	public EditorView(Property<String> name, E editor) {
		nameProperty().bind(name);
		setEditor(editor);
		setFocusTraversable(false);
		actionsProperty().bind(editorProperty().flatMap(Editor::actionsProperty));
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new EditorViewSkin<>(this);
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

	private final ListProperty<Action> actions = new SimpleListProperty<>(this, "actions", FXCollections.observableArrayList());

	public ListProperty<Action> actionsProperty() {
		return this.actions;
	}

	public ObservableList<Action> getActions() {
		return this.actionsProperty().get();
	}

	public void setActions(ObservableList<Action> value) {
		this.actionsProperty().set(value);
	}



}
