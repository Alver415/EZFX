package com.ezfx.controls.editor.introspective;

import com.ezfx.controls.editor.Category;
import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.PropertiesEditor;
import com.ezfx.controls.editor.factory.EditorFactory;
import com.ezfx.controls.editor.skin.TabPaneCategorizedSkin;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.control.Skin;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ezfx.base.utils.EZFX.mergeFunction;
import static com.ezfx.base.utils.EZFX.observableTreeMapSupplier;

public class ClassHierarchyEditor<T> extends PropertiesEditor<T> {

	private final Map<Class<?>, PropertiesEditor<?>> cache = new ConcurrentHashMap<>();

	public ClassHierarchyEditor() {
		super();

		setCategorizer(value -> {
			List<Class<?>> classesInHierarchy = getClassesInHierarchy(value.getClass());
			Function<Class<?>, Category> keyFunction = this::getCategory;
			Function<Class<?>, PropertiesEditor<T>> valueFunction = this::getCategoryEditor;
			ObservableMap<Category, PropertiesEditor<T>> map = FXCollections.observableMap(new TreeMap<>());
			for (Class<?> clazz : classesInHierarchy){
				Category key = keyFunction.apply(clazz);
				PropertiesEditor<T> val = valueFunction.apply(clazz);
				val.setValue(value);
				if (val.getEditors().isEmpty()){
					continue;
				}
				map.put(key, val);
			}
			return map;
		});
	}

	private Category getCategory(Class<?> entry) {
		return Category.of(entry.getSimpleName(), getClassesInHierarchy(entry).size());
	}

	@SuppressWarnings("unchecked")
	private <C extends T> PropertiesEditor<C> getCategoryEditor(Class<?> clazz) {
		return (PropertiesEditor<C>) cache.computeIfAbsent(clazz, _ -> buildCategoryEditor((Class<C>) clazz));
	}

	protected <C extends T> PropertiesEditor<C> buildCategoryEditor(Class<C> clazz) {
		return new ClassPropertiesEditor<>(clazz);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new TabPaneCategorizedSkin<>(this);
	}

	private List<Class<?>> getClassesInHierarchy(Class<?> baseClass) {
		Stream<Class<?>> stream = Stream.iterate(
				baseClass.getSuperclass(), Objects::nonNull, Class::getSuperclass);
		return Stream.concat(Stream.of(baseClass), stream).toList();
	}
}
