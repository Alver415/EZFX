package com.ezfx.controls.utils;


import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Arrays;
import java.util.List;

public interface TableColumns {

	static <A, B> TableColumn<A, B> of(String property) {
		return of(property, property);
	}

	static <A, B> TableColumn<A, B> of(String name, String property) {
		TableColumn<A, B> tableColumn = new TableColumn<>(name);
		tableColumn.setCellValueFactory(new PropertyValueFactory<>(property));
		return tableColumn;
	}


	static <A> List<? extends TableColumn<A, ?>> list(String... properties) {
		return Arrays.stream(properties)
				.map(p -> (TableColumn<A, ?>)of(p))
				.toList();
	}
}
