package com.ezfx.controls.icons;

import com.ezfx.base.utils.Resources;
import javafx.scene.image.Image;

public interface Icons {
	Image X = load("x.png");
	Image PLUS = load("plus.png");
	Image MINUS = load("minus.png");
	Image LOCKED = load("locked.png");
	Image UNLOCKED = load("unlocked.png");

	private static Image load(String image) {
		return Resources.image(Icons.class, image);
	}

}
