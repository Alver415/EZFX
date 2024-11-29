package com.ezfx.controls.editor.skin;

import com.ezfx.base.utils.Converter;
import com.ezfx.controls.editor.impl.javafx.ImagePatternEditor;
import com.ezfx.controls.editor.impl.javafx.ImageSelectionEditor;
import javafx.scene.paint.ImagePattern;
import javafx.util.Subscription;

import static com.ezfx.base.utils.ComplexBinding.bindBidirectional;

public class ImagePatternEditorSkin extends EditorSkin<ImagePatternEditor, ImagePattern> {

	private final ImageSelectionEditor imageSelectionEditor = new ImageSelectionEditor();
	private Subscription subscription;

	public ImagePatternEditorSkin(ImagePatternEditor editor) {
		super(editor);
		setChildren(imageSelectionEditor);
	}

	@Override
	public void install() {
		super.install();
		if (subscription != null){
			subscription.unsubscribe();
		}
		subscription = bindBidirectional(imageSelectionEditor.property(), property(), Converter.of(
				image -> image == null ? null : new ImagePattern(image),
				pattern -> pattern == null ? null : pattern.getImage()));
	}

	@Override
	public void dispose() {
		super.dispose();
		if (subscription != null){
			subscription.unsubscribe();
		}
	}
}