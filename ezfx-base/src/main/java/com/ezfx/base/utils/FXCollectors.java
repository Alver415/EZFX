package com.ezfx.base.utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.stream.Collector;
import java.util.stream.Collectors;

public interface FXCollectors {

	static <T> Collector<T, ?, ObservableList<T>> toObservableArrayList() {
		return Collectors.toCollection(FXCollections::observableArrayList);
	}
}
