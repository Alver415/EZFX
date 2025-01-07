package com.ezfx.app.editor;

import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.impl.standard.StringEditor;
import com.ezfx.controls.editor.introspective.IntrospectingPropertiesEditor;
import com.ezfx.controls.misc.FilterableTreeItem;
import com.ezfx.controls.nodetree.NodeTreeCell;
import com.ezfx.controls.nodetree.NodeTreeItem;
import com.ezfx.controls.nodetree.NodeTreeView;
import com.ezfx.controls.viewport.Viewport;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.reactfx.EventStreams;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.ezfx.base.utils.EZFX.*;

public class SceneEditorSkin extends SkinBase<SceneEditor> {

	private final Map<Node, Editor<?>> cache = new ConcurrentHashMap<>();

	private final NodeTreeView treeView = new NodeTreeView();
	private final Viewport viewport = new Viewport();
	private final StackPane editorWrapper;

	protected BorderPane borderPane = new BorderPane();
	protected Pane overlay = new Pane();

	protected SceneEditorSkin(SceneEditor control) {
		super(control);
		getChildren().setAll(borderPane);
		overlay.setPickOnBounds(false);

		StackPane center = new StackPane(viewport, overlay);
		borderPane.setCenter(center);

		StringEditor filterEditor = new StringEditor();
		filterEditor.setPadding(new Insets(4));
		filterEditor.setPromptText("Filter...");
		Property<String> filterProperty = filterEditor.valueProperty();
		if (treeView.getRoot() instanceof FilterableTreeItem<Node> root) {
			root.predicateProperty().bind(Bindings.createObjectBinding(() -> node -> {
				if (filterProperty.getValue() == null) return true;
				String filterText = filterProperty.getValue().toLowerCase();

				if (filterText.startsWith("#")) {
					if (node.getId() != null && node.getId().toLowerCase().contains(filterText.substring(1))) {
						return true;
					}
				} else if (filterText.startsWith(".")) {
					for (String styleClass : node.getStyleClass()) {
						if (styleClass.toLowerCase().contains(filterText.substring(1))) {
							return true;
						}
					}
				} else {
					if (node.getClass().getSimpleName().toLowerCase().contains(filterText)) {
						return true;
					}
				}
				return false;
			}, filterProperty));
		}
		VBox left = new VBox(filterEditor, treeView);
		VBox.setVgrow(treeView, Priority.ALWAYS);
		left.setPrefWidth(450);
		borderPane.setLeft(left);

		editorWrapper = new StackPane();
		ScrollPane right = new ScrollPane(editorWrapper);
		right.setFitToWidth(true);
		right.setFitToHeight(true);
		right.setPrefWidth(450);
		borderPane.setRight(right);

		ObservableValue<Node> selected = treeView.getSelectionModel().selectedItemProperty().map(TreeItem::getValue);
		Property<Node> hovered = new SimpleObjectProperty<>();
		treeView.setCellFactory(_ -> new NodeTreeCell() {
			{
				setOnMouseEntered(a -> {
					if (isEmpty() || getItem() == null) return;
					hovered.setValue(getItem());
				});
				setOnMouseExited(a -> {
					if (hovered.getValue() == getItem()) {
						hovered.setValue(null);
					}
				});
			}
		});

		hovered.map(n -> new Shadow(n, Color.LIGHTBLUE.interpolate(Color.TRANSPARENT, 0.5))).subscribe((oldValue, newValue) -> {
			Optional.ofNullable(oldValue).map(overlay.getChildren()::remove);
			Optional.ofNullable(newValue).map(overlay.getChildren()::add);
		});
		selected.map(n -> new Shadow(n, Color.BLUEVIOLET.interpolate(Color.TRANSPARENT, 0.5))).subscribe((oldValue, newValue) -> {
			Optional.ofNullable(oldValue).map(overlay.getChildren()::remove);
			Optional.ofNullable(newValue).map(overlay.getChildren()::add);
		});
		treeView.rootProperty().bind(control.targetProperty().map(NodeTreeItem::new));
		viewport.contentProperty().bind(control.targetProperty().map(t -> t instanceof Parent p ? p : new StackPane(t)).orElse(new StackPane()));

		ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
		EventStreams.valuesOf(treeView.getSelectionModel().selectedItemProperty())
				.threadBridgeFromFx(executor)
				.map(this::getEditorByTreeItem)
				.threadBridgeToFx(executor)
				.feedTo(editorProperty());

		editorProperty().subscribe((oldValue, newValue) -> {
			Optional.ofNullable(oldValue).ifPresent(ov -> ov.setVisible(false));
			Optional.ofNullable(newValue).ifPresent(nv -> nv.setVisible(true));
		});

		treeView.rootProperty().subscribe(treeView.getSelectionModel()::select);
	}

