package com.ezfx.controls.editor.impl.filesystem;

import com.ezfx.base.utils.Converter;
import com.ezfx.controls.editor.EditorBase;
import com.ezfx.controls.editor.EditorSkinBase;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;

import java.nio.file.Path;

import static com.ezfx.base.utils.ComplexBinding.bindBidirectional;

public class PathEditor extends EditorBase<Path> {
	public PathEditor() {
		this(new SimpleObjectProperty<>());
	}

	public PathEditor(Property<Path> property) {
		super(property);
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

	@Override
	protected Skin<?> createDefaultSkin() {
		return new DefaultSkin(this);
	}

	public static class DefaultSkin extends EditorSkinBase<PathEditor, Path> {
		private final TextField textField;
		public DefaultSkin(PathEditor editor) {
			super(editor);
			textField = new TextField();
			getChildren().setAll(textField);
		}

		@Override
		public void install() {
			super.install();
			bindBidirectional(textField.promptTextProperty(), editor.promptTextProperty());
			bindBidirectional(textField.textProperty(), editor.valueProperty(),
					Converter.of(Path::of, Path::toString));
		}
	}

}
