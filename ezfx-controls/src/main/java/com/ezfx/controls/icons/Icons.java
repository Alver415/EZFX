package com.ezfx.controls.icons;

import com.ezfx.base.utils.Resources;
import javafx.scene.image.Image;

public interface Icons {
	Image X = load("x.png");
	Image PLUS = load("plus.png");
	Image MINUS = load("minus.png");
	Image LOCKED = load("locked.png");
	Image UNLOCKED = load("unlocked.png");

	Image EDIT = load("font-awesome/edit.png");
	Image LOADING = load("gifs/loading.gif");

	Image CLOSE = load("mycons/close.png");
	Image RESTORE = load("mycons/restore.png");
	Image MAXIMIZE = load("mycons/maximize.png");
	Image MINIMIZE = load("mycons/minimize.png");

	private static Image load(String image) {
		return Resources.image(Icons.class, image);
	}

}
