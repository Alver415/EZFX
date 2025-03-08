package com.ezfx.controls.editor;

import com.ezfx.controls.editor.factory.EditorFactory;
import com.ezfx.controls.editor.factory.IntrospectingEditorFactory;
import com.ezfx.controls.editor.introspective.Introspector;
import com.ezfx.controls.editor.skin.MultiEditorSkin;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Skin;

public abstract class MultiEditorBase<T> extends EditorBase<T> implements MultiEditor<T> {

	public MultiEditorBase() {
		this(new SimpleObjectProperty<>());
	}

	public MultiEditorBase(T initialValue) {
		this(new SimpleObjectProperty<>(initialValue));
	}

	public MultiEditorBase(Property<T> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new MultiEditorSkin.VerticalEditorSkin<>(this);
	}

	private final ListProperty<Editor<?>> editors = new SimpleListProperty<>(this, "editors", FXCollections.observableArrayList());

	@Override
	public ListProperty<Editor<?>> editorsProperty() {
		return this.editors;
	}

	public ObservableList<Editor<?>> getEditors() {
		return this.editorsProperty().getValue();
	}

	public void setEditors(ObservableList<Editor<?>> value) {
		this.editorsProperty().setValue(value);
	}

	private final Property<EditorFactory> editorFactory = new SimpleObjectProperty<>(this, "editorFactory", IntrospectingEditorFactory.DEFAULT_FACTORY);

	public Property<EditorFactory> editorFactoryProperty() {
		return this.editorFactory;
	}

	public EditorFactory getEditorFactory() {
		return this.editorFactoryProperty().getValue();
	}

	public void setEditorFactory(EditorFactory value) {
		this.editorFactoryProperty().setValue(value);
	}

	private final ObjectProperty<Introspector> introspector = new SimpleObjectProperty<>(this, "introspector");

	public ObjectProperty<Introspector> introspectorProperty() {
		return this.introspector;
	}

	public Introspector getIntrospector() {
		return this.introspectorProperty().get();
	}

	public void setIntrospector(Introspector value) {
		this.introspectorProperty().set(value);
	}

}