package com.ezfx.controls.editor.impl.filesystem;

import com.ezfx.base.utils.Converter;
import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.skin.EditorSkin;
import com.ezfx.controls.editor.skin.TextFieldSkin;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;

import java.nio.file.Path;

import static com.ezfx.base.utils.ComplexBinding.bindBidirectional;

public class PathEditor extends Editor<Path> {
	public PathEditor() {
		this(new SimpleObjectProperty<>());
	}

	public PathEditor(Property<Path> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new EditorSkin<>(this){
			{
				TextField textField = new TextField();
				Converter<String, Path> converter = Converter.of(Path::of, Path::toString);
				bindBidirectional(textField.textProperty(), PathEditor.this.valueProperty(), converter);
				textField.promptTextProperty().bindBidirectional(PathEditor.this.promptTextProperty());
				getChildren().setAll(textField);
			}
		};
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

}
