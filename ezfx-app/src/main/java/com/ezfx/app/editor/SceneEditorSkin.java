package com.ezfx.app.editor;

import com.ezfx.base.utils.ScreenBounds;
import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.impl.javafx.NodeEditor;
import com.ezfx.controls.editor.impl.standard.StringEditor;
import com.ezfx.controls.misc.FilterableTreeItem;
import com.ezfx.controls.nodetree.NodeTreeCell;
import com.ezfx.controls.nodetree.NodeTreeItem;
import com.ezfx.controls.nodetree.NodeTreeView;
import com.ezfx.controls.viewport.Viewport;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.reactfx.EventStreams;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.ezfx.base.utils.EZFX.runFX;

public class SceneEditorSkin extends SkinBase<SceneEditor> {

	private static final BoundingBox DEFAULT_BOUNDING_BOX = new BoundingBox(0, 0, 0, 0);
	private final Map<Node, Editor<?>> cache = new ConcurrentHashMap<>(new LinkedHashMap<>(5, 0.75f, true));

	private final NodeTreeView treeView;
	private final Viewport viewport;
	private final Canvas overlay;
	private final StackPane editorWrapper;
//	private final MonadicBinding<Bounds> highlightedRegion;

	protected BorderPane borderPane = new BorderPane();

	protected SceneEditorSkin(SceneEditor control) {
		super(control);
		treeView = new NodeTreeView();
		viewport = new Viewport();
		overlay = new Canvas();
		overlay.setMouseTransparent(true);
		overlay.setOpacity(0.5);

		getChildren().setAll(borderPane);

		borderPane.setCenter(new StackPane(viewport, overlay));

		StringEditor filterEditor = new StringEditor();
		filterEditor.setPadding(new Insets(4));
		filterEditor.setPromptText("Filter...");
		Property<String> filterTextProperty = filterEditor.valueProperty();
		treeView.rootProperty().subscribe(root -> {
			if (root instanceof FilterableTreeItem<Node> filterableRoot) {
				filterableRoot.predicateProperty().bind(filterTextProperty.map(filterText -> node -> filter(filterText, node)));
			}
		});

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

		ObservableValue<Node> selectedItem = treeView.getSelectionModel().selectedItemProperty().map(TreeItem::getValue);
		Property<Node> hoveredItem = new SimpleObjectProperty<>();
		treeView.setCellFactory(_ -> new NodeTreeCell() {
			{
				setOnMouseEntered(_ -> {
					if (isEmpty() || getItem() == null) return;
					hoveredItem.setValue(getItem());
				});
				setOnMouseExited(_ -> {
					if (hoveredItem.getValue() == getItem()) {
						hoveredItem.setValue(null);
					}
				});
			}
		});

		overlay.widthProperty().bind(viewport.widthProperty());
		overlay.heightProperty().bind(viewport.heightProperty());

		selectedItem.flatMap(ScreenBounds.CACHED::of)
				.subscribe(screenBounds -> {
					if (screenBounds == null) return;
					Bounds local = overlay.screenToLocal(screenBounds);
					GraphicsContext gc = overlay.getGraphicsContext2D();
					gc.setFill(Color.GRAY.interpolate(Color.TRANSPARENT, 0.5));
					gc.clearRect(0, 0, 10000, 10000);
					gc.fillRect(0, 0, 10000, 10000);
					gc.clearRect(local.getMinX(), local.getMinY(), local.getWidth(), local.getHeight());
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

	private void updateOverlay(Bounds bounds) {
		GraphicsContext gc = overlay.getGraphicsContext2D();
		gc.setFill(Color.GRAY);
		gc.fillRect(0, 0, overlay.getWidth(), overlay.getHeight());
		gc.clearRect(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
	}

	private static boolean filter(String filterText, Node node) {
		if (filterText == null || filterText.isEmpty()) return true;
		filterText = filterText.toLowerCase();
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
	}

	private Editor<?> getEditorByTreeItem(TreeItem<Node> treeItem) {
		Node node = Optional.ofNullable(treeItem).map(TreeItem::getValue).orElse(null);
		if (node == null) return null;
		return cache.computeIfAbsent(node, _ -> {
			NodeEditor editor = new NodeEditor(node);
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
}
