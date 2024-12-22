package com.ezfx.controls.editor;

import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import static javafx.collections.FXCollections.observableArrayList;

public class Registry<K, V> {
	private final MapProperty<K, ListProperty<V>> registry = new SimpleMapProperty<>(this, "registry", FXCollections.observableHashMap());

	public MapProperty<K, ListProperty<V>> registryProperty() {
		return registry;
	}

	public ObservableMap<K, ListProperty<V>> getRegistry() {
		return this.registryProperty().getValue();
	}

	public void setRegistry(ObservableMap<K, ListProperty<V>> value) {
		this.registryProperty().setValue(value);
	}

	public void register(K key, V value) {
		ListProperty<V> list = registry.computeIfAbsent(key, _ -> new SimpleListProperty<>(observableArrayList()));
		list.add(value);
	}

	public void unregisterAll(K key) {
		registry.remove(key);
	}

	public void unregister(K key, V value) {
		if (registry.containsKey(key)) {
			ListProperty<V> list = registry.get(key);
			list.remove(value);
		}
	}

	public ObservableList<V> get(K key) {
		return registry.get(key);
	}
}
