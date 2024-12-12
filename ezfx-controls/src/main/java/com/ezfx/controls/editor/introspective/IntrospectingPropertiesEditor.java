package com.ezfx.controls.editor.introspective;

import com.ezfx.controls.editor.Category;
import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.EditorFactory;
import com.ezfx.controls.editor.PropertiesEditor;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.TreeMap;

import static com.ezfx.controls.editor.EditorFactory.DEFAULT_FACTORY;
import static com.ezfx.controls.editor.introspective.IntrospectorFX.DEFAULT_INTROSPECTOR;

@SuppressWarnings("unchecked")
public class IntrospectingPropertiesEditor<T> extends PropertiesEditor<T> {

	private static final Logger log = LoggerFactory.getLogger(IntrospectingPropertiesEditor.class);

	public IntrospectingPropertiesEditor() {
		this(new SimpleObjectProperty<>());
	}

	public IntrospectingPropertiesEditor(T target) {
		this(new SimpleObjectProperty<>(target));
	}

	public IntrospectingPropertiesEditor(Property<T> property) {
		this(property, DEFAULT_INTROSPECTOR, DEFAULT_FACTORY);
	}

	public IntrospectingPropertiesEditor(
			Property<T> property, Introspector introspector, EditorFactory factory) {
		super(property);
		setIntrospector(introspector);
		setEditorFactory(factory);

		categorizedEditorsProperty().bind(property()
				.map(T::getClass)
				.map(getIntrospector()::getPropertyInfo)
				.map(propertyInfoList -> {
					ObservableMap<Category, ObservableList<Editor<?>>> categorized = FXCollections.observableMap(new TreeMap<>());
					for (PropertyInfo propertyInfo : propertyInfoList) {
						Editor<?> subEditor = buildSubEditor(property.getValue(), propertyInfo);
						if (subEditor == null) continue;
						Category category = propertyInfo.category();
						ObservableList<Editor<?>> list = categorized.computeIfAbsent(category, _ -> FXCollections.observableArrayList());
						list.add(subEditor);
					}
					return categorized;
				}));

		editorsProperty().bind(categorizedEditorsProperty().map(map -> {
			ObservableList<Editor<?>> list = FXCollections.observableArrayList();
			map.values().forEach(list::addAll);
			return list;
		}));
	}

	private <R> Editor<R> buildSubEditor(T target, PropertyInfo propertyInfo) {
		try {
			Method propertyMethod = propertyInfo.property();
			if (!propertyMethod.canAccess(target)) return null;
			Property<R> subProperty = (Property<R>) propertyMethod.invoke(target);
			return getEditorFactory().buildEditor(propertyInfo, subProperty);
		} catch (IllegalAccessException | InvocationTargetException e) {
			log.warn(e.getMessage(), e);
			return getEditorFactory().buildEditor(propertyInfo, new SimpleObjectProperty<>(this, propertyInfo.name(), null));
		}
	}

	private final Property<Introspector> introspector = new SimpleObjectProperty<>(this, "introspector");

	public Property<Introspector> introspectorProperty() {
		return this.introspector;
	}

	public Introspector getIntrospector() {
		return this.introspectorProperty().getValue();
	}

	public void setIntrospector(Introspector value) {
		this.introspectorProperty().setValue(value);
	}

	private final Property<EditorFactory> editorFactory = new SimpleObjectProperty<>(this, "editorFactory");

	public Property<EditorFactory> editorFactoryProperty() {
		return this.editorFactory;
	}

	public EditorFactory getEditorFactory() {
		return this.editorFactoryProperty().getValue();
	}

	public void setEditorFactory(EditorFactory value) {
		this.editorFactoryProperty().setValue(value);
	}
}
