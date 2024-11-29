package com.ezfx.spring.test.bidi;

import com.ezfx.spring.annotations.FXMLPrototype;
import com.sun.javafx.scene.control.IntegerField;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.List;
import java.util.stream.IntStream;

@FXMLPrototype
public class BidirectionalBindingTestController {

	private final StringProperty text = new SimpleStringProperty(this, "text", "");

	public StringProperty textProperty() {
		return text;
	}

	public String getText() {
		return textProperty().get();
	}

	public void setText(String text) {
		textProperty().set(text);
	}

	@FXML
	public IntegerField count;
	@FXML
	public ListView<String> listView;

	@FXML
	public void requestGarbageCollection() {
		System.gc();
	}

	@FXML
	public void submit() {
		List<String> list = IntStream.range(0, count.getValue()).mapToObj(String::valueOf).toList();
		listView.getItems().addAll(list);
	}
}
