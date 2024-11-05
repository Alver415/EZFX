package com.fsfx.control.editor;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.TriangleMesh;

import java.util.List;
import java.util.function.Function;

public class InsetsEditor extends EditorControl<Insets> {
	public InsetsEditor(Property<Insets> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new InsetsEditorSkin(this);
	}

	public static class InsetsEditorSkin extends SkinBase<InsetsEditor> {
		private InsetsEditorSkin(InsetsEditor control) {
			super(control);

			DoubleProperty top = new SimpleDoubleProperty();
			DoubleProperty bottom = new SimpleDoubleProperty();
			DoubleProperty right = new SimpleDoubleProperty();
			DoubleProperty left = new SimpleDoubleProperty();

			control.propertyProperty().flatMap(Function.identity()).subscribe(insets -> {
				insets = insets == null ? Insets.EMPTY : insets;
				top.set(insets.getTop());
				right.set(insets.getRight());
				bottom.set(insets.getBottom());
				left.set(insets.getLeft());
			});

			List.of(top, right, bottom, left).forEach(property -> property.subscribe(_ ->
					control.getProperty().setValue(new Insets(top.get(), right.get(), bottom.get(), left.get()))));

			BeanEditor<Object> beanEditor = new BeanEditor<>();
			beanEditor.setSkin(new BeanEditorSkinBase.HBoxSkin<>(beanEditor));
			beanEditor.getEditors().setAll(
					new Editor<>("Top", new DoubleEditor(top)),
					new Editor<>("Right", new DoubleEditor(right)),
					new Editor<>("Bottom", new DoubleEditor(bottom)),
					new Editor<>("Left", new DoubleEditor(left)));
			getChildren().setAll(beanEditor);
		}
	}
}
