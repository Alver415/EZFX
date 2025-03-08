package com.ezfx.controls.editor.impl.standard;

import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.EditorSkinBase;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.util.List;

public class SelectionEditor<T> extends ObjectEditor<T> {

	public SelectionEditor(ObservableList<T> items) {
		super(new SimpleObjectProperty<>());
		setItems(items);
	}

	public SelectionEditor(Property<T> property, ObservableList<T> items) {
		super(property);
		setItems(items);
	}

	public SelectionEditor(List<T> items) {
		super(new SimpleObjectProperty<>());
		getItems().setAll(items);
	}

	public SelectionEditor(Property<T> property, List<T> items) {
		super(property);
		getItems().setAll(items);
	}

	private final ListProperty<T> items = new SimpleListProperty<>(this, "items", FXCollections.observableArrayList());

	public ListProperty<T> itemsProperty() {
		return this.items;
	}

	public ObservableList<T> getItems() {
		return this.itemsProperty().get();
	}

	public void setItems(ObservableList<T> value) {
		this.itemsProperty().set(value);
	}

	private final ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty<>(this, "converter");

	public ObjectProperty<StringConverter<T>> converterProperty() {
		return this.converter;
	}

	public StringConverter<T> getConverter() {
		return this.converterProperty().get();
	}

	public void setConverter(StringConverter<T> value) {
		this.converterProperty().set(value);
	}
	private final ObjectProperty<Callback<ListView<T>, ListCell<T>>> cellFactory = new SimpleObjectProperty<>(this, "cellFactory");

	public ObjectProperty<Callback<ListView<T>, ListCell<T>>> cellFactoryProperty() {
		return this.cellFactory;
	}

	public Callback<ListView<T>, ListCell<T>> getCellFactory() {
		return this.cellFactoryProperty().get();
	}

	public void setCellFactory(Callback<ListView<T>, ListCell<T>> value) {
		this.cellFactoryProperty().set(value);
	}

	private final Property<ListCell<T>> buttonCell = new SimpleObjectProperty<>(this, "buttonCell");

	public Property<ListCell<T>> buttonCellProperty() {
		return this.buttonCell;
	}

	public ListCell<T> getButtonCell() {
		return this.buttonCellProperty().getValue();
	}

	public void setButtonCell(ListCell<T> value) {
		this.buttonCellProperty().setValue(value);
	}


	@Override
	protected Skin<?> createDefaultSkin() {
		return new DefaultSkin<>(this);
	}

	public static class DefaultSkin<T> extends EditorSkinBase<SelectionEditor<T>, T> {

		public DefaultSkin(SelectionEditor<T> control) {
			super(control);
			ComboBox<T> comboBox = new ComboBox<>();
			comboBox.converterProperty().bindBidirectional(control.converterProperty());
			comboBox.itemsProperty().bindBidirectional(control.itemsProperty());
			comboBox.cellFactoryProperty().bindBidirectional(control.cellFactoryProperty());
			comboBox.buttonCellProperty().bindBidirectional(control.buttonCellProperty());
			comboBox.valueProperty().bindBidirectional(control.valueProperty());

			HBox hBox = new HBox(comboBox);
			getChildren().setAll(hBox);
		}
	}
}
