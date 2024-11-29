package com.ezfx.controls.nodetree;

import javafx.scene.Node;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;

public class NodeTreeTableView extends TreeTableView<Node> {

	public NodeTreeTableView() {
		TreeTableColumn<Node, Node> name = new TreeTableColumn<>();
		name.setCellValueFactory(cdf -> cdf.getValue().valueProperty());
		name.setCellFactory(_ -> new NodeTreeTableCell());
		getColumns().add(name);

		setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
	}

	public NodeTreeTableView(Node node) {
		this();
		setRoot(new NodeTreeItem(node));
	}
}
