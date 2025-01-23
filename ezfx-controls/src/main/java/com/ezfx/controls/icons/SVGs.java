package com.ezfx.controls.icons;

import com.ezfx.base.utils.Resources;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.transform.Transform;

import static com.ezfx.controls.icons.SVGPaths._parse;

public enum SVGs {
	MINIMIZE,
	MAXIMIZE,
	RESTORE,
	CLOSE,
	GEAR,
	GEAR2;

	public Group svg() {
		return _parse(Resources.file(Icons.class, "mycons/%s.svg".formatted(name().toLowerCase())));
	}

	public Image image(int size) {
		return image(size, size);
	}
	public Image image(int width, int height) {
		SnapshotParameters params = new SnapshotParameters();
		params.setFill(Color.TRANSPARENT);

		// TODO: These shouldn't be hardcoded. But otherwise we'd need standards for size so we know what we're working with/
		// Currently, the svg files are 16x16 and this scales them up to 32x32
		params.setViewport(new Rectangle2D(0, 0, 16, 16));
		params.setTransform(Transform.scale(2, 2));

		// BUG: For some reason, windows doesn't use icons generate by snapshot for the stage icon, but copying it into a new image works.
		return copyImage(svg().snapshot(params, new WritableImage(width, height)));
	}

	private static Image copyImage(Image source){
		// Create a writable image with the same dimensions as the source
		int width = (int) source.getWidth();
		int height = (int) source.getHeight();
		WritableImage destination = new WritableImage(width, height);

		// Get the PixelReader from the source image and PixelWriter for the destination image
		PixelReader pixelReader = source.getPixelReader();
		PixelWriter pixelWriter = destination.getPixelWriter();

		// Copy pixel-by-pixel from source to destination
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				pixelWriter.setColor(x, y, pixelReader.getColor(x, y));
			}
		}
		return destination;
	}
}