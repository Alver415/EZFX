package com.ezfx.controls.editor.skin;

import com.ezfx.controls.editor.EditorBase;
import com.ezfx.controls.editor.EditorSkinBase;
import com.ezfx.controls.editor.impl.javafx.ColorEditor;
import com.ezfx.controls.editor.impl.javafx.PaintEditor;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.MapProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.paint.Paint;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import static javafx.beans.binding.Bindings.createObjectBinding;

public class PaintEditorSkin extends EditorSkinBase<PaintEditor, Paint> {

	public PaintEditorSkin(PaintEditor editor) {
		super(editor);
		ObjectBinding<? extends EditorBase<? extends Paint>> currentEditor = createObjectBinding(
				() -> editorMapProperty().computeIfAbsent(editorClassProperty().getValue(), this::constructEditor), //Binding
				editorMapProperty(), editorClassProperty());

		currentEditor.subscribe((oldValue, newValue) -> {
			Optional.ofNullable(oldValue).map(Control::getSkin).ifPresent(Skin::dispose);
			valueProperty().setValue(null);
		});
		editorProperty().bind(currentEditor);

		editorProperty().subscribe(d -> setChildren(d));
	}

	private EditorBase<? extends Paint> constructEditor(Class<? extends EditorBase<? extends Paint>> editorClass) {
		try {

			return editorClass.getConstructor(Property.class).newInstance(valueProperty());
		} catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
		         IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private final MapProperty<Class<? extends EditorBase<? extends Paint>>, EditorBase<? extends Paint>> editorMap
			= new SimpleMapProperty<>(FXCollections.observableHashMap());

	public MapProperty<Class<? extends EditorBase<? extends Paint>>, EditorBase<? extends Paint>> editorMapProperty() {
		return editorMap;
	}

	public ObservableMap<Class<? extends EditorBase<? extends Paint>>, EditorBase<? extends Paint>> getEditorMap() {
		return editorMapProperty().getValue();
	}

	public void setEditorMap(ObservableMap<Class<? extends EditorBase<? extends Paint>>, EditorBase<? extends Paint>> value) {
		editorMapProperty().setValue(value);
	}

	private final Property<EditorBase<? extends Paint>> editor = new SimpleObjectProperty<>(this, "editor");

	public Property<EditorBase<? extends Paint>> editorProperty() {
		return this.editor;
	}

	public EditorBase<? extends Paint> getEditor() {
		return this.editorProperty().getValue();
	}

	public void setEditor(EditorBase<? extends Paint> value) {
		this.editorProperty().setValue(value);
	}

	private final Property<Class<? extends EditorBase<? extends Paint>>> editorClass =
			new SimpleObjectProperty<>(this, "editorClass", ColorEditor.class);

	public Property<Class<? extends EditorBase<? extends Paint>>> editorClassProperty() {
		return this.editorClass;
	}

	public Class<? extends EditorBase<? extends Paint>> getEditorClass() {
		return this.editorClassProperty().getValue();
	}

	public void setEditorClass(Class<? extends EditorBase<? extends Paint>> value) {
		this.editorClassProperty().setValue(value);
	}


}
