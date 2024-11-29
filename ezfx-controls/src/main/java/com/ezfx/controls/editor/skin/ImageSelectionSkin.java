package com.ezfx.controls.editor.skin;

import com.ezfx.base.utils.Converter;
import com.ezfx.controls.editor.impl.javafx.ImageSelectionEditor;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.ezfx.base.utils.ComplexBinding.bindBidirectional;

public class ImageSelectionSkin extends EditorSkin<ImageSelectionEditor, Image> {
	private static final Image EMPTY_IMAGE = new WritableImage(16, 16);
	private static final Logger log = LoggerFactory.getLogger(ImageSelectionSkin.class);

	public ImageSelectionSkin(ImageSelectionEditor editor) {
		super(editor);
		TextField urlTextField = new TextField();

		bindBidirectional(urlTextField.textProperty(), editor.property(), STRING_TO_IMAGE);
		getChildren().setAll(urlTextField);
	}

	private static final Converter<String, Image> STRING_TO_IMAGE = Converter.of(url -> {
				try {
					return new Image(url);
				} catch (Exception e) {
					log.warn(e.getMessage(), e);
					return EMPTY_IMAGE;
				}
			},
			image -> image == null ? "" : image.getUrl());
}
