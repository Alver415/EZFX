package com.ezfx.controls.editor.impl.javafx;

import com.ezfx.base.utils.Converter;
import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.EditorSkinBase;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Skin;
import javafx.scene.paint.ImagePattern;
import javafx.util.Subscription;

import static com.ezfx.base.utils.ComplexBinding.bindBidirectional;

public class ImagePatternEditor extends ObjectEditor<ImagePattern> {

	public ImagePatternEditor() {
		super(new SimpleObjectProperty<>());
	}
	public ImagePatternEditor(Property<ImagePattern> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new DefaultSkin(this);
	}
	public static class DefaultSkin extends EditorSkinBase<ImagePatternEditor, ImagePattern> {

		private final ImageSelectionEditor imageSelectionEditor = new ImageSelectionEditor();
		private Subscription subscription;

		public DefaultSkin(ImagePatternEditor editor) {
			super(editor);
			setChildren(imageSelectionEditor);
		}

		@Override
		public void install() {
			super.install();
			if (subscription != null) {
				subscription.unsubscribe();
			}
			subscription = bindBidirectional(imageSelectionEditor.valueProperty(), valueProperty(), Converter.of(
					image -> image == null ? null : new ImagePattern(image),
					pattern -> pattern == null ? null : pattern.getImage()));
		}

		@Override
		public void dispose() {
			super.dispose();
			if (subscription != null) {
				subscription.unsubscribe();
			}
		}
	}
}
