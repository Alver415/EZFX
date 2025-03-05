package com.ezfx.controls.tree;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.util.function.Function;

public class LazyFilterableTreeItem<T> extends FilterableTreeItem<T> {

	private final Function<T, ObservableList<T>> childrenProvider;

	public LazyFilterableTreeItem(T rootNode, Function<T, ObservableList<T>> childrenProvider) {
		super(rootNode);
		this.childrenProvider = childrenProvider;
	}

	private boolean isInitialized = false;

	@Override
	public ObservableList<TreeItem<T>> getSourceChildren() {
		if (!isInitialized) initializeSourceChildren();
		return super.getSourceChildren();
	}

	private void initializeSourceChildren() {
		sourceChildren.clear();
		ObservableList<T> childrenNodes = childrenProvider.apply(getValue());
		for (T child : childrenNodes) {
			LazyFilterableTreeItem<T> childItem = new LazyFilterableTreeItem<>(child, childrenProvider);
			sourceChildren.add(childItem);
		}
		childrenNodes.addListener((ListChangeListener<T>) change -> {
			while (change.next()) {
				if (change.wasRemoved()) {
					for (T child : change.getRemoved()) {
						sourceChildren.removeIf(item -> item.getValue() == child);
					}
				}
				if (change.wasAdded()) {
					for (T child : change.getAddedSubList()) {
						sourceChildren.add(new LazyFilterableTreeItem<>(child, childrenProvider));
					}
				}
			}
		});
		isInitialized = true;
	}

	@Override
	public boolean isLeaf() {
		return getSourceChildren().isEmpty();
	}
}
