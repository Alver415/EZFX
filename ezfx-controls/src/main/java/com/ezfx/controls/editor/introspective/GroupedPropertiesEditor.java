package com.ezfx.controls.editor.introspective;

import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.PropertiesEditor;
import javafx.beans.property.Property;
import javafx.util.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ezfx.base.utils.ComplexBinding.bindBidirectional;

public abstract class GroupedPropertiesEditor<T, K> extends PropertiesEditor<T> {

	private final Map<K, Editor<?>> subEditorCache = new HashMap<>();
	private Subscription bindingSubscriptions = Subscription.EMPTY;

	public GroupedPropertiesEditor(List<K> keys) {
		//Build all subEditors for this specific subclass (not super classes)
		List<Editor<Object>> subEditors = keys
				.stream()
				.map(this::getEditor)
				.toList();
		getEditors().setAll(subEditors);

		valueProperty().subscribe(_ -> {
			bindingSubscriptions.unsubscribe();
			bindingSubscriptions = Subscription.EMPTY;
			subEditorCache.forEach(this::bindSubEditor);
		});
	}

	private <A> void bindSubEditor(K propertyInfo, Editor<A> editor) {
		T value = getValue();
		if (value == null) return;
		Property<A> valueProperty = getProperty(propertyInfo, value);
		Property<A> editorProperty = editor.valueProperty();
		bindingSubscriptions = bindingSubscriptions.and(bindBidirectional(editorProperty, valueProperty));
	}

	protected abstract Editor<?> initializeEditor(K k);

	protected abstract <R> Property<R> getProperty(K key, T value);

	private <A> Editor<A> getEditor(K propertyInfo) {
		//noinspection unchecked
		return (Editor<A>) subEditorCache.computeIfAbsent(propertyInfo, this::initializeEditor);
	}

}
