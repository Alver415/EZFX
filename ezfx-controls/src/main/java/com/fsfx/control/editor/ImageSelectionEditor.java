package com.fsfx.control.editor;

import com.ezfx.base.utils.Converter;
import javafx.beans.property.Property;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;

import static com.ezfx.base.MappedBinding.bindBidirectional;

public class ImageSelectionEditor extends EditorControl<Image> {

	public ImageSelectionEditor(Property<Image> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new ImageSelectionSkin();
	}

	private class ImageSelectionSkin extends SkinBase<ImageSelectionEditor> {

		protected ImageSelectionSkin() {
			super(ImageSelectionEditor.this);
			TextField urlTextField = new TextField();

			Converter.Simple<String, Image> converter = Converter.of(this::toImage, this::fromUrl);
			bindBidirectional(urlTextField.textProperty(), getProperty(), converter);
			getChildren().setAll(urlTextField);
		}

		private Image toImage(String url) {
			try {
				return new Image(url, true);
			} catch (Exception e) {
				return null;
			}
		}

		private String fromUrl(Image image) {
			return image == null ? null : image.getUrl();
		}
	}
}
