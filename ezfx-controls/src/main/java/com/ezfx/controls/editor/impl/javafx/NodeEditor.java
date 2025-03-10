package com.ezfx.controls.editor.impl.javafx;

import com.ezfx.base.utils.Converter;
import com.ezfx.base.utils.Converters;
import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.Editors;
import com.ezfx.controls.editor.factory.EditorFactory;
import com.ezfx.controls.editor.impl.standard.DoubleEditor;
import com.ezfx.controls.editor.introspective.ClassBasedEditor;
import com.ezfx.controls.editor.introspective.ClassHierarchyEditor;
import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.layout.Region;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static com.ezfx.base.utils.ComplexBinding.bindBidirectional;
import static com.ezfx.base.utils.Properties.convert;
import static com.ezfx.controls.editor.Editors.key;

public class NodeEditor extends ClassHierarchyEditor<Node> {

	public NodeEditor() {
		setEditorFactory(new EditorFactory() {
			@SuppressWarnings("unchecked")
			@Override
			public <T> Optional<Editor<T>> buildEditor(Type type) {
				if (type.equals(Node.class)) {
					return Optional.of((Editor<T>) new NodeClassEditor());
				} else if (type.equals(Region.class)) {
					return Optional.of((Editor<T>) new RegionClassEditor());
				} else if (type instanceof Class<?> clazz) {
					return Optional.of((Editor<T>) new ClassBasedEditor<>(clazz));
				} else {
					return Optional.empty();
				}
			}
		});
	}

	private static final Converter<Double, Number> DOUBLE_TO_NUMBER = Converters.NUMBER_TO_DOUBLE.inverted();

	public static class NodeClassEditor extends ClassBasedEditor<Node> {

		private final Predicate<Editor<?>> filter = editor ->
				editor.getTitle().startsWith("on") ||
						editor.getTitle().startsWith("accessible") ||
						List.of("layoutx", "layouty", "rotate", "rotationaxis",
										"translatex", "translatey", "translatez",
										"scalex", "scaley", "scalez")
								.contains(editor.getTitle().toLowerCase());

		public NodeClassEditor() {
			super(Node.class);
			Editor<Node> layoutEditor = Editors.group(valueProperty(), "Layout", List.of(
					key(new DoubleEditor("x"), node -> convert(node.layoutXProperty(), DOUBLE_TO_NUMBER)),
					key(new DoubleEditor("y"), node -> convert(node.layoutYProperty(), DOUBLE_TO_NUMBER))));

			Editor<Node> translateEditor1 = Editors.group(valueProperty(), "Translate", List.of(
					key(new DoubleEditor("x"), node -> convert(node.translateXProperty(), DOUBLE_TO_NUMBER)),
					key(new DoubleEditor("y"), node -> convert(node.translateYProperty(), DOUBLE_TO_NUMBER)),
					key(new DoubleEditor("z"), node -> convert(node.translateZProperty(), DOUBLE_TO_NUMBER))));

			Editor<Node> scaleEditor = Editors.group(valueProperty(), "Scale", List.of(
					key(new DoubleEditor("x"), node -> convert(node.scaleXProperty(), DOUBLE_TO_NUMBER)),
					key(new DoubleEditor("y"), node -> convert(node.scaleYProperty(), DOUBLE_TO_NUMBER)),
					key(new DoubleEditor("z"), node -> convert(node.scaleZProperty(), DOUBLE_TO_NUMBER))));

			Editor<Node> rotationEditor = Editors.group(valueProperty(), "Rotation", Orientation.VERTICAL, List.of(
					key(new DoubleEditor("angle"), node -> convert(node.rotateProperty(), DOUBLE_TO_NUMBER)),
					key(new Point3DEditor("axis"), Node::rotationAxisProperty)));

			ObservableList<Editor<?>> editors = getEditors();
			editors.removeIf(filter);

			editors.addFirst(rotationEditor);
			editors.addFirst(scaleEditor);
			editors.addFirst(translateEditor1);
			editors.addFirst(layoutEditor);

			Property<Node> translateEditor = translateEditor1.valueProperty();
			bindBidirectional(translateEditor, valueProperty());
		}
	}

	public static class RegionClassEditor extends ClassBasedEditor<Region> {

		private final Predicate<Editor<?>> filter = editor ->
				List.of("prefwidth", "minwidth", "maxwidth", "prefheight", "minheight", "maxheight")
						.contains(editor.getTitle().toLowerCase());

		public RegionClassEditor() {
			super(Region.class);

			Editor<Region> widthEditor = Editors.group(valueProperty(), "Width", List.of(
					key(new DoubleEditor("Pref"), region -> convert(region.prefWidthProperty(), DOUBLE_TO_NUMBER)),
					key(new DoubleEditor("Min"), region -> convert(region.minWidthProperty(), DOUBLE_TO_NUMBER)),
					key(new DoubleEditor("Max"), region -> convert(region.maxWidthProperty(), DOUBLE_TO_NUMBER))));

			Editor<Region> heightEditor = Editors.group(valueProperty(), "Height", List.of(
					key(new DoubleEditor("Pref"), region -> convert(region.prefHeightProperty(), DOUBLE_TO_NUMBER)),
					key(new DoubleEditor("Min"), region -> convert(region.minHeightProperty(), DOUBLE_TO_NUMBER)),
					key(new DoubleEditor("Max"), region -> convert(region.maxHeightProperty(), DOUBLE_TO_NUMBER))));

			ObservableList<Editor<?>> editors = getEditors();
			editors.removeIf(filter);
			editors.addFirst(heightEditor);
			editors.addFirst(widthEditor);
		}
	}
}
