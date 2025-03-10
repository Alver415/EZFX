package com.ezfx.controls.editor;

import com.ezfx.controls.editor.skin.MultiEditorSkin;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.control.Skin;

import java.util.Collection;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Base class for editors which use multiple sub-editors to edit properties of an object.
 * <p>
 * For example, a ColorEditor might have 3 sub-editors, one for each of Red, Green, Blue.
 */
public class PropertiesEditor<T> extends ObjectEditor<T> implements CategorizedMultiEditor<T> {

	public PropertiesEditor() {
		this(new SimpleObjectProperty<>());
	}

	public PropertiesEditor(T target) {
		this(new SimpleObjectProperty<>(target));
	}

	public PropertiesEditor(Property<T> property) {
		super(property);

		categorizerProperty().subscribe(categorizer -> {
			if (categorizer == null) {
				categorizedEditorsProperty().unbind();
				editorsProperty().unbind();
				return;
			}
			categorizedEditorsProperty().bind(valueProperty().map(categorizer));
			editorsProperty().bind(categorizedEditorsProperty().map(categorized ->
					categorized.values().stream()
							.flatMap(Collection::stream)
							.collect(Collectors.toCollection(FXCollections::observableArrayList))));
		});
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new MultiEditorSkin.VerticalEditorSkin<>(this);
	}

	private final ListProperty<Editor<?>> editors = new SimpleListProperty<>(
			this, "editors", FXCollections.observableArrayList());

	public ListProperty<Editor<?>> editorsProperty() {
		return this.editors;
	}

	private final MapProperty<Category, ObservableList<Editor<?>>> categorizedEditors = new SimpleMapProperty<>(
			this, "categorizedEditors", FXCollections.observableMap(new TreeMap<>()));

	@Override
	public MapProperty<Category, ObservableList<Editor<?>>> categorizedEditorsProperty() {
		return categorizedEditors;
	}

	private final Property<Function<T, ObservableMap<Category, ObservableList<Editor<?>>>>> categorizer = new SimpleObjectProperty<>(this, "categorizer");

	public Property<Function<T, ObservableMap<Category, ObservableList<Editor<?>>>>> categorizerProperty() {
		return this.categorizer;
	}

	public Function<T, ObservableMap<Category, ObservableList<Editor<?>>>> getCategorizer() {
		return this.categorizerProperty().getValue();
	}

	public void setCategorizer(Function<T, ObservableMap<Category, ObservableList<Editor<?>>>> value) {
		this.categorizerProperty().setValue(value);
	}
}
