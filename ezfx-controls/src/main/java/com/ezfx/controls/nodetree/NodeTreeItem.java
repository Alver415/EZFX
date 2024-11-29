package com.ezfx.controls.nodetree;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TreeItem;

import java.util.function.Function;

public class NodeTreeItem extends TreeItem<Node> {

	public static final Function<Node, ObservableList<Node>> DEFAULT_CHILDREN_PROVIDER =
			node -> node instanceof Parent parent ?
					parent.getChildrenUnmodifiable() :
					FXCollections.emptyObservableList();

	private final Function<Node, ObservableList<Node>> childrenProvider;

	public NodeTreeItem(Node rootNode) {
		this(rootNode, DEFAULT_CHILDREN_PROVIDER);
	}

	public NodeTreeItem(Node rootNode, Function<Node, ObservableList<Node>> childrenProvider) {
		super(rootNode);
		this.childrenProvider = childrenProvider;
		this.expandedProperty().subscribe(expanded -> {
			if (expanded && getChildren().isEmpty()) {
				loadChildren();
			}
		});
		setExpanded(true);
	}


	private void loadChildren() {
		getChildren().clear();
		ObservableList<Node> childrenNodes = childrenProvider.apply(getValue());
		if (childrenNodes == null) {
			return;
		}
		for (Node child : childrenNodes) {
			NodeTreeItem childItem = new NodeTreeItem(child, childrenProvider);
			getChildren().add(childItem);
		}
		childrenNodes.addListener((ListChangeListener<? super Node>) change -> {
			while (change.next()) {
				if (change.wasAdded()) {
					for (Node child : change.getAddedSubList()) {
						getChildren().add(new NodeTreeItem(child, childrenProvider));
					}
				}
				if (change.wasRemoved()) {
					for (Node child : change.getRemoved()) {
						getChildren().removeIf(item -> item.getValue() == child);
					}
				}
			}
		});
	}

	@Override
	public boolean isLeaf() {
		return getChildren().isEmpty();
	}
}
