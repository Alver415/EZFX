package com.ezfx.controls.tree;

import com.ezfx.controls.info.FXItem;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

import java.util.function.BiFunction;
import java.util.function.Function;

public class FXTreeControl extends TreeControl<FXItem<?,?>> {

	public FXTreeControl() {
		setChildrenProvider(CHILDREN_PROVIDER);
		setFilterFunction(FILTER_FUNCTION);
		setCellFactory(CELL_FACTORY);
	}

	public static final Callback<TreeView<FXItem<?, ?>>, TreeCell<FXItem<?, ?>>> CELL_FACTORY =
			_ -> new FXTreeCell();

	public static final Function<FXItem<?, ?>, ObservableList<FXItem<?, ?>>> CHILDREN_PROVIDER =
			item -> (ObservableList<FXItem<?, ?>>) item.getChildren();

	public static final BiFunction<String, FXItem<?, ?>, Boolean> FILTER_FUNCTION = (filterText, item) -> {
		if (filterText == null || filterText.isEmpty()) return true;
		filterText = filterText.toLowerCase();
		if (filterText.startsWith("#")) {
			if (item.getId() != null && item.getId().toLowerCase().contains(filterText.substring(1))) {
				return true;
			}
		} else if (filterText.startsWith(".")) {
			for (String styleClass : item.getStyleClass()) {
				if (styleClass.toLowerCase().contains(filterText.substring(1))) {
					return true;
				}
			}
		} else {
			if (item.getClass().getSimpleName().toLowerCase().contains(filterText)) {
				return true;
			}
		}
		return false;
	};

}
