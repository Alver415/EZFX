package com.ezfx.app.editor;

import com.ezfx.base.utils.ScreenBounds;
import com.ezfx.controls.editor.EditorBase;
import com.ezfx.controls.editor.impl.javafx.NodeEditor;
import com.ezfx.controls.tree.SceneGraphTreeControl;
import com.ezfx.controls.viewport.Viewport;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SkinBase;
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

	private final Map<Node, EditorBase<?>> cache = new ConcurrentHashMap<>(new LinkedHashMap<>(5, 0.75f, true));

	private final SceneGraphTreeControl treeControl;
	private final Viewport viewport;
	private final Canvas overlay;
	private final StackPane editorWrapper;
	private final ObservableValue<Node> target;

	protected BorderPane borderPane = new BorderPane();

	protected SceneEditorSkin(SceneEditor control) {
		super(control);
		treeControl = new SceneGraphTreeControl();
		viewport = new Viewport();
		overlay = new Canvas();
		overlay.setMouseTransparent(true);
		overlay.setOpacity(0.5);

		getChildren().setAll(borderPane);

		borderPane.setCenter(new StackPane(viewport, overlay));

		VBox left = new VBox(treeControl);
		VBox.setVgrow(treeControl, Priority.ALWAYS);
		left.setPrefWidth(450);
		borderPane.setLeft(left);

		editorWrapper = new StackPane();
		ScrollPane right = new ScrollPane(editorWrapper);
		right.setFitToWidth(true);
		right.setFitToHeight(true);
		right.setPrefWidth(450);
		borderPane.setRight(right);

		target = Bindings.createObjectBinding(
				() -> treeControl.getHoveredItem() != null ? treeControl.getHoveredItem() : treeControl.getSelectedItem(),
				treeControl.selectedItemProperty(), treeControl.hoveredItemProperty());

		overlay.widthProperty().bind(viewport.widthProperty());
		overlay.heightProperty().bind(viewport.heightProperty());

		target.flatMap(ScreenBounds.CACHED::of)
				.subscribe(screenBounds -> {
					if (screenBounds == null) return;
					Bounds local = overlay.screenToLocal(screenBounds);
					GraphicsContext gc = overlay.getGraphicsContext2D();
					gc.setFill(Color.GRAY.interpolate(Color.TRANSPARENT, 0.5));
					gc.clearRect(0, 0, 10000, 10000);
					gc.fillRect(0, 0, 10000, 10000);
					gc.clearRect(local.getMinX(), local.getMinY(), local.getWidth(), local.getHeight());
				});


		treeControl.rootProperty().bind(control.targetProperty());
		viewport.contentProperty().bind(control.targetProperty().map(t -> t instanceof Parent p ? p : new StackPane(t)).orElse(new StackPane()));

		ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
		EventStreams.valuesOf(treeControl.selectedItemProperty())
				.threadBridgeFromFx(executor)
				.map(this::getEditorByTreeItem)
				.threadBridgeToFx(executor)
				.feedTo(editorProperty());

		editorProperty().subscribe((oldValue, newValue) -> {
			Optional.ofNullable(oldValue).ifPresent(ov -> ov.setVisible(false));
			Optional.ofNullable(newValue).ifPresent(nv -> nv.setVisible(true));
		});
	}


	private EditorBase<?> getEditorByTreeItem(Node node) {
		if (node == null) return null;
		return cache.computeIfAbsent(node, _ -> {
			NodeEditor editor = new NodeEditor(node);
			editor.setVisible(false);
			runFX(() -> editorWrapper.getChildren().add(editor));
			return editor;
		});
	}

	private final Property<EditorBase<?>> editor = new SimpleObjectProperty<>(this, "editor");

	public Property<EditorBase<?>> editorProperty() {
		return this.editor;
	}

	public EditorBase<?> getEditor() {
		return this.editorProperty().getValue();
	}

	public void setEditor(EditorBase<?> value) {
		this.editorProperty().setValue(value);
	}
}
