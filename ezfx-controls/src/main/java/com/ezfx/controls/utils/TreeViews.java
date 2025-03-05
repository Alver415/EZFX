package com.ezfx.controls.utils;

import javafx.scene.control.TreeItem;

public interface TreeViews {

	static void recursiveExpand(TreeItem<?> item, int levels, boolean expanded){
		if (levels == 0) return;
		item.setExpanded(expanded);
		item.getChildren().forEach(child -> recursiveExpand(child, levels - 1, expanded));
	}
}
