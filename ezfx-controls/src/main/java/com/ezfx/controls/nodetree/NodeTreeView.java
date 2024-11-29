package com.ezfx.controls.nodetree;

import javafx.scene.Node;
import javafx.scene.control.TreeView;

public class NodeTreeView extends TreeView<Node> {

	public NodeTreeView() {
		setCellFactory(_ -> new NodeTreeCell());
	}

	public NodeTreeView(Node node){
		this();
		setRoot(new NodeTreeItem(node));
	}
}
