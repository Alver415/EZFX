package com.ezfx.controls.explorer;

import com.ezfx.base.utils.Resources;
import javafx.scene.control.TreeView;

public class ApplicationTreeView<A, B> extends TreeView<TreeValue<A, B>> {
	private static final String STYLE_SHEET = Resources.css(ApplicationTreeCell.class, "ApplicationTreeView.css");

	public ApplicationTreeView() {
		getStylesheets().add(STYLE_SHEET);
		setCellFactory(_ -> new ApplicationTreeCell<>());
	}
}
