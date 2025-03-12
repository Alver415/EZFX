package com.ezfx.controls.editor.introspective;

import com.ezfx.controls.editor.*;
import com.ezfx.controls.editor.factory.EditorFactory;
import com.ezfx.controls.editor.skin.TabPaneCategorizedSkin;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.control.Skin;
import javafx.util.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.ezfx.base.utils.ComplexBinding.bindBidirectional;
import static com.ezfx.controls.editor.factory.IntrospectingEditorFactory.DEFAULT_FACTORY;
import static com.ezfx.controls.editor.introspective.EZFXIntrospector.DEFAULT_INTROSPECTOR;

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

		categorizedEditorsProperty().bind(valueProperty().map(value -> {
			subscription.unsubscribe();
			subscription = Subscription.EMPTY;
			Class<?> type = value.getClass();
			List<PropertyInfo> propertyInfoList = getIntrospector().getPropertyInfo(type);
			ObservableMap<Category, PropertiesEditor<T>> categorized = FXCollections.observableMap(new TreeMap<>());
			for (PropertyInfo propertyInfo : propertyInfoList) {
				Editor<?> subEditor = getEditor(propertyInfo);
				Category category = propertyInfo.category();
				PropertiesEditor<?> list = categorized.computeIfAbsent(category, _ -> new PropertiesEditor<>());
				list.getEditors().add(subEditor);
			}
			return categorized;
		}));

		editorsProperty().bind(categorizedEditorsProperty().map(map -> {
			ObservableList<Editor<?>> list = FXCollections.observableArrayList();
			map.values().forEach(list::addAll);
			return list;
		}));
	}

	private Subscription subscription = Subscription.EMPTY;

	private <R> Property<R> getProperty(PropertyInfo propertyInfo) {
		Property<R> property;
		try {
			property = (Property<R>) propertyInfo.property().invoke(getValue());
		} catch (IllegalAccessException | InvocationTargetException e) {
			log.warn("Failed to access: %s".formatted(propertyInfo), e);
			property = new SimpleObjectProperty<>(this, propertyInfo.name(), null);
		}
		return property;
	}


	private final Map<PropertyInfo, Editor<?>> editorMap = new HashMap<>();

	private <R> Editor<R> getEditor(PropertyInfo propertyInfo) {
		return (Editor<R>) editorMap.computeIfAbsent(propertyInfo, this::buildEditor);
	}

	private <R> Editor<R> buildEditor(PropertyInfo propertyInfo) {
		Editor<R> editor;
		Type type = propertyInfo.getter().getGenericReturnType();
		if (type instanceof ParameterizedType parameterizedType) {
			Type rawType = parameterizedType.getRawType();
			Type genericType = parameterizedType.getActualTypeArguments()[0];
			if (rawType instanceof Class clazz && genericType instanceof Class genericClazz && List.class.isAssignableFrom(clazz)) {
				ListEditor<R> listEditor = new ListEditor<>();
				listEditor.setGenericType(genericClazz);
				editor = (EditorBase<R>) listEditor;
			} else {
				editor = (Editor<R>) getEditorFactory().buildEditor(rawType).orElseGet(EditorBase::new);
			}
		} else if (type instanceof Class clazz) {
			editor = (Editor<R>) getEditorFactory().buildEditor((Class<R>) clazz).orElseGet(EditorBase::new);
		} else {
			editor = new EditorBase<>();
		}
		editor.setTitle(propertyInfo.displayName());
		return editor;
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

	@Override
	protected Skin<?> createDefaultSkin() {
		return new TabPaneCategorizedSkin<>(this);
	}
}
