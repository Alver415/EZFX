package com.ezfx.app.editor;

import com.ezfx.base.utils.CachedProxy;
import com.ezfx.base.utils.ScreenBounds;
import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.EditorSkinBase;
import com.ezfx.controls.editor.FXItemEditor;
import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.item.FXItem;
import com.ezfx.controls.item.FXItemFactory;
import com.ezfx.controls.item.FXItemFactoryImpl;
import com.ezfx.controls.item.FXItemTreeControl;
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
import javafx.scene.control.Skin;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import static com.ezfx.base.utils.ComplexBinding.bindBidirectional;
import static com.ezfx.base.utils.EZFX.runFX;

public class SceneGraphEditor extends ObjectEditor<Node> {

	public SceneGraphEditor() {
		super();
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new DefaultSkin(this);
	}

	private final Property<FXItem<?, ?>> target = new SimpleObjectProperty<>(this, "target");

	public Property<FXItem<?, ?>> targetProperty() {
		return this.target;
	}

	public FXItem<?, ?> getTarget() {
		return this.targetProperty().getValue();
	}

	public void setTarget(FXItem<?, ?> value) {
		this.targetProperty().setValue(value);
	}

	public static class DefaultSkin extends EditorSkinBase<SceneGraphEditor, Node> {

		private final FXItemTreeControl treeControl;
		private final Viewport viewport;
		private final Canvas overlay;
		private final StackPane editorWrapper;
		private final Editor<FXItem<?, ?>> targetEditor;
		private final ObservableValue<FXItem<?, ?>> target;

		protected BorderPane borderPane = new BorderPane();

		protected DefaultSkin(SceneGraphEditor control) {
			super(control);
			treeControl = new FXItemTreeControl();
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

			targetEditor = new FXItemEditor();
			bindBidirectional(targetEditor.valueProperty(), control.targetProperty());
			editorWrapper = new StackPane(targetEditor.getNode());
			ScrollPane right = new ScrollPane(editorWrapper);
			right.setFitToWidth(true);
			right.setFitToHeight(true);
			right.setPrefWidth(450);
			borderPane.setRight(right);

			target = Bindings.createObjectBinding(
					() -> treeControl.getHoveredItem() != null ?
							treeControl.getHoveredItem() :
							treeControl.getSelectedItem(),
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


			treeControl.rootProperty().bind(control.valueProperty().map(FXItemFactory.CACHED::create));
			viewport.contentProperty().bind(control.valueProperty()
					.map(t -> t instanceof Parent p ? p : new StackPane(t)).orElse(new StackPane()));

			treeControl.selectedItemProperty().subscribe(targetEditor::setValue);
		}
	}
}