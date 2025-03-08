package com.ezfx.controls.editor.impl.javafx;

import com.ezfx.controls.editor.EditorBase;
import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.impl.standard.DoubleEditor;
import com.ezfx.controls.editor.EditorSkinBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point3D;
import javafx.scene.control.Skin;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.util.function.Consumer;

public class Point3DEditor extends ObjectEditor<Point3D> {
	public Point3DEditor() {
		super(new SimpleObjectProperty<>());
	}

	public Point3DEditor(Property<Point3D> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new DefaultSkin(this);
	}

	public static class DefaultSkin extends EditorSkinBase<EditorBase<Point3D>, Point3D> {

		private final ObservableValue<Point3D> point;

		private final DoubleProperty x = new SimpleDoubleProperty(this, "x");
		private final DoubleProperty y = new SimpleDoubleProperty(this, "y");
		private final DoubleProperty z = new SimpleDoubleProperty(this, "z");

		public DefaultSkin(EditorBase<Point3D> control) {
			super(control);

			point = valueProperty().orElse(Point3D.ZERO);
			point.map(Point3D::getX).subscribe(x::set);
			point.map(Point3D::getY).subscribe(y::set);
			point.map(Point3D::getZ).subscribe(z::set);

			Consumer<Number> updateValue = _ -> setValue(new Point3D(x.get(), y.get(), z.get()));
			x.subscribe(updateValue);
			y.subscribe(updateValue);
			z.subscribe(updateValue);

			getChildren().setAll(new HBox(4,
					new Text("(x, y, z):"),
					new DoubleEditor(x),
					new DoubleEditor(y),
					new DoubleEditor(z)));
		}
	}
}
