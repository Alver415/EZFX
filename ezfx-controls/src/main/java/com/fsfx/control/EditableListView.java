package com.fsfx.control;


import javafx.scene.control.ListView;

public class EditableListView<T> extends ListView<T> {

	public EditableListView() {
		setCellFactory(_ -> new EditableListCell<>(this));
	}

}
