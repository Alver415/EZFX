package com.ezfx.controls.editor;

import com.ezfx.base.observable.ObservableObjectArray;
import com.ezfx.controls.editor.impl.javafx.BlendModeEditor;
import com.ezfx.controls.editor.impl.javafx.FontEditor;
import com.ezfx.controls.editor.impl.javafx.ImageSelectionEditor;
import com.ezfx.controls.editor.impl.javafx.ShapeEditor;
import com.ezfx.controls.editor.impl.standard.*;
import com.ezfx.controls.editor.introspective.IntrospectingEditor;
import com.ezfx.controls.editor.introspective.IntrospectingPropertiesEditor;
import com.ezfx.controls.editor.introspective.PropertyInfo;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

import static com.ezfx.controls.editor.introspective.IntrospectorFX.DEFAULT_INTROSPECTOR;

public class EditorFactory {

	public static final EditorFactory DEFAULT_FACTORY = new EditorFactory();

	public <T> Editor<T> buildEditor(PropertyInfo propertyInfo, Property<T> property) {
		Type type = propertyInfo.getter().getGenericReturnType();
		if (type instanceof ParameterizedType parameterizedType){
			Type rawType = parameterizedType.getRawType();
			Type genericType = parameterizedType.getActualTypeArguments()[0];
			return buildEditor(rawType, genericType, property);
		}
		return buildEditor((Class<T>) type, property);
	}

	public <T> Editor<T> buildEditor(Class<T> type) {
		return buildEditor(type, new SimpleObjectProperty<>());
	}

	@SuppressWarnings("unchecked")
	public <T> Editor<T> buildEditor(Class<T> type, Property<T> property) {
		Objects.requireNonNull(property);
		Editor<T> editor;
		if (String.class.equals(type)) {
			editor = (Editor<T>) new StringEditor((Property<String>) property);
		} else if (boolean.class.equals(type) || Boolean.class.equals(type)) {
			editor = (Editor<T>) new BooleanEditor((Property<Boolean>) property);
		} else if (double.class.equals(type) || Double.class.equals(type)) {
			editor = (Editor<T>) new DoubleEditor((Property<Double>) property);
		} else if (int.class.equals(type) || Integer.class.equals(type)) {
			editor = (Editor<T>) new IntegerEditor((Property<Integer>) property);
		} else if (Shape.class.isAssignableFrom(type)) {
			editor = (Editor<T>) new ShapeEditor((Property<Shape>) property);
//		} else if (Paint.class.equals(type)) {
//			editor = (Editor<T>) new PaintEditor((Property<Paint>) property);
//		} else if (Color.class.equals(type)) {
//			editor = (Editor<T>) new ColorEditor((Property<Color>) property);
			//TODO Implement LinearGradientEditor and RadialGradientEditor
//		} else if (LinearGradient.class.equals(type)) {
//			editor = (Editor<T>) new LinearGradientEditor((Property<Color>) property);
//		} else if (RadialGradient.class.equals(type)) {
//			editor = (Editor<T>) new RadialGradientEditor((Property<Color>) property);
		} else if (Image.class.equals(type)) {
			editor = (Editor<T>) new ImageSelectionEditor((Property<Image>) property);
		} else if (File.class.equals(type)) {
			editor = (Editor<T>) new FileSelectionEditor((Property<File>) property);
//		} else if (Background.class.equals(type)) {
//			editor = (Editor<T>) new BackgroundEditor((Property<Background>) property);
//		} else if (BackgroundFill.class.equals(type)) {
//			editor = (Editor<T>) new BackgroundFillEditor((Property<BackgroundFill>) property);
//		} else if (Insets.class.equals(type)) {
//			editor = (Editor<T>) new InsetsEditor((Property<Insets>) property);
//		} else if (CornerRadii.class.equals(type)) {
//			editor = (Editor<T>) new CornerRadiiEditor((Property<CornerRadii>) property);
		} else if (Font.class.equals(type)) {
			editor = (Editor<T>) new FontEditor((Property<Font>) property);
		} else if (BlendMode.class.equals(type)) {
			editor = (Editor<T>) new BlendModeEditor((Property<BlendMode>) property);
		} else if (type.isArray()) {
			Class<T> componentType = (Class<T>) type.getComponentType();
			editor = (Editor<T>) new ArrayEditor<>(componentType, (Property<ObservableObjectArray<T>>) property);
		} else if (type.isEnum()) {
			editor = new SelectionEditor<>(property, List.of(type.getEnumConstants()));
		} else {
			boolean hasProperties = !DEFAULT_INTROSPECTOR.getPropertyInfo(type).isEmpty();
			if (hasProperties) {
				editor = new IntrospectingPropertiesEditor<>(property);
			} else {
				editor = new IntrospectingEditor<>(property, type);
			}
		}
		return editor;
	}
	public <T> Editor<T> buildEditor(Type rawType, Type genericType, Property<T> property) {
		if (rawType instanceof Class clazz && List.class.isAssignableFrom(clazz)) {
			ListEditor<T> listEditor = new ListEditor<>((Property<ObservableList<T>>) property);
			listEditor.setGenericType((Class<T>)genericType);
			return (Editor<T>) listEditor;
		}
		return buildEditor((Class<T>)rawType, property);
	}

}
