package com.ezfx.controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.util.HashSet;

public class SetView<T> extends Control {

	private final SetProperty<T> set = new SimpleSetProperty<>(this, "set", FXCollections.observableSet(new HashSet<>()));

	public SetProperty<T> setProperty() {
		return this.set;
	}

	public ObservableSet<T> getSet() {
		return this.setProperty().getValue();
	}

	public void setSet(ObservableSet<T> value) {
		this.setProperty().setValue(value);
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

	@Override
	protected Skin<?> createDefaultSkin() {
		return new DefaultSkin<>(this);
	}

	private static class DefaultSkin<T> extends SkinBase<SetView<T>> {

		private final ListView<T> listView;

		public DefaultSkin(SetView<T> control) {
			super(control);
			listView = new ListView<>();
			getChildren().setAll(listView);
		}

		@Override
		public void install() {
			super.install();
			SetView<T> view = getSkinnable();

			listView.cellFactoryProperty().bind(view.cellFactoryProperty());

			view.setProperty().forEach(value -> listView.getItems().add(value));
			view.setProperty().addListener((SetChangeListener<? super T>) change -> {
				if (change.wasRemoved()) {
					listView.getItems().remove(change.getElementRemoved());
				}
				if (change.wasAdded()) {
					listView.getItems().add(change.getElementAdded());
				}
			});
		}
	}
}
