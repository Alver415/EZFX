package com.ezfx.controls.item;

import com.ezfx.controls.tree.TreeControl;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

import java.util.function.BiFunction;
import java.util.function.Function;

public class FXItemTreeControl extends TreeControl<FXItem<?, ?>> {

	public FXItemTreeControl() {
		setChildrenProvider(CHILDREN_PROVIDER);
		setFilterFunction(FILTER_FUNCTION);
		setCellFactory(CELL_FACTORY);
	}

	public static final Callback<TreeView<FXItem<?, ?>>, TreeCell<FXItem<?, ?>>> CELL_FACTORY =
			_ -> new FXItemTreeCell();

	public static final Function<FXItem<?, ?>, ObservableList<? extends FXItem<?, ?>>> CHILDREN_PROVIDER =
			FXItem::getChildren;

	public static final BiFunction<String, FXItem<?, ?>, Boolean> FILTER_FUNCTION = (filterText, item) -> {
		if (filterText == null || filterText.isEmpty()) return true;
		filterText = filterText.toLowerCase();

		Function<ObservableValue<String>, String> prep = obs -> obs.map(String::toLowerCase).orElse("").getValue();
		String primaryInfo = prep.apply(item.getPrimaryInfo());
		String secondaryInfo = prep.apply(item.getSecondaryInfo());
		String tertiaryInfo = prep.apply(item.getTertiaryInfo());

		if (filterText.startsWith("#")) {
			if (secondaryInfo.contains(filterText.substring(1))) {
				return true;
			}
		} else if (filterText.startsWith(".")) {
			for (String styleClass : tertiaryInfo.split("\\.")) {
				if (styleClass.toLowerCase().contains(filterText.substring(1))) {
					return true;
				}
			}
		} else {
			if (primaryInfo.contains(filterText)) {
				return true;
			}
		}
		return false;
	};

}
