package com.ezfx.controls.editor.impl.javafx;

import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.impl.standard.DoubleEditor;
import com.ezfx.controls.editor.skin.EditorSkin;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.util.function.Consumer;

public class ScaleEditor extends ObjectEditor<Node> {
	public ScaleEditor() {
		super(new SimpleObjectProperty<>());
	}

	public ScaleEditor(Property<Node> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new DefaultSkin(this);
	}

	public static class DefaultSkin extends EditorSkin<Editor<Node>, Node> {

		private final DoubleProperty x = new SimpleDoubleProperty(this, "x");
		private final DoubleProperty y = new SimpleDoubleProperty(this, "y");
		private final DoubleProperty z = new SimpleDoubleProperty(this, "z");

		public DefaultSkin(Editor<Node> editor) {
			super(editor);

			valueProperty().map(Node::translateXProperty).subscribe(property -> property.bindBidirectional(x));
			valueProperty().map(Node::translateYProperty).subscribe(property -> property.bindBidirectional(y));
			valueProperty().map(Node::translateZProperty).subscribe(property -> property.bindBidirectional(z));

			getChildren().setAll(new HBox(4,
					new Text("(x, y, z):"),
					new DoubleEditor(x),
					new DoubleEditor(y),
					new DoubleEditor(z)));
		}
	}
}