	private Editor<?> getEditorByTreeItem(TreeItem<Node> treeItem) {
		Node node = Optional.ofNullable(treeItem).map(TreeItem::getValue).orElse(null);
		if (node == null) return null;
		return cache.computeIfAbsent(node, _ -> {
			IntrospectingPropertiesEditor<Node> editor = new IntrospectingPropertiesEditor<>(node);
			editor.setVisible(false);
			runFX(() -> editorWrapper.getChildren().add(editor));
			return editor;
		});
	}

	private final Property<Editor<?>> editor = new SimpleObjectProperty<>(this, "editor");

	public Property<Editor<?>> editorProperty() {
		return this.editor;
	}

	public Editor<?> getEditor() {
		return this.editorProperty().getValue();
	}

	public void setEditor(Editor<?> value) {
		this.editorProperty().setValue(value);
	}

	private class Shadow extends Region {

		private Shadow(Node node, Color color) {
			setPickOnBounds(false);
			Region region = getParentRegion(node);
			ObjectBinding<Bounds> bounds = Bindings.createObjectBinding(() ->
							region.boundsInLocalProperty()
									.map(region::localToScreen)
									.map(overlay::screenToLocal).getValue(),
					region.boundsInLocalProperty(),
					region.boundsInParentProperty(),
					viewport.contentScaleProperty(),
					viewport.contentPositionXProperty(),
					viewport.contentPositionYProperty());

			translateXProperty().bind(bounds.map(Bounds::getMinX));
			translateYProperty().bind(bounds.map(Bounds::getMinY));
			prefWidthProperty().bind(bounds.map(Bounds::getWidth));
			prefHeightProperty().bind(bounds.map(Bounds::getHeight));

			setBackground(Background.fill(Color.TRANSPARENT));
			setBorder(buildBorder(color));
			setBlendMode(BlendMode.DIFFERENCE);
		}

		//TODO: Cleanup
		private static Border buildBorder(Color color) {
			color = color.invert();

			BorderStrokeStyle solid = BorderStrokeStyle.SOLID;
			CornerRadii radii = CornerRadii.EMPTY;
			BorderWidths widths = new BorderWidths(2);
			Insets insets = new Insets(-2);
			BorderStroke stroke = new BorderStroke(
					color, color, color, color,
					solid, solid, solid, solid,
					radii,
					widths,
					insets);

			Color inverse = color.brighter().interpolate(Color.TRANSPARENT, 0.5);
			CornerRadii radii2 = new CornerRadii(4);
			BorderWidths widths2 = new BorderWidths(2);
			Insets insets2 = new Insets(-4);
			BorderStroke stroke2 = new BorderStroke(
					inverse, inverse, inverse, inverse,
					solid, solid, solid, solid,
					radii2,
					widths2,
					insets2);

			return new Border(stroke2, stroke);
		}

		//TODO: This should be observable so as the scene graph changes, we stay updated
		public Region getParentRegion(Node node) {
			while (node != null) {
				if (node instanceof Region region) {
					return region;
				}
				node = node.getParent();
			}
			return null;
		}
	}
}
