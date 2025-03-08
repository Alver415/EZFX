package com.ezfx.controls.editor.impl.javafx;

import com.ezfx.controls.editor.EditorBase;
import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.impl.standard.DoubleEditor;
import com.ezfx.controls.editor.EditorSkinBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Subscription;

import static com.ezfx.base.utils.ComplexBinding.bindBidirectional;

public class RotationEditor extends ObjectEditor<Node> {
	public RotationEditor() {
		super(new SimpleObjectProperty<>());
	}

	public RotationEditor(Property<Node> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new DefaultSkin(this);
	}

	public static class DefaultSkin extends EditorSkinBase<EditorBase<Node>, Node> {
		private final DoubleProperty rotation = new SimpleDoubleProperty(this, "rotation");
		private final Property<Point3D> axis = new SimpleObjectProperty<>(this, "axis");

		private Subscription subscription = Subscription.EMPTY;

		public DefaultSkin(EditorBase<Node> editor) {
			super(editor);

			editor.valueProperty().subscribe(node -> {
				if (node == null) return;
				subscription.unsubscribe();
				subscription = Subscription.combine(
						bindBidirectional(node.rotateProperty(), rotation),
						bindBidirectional(node.rotationAxisProperty(), axis)
				);
			});

			DoubleEditor rotationEditor = new DoubleEditor(rotation);
			Point3DEditor axisEditor = new Point3DEditor(axis);
			getChildren().setAll(new VBox(
					new HBox(4, new Text("rotation:"), rotationEditor),
					new HBox(4, new Text("axis:"), axisEditor)));
		}
	}
}
