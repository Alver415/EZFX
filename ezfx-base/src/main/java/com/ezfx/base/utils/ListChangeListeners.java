package com.ezfx.base.utils;

import javafx.collections.ListChangeListener;

import java.util.function.Consumer;

public interface ListChangeListeners {

	static <T> ListChangeListener<T> forEach(Consumer<ListChangeListener.Change<? extends T>> consumer){
		return change -> {
			while (change.next()){
				consumer.accept(change);
			}
		};
	}
	static <T> ListChangeListener<T> forEachAdded(Consumer<T> consumer){
		return change -> {
			while (change.next()){
				change.getAddedSubList().forEach(consumer);
			}
		};
	}
	static <T> ListChangeListener<T> forEachRemoved(Consumer<T> consumer){
		return change -> {
			while (change.next()){
				change.getRemoved().forEach(consumer);
			}
		};
	}
}
