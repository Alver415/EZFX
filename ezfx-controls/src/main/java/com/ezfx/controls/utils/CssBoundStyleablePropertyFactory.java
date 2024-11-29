package com.ezfx.controls.utils;

import com.ezfx.base.utils.Colors;
import javafx.beans.value.ChangeListener;
import javafx.css.*;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.ColorConverter;
import javafx.css.converter.PaintConverter;
import javafx.css.converter.SizeConverter;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Pair;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CssBoundStyleablePropertyFactory<S extends Styleable> {

	protected final Map<String, Pair<Class<?>, CssMetaData<S, ?>>> metaDataMap;
	protected final List<CssMetaData<? extends Styleable, ?>> unmodifiableMetaDataList;
	protected final List<CssMetaData<? extends Styleable, ?>> metaDataList;

	public CssBoundStyleablePropertyFactory(List<CssMetaData<? extends Styleable, ?>> parentCssMetaData) {
		this.metaDataList = new ArrayList<>();
		this.unmodifiableMetaDataList = Collections.unmodifiableList(this.metaDataList);
		if (parentCssMetaData != null) this.metaDataList.addAll(parentCssMetaData);
		this.metaDataMap = new HashMap<>();
	}


	public final StyleableObjectProperty<Boolean> createBooleanProperty(
			S styleable,
			String propertyName,
			String cssProperty,
			Function<S, StyleableObjectProperty<Boolean>> propertyGetter,
			Boolean initialValue,
			boolean inherits) {
		return createPropertyWithMetaData(
				Boolean.class, styleable, propertyName, cssProperty, initialValue, inherits, propertyGetter);
	}

	public final StyleableObjectProperty<Color> createColorProperty(
			S styleable,
			String propertyName,
			String cssProperty,
			Function<S, StyleableObjectProperty<Color>> propertyGetter,
			Color initialValue,
			boolean inherits) {
		return createPropertyWithMetaData(
				Color.class, styleable, propertyName, cssProperty, initialValue, inherits, propertyGetter);
	}

	public final StyleableObjectProperty<Paint> createPaintProperty(
			S styleable,
			String propertyName,
			String cssProperty,
			Function<S, StyleableObjectProperty<Paint>> propertyGetter,
			Paint initialValue,
			boolean inherits) {
		return createPropertyWithMetaData(
				Paint.class, styleable, propertyName, cssProperty, initialValue, inherits, propertyGetter);
	}

	public <T> StyleableObjectProperty<T> createPropertyWithMetaData(
			Class<T> type,
			S styleable,
			String propertyName,
			String cssProperty,
			T initialValue,
			boolean inherits,
			Function<S, StyleableObjectProperty<T>> propertyGetter) {

		CssMetaData<S, T> cssMetaData = getOrCreateCssMetaData(
				type, cssProperty, propertyGetter, initialValue, inherits);

		return createProperty(type, styleable, propertyName, cssProperty, initialValue, cssMetaData);
	}

	protected static <S extends Styleable, T> StyleableObjectProperty<T> createProperty(
			Class<T> type,
			S styleable,
			String fxPropertyName,
			String cssPropertyName,
			T initialValue,
			CssMetaData<S, T> cssMetaData) {
		StyleableObjectProperty<T> property = new SimpleStyleableObjectProperty<>(
				cssMetaData, styleable, fxPropertyName, initialValue);

		Function<T, String> cssFunction = getCssFunction(type);
		bindCssStyle(getNode(styleable), cssPropertyName, property, cssFunction);
		property.set(initialValue);
		return property;
	}


	protected static <S extends Styleable, T> void bindCssStyle(
			Node node, String cssProperty, StyleableObjectProperty<T> property, Function<T, String> cssFunction) {
		String regex = "%s:.*?;".formatted(cssProperty);
		ChangeListener<T> cssChangeListener = (_, _, value) -> {
			String newPropertyStyle = "%s:%s;".formatted(cssProperty, cssFunction.apply(value));
			String oldStyle = node.getStyle();
			if (Pattern.matches(regex, oldStyle)) {
				node.setStyle(oldStyle.replaceAll(regex, newPropertyStyle));
			} else {
				node.setStyle(oldStyle + newPropertyStyle);
			}
			node.applyCss();
		};
		cssChangeListener.changed(property, property.get(), property.get());
		property.addListener(cssChangeListener);
	}

	public final CssMetaData<S, Paint> getOrCreatePaintCssMetaData(
			final String property,
			final Function<S, StyleableObjectProperty<Paint>> function,
			final Paint initialValue,
			final boolean inherits) {
		return getOrCreateCssMetaData(Paint.class, property, function, initialValue, inherits);
	}

	public final <T> CssMetaData<S, T> getOrCreateCssMetaData(
			final Class<T> type,
			final String property,
			final Function<S, StyleableObjectProperty<T>> function,
			final T initialValue,
			final boolean inherits) {
		Objects.requireNonNull(property);
		Objects.requireNonNull(function);

		return getOrCreateCssMetaData(type, property,
				key -> new SimpleCssMetaData<>(key, function, getCssConverter(type), initialValue, inherits));
	}


	public final List<CssMetaData<? extends Styleable, ?>> getOrCreateCssMetaData() {
		return unmodifiableMetaDataList;
	}


	protected <T> CssMetaData<S, T> getOrCreateCssMetaData(final Class<T> ofClass, String property) {
		return getOrCreateCssMetaData(ofClass, property, null);
	}

	protected <T> CssMetaData<S, T> getOrCreateCssMetaData(
			final Class<T> ofClass, String property, final Function<String, CssMetaData<S, T>> createFunction) {

		final String key = property.toLowerCase();

		Pair<Class<?>, CssMetaData<S, ?>> entry = metaDataMap.get(key);
		if (entry != null) {
			if (entry.getKey() == ofClass) {
				//noinspection unchecked
				return (CssMetaData<S, T>) entry.getValue();
			} else {
				throw new ClassCastException("CssMetaData value is not " + ofClass + ": " + entry.getValue());
			}
		} else if (createFunction == null) {
			throw new NoSuchElementException("No CssMetaData for " + key);
		}

		CssMetaData<S, T> cssMetaData = createFunction.apply(key);
		metaDataMap.put(key, new Pair<>(ofClass, cssMetaData));
		metaDataList.add(cssMetaData);

		return cssMetaData;
	}

	protected static <S extends Styleable> Node getNode(S styleable) {
		Node node = styleable.getStyleableNode();
		if (node != null) {
			return node;
		} else if (styleable instanceof Node n) {
			return n;
		} else {
			throw new RuntimeException("No Styleable Node.");
		}
	}


	protected static final Map<Class<?>, StyleConverter<?, ?>> cssConverters;
	protected static final Map<Class<?>, Function<?, String>> cssFunctions;

	static {
		Pair<Class<Boolean>, Function<Boolean, String>> booleanFunction = new Pair<>(Boolean.class, String::valueOf);
		Pair<Class<Integer>, Function<Integer, String>> integerFunction = new Pair<>(Integer.class, String::valueOf);
		Pair<Class<Double>, Function<Double, String>> doubleFunction = new Pair<>(Double.class, String::valueOf);
		Pair<Class<Paint>, Function<Paint, String>> paintFunction = new Pair<>(Paint.class, Colors::toRgba);
		Pair<Class<Color>, Function<Color, String>> colorFunction = new Pair<>(Color.class, Colors::toRgba);

		cssFunctions = Stream.of(booleanFunction, integerFunction, doubleFunction, paintFunction, colorFunction)
				.collect(Collectors.toMap(Pair::getKey, Pair::getValue));

		Pair<Class<Boolean>, StyleConverter<String, Boolean>> booleanConverter = new Pair<>(
				Boolean.class, BooleanConverter.getInstance());
		Pair<Class<Number>, StyleConverter<ParsedValue<?, Size>, Number>> numberConverter = new Pair<>(
				Number.class, SizeConverter.getInstance());
		Pair<Class<Paint>, StyleConverter<ParsedValue<?, Paint>, Paint>> paintConverter = new Pair<>(
				Paint.class, PaintConverter.getInstance());
		Pair<Class<Color>, StyleConverter<String, Color>> colorConverter = new Pair<>(
				Color.class, ColorConverter.getInstance());

		cssConverters = Stream.of(booleanConverter, numberConverter, paintConverter, colorConverter).collect(
				Collectors.toMap(Pair::getKey, Pair::getValue));
	}


	protected static <T> StyleConverter<?, T> getCssConverter(Class<T> type) {
		//noinspection unchecked
		return (StyleConverter<?, T>) cssConverters.get(type);
	}

	protected static <T> Function<T, String> getCssFunction(Class<T> type) {
		//noinspection unchecked
		return (Function<T, String>) cssFunctions.get(type);
	}

	public static class SimpleCssMetaData<S extends Styleable, V> extends CssMetaData<S, V> {

		protected final Function<S, StyleableObjectProperty<V>> propertyGetter;

		public SimpleCssMetaData(
				String property,
				Function<S, StyleableObjectProperty<V>> propertyGetter,
				StyleConverter<?, V> converter,
				V initialValue) {
			this(property, propertyGetter, converter, initialValue, true);
		}

		public SimpleCssMetaData(
				String property,
				Function<S, StyleableObjectProperty<V>> propertyGetter,
				StyleConverter<?, V> converter,
				V initialValue,
				boolean inherits) {
			super(property, converter, initialValue, inherits);
			this.propertyGetter = propertyGetter;
		}

		@Override
		public boolean isSettable(S styleable) {
			return !propertyGetter.apply(styleable).isBound();
		}

		@Override
		public StyleableProperty<V> getStyleableProperty(S styleable) {
			return propertyGetter.apply(styleable);
		}

	}
}
