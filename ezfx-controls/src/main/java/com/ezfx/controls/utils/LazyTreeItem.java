package com.ezfx.controls.utils;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.util.function.Function;

public abstract class LazyTreeItem<T> extends TreeItem<T> {

	private boolean isLeaf;
	private boolean isFirstTimeChildren = true;
	private boolean isFirstTimeLeaf = true;

	private final Function<T, ObservableList<T>> childrenProvider;

	public LazyTreeItem(T value, Function<T, ObservableList<T>> childrenProvider) {
		super(value);
		this.childrenProvider = childrenProvider;
	}

	protected abstract LazyTreeItem<T> create(T childValue, Function<T, ObservableList<T>> childrenProvider);

	@Override
	public ObservableList<TreeItem<T>> getChildren() {
		if (isFirstTimeChildren) {
			isFirstTimeChildren = false;
			super.getChildren().setAll(buildChildren(this));
		}
		return super.getChildren();
	}

	@Override
	public boolean isLeaf() {
		if (isFirstTimeLeaf) {
			isFirstTimeLeaf = false;
			isLeaf = childrenProvider.apply(getValue()).isEmpty();
		}
		return isLeaf;
	}

	private ObservableList<TreeItem<T>> buildChildren(TreeItem<T> parent) {
		T value = parent.getValue();
		ObservableList<T> children = childrenProvider.apply(value);
		ObservableList<TreeItem<T>> childrenItems = FXCollections.observableArrayList();

		for (T child : children) {
			LazyTreeItem<T> childItem = create(child, childrenProvider);
			childrenItems.add(childItem);
		}

		children.addListener((ListChangeListener<? super T>) change -> {
			while (change.next()) {
				if (change.wasAdded()) {
					for (T child : change.getAddedSubList()) {
						getChildren().add(create(child, childrenProvider));
					}
				}
				if (change.wasRemoved()) {
					for (T child : change.getRemoved()) {
						getChildren().removeIf(item -> item.getValue() == child);
					}
				}
			}
		});


		return childrenItems;

	}

}