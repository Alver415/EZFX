package com.ezfx.controls.editor.skin;

import com.ezfx.base.utils.Converter;
import com.ezfx.controls.editor.impl.javafx.ImageSelectionEditor;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.ezfx.base.utils.ComplexBinding.bindBidirectional;

public class ImageSelectionSkin extends EditorSkin<ImageSelectionEditor, Image> {
	private static final Logger log = LoggerFactory.getLogger(ImageSelectionSkin.class);

	private static final Image EMPTY_IMAGE = new WritableImage(16, 16);
	private static final Converter<String, Image> STRING_TO_IMAGE = Converter.of(
			ImageSelectionSkin::getImageOrNull,
			ImageSelectionSkin::getUrlOrEmpty);

	private final HBox hBox;
	private final ImageView thumbnail;
	private final TextField urlTextField;

	public ImageSelectionSkin(ImageSelectionEditor editor) {
		super(editor);
		hBox = new HBox();
		thumbnail = new ImageView();
		urlTextField = new TextField();

		thumbnail.fitWidthProperty().bind(urlTextField.heightProperty());
		thumbnail.fitHeightProperty().bind(urlTextField.heightProperty());
		hBox.setSpacing(8);
		hBox.setAlignment(Pos.CENTER_LEFT);
		hBox.getChildren().addAll(thumbnail, urlTextField);

		bindBidirectional(urlTextField.textProperty(), editor.valueProperty(), STRING_TO_IMAGE);
		editor.valueProperty().subscribe(this::onImageChanged);
		getChildren().setAll(hBox);
	}

	private void onImageChanged(Image image) {
		if (image == null || image.isError()) {
			thumbnail.setImage(EMPTY_IMAGE);
		} else if (image.isBackgroundLoading()) {
			thumbnail.setImage(EMPTY_IMAGE);
			image.progressProperty().addListener((_, _, progress) -> {
				if (image.isError()) {
					thumbnail.setImage(EMPTY_IMAGE);
				} else if (progress.doubleValue() >= 1) {
					thumbnail.setImage(image);
				}
			});
		} else {
			thumbnail.setImage(image);
		}
	}


	private static String getUrlOrEmpty(Image image) {
		return image == null ? "" : image.getUrl();
	}

	private static Image getImageOrNull(String url) {
		try {
			return new Image(url);
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			return EMPTY_IMAGE;
		}
	}

}
