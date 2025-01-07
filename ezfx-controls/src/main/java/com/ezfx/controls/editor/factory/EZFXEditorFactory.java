package com.ezfx.controls.editor.factory;

import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.code.CSSEditorSkin;
import com.ezfx.controls.editor.impl.javafx.*;
import com.ezfx.controls.editor.impl.standard.StringEditor;
import javafx.application.Application;
import javafx.beans.property.Property;
import javafx.geometry.Insets;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.Objects;
import java.util.Optional;

public class EZFXEditorFactory implements EditorFactory {

	@SuppressWarnings("unchecked")
	@Override
	public <T> Optional<Editor<T>> buildEditor(Class<T> type, Property<T> property) {
		Objects.requireNonNull(property);
		Editor<T> editor = null;

		// Special case for css style property.
		if (String.class.equals(type) && "style".equals(property.getName())) {
			StringEditor stringEditor = new StringEditor((Property<String>) property);
			stringEditor.setSkin(new CSSEditorSkin(stringEditor));
			editor = (Editor<T>) stringEditor;
		} else if (Background.class.equals(type)) {
			editor = (Editor<T>) new BackgroundEditor((Property<Background>) property);
		} else if (BackgroundFill.class.equals(type)) {
			editor = (Editor<T>) new BackgroundFillEditor((Property<BackgroundFill>) property);
		} else if (Insets.class.equals(type)) {
			editor = (Editor<T>) new InsetsEditor((Property<Insets>) property);
		} else if (CornerRadii.class.equals(type)) {
			editor = (Editor<T>) new CornerRadiiEditor((Property<CornerRadii>) property);
		} else if (BlendMode.class.equals(type)) {
			editor = (Editor<T>) new BlendModeEditor((Property<BlendMode>) property);
		} else if (Paint.class.equals(type)) {
			editor = (Editor<T>) new PaintEditor((Property<Paint>) property);
		} else if (Color.class.equals(type)) {
			editor = (Editor<T>) new ColorEditor((Property<Color>) property);
//		} else if (LinearGradient.class.equals(type)) {
//			editor =  (Editor<T>) new LinearGradientEditor((Property<Color>) property);
//		} else if (RadialGradient.class.equals(type)) {
//			editor =  (Editor<T>) new RadialGradientEditor((Property<Color>) property);
		}
		// TODO: Make the rest work with 'assignableFrom' instead of 'equals'
		else if (Application.class.isAssignableFrom(type)) {
			editor = (Editor<T>) new ApplicationEditor((Property<Application>) property);
		}
		return Optional.ofNullable(editor);
	}
}
