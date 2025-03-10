package com.ezfx.controls.editor.introspective;

import com.ezfx.controls.editor.Category;
import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.EditorBase;
import com.ezfx.controls.editor.PropertiesEditor;
import com.ezfx.controls.editor.factory.EditorFactory;
import com.ezfx.controls.editor.skin.TabPaneCategorizedSkin;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Skin;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClassHierarchyEditor<T> extends PropertiesEditor<T> {

	private final Map<Class<?>, Editor<?>> cache = new HashMap<>();

	public ClassHierarchyEditor() {
		super();

		setCategorizer(value -> getClassesInHierarchy(value.getClass()).stream()
				.collect(Collectors.toMap(
						entry -> Category.of(entry.getSimpleName(), getClassesInHierarchy(entry).size()),
						entry -> {
							ObservableList<Editor<?>> list = FXCollections.observableArrayList();
							Editor<?> editor = getEditor(entry);
							updateSubEditor(entry);
							list.setAll(editor);
							return list;
						},
						(a, b) -> a,
						() -> FXCollections.observableMap(new TreeMap<>())
				)));
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new TabPaneCategorizedSkin<>(this);
	}

	private <S extends T> List<Class<? super S>> getClassesInHierarchy(Class<?> baseClass) {
		//noinspection unchecked
		return _getClassesInHierarchy((Class<S>) baseClass);
	}

	private <S extends T> List<Class<? super S>> _getClassesInHierarchy(Class<S> baseClass) {
		Stream<Class<? super S>> stream = Stream.iterate(
				baseClass.getSuperclass(), Objects::nonNull, Class::getSuperclass);
		return Stream.concat(Stream.of(baseClass), stream).toList();
	}

	@SuppressWarnings("unchecked")
	private <S> Editor<S> getEditor(Class<S> clazz) {
		return (Editor<S>) cache.computeIfAbsent(clazz,
				c -> getEditorFactory().buildEditor(c).orElse(new EditorBase<>()));
	}

	protected <C> Editor<C> buildEditor(Class<C> clazz){
		return editorFactory.getValue().buildEditor(clazz).orElse(new EditorBase<>());
	}

	private <C> void updateSubEditor(Class<C> clazz) {
		Optional.ofNullable(getValue())
				.filter(clazz::isInstance)
				.map(clazz::cast)
				.ifPresent(getEditor(clazz)::setValue);
	}


	private final Property<EditorFactory> editorFactory;

	{
		EditorFactory factory = new EditorFactory() {
			@Override
			public <E> Optional<Editor<E>> buildEditor(Type type) {
				if (type instanceof Class<?> clazz) {
					//noinspection unchecked
					return Optional.of((Editor<E>)new ClassBasedEditor<>(clazz));
				} else {
					return Optional.empty();
				}
			}
		};

		editorFactory = new SimpleObjectProperty<>(this, "editorFactory", factory);
	}

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
