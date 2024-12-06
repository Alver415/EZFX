package com.ezfx.app.stage;

import com.ezfx.base.utils.Resources;
import com.ezfx.controls.icons.Icons;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.function.Function;

public class DecoratedStage extends Stage {
	private static final Image ICON_CLOSE = Resources.image(Icons.class, "close.png");
	private static final Image ICON_RESTORE = Resources.image(Icons.class, "restore.png");
	private static final Image ICON_MINIMIZE = Resources.image(Icons.class, "minimize.png");
	private static final Image ICON_MAXIMIZE = Resources.image(Icons.class, "maximize.png");

	private final BorderPane stage;
	private final StackPane border;

	public DecoratedStage() {
		this(StageStyle.TRANSPARENT);
	}

	public DecoratedStage(StageStyle stageStyle) {
		super(stageStyle);


		Function<Boolean, Boolean> invert = b -> !b;
		ContextMenu contextMenu = new ContextMenu();

		MenuItem restoreItem = buildMenuItem("Restore", ICON_RESTORE, this::restore);
		MenuItem minimizeItem = buildMenuItem("Minimize", ICON_MINIMIZE, this::minimize);
		MenuItem maximizeItem = buildMenuItem("Maximize", ICON_MAXIMIZE, this::maximize);
		MenuItem closeItem = buildMenuItem("Close", ICON_CLOSE, this::close);

		restoreItem.disableProperty().bind(maximizedProperty().map(invert));
		minimizeItem.disableProperty().bind(iconifiedProperty());
		maximizeItem.disableProperty().bind(maximizedProperty());

		contextMenu.getItems().setAll(restoreItem, minimizeItem, maximizeItem, new SeparatorMenuItem(), closeItem);

		stage = new BorderPane();
		stage.getStyleClass().add("stage");
		BorderPane titleBar = new BorderPane();
		titleBar.getStyleClass().add("stage-title-bar");
		makeDraggable(titleBar);

		ImageView iconView = new ImageView();
		ObjectBinding<Image> firstIcon = Bindings.createObjectBinding(() -> getIcons().stream().findFirst().orElse(null), getIcons());
		iconView.imageProperty().bind(firstIcon);
		iconView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			contextMenu.show(iconView, event.getScreenX(), event.getScreenY());
		});


		Label title = new Label();
		title.getStyleClass().add("title");
		title.textProperty().bind(titleProperty());

		Label description = new Label();
		description.getStyleClass().add("description");
		description.textProperty().bind(descriptionProperty());

		HBox header = new HBox(iconView, title, new Label("-"), description);
		titleBar.setLeft(header);
		HBox buttons = new HBox();

		Button closeButton = buildButton("Close", ICON_CLOSE, this::close);
		Button restoreButton = buildButton("Restore", ICON_RESTORE, this::restore);
		Button maximizeButton = buildButton("Maximize", ICON_MAXIMIZE, this::maximize);
		Button minimizeButton = buildButton("Minimize", ICON_MINIMIZE, this::minimize);

		restoreButton.visibleProperty().bind(maximizedProperty());
		restoreButton.managedProperty().bind(maximizedProperty());
		maximizeButton.visibleProperty().bind(maximizedProperty().map(invert));
		maximizeButton.managedProperty().bind(maximizedProperty().map(invert));

		buttons.getChildren().setAll(minimizeButton, restoreButton, maximizeButton, closeButton);
		titleBar.setRight(buttons);
		stage.setTop(titleBar);

		border = new StackPane(stage);
		border.setAlignment(Pos.TOP_CENTER);
		border.getStylesheets().add(Resources.css(DecoratedStage.class, "DecoratedStage.css"));
		border.getStyleClass().add("stage-border");

		sceneProperty().addListener((_, _, scene) -> {
			stage.setCenter(scene.getRoot());
			scene.setRoot(border);
			scene.setFill(Color.TRANSPARENT);
			setScene(scene);
			addResizeListener();
		});

		DoubleBinding horizontalPadding = Bindings.createDoubleBinding(() -> border.getPadding().getLeft() + border.getPadding().getRight(), border.paddingProperty());
		DoubleBinding verticalPadding = Bindings.createDoubleBinding(() -> border.getPadding().getTop() + border.getPadding().getBottom(), border.paddingProperty());
		minWidthProperty().bind(buttons.widthProperty()
				.add(header.widthProperty())
				.add(horizontalPadding));
		minHeightProperty().bind(titleBar.heightProperty().add(verticalPadding));
	}

	private MenuItem buildMenuItem(String text, Image icon, Runnable action) {
		MenuItem restore = new MenuItem(text, new ImageView(icon));
		restore.setOnAction(_ -> action.run());
		return restore;
	}

	private Button buildButton(String description, Image icon, Runnable action) {
		Button button = new Button("", new ImageView(icon));
		button.getStyleClass().add("stage-button");
		button.setTooltip(new Tooltip(description));
		button.setOnAction(_ -> action.run());
		button.setFocusTraversable(false);
		return button;
	}

	private void restore() {
		setMaximized(false);
	}

	private void minimize() {
		setIconified(true);
	}

	private void maximize() {
		setMaximized(true);
	}

	public void makeDraggable(final Node node) {
		final Delta dragDelta = new Delta();
		node.setOnMousePressed(event -> {
			if (event.getTarget() != node) return;
			if (event.getClickCount() == 2) {
				setMaximized(!isMaximized());
			}
			dragDelta.x = this.getX() - event.getScreenX();
			dragDelta.y = this.getY() - event.getScreenY();
		});
		node.setOnMouseReleased(event -> {
			if (event.getTarget() != node) return;
			node.setCursor(Cursor.DEFAULT);

		});
		node.setOnMouseDragged(event -> {
			if (event.getTarget() != node) return;
			node.setCursor(Cursor.MOVE);
			this.setX(event.getScreenX() + dragDelta.x);
			this.setY(event.getScreenY() + dragDelta.y);
		});
		node.setOnMouseExited(event -> {
			if (event.getTarget() != node) return;
			if (!event.isPrimaryButtonDown()) {
				node.setCursor(Cursor.DEFAULT);
			}
		});
	}

	/**
	 * records relative x and y co-ordinates.
	 */
	private static class Delta {
		double x, y;
	}

	public void addResizeListener() {
		ResizeListener resizeListener = new ResizeListener();
		getScene().addEventHandler(MouseEvent.MOUSE_MOVED, resizeListener);
		getScene().addEventHandler(MouseEvent.MOUSE_PRESSED, resizeListener);
		getScene().addEventHandler(MouseEvent.MOUSE_DRAGGED, resizeListener);
		getScene().addEventHandler(MouseEvent.MOUSE_EXITED, resizeListener);
		getScene().addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, resizeListener);
	}

	class ResizeListener implements EventHandler<MouseEvent> {
		private Cursor cursor = Cursor.DEFAULT;
		private int borderWidth = 8;
		private double startX = 0;
		private double startY = 0;

		@Override
		public void handle(MouseEvent event) {
			EventType<? extends MouseEvent> eventType = event.getEventType();
			Scene scene = getScene();

			double eventX = event.getSceneX(),
					eventY = event.getSceneY(),
					sceneWidth = scene.getWidth(),
					sceneHeight = scene.getHeight();

			if (MouseEvent.MOUSE_MOVED.equals(eventType)) {

				EventTarget target = event.getTarget();
				if (target != border){
					cursor = Cursor.DEFAULT;
				}
				if (eventX < borderWidth && eventY < borderWidth) {
					cursor = Cursor.NW_RESIZE;
				} else if (eventX < borderWidth && eventY > sceneHeight - borderWidth) {
					cursor = Cursor.SW_RESIZE;
				} else if (eventX > sceneWidth - borderWidth && eventY < borderWidth) {
					cursor = Cursor.NE_RESIZE;
				} else if (eventX > sceneWidth - borderWidth && eventY > sceneHeight - borderWidth) {
					cursor = Cursor.SE_RESIZE;
				} else if (eventX < borderWidth) {
					cursor = Cursor.W_RESIZE;
				} else if (eventX > sceneWidth - borderWidth) {
					cursor = Cursor.E_RESIZE;
				} else if (eventY < borderWidth) {
					cursor = Cursor.N_RESIZE;
				} else if (eventY > sceneHeight - borderWidth) {
					cursor = Cursor.S_RESIZE;
				} else {
					cursor = Cursor.DEFAULT;
				}
				scene.setCursor(cursor);
			} else if (MouseEvent.MOUSE_EXITED.equals(eventType) || MouseEvent.MOUSE_EXITED_TARGET.equals(eventType)) {
				scene.setCursor(Cursor.DEFAULT);
			} else if (MouseEvent.MOUSE_PRESSED.equals(eventType)) {
				startX = getWidth() - eventX;
				startY = getHeight() - eventY;
			} else if (MouseEvent.MOUSE_DRAGGED.equals(eventType)) {
				if (!Cursor.DEFAULT.equals(cursor)) {
					if (!Cursor.W_RESIZE.equals(cursor) && !Cursor.E_RESIZE.equals(cursor)) {
						double minHeight = getMinHeight() > (borderWidth * 2) ? getMinHeight() : (borderWidth * 2);
						if (Cursor.NW_RESIZE.equals(cursor) || Cursor.N_RESIZE.equals(cursor) || Cursor.NE_RESIZE.equals(cursor)) {
							if (getHeight() > minHeight || eventY < 0) {
								setHeight(getY() - event.getScreenY() + getHeight());
								setY(event.getScreenY());
							}
						} else {
							if (getHeight() > minHeight || eventY + startY - getHeight() > 0) {
								setHeight(eventY + startY);
							}
						}
					}

					if (!Cursor.N_RESIZE.equals(cursor) && !Cursor.S_RESIZE.equals(cursor)) {
						double minWidth = getMinWidth() > (borderWidth * 2) ? getMinWidth() : (borderWidth * 2);
						if (Cursor.NW_RESIZE.equals(cursor) || Cursor.W_RESIZE.equals(cursor) || Cursor.SW_RESIZE.equals(cursor)) {
							if (getWidth() > minWidth || eventX < 0) {
								setWidth(getX() - event.getScreenX() + getWidth());
								setX(event.getScreenX());
							}
						} else {
							if (getWidth() > minWidth || eventX + startX - getWidth() > 0) {
								setWidth(eventX + startX);
							}
						}
					}
				}
			}
		}
	}

	private final StringProperty description = new SimpleStringProperty(this, "description");

	public StringProperty descriptionProperty() {
		return this.description;
	}

	public String getDescription() {
		return this.descriptionProperty().getValue();
	}

	public void setDescription(String value) {
		this.descriptionProperty().setValue(value);
	}
}
