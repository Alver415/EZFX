package com.ezfx.controls.editor.impl.javafx;

import com.ezfx.controls.editor.Category;
import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.PropertiesEditor;
import com.ezfx.controls.editor.factory.EditorFactory;
import com.ezfx.controls.editor.introspective.IntrospectingPropertiesEditor;
import com.ezfx.controls.editor.introspective.PropertyInfo;
import com.ezfx.controls.editor.skin.MultiEditorSkin;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ezfx.base.utils.Properties.copyWithName;

public class NodeEditor extends IntrospectingPropertiesEditor<Node> {

	private static final Logger log = LoggerFactory.getLogger(NodeEditor.class);

	public NodeEditor() {
		this(new SimpleObjectProperty<>());
	}

	public NodeEditor(Node node) {
		this(new SimpleObjectProperty<>(node));
	}

	public NodeEditor(Property<Node> property) {
		super(property);
	}
	protected void init(){
		categorizedEditorsProperty().bind(valueProperty()
				.map(n -> Optional.ofNullable(n)
						.map(Node::getClass)
						.map(this::introspectPropertyInfo)
						.map(this::filterPropertyInfo)
						.map(this::categorizePropertyInfo)
						.map(this::convertToEditors)
						.map(this::addAdditionalEditors)
						.map(v -> {
							ObservableMap<Category, ObservableList<Editor<?>>> map = FXCollections.observableMap(new LinkedHashMap<>());
							v.forEach((key, value) -> map.put(key, FXCollections.observableArrayList(value)));
							return map;
						}).orElse(FXCollections.emptyObservableMap())
				));

		editorsProperty().bind(categorizedEditorsProperty().map(map -> {
			ObservableList<Editor<?>> list = FXCollections.observableArrayList();
			map.values().forEach(list::addAll);
			return list;
		}));
	}

	private List<PropertyInfo> introspectPropertyInfo(Class<?> aClass) {
		log.info("1 introspectPropertyInfo");
		return getIntrospector().getPropertyInfo(aClass);
	}

	private List<PropertyInfo> filterPropertyInfo(List<PropertyInfo> infoList) {
		log.info("2 filterPropertyInfo");
		return infoList.stream().filter(info -> filters.stream()
				.map(filter1 -> filter1.test(info))
				.reduce(true, (a, b) -> a && b)).toList();
	}

	private Map<Category, List<PropertyInfo>> categorizePropertyInfo(List<PropertyInfo> filtered) {
		log.info("3 categorizePropertyInfo");
		return filtered.stream()
				.collect(Collectors.groupingBy(
						PropertyInfo::category,
						TreeMap::new,
						Collectors.toList()));
	}

	private Map<Category, List<Editor<?>>> convertToEditors(
			Map<Category, List<PropertyInfo>> categorizedProperties) {
		log.info("4 convertToEditors");
		Stream<Map.Entry<Category, List<PropertyInfo>>> stream = categorizedProperties.entrySet().stream();
		Function<Map.Entry<Category, List<PropertyInfo>>, Category> keyMapper = entry -> entry.getKey();
		Function<Map.Entry<Category, List<PropertyInfo>>, List<Editor<?>>> valueMapper = entry -> {
			List<PropertyInfo> value = entry.getValue();
			List<Editor<?>> list = new ArrayList<>();
			value.forEach(info -> list.add(buildSubEditor(getValue(), info)));
			return list;
		};
		return stream.collect(Collectors.toMap(
				keyMapper,
				valueMapper,
				(a, b) -> {
					a.addAll(b);
					return a;
				},
				LinkedHashMap::new));
	}

	private Map<Category, List<Editor<?>>> addAdditionalEditors(
			Map<Category, List<Editor<?>>> categorizedEditors) {
		log.info("5 addAdditionalEditors");

		for (Category category : categorizedEditors.keySet()) {
			List<Editor<?>> list = categorizedEditors.get(category);
			for (Category c : additionalEditors.keySet()) {
				if (c.title().equals(category.title())) {
					list.addAll(0, additionalEditors.getOrDefault(c, List.of()).stream().map(f -> f.apply(valueProperty())).toList());
				}
			}
		}

		return categorizedEditors;
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

	private static List<String> nameFilters = List.of(
			"rotate", "rotationaxis",
			"layoutx", "layouty",
			"translatex", "translatey", "translatez",
			"scalex", "scaley", "scalez"
	);
	private static List<Predicate<PropertyInfo>> filters = List.of(
			info -> !nameFilters.contains(info.name()));

	private static Map<Category, List<Function<Property<Node>, Editor<?>>>> additionalEditors = Map.of(
			Category.of("Node"), List.of(
					property -> combinedNumberEditors(property, "Layout", List.of(
							Node::layoutXProperty,
							Node::layoutYProperty)),
					property -> combinedNumberEditors(property, "Translation", List.of(
							Node::translateXProperty,
							Node::translateYProperty,
							Node::translateZProperty)),
					property -> combinedNumberEditors(property, "Scale", List.of(
							Node::scaleXProperty,
							Node::scaleYProperty,
							Node::scaleZProperty)),
					property -> combinedNumberEditors(property, "Rotation", Orientation.VERTICAL, List.of(
							Node::rotateProperty,
							Node::rotationAxisProperty))
			));

	private static PropertiesEditor<Node> combinedNumberEditors(
			Property<Node> property, String name, List<Function<Node, Property<?>>> accessors) {
		return combinedNumberEditors(property, name, Orientation.HORIZONTAL, accessors);
	}

	private static PropertiesEditor<Node> combinedNumberEditors(
			Property<Node> property, String name, Orientation orientation, List<Function<Node, Property<?>>> accessors) {
		PropertiesEditor<Node> editor = new PropertiesEditor<>(copyWithName(property, name));
		editor.setSkin(orientation == Orientation.HORIZONTAL ?
				new MultiEditorSkin.HorizontalEditorSkin<>(editor) :
				new MultiEditorSkin.VerticalEditorSkin<>(editor));
		Node node = property.getValue();

		editor.getEditors().addAll(accessors.stream()
				.map(accessor -> EditorFactory.DEFAULT_FACTORY.buildEditor(accessor.apply(node)))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.toList());

		return editor;
	}
}
