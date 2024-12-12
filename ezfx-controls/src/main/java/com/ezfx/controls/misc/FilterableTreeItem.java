package com.ezfx.controls.misc;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.TreeItem;

import java.util.function.Predicate;

public class FilterableTreeItem<T> extends TreeItem<T> {
	private final ObservableList<TreeItem<T>> sourceChildren = FXCollections.observableArrayList();
	private final FilteredList<TreeItem<T>> filteredChildren = new FilteredList<>(sourceChildren);

	public FilterableTreeItem(T value) {
		super(value);

		filteredChildren.predicateProperty().bind(Bindings.createObjectBinding(() -> child -> {
			if (child instanceof FilterableTreeItem<T> filterableChild) {
				filterableChild.setPredicate(this.getPredicate());
			}
			if (getPredicate() == null || !child.getChildren().isEmpty()) {
				return true;
			}
			return getPredicate().test(child.getValue());
		}, predicateProperty()));

		filteredChildren.addListener((ListChangeListener<TreeItem<T>>) c -> {
			while (c.next()) {
				getChildren().removeAll(c.getRemoved());
				getChildren().addAll(c.getAddedSubList());
			}
		});
	}

	public ObservableList<TreeItem<T>> getSourceChildren() {
		return sourceChildren;
	}

	private final Property<Predicate<T>> predicate = new SimpleObjectProperty<>(this, "predicate");

	public Property<Predicate<T>> predicateProperty() {
		return this.predicate;
	}

	public Predicate<T> getPredicate() {
		return this.predicateProperty().getValue();
	}

	public void setPredicate(Predicate<T> value) {
		this.predicateProperty().setValue(value);
	}
}
