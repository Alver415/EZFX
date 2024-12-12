package com.ezfx.controls.editor;

import com.ezfx.controls.editor.skin.TabPaneCategorizedSkin;
import com.ezfx.controls.editor.skin.TitledPaneCategorizedSkin;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Skin;

import java.util.TreeMap;

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
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new TabPaneCategorizedSkin<>(this);
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
}
