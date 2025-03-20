package com.ezfx.controls.editor;

import com.ezfx.base.introspector.Category;
import com.ezfx.controls.editor.skin.MultiEditorSkin;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.control.Skin;

import java.util.TreeMap;
import java.util.function.Function;

import static com.ezfx.base.utils.EZFX.toObservableArrayList;

/**
 * Base class for editors which use multiple sub-editors to edit properties of an object.
 * <p>
 * For example, a ColorEditor might have 3 sub-editors, one for each of Red, Green, Blue.
 */
public class PropertiesEditor<T> extends ObjectEditor<T> implements CategorizedMultiEditor<T, PropertiesEditor<T>> {

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
					categorized.values().stream().collect(toObservableArrayList())));
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

	private final MapProperty<Category, PropertiesEditor<T>> categorizedEditors = new SimpleMapProperty<>(
			this, "categorizedEditors", FXCollections.observableMap(new TreeMap<>()));

	@Override
	public MapProperty<Category, PropertiesEditor<T>> categorizedEditorsProperty() {
		return categorizedEditors;
	}

	private final Property<Function<T, ObservableMap<Category, PropertiesEditor<T>>>> categorizer = new SimpleObjectProperty<>(this, "categorizer");

	public Property<Function<T, ObservableMap<Category, PropertiesEditor<T>>>> categorizerProperty() {
		return this.categorizer;
	}

	public Function<T, ObservableMap<Category, PropertiesEditor<T>>> getCategorizer() {
		return this.categorizerProperty().getValue();
	}

	public void setCategorizer(Function<T, ObservableMap<Category, PropertiesEditor<T>>> value) {
		this.categorizerProperty().setValue(value);
	}
}
