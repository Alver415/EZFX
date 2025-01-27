package com.ezfx.controls.editor.impl.javafx;

import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.impl.standard.DoubleEditor;
import com.ezfx.controls.editor.skin.EditorSkin;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Subscription;

import java.util.List;

import static com.ezfx.base.utils.ComplexBinding.bindBidirectional;

public class LayoutEditor extends ObjectEditor<Node> {
	public LayoutEditor() {
		super(new SimpleObjectProperty<>());
	}

	public LayoutEditor(Property<Node> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new DefaultSkin(this);
	}

	public static class DefaultSkin extends EditorSkin<Editor<Node>, Node> {
		private final DoubleProperty layoutX = new SimpleDoubleProperty(this, "x");
		private final DoubleProperty layoutY = new SimpleDoubleProperty(this, "y");
		private final DoubleEditor xEditor = new DoubleEditor(layoutX);
		private final DoubleEditor yEditor = new DoubleEditor(layoutY);

		private final Text text = new Text("(layoutX, layoutY):");
		private final HBox hBox = new HBox(4, text, xEditor, yEditor);

		private Subscription subscription = () -> {};

		public DefaultSkin(Editor<Node> editor) {
			super(editor);

			editor.valueProperty().subscribe(node -> {
				if (node == null) return;
				subscription.unsubscribe();
				subscription = Subscription.combine(
						bindBidirectional(node.layoutXProperty(), layoutX),
						bindBidirectional(node.layoutYProperty(), layoutY)
				);
			});

			getChildren().setAll(hBox);
		}
	}
}
