package com.ezfx.controls.editor.impl.standard;

import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.EditorSkinBase;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Skin;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.util.Subscription;

import static com.ezfx.base.utils.ComplexBinding.bindBidirectional;

public class StringEditor extends ObjectEditor<String> {

	public StringEditor() {
		this(new SimpleStringProperty());
	}

	public StringEditor(Property<String> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new TextFieldSkin(this);
	}

	private final StringProperty promptText = new SimpleStringProperty(this, "promptText");

	public StringProperty promptTextProperty() {
		return this.promptText;
	}

	public String getPromptText() {
		return this.promptTextProperty().getValue();
	}

	public void setPromptText(String value) {
		this.promptTextProperty().setValue(value);
	}

	public static class TextInputControlSkinBase extends EditorSkinBase<StringEditor, String> {

		private final TextInputControl inputControl;

		public TextInputControlSkinBase(StringEditor control, TextInputControl inputControl) {
			super(control);
			this.inputControl = inputControl;
			getChildren().setAll(inputControl);
		}

		private Subscription subscription = () -> {
		};

		@Override
		public void install() {
			subscription = Subscription.combine(
					bindBidirectional(inputControl.textProperty(), editor.valueProperty()),
					bindBidirectional(inputControl.promptTextProperty(), editor.promptTextProperty())
			);
		}

		@Override
		public void dispose() {
			subscription.unsubscribe();
		}
	}

	public static class TextFieldSkin extends TextInputControlSkinBase {
		public TextFieldSkin(StringEditor control) {
			super(control, new TextField());
		}
	}

	public static class TextAreaSkin extends TextInputControlSkinBase {
		public TextAreaSkin(StringEditor control) {
			super(control, new TextArea());
		}
	}

}
