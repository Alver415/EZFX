package com.ezfx.controls.editor.impl.javafx;

import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.PropertiesEditor;
import com.ezfx.controls.editor.impl.standard.DoubleEditor;
import com.ezfx.controls.editor.skin.EditorSkin;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Skin;

import java.util.List;

public class InsetsEditor extends ObjectEditor<Insets> {
	public InsetsEditor() {
		super(new SimpleObjectProperty<>());
	}

	public InsetsEditor(Property<Insets> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new DefaultSkin(this);
	}


	public static class DefaultSkin extends EditorSkin<Editor<Insets>, Insets> {
		private final DoubleProperty top = new SimpleDoubleProperty(this, "top");
		private final DoubleProperty bottom = new SimpleDoubleProperty(this, "bottom");
		private final DoubleProperty right = new SimpleDoubleProperty(this, "right");
		private final DoubleProperty left = new SimpleDoubleProperty(this, "left");

		public DefaultSkin(Editor<Insets> control) {
			super(control);

			control.valueProperty().subscribe(insets -> {
				insets = insets == null ? Insets.EMPTY : insets;
				top.set(insets.getTop());
				right.set(insets.getRight());
				bottom.set(insets.getBottom());
				left.set(insets.getLeft());
			});

			List.of(top, right, bottom, left).forEach(property -> property.subscribe(_ ->
					control.valueProperty().setValue(new Insets(top.get(), right.get(), bottom.get(), left.get()))));

			PropertiesEditor<?> beanEditor = new PropertiesEditor<>();
			beanEditor.editorsProperty().setAll(
					new DoubleEditor(top),
					new DoubleEditor(right),
					new DoubleEditor(bottom),
					new DoubleEditor(left)
			);
			getChildren().setAll(beanEditor);
		}
	}

}
