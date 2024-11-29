package com.ezfx.controls.editor.impl.javafx;

import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.skin.EditorSkin;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class ShapeEditor extends ObjectEditor<Shape> {

	public ShapeEditor() {
		super(new SimpleObjectProperty<>());
	}

	public ShapeEditor(Property<Shape> property) {
		super(property);
		getKnownValues().addAll(
				new Rectangle(16, 16, Color.RED),
				new Circle(8, 8, 8, Color.BLUE)
		);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new EditorSkin<>(this);
	}


}
