package com.ezfx.controls.utils;


import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Arrays;
import java.util.List;

public interface TableViews {

	static <A> TableView<A> create(ObservableList<A> items, String... columns) {
		TableView<A> tableView = new TableView<>(items);
		tableView.getColumns().setAll(TableColumns.list(columns));
		tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
		return tableView;
	}
}
