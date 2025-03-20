package com.ezfx.controls;

import com.ezfx.base.utils.ObservableConstant;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.control.*;

import java.util.Objects;

public class MapView<K, V> extends Control {

	private final MapProperty<K, V> map = new SimpleMapProperty<>(this, "map", FXCollections.observableHashMap());

	public MapProperty<K, V> mapProperty() {
		return this.map;
	}

	public ObservableMap<K, V> getMap() {
		return this.mapProperty().getValue();
	}

	public void setMap(ObservableMap<K, V> value) {
		this.mapProperty().setValue(value);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new DefaultSkin<>(this);
	}

	private static class DefaultSkin<K, V> extends SkinBase<MapView<K, V>> {

		private record Item<K, V>(K key, V value) {
		}

		private final TableView<Item<K, V>> tableView;
		private final TableColumn<Item<K, V>, K> keyColumn;
		private final TableColumn<Item<K, V>, V> valueColumn;

		public DefaultSkin(MapView<K, V> mapView) {
			super(mapView);
			tableView = new TableView<>();
			tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
			keyColumn = new TableColumn<>("Key");
			valueColumn = new TableColumn<>("Value");

			keyColumn.setCellValueFactory(entry -> ObservableConstant.constant(entry.getValue().key()));
			valueColumn.setCellValueFactory(entry -> ObservableConstant.constant(entry.getValue().value()));

			//TODO: Add CellFactories for both key and value columns bound via control.

			tableView.getColumns().add(keyColumn);
			tableView.getColumns().add(valueColumn);

			getChildren().setAll(tableView);
		}

		@Override
		public void install() {
			super.install();
			MapProperty<K, V> map = getSkinnable().mapProperty();
			map.forEach((key, value) -> tableView.getItems().add(new Item<>(key, value)));
			map.addListener((MapChangeListener<? super K, ? super V>) change -> {
				if (change.wasRemoved()) {
					tableView.getItems().removeIf(item -> Objects.equals(item.key, change.getKey()));
				}
				if (change.wasAdded()) {
					tableView.getItems().add(new Item<>(change.getKey(), change.getValueAdded()));
				}
			});
		}

		@Override
		public void dispose() {
			super.dispose();
		}
	}
}
