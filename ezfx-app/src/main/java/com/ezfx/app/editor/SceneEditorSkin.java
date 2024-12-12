package com.ezfx.app.editor;

import com.ezfx.controls.editor.impl.standard.StringEditor;
import com.ezfx.controls.misc.FilterableTreeItem;
import com.ezfx.controls.nodetree.NodeTreeCell;
import com.ezfx.controls.nodetree.NodeTreeView;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public class SceneEditorSkin extends SkinBase<SceneEditor> {

	protected BorderPane borderPane = new BorderPane();
	protected Pane overlay = new Pane();

	protected SceneEditorSkin(SceneEditor control) {
		super(control);
		getChildren().setAll(borderPane);
		overlay.setPickOnBounds(false);

		MenuBar menuBar = new MenuBar();
		Menu fileMenu = new Menu("File");
		MenuItem open = new MenuItem("Open...");
		open.setOnAction(a -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setInitialDirectory(Path.of(System.getProperty("user.dir")).toFile());
			File file = fileChooser.showOpenDialog(control.getScene().getWindow());
			if (file != null) {
				try {
					FXMLLoader loader = new FXMLLoader();
					loader.setLocation(file.toURI().toURL());
					Node load = loader.load();
					control.setTarget(load);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});
		fileMenu.getItems().setAll(open);
		menuBar.getMenus().setAll(fileMenu);
		borderPane.setTop(menuBar);

		StackPane center = new StackPane(control.getViewport(), overlay);
		borderPane.setCenter(center);

		StringEditor filterEditor = new StringEditor();
		filterEditor.setPadding(new Insets(4));
		filterEditor.setPromptText("Filter...");
		Property<String> filterProperty = filterEditor.property();
		NodeTreeView treeView = control.getTreeView();
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

		ScrollPane right = new ScrollPane();
		control.editorProperty().subscribe(right::setContent);
		right.setFitToWidth(true);
		right.setFitToHeight(true);
		right.setPrefWidth(450);
		borderPane.setRight(right);

		setup();
	}

	private void setup() {
		SceneEditor sceneEditor = getSkinnable();
		NodeTreeView treeView = sceneEditor.getTreeView();
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
	}

	private class Shadow extends Region {

		private Shadow(Node node, Color color) {
			setPickOnBounds(false);
			SceneEditor sceneEditor = getSkinnable();
			Region region = getParentRegion(node);
			ObjectBinding<Bounds> bounds = Bindings.createObjectBinding(() ->
							region.boundsInLocalProperty()
									.map(region::localToScreen)
									.map(overlay::screenToLocal).getValue(),
					region.boundsInLocalProperty(),
					region.boundsInParentProperty(),
					sceneEditor.getViewport().contentScaleProperty(),
					sceneEditor.getViewport().contentPositionXProperty(),
					sceneEditor.getViewport().contentPositionYProperty());

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
