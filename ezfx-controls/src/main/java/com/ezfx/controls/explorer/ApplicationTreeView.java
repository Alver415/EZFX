package com.ezfx.controls.explorer;

import javafx.scene.control.TreeView;

public class ApplicationTreeView<A, B> extends TreeView<TreeValue<A, B>> {

	public ApplicationTreeView() {
		setCellFactory(_ -> new ApplicationTreeCell<>());
	}
}
