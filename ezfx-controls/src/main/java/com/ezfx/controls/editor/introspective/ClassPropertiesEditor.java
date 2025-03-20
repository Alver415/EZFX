package com.ezfx.controls.editor.introspective;

import com.ezfx.base.introspector.EZFXIntrospector;
import com.ezfx.base.introspector.PropertyInfo;
import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.EditorBase;
import com.ezfx.controls.editor.ListEditor;
import com.ezfx.controls.editor.PropertiesEditor;
import com.ezfx.controls.editor.factory.EditorFactory;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import static com.ezfx.base.utils.ComplexBinding.bindBidirectional;

public class ClassPropertiesEditor<T> extends PropertiesEditor<T> {

	private static final Logger log = LoggerFactory.getLogger(ClassPropertiesEditor.class);

	private final Map<PropertyInfo, Editor<?>> subEditorCache = new HashMap<>();
	private Subscription bindingSubscriptions = Subscription.EMPTY;

	public ClassPropertiesEditor(Class<T> clazz) {
		//Build all subEditors for this specific subclass (not super classes)
		List<Editor<Object>> subEditors = EZFXIntrospector.DEFAULT_INTROSPECTOR
				.getDeclaredPropertyInfo(clazz)
				.stream()
				.map(this::getEditor)
				.toList();
		getEditors().setAll(subEditors);

		valueProperty().subscribe(_ -> {
			bindingSubscriptions.unsubscribe();
			bindingSubscriptions = Subscription.EMPTY;
			subEditorCache.forEach(this::handleBinding);
		});
	}

	private <A> void handleBinding(PropertyInfo propertyInfo, Editor<A> editor) {
		T value = getValue();
		if (value == null) return;
		Property<A> valueProperty = getProperty(propertyInfo, value);
		Property<A> editorProperty = editor.valueProperty();
		if (valueProperty.isBound()){
			editorProperty.bind(valueProperty);
			editor.getNode().setDisable(true);
			bindingSubscriptions = bindingSubscriptions.and(editorProperty::unbind);
			bindingSubscriptions = bindingSubscriptions.and(() -> editor.getNode().setDisable(false));
		} else {
			bindingSubscriptions = bindingSubscriptions.and(bindBidirectional(editorProperty, valueProperty));
		}
	}

	private <R> Property<R> getProperty(PropertyInfo propertyInfo, Object value) {
		try {
			if (propertyInfo.property().canAccess(value)) {
				//noinspection unchecked
				return (Property<R>) propertyInfo.property().invoke(value);
			} else {
				log.debug("Cannot access: %s".formatted(propertyInfo));
				return new SimpleObjectProperty<>(this, propertyInfo.name(), null);
			}
		} catch (IllegalAccessException | InvocationTargetException e) {
			log.warn("Failed to access: %s".formatted(propertyInfo), e);
			return new SimpleObjectProperty<>(this, propertyInfo.name(), null);
		}
	}

	private <A> Editor<A> getEditor(PropertyInfo propertyInfo) {
		//noinspection unchecked
		return (Editor<A>) subEditorCache.computeIfAbsent(propertyInfo, _ -> {
			Editor<A> editor;
			Type type = propertyInfo.getter().getGenericReturnType();
			if (type instanceof ParameterizedType parameterizedType) {
				Type rawType = parameterizedType.getRawType();
				Type genericType = parameterizedType.getActualTypeArguments()[0];
				if (rawType instanceof Class clazz && genericType instanceof Class genericClazz && List.class.isAssignableFrom(clazz)) {
					ListEditor<A> listEditor = new ListEditor<>();
					listEditor.setGenericType(genericClazz);
					editor = (Editor<A>) listEditor;
				} else {
					editor = (Editor<A>) EditorFactory.DEFAULT_FACTORY.buildEditor(rawType).orElseGet(EditorBase::new);
				}
			} else if (type instanceof Class clazz) {
				editor = EditorFactory.DEFAULT_FACTORY.buildEditor((Class<A>) clazz).orElseGet(EditorBase::new);
			} else {
				editor = new EditorBase<>();
			}
			editor.setTitle(propertyInfo.displayName());
			return editor;
		});
	}
}
