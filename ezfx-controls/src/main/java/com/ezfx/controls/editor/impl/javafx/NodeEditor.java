package com.ezfx.controls.editor.impl.javafx;

import com.ezfx.base.utils.Converter;
import com.ezfx.controls.editor.Category;
import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.PropertiesEditor;
import com.ezfx.controls.editor.impl.standard.DoubleEditor;
import com.ezfx.controls.editor.introspective.IntrospectingPropertiesEditor;
import com.ezfx.controls.editor.introspective.PropertyInfo;
import com.ezfx.controls.editor.skin.MultiEditorSkin;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Node;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.ezfx.base.utils.Converters.NUMBER_TO_DOUBLE;
import static com.ezfx.base.utils.EZFX.toObservableArrayList;
import static com.ezfx.base.utils.Properties.convert;
import static com.ezfx.base.utils.Properties.copyWithName;

public class NodeEditor extends IntrospectingPropertiesEditor<Node> {

	public NodeEditor() {
		this(new SimpleObjectProperty<>());
	}

	public NodeEditor(Node node) {
		this(new SimpleObjectProperty<>(node));
	}

	public NodeEditor(Property<Node> property) {
		super(property);

		categorizedEditorsProperty().bind(valueProperty()
				.map(Node::getClass)
				.map(this::introspectPropertyInfo)
				.map(this::filterPropertyInfo)
				.map(this::categorizePropertyInfo)
				.map(this::convertToEditors)
				.map(this::addAdditionalEditors));

		editorsProperty().bind(categorizedEditorsProperty().map(map -> {
			ObservableList<Editor<?>> list = FXCollections.observableArrayList();
			map.values().forEach(list::addAll);
			return list;
		}));
	}

	private List<PropertyInfo> introspectPropertyInfo(Class<?> aClass) {
		return getIntrospector().getPropertyInfo(aClass);
	}

	private ObservableMap<Category, ObservableList<Editor<?>>> addAdditionalEditors(
			ObservableMap<Category, ObservableList<Editor<?>>> categorizedEditors) {

		for (Category category : categorizedEditors.keySet()) {
			ObservableList<Editor<?>> list = categorizedEditors.get(category);
			for (Category c : additionalEditors.keySet()) {
				if (c.title().equals(category.title())) {
					list.addAll(0, additionalEditors.getOrDefault(c, List.of()).stream().map(f -> f.apply(valueProperty())).toList());
				}
			}
		}

		return categorizedEditors;
	}

	private ObservableMap<Category, ObservableList<Editor<?>>> convertToEditors(
			ObservableMap<Category, ObservableList<PropertyInfo>> categorizedProperties) {
		return categorizedProperties.entrySet().stream()
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						entry -> entry.getValue()
								.stream()
								.map(info -> buildSubEditor(getValue(), info))
								.collect(toObservableArrayList()),
						accumulating(),
						toObservableLinkedHashMap()));
	}

	private ObservableMap<Category, ObservableList<PropertyInfo>> categorizePropertyInfo(List<PropertyInfo> filtered) {
		return filtered.stream()
				.collect(Collectors.groupingBy(
						PropertyInfo::category,
						toObservableTreeMap(),
						toObservableArrayList()));
	}

	private List<PropertyInfo> filterPropertyInfo(List<PropertyInfo> infoList) {
		return infoList.stream().filter(info -> filters.stream()
				.map(filter1 -> filter1.test(info))
				.reduce(true, (a, b) -> a && b)).toList();
	}

	private static <T> BinaryOperator<ObservableList<T>> accumulating() {
		return (a, b) -> {
			a.addAll(b);
			return a;
		};
	}

	private static <T, R> Supplier<ObservableMap<T, ObservableList<R>>> toObservableLinkedHashMap() {
		return () -> FXCollections.observableMap(new LinkedHashMap<>());
	}

	private static <T, R> Supplier<ObservableMap<T, ObservableList<R>>> toObservableTreeMap() {
		//noinspection SortedCollectionWithNonComparableKeys
		return () -> FXCollections.observableMap(new TreeMap<>());
	}

	private List<String> nameFilters = List.of(
			"rotate",
			"layoutx", "layouty",
			"translatex", "translatey", "translatez",
			"scalex", "scaley", "scalez"
	);
	private List<Predicate<PropertyInfo>> filters = List.of(
			info -> !nameFilters.contains(info.name()));

	private Map<Category, List<Function<Property<Node>, Editor<?>>>> additionalEditors = Map.of(
			Category.of("Node"), List.of(
					property -> combinedEditor(property, "Layout", List.of(
							Node::layoutXProperty,
							Node::layoutYProperty)),
					property -> combinedEditor(property, "Translation", List.of(
							Node::translateXProperty,
							Node::translateYProperty,
							Node::translateZProperty)),
					property -> combinedEditor(property, "Scale", List.of(
							Node::scaleXProperty,
							Node::scaleYProperty,
							Node::scaleZProperty)),
					RotationEditor::new
			));

	private static PropertiesEditor<Node> combinedEditor(
			Property<Node> property, String name, List<Function<Node, Property<Number>>> accessors) {
		PropertiesEditor<Node> editor = new PropertiesEditor<>(copyWithName(property, name));
		editor.setSkin(new MultiEditorSkin.HorizontalEditorSkin<>(editor));
		Converter<Double, Number> converter = NUMBER_TO_DOUBLE.inverted();
		Node node = property.getValue();

		editor.getEditors().addAll(accessors.stream()
				.map(accessor -> convert(accessor.apply(node), converter))
				.map(DoubleEditor::new)
				.toList());

		return editor;
	}
}
