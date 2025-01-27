package com.ezfx.controls.editor.skin;

import com.ezfx.controls.editor.impl.standard.StringEditor;
import javafx.scene.control.TextInputControl;
import javafx.util.Subscription;

import static com.ezfx.base.utils.ComplexBinding.bindBidirectional;

public class StringEditorSkin extends EditorSkin<StringEditor, String> {

	private final TextInputControl inputControl;

	public StringEditorSkin(StringEditor control, TextInputControl inputControl) {
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


