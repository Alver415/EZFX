package com.ezfx.controls.editor;

import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.monadic.MonadicBinding;

import static javafx.collections.FXCollections.observableArrayList;

@SuppressWarnings("unchecked")
public class PropertyValueRepository {

	public <T> void register(Class<T> type, T value) {
		ListProperty<T> list = (ListProperty<T>) map.computeIfAbsent(type,
				_ -> new SimpleListProperty<>(this, type.getName(), observableArrayList()));
		list.add(value);
	}

	public <T> ObservableList<T> get(Class<T> type) {

		MonadicBinding<? extends ObservableList<T>> binding = EasyBind.select(map)
				.selectObject(m -> (ObservableValue<? extends ObservableList<T>>) m.get(type));

		return binding.get();

		// TODO: Implement observable correctly.
		// Currently only listens for changes in the list.
		// If the map changes, or the list is replaced, events are missed.
//		ObservableList<T> list = observableArrayList();
//		ListChangeListener<? super T> listener = change -> {
//			while (change.next()){
//
//			}
//		};
//
//		binding.addListener((_, oldValue, newValue) -> {
//			Optional.ofNullable(oldValue).ifPresent(o -> o.removeListener(listener));
//			Optional.ofNullable(newValue).ifPresent(n -> n.addListener(listener));
//		});
//
//
//		return list;
	}

	private final MapProperty<Class<?>, ListProperty<?>> map = new SimpleMapProperty<>(this, "map", FXCollections.observableHashMap());

	public MapProperty<Class<?>, ListProperty<?>> mapProperty() {
		return map;
	}

	public ObservableMap<Class<?>, ListProperty<?>> getMap() {
		return this.mapProperty().getValue();
	}

	public void setMap(ObservableMap<Class<?>, ListProperty<?>> value) {
		this.mapProperty().setValue(value);
	}
}
