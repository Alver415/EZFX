package com.ezfx.controls.explorer;

import com.ezfx.controls.misc.FilterableTreeItem;
import com.ezfx.controls.nodetree.NodeTreeItem;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.function.Function;

public class GenericTreeItem<T, C> extends FilterableTreeItem<TreeValue<T, C>> {

	public GenericTreeItem(T value) {
		super(TreeValue.build(value));
		this.expandedProperty().subscribe(expanded -> {
			if (expanded && getSourceChildren().isEmpty()) {
				loadChildren();
			}
		});
//		setExpanded(true);
	}

	private void loadChildren() {
		getSourceChildren().clear();
		TreeValue<T, C> treeValue = getValue();
		ObservableList<C> childrenNodes = treeValue.getChildren();
		if (childrenNodes == null) {
			return;
		}
		for (C child : childrenNodes) {
			GenericTreeItem childItem = new GenericTreeItem(child);
			getSourceChildren().add(childItem);
		}
		childrenNodes.addListener((ListChangeListener<Object>) change -> {
			while (change.next()) {
				if (change.wasReplaced() && change.getAddedSubList().equals(change.getRemoved())){
					return;
				}
				if (change.wasRemoved()) {
					for (Object child : change.getRemoved()) {
						getSourceChildren().removeIf(item -> item.getValue().getValue() == child);
					}
				}
				if (change.wasAdded()) {
					for (Object child : change.getAddedSubList()) {
						getSourceChildren().add(new GenericTreeItem(child));
					}
				}
			}
		});
	}

	@Override
	public boolean isLeaf() {
		return false;
	}
}
