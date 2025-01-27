package com.ezfx.base.utils;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

import static com.ezfx.base.utils.ComplexBinding.bindBidirectional;

public interface Properties {

	static <T> Property<T> copyWithName(Property<T> property, String name){
		Property<T> bound = new SimpleObjectProperty<>(null, name);
		bindBidirectional(bound, property);
		return bound;
	}

	static <T, R> Property<R> convert(Property<T> property, Converter<R, T> converter){
		Property<R> bound = new SimpleObjectProperty<>(property.getBean(), property.getName());
		bindBidirectional(bound, property, converter);
		return bound;
	}
}
