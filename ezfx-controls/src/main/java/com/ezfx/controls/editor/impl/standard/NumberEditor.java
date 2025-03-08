package com.ezfx.controls.editor.impl.standard;

import com.ezfx.base.utils.Converter;
import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.EditorSkinBase;
import com.ezfx.controls.icons.SVGs;
import com.ezfx.controls.misc.RepeatingButton;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.UnaryOperator;

import static com.ezfx.base.utils.ComplexBinding.bindBidirectional;
import static com.ezfx.base.utils.Converters.NUMBER_TO_DOUBLE;
import static com.ezfx.base.utils.Converters.asStringConverter;

public abstract class NumberEditor<T extends Number> extends ObjectEditor<T> {

	protected NumberEditor() {
		this(new SimpleObjectProperty<>());
	}

	protected NumberEditor(Property<T> property) {
		this(property, null, null);
	}

	protected NumberEditor(Property<T> property, T min, T max) {
		super(property);
		setMin(min);
		setMax(max);
	}

	abstract Converter<Number, T> numberToValueConverter();

	abstract Converter<String, T> stringToValueConverter();

	abstract UnaryOperator<TextFormatter.Change> textFormatFilter();

	private final Property<T> defaultValue = new SimpleObjectProperty<>(this, "defaultValue");

	public Property<T> defaultValueProperty() {
		return this.defaultValue;
	}

	public T getDefaultValue() {
		return this.defaultValueProperty().getValue();
	}

	public void setDefaultValue(T value) {
		this.defaultValueProperty().setValue(value);
	}

	private final Property<T> min = new SimpleObjectProperty<>(this, "min");

	public Property<T> minProperty() {
		return this.min;
	}

	public T getMin() {
		return this.minProperty().getValue();
	}

	public void setMin(T value) {
		this.minProperty().setValue(value);
	}

	private final Property<T> max = new SimpleObjectProperty<>(this, "max");

	public Property<T> maxProperty() {
		return this.max;
	}

	public T getMax() {
		return this.maxProperty().getValue();
	}

	public void setMax(T value) {
		this.maxProperty().setValue(value);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new TextFieldWithButtonsSkin<>(this);
	}

	public static class TextFieldSkin<T extends Number> extends EditorSkinBase<NumberEditor<T>, T> {
		public TextFieldSkin(NumberEditor<T> editor) {
			super(editor);
			TextField textField = new TextField();
			bindBidirectional(textField.textProperty(), editor.valueProperty(), editor.stringToValueConverter());
			getChildren().setAll(textField);
		}
	}

	public static class TextFieldWithButtonsSkin<T extends Number> extends EditorSkinBase<NumberEditor<T>, T> {

		private final Converter<T, Double> incrementConverter =
				editor.numberToValueConverter().inverted().compound(NUMBER_TO_DOUBLE);

		public TextFieldWithButtonsSkin(NumberEditor<T> editor) {
			super(editor);
			TextField textField = new TextField();


			Button increment = new RepeatingButton(SVGs.PLUS.svg(0.5));
			increment.setFocusTraversable(false);
			increment.setOnAction(_ -> increment(1));

			Button decrement = new RepeatingButton(SVGs.MINUS.svg(0.5));
			decrement.setFocusTraversable(false);
			decrement.setOnAction(_ -> increment(-1));

			TextFormatter<T> textFormatter = new TextFormatter<>(
					asStringConverter(editor.stringToValueConverter()),
					editor.getDefaultValue(),
					editor.textFormatFilter());
			textField.setTextFormatter(textFormatter);
			bindBidirectional(textField.textProperty(), editor.valueProperty(), editor.stringToValueConverter());


			//TODO: Cleanup this size binding. Ideally move to css if not performance issue
			increment.maxHeightProperty().bind(textField.heightProperty().divide(2));
			increment.minHeightProperty().bind(textField.heightProperty().divide(2));
			increment.prefHeightProperty().bind(textField.heightProperty().divide(2));
			decrement.minHeightProperty().bind(textField.heightProperty().divide(2));
			decrement.maxHeightProperty().bind(textField.heightProperty().divide(2));
			decrement.prefHeightProperty().bind(textField.heightProperty().divide(2));
			decrement.maxWidthProperty().bind(increment.widthProperty());
			decrement.minWidthProperty().bind(increment.widthProperty());
			decrement.prefWidthProperty().bind(increment.widthProperty());


			getChildren().setAll(new HBox(textField, new VBox(increment, decrement)));
		}

		private void increment(double amount) {
			Double doubleValue = incrementConverter.to(getValue());
			if (doubleValue == null) return;
			setValue(incrementConverter.from(doubleValue + amount));
		}
	}

	public static class SliderSkin<T extends Number> extends EditorSkinBase<NumberEditor<T>, T> {
		public SliderSkin(NumberEditor<T> editor) {
			super(editor);
			Slider slider = new Slider();
			bindBidirectional(slider.valueProperty(), editor.valueProperty(), editor.numberToValueConverter());
			slider.minProperty().bind(editor.minProperty());
			slider.maxProperty().bind(editor.maxProperty());
			slider.setSnapToTicks(true);
			getChildren().setAll(slider);
		}
	}

}
