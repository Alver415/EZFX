package com.ezfx.controls.utils;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;

public interface SplitPanes {

	static SplitPane horizontal(Node... nodes){
		return create(Orientation.HORIZONTAL, nodes);
	}
	static SplitPane vertical(Node... nodes){
		return create(Orientation.VERTICAL, nodes);
	}
	static SplitPane create(Orientation orientation, Node... nodes){
		SplitPane splitPane = new SplitPane(nodes);
		splitPane.setOrientation(orientation);
		return splitPane;
	}
}
