package com.ezfx.controls.editor.factory;

import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.FXItemEditor;
import com.ezfx.controls.editor.impl.javafx.*;
import com.ezfx.controls.info.FXItem;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.lang.reflect.Type;
import java.util.Optional;

public class EZFXEditorFactory implements EditorFactory {

	@SuppressWarnings("unchecked")
	@Override
	public <T> Optional<Editor<T>> buildEditor(Type type) {
		if (!(type instanceof Class<?> classType)) return Optional.empty();
		Editor<T> editor = null;

		if (FXItem.class.isAssignableFrom(classType)) {
			editor = (Editor<T>) new FXItemEditor();
		} else if (Node.class.isAssignableFrom(classType)) {
			editor = (Editor<T>) new NodeEditor();
		} else if (Font.class.equals(classType)) {
			editor = (Editor<T>) new FontEditor();
		} else if (Background.class.equals(classType)) {
			editor = (Editor<T>) new BackgroundEditor();
		} else if (BackgroundFill.class.equals(classType)) {
			editor = (Editor<T>) new BackgroundFillEditor();
		} else if (Insets.class.equals(classType)) {
			editor = (Editor<T>) new InsetsEditor();
		} else if (CornerRadii.class.equals(classType)) {
			editor = (Editor<T>) new CornerRadiiEditor();
		} else if (BlendMode.class.equals(classType)) {
			editor = (Editor<T>) new BlendModeEditor();
		} else if (Paint.class.equals(classType)) {
			editor = (Editor<T>) new PaintEditor();
		} else if (Color.class.equals(classType)) {
			editor = (Editor<T>) new ColorEditor();
		} else if (Point3D.class.equals(classType)) {
			editor = (Editor<T>) new Point3DEditor();
		} else if (Image.class.equals(classType)) {
			editor = (Editor<T>) new ImageSelectionEditor();
		}
//		} else if (LinearGradient.class.equals(type)) {
//			editor =  (Editor<T>) new LinearGradientEditor();
//		} else if (RadialGradient.class.equals(type)) {
//			editor =  (Editor<T>) new RadialGradientEditor();
//		}
		// TODO: Make the rest work with 'assignableFrom' instead of 'equals'
		else if (Application.class.isAssignableFrom(classType)) {
			editor = (Editor<T>) new ApplicationEditor();
		}
		return Optional.ofNullable(editor);
	}
}
