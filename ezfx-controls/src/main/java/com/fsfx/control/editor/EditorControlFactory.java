package com.fsfx.control.editor;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.io.File;
import java.util.List;

public class EditorControlFactory {

	public <T> EditorControl<T> buildEditorControl(PropertyInfo<T> propertyInfo, Property<T> property) {
		return buildEditorControl(propertyInfo.type(), property);
	}
	@SuppressWarnings("unchecked")
	public <T> EditorControl<T> buildEditorControl(Class<T> type, Property<T> property) {
		EditorControl<T> editor;
		if (type.isEnum()) {
			T[] enums = type.getEnumConstants();
			editor = new SelectionEditor<>(property, List.of(enums));
		} else if (String.class.equals(type)) {
			editor = (EditorControl<T>) new TextEditor((Property<String>) property);
		} else if (Boolean.class.equals(type)) {
			editor = (EditorControl<T>) new BooleanEditor((Property<Boolean>) property);
		} else if (Double.class.equals(type)) {
			editor = (EditorControl<T>) new DoubleEditor((Property<Double>) property);
		} else if (Integer.class.equals(type)) {
			editor = (EditorControl<T>) new IntegerEditor((Property<Integer>) property);
		} else if (Color.class.equals(type)) {
			editor = (EditorControl<T>) new ColorSelectionEditor((Property<Color>) property);
		} else if (Image.class.equals(type)) {
			editor = (EditorControl<T>) new ImageSelectionEditor((Property<Image>) property);
		} else if (File.class.equals(type)) {
			editor = (EditorControl<T>) new FileSelectionEditor((Property<File>) property);
		} else if (Background.class.equals(type)) {
			editor = (EditorControl<T>) new BackgroundEditor((Property<Background>) property);
		} else if (BackgroundFill.class.equals(type)) {
			editor = (EditorControl<T>) new BackgroundFillEditor((Property<BackgroundFill>) property);
		} else if (Paint.class.equals(type)) {
			editor = (EditorControl<T>) new PaintEditor((Property<Paint>) property);
		} else if (Insets.class.equals(type)) {
			editor = (EditorControl<T>) new InsetsEditor((Property<Insets>) property);
		}  else if (CornerRadii.class.equals(type)) {
			editor = (EditorControl<T>) new CornerRadiiEditor((Property<CornerRadii>) property);
		} else if (Font.class.equals(type)) {
			editor = (EditorControl<T>) new FontEditor((Property<Font>) property);
		} else if (ObservableList.class.equals(type)) {
			editor = (EditorControl<T>) new ListEditor<>((Property<ObservableList<T>>) property);
		} else {
			editor = new IntrospectingBeanEditor<>(property);
		}
		return editor;
	}
}
