package com.ezfx.app.stage;

import com.ezfx.base.utils.Resources;
import com.ezfx.controls.editor.introspective.ActionIntrospector;
import com.ezfx.controls.icons.Icons;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.logging.log4j.util.Strings;
import org.controlsfx.control.SegmentedButton;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionProxy;
import org.controlsfx.control.action.ActionUtils;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class StageDecorationSkin extends SkinBase<StageDecoration> {

	private static final ObservableList<Image> EMPTY_LIST = FXCollections.emptyObservableList();

	private final StackPane boundary;
	private final BorderPane borderPane;
	private final ContextMenu contextMenu;

	private final BorderPane titleBar;
	private final HBox titleHeader;
	private final ImageView iconView;
	private final Text titleText;
	private final Text separator;
	private final Text descriptionText;
	private final ButtonBar actionButtons;

	private final SubScene subScene;

	private final DoubleBinding horizontalPadding;
	private final DoubleBinding verticalPadding;

	public StageDecorationSkin(StageDecoration control) {
		super(control);
		stage.bind(control.sceneProperty().flatMap(Scene::windowProperty).map(window -> (Stage) window));

		borderPane = new BorderPane();
		borderPane.getStyleClass().add("stage");

		boundary = new StackPane(borderPane);
		boundary.getStyleClass().add("boundary");

		ActionIntrospector.register(this);
		Action minimizeAction = ActionIntrospector.action("minimize");
		Action maximizeAction = ActionIntrospector.action("maximize");
		Action restoreAction = ActionIntrospector.action("restore");
		Action closeAction = ActionIntrospector.action("close");
		Collection<Action> actions = List.of(minimizeAction, maximizeAction, restoreAction, closeAction);

		Function<Boolean, Boolean> invert = b -> !b;
		restoreAction.disabledProperty().bind(stageProperty().flatMap(Stage::maximizedProperty).map(invert));
		maximizeAction.disabledProperty().bind(stageProperty().flatMap(Stage::maximizedProperty));
		minimizeAction.disabledProperty().bind(stageProperty().flatMap(Stage::iconifiedProperty));

		contextMenu = ActionUtils.createContextMenu(actions);
//		actionButtons = ActionUtils.createSegmentedButton(ActionUtils.ActionTextBehavior.HIDE, actions);
		actionButtons = createActionButtons(actions);
		actionButtons.getStyleClass().add("button-bar");

		titleBar = new BorderPane();
		titleBar.getStyleClass().add("title-bar");

		iconView = new ImageView();
		iconView.setPickOnBounds(true);
		ObjectBinding<Image> firstIcon = Bindings.createObjectBinding(
				() -> stageProperty().map(Stage::getIcons).orElse(EMPTY_LIST).getValue()
						.stream().findFirst().orElse(null), stageProperty());
		iconView.imageProperty().bind(firstIcon);
		iconView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> contextMenu.show(iconView, event.getScreenX(), event.getScreenY()));

		titleText = new Text();
		titleText.getStyleClass().add("title");

		descriptionText = new Text();
		descriptionText.getStyleClass().add("description");
		titleText.textProperty().bind(stageProperty().flatMap(Stage::titleProperty));
		descriptionText.textProperty().bind(this.descriptionProperty());

		separator = new Text();
		separator.textProperty().bind(Bindings.createStringBinding(() ->
						!Strings.isEmpty(titleText.getText()) && !Strings.isEmpty(descriptionText.getText()) ? " - " : "",
				titleText.textProperty(), descriptionText.textProperty()));

		titleHeader = new HBox(iconView, titleText, separator, descriptionText);
		titleHeader.getStyleClass().add("title-header");
		titleBar.setLeft(titleHeader);
		titleBar.setRight(actionButtons);
		borderPane.setTop(titleBar);

		horizontalPadding = Bindings.createDoubleBinding(() -> boundary.getPadding().getLeft() + boundary.getPadding().getRight(), boundary.paddingProperty());
		verticalPadding = Bindings.createDoubleBinding(() -> boundary.getPadding().getTop() + boundary.getPadding().getBottom(), boundary.paddingProperty());

		stageProperty().subscribe(stage -> {
			stage.minWidthProperty().bind(
					actionButtons.widthProperty()
							.add(titleHeader.widthProperty())
							.add(horizontalPadding));
			stage.minHeightProperty().bind(
					titleBar.heightProperty()
							.add(verticalPadding));
		});

		subScene = new SubScene(new Pane(), 600, 400);
		subScene.getStyleClass().add("scene");
		subScene.fillProperty().bind(control.sceneFillProperty());
		subScene.userAgentStylesheetProperty().bind(control.sceneProperty().flatMap(Scene::userAgentStylesheetProperty));
		ObservableValue<Number> stageWidth = stageProperty().flatMap(Stage::widthProperty);
		ObservableValue<Number> stageHeight = stageProperty().flatMap(Stage::heightProperty);
		subScene.widthProperty().bind(Bindings.createDoubleBinding(
				() -> -horizontalPadding.get() + stageWidth.getValue().doubleValue(),
				horizontalPadding, stageWidth));
		subScene.heightProperty().bind(Bindings.createDoubleBinding(
				() -> -verticalPadding.get() - titleHeader.heightProperty().get() + stageHeight.getValue().doubleValue(),
				verticalPadding, titleHeader.heightProperty(), stageHeight));
		borderPane.setCenter(subScene);

		subScene.rootProperty().bind(control.rootProperty());
		getChildren().setAll(boundary);
	}

	private ButtonBar createActionButtons(Collection<Action> actions) {
		ButtonBar buttonBar = new ButtonBar();
		buttonBar.getStyleClass().add("button-bar");
		List<Button> buttons = actions.stream().map(action -> {
			Button button = new Button();
			button.getStyleClass().add("action");
			button.graphicProperty().bind(action.graphicProperty());
			button.setOnAction(action);
			return button;
		}).toList();
		buttonBar.getButtons().setAll(buttons);
		return buttonBar;
	}

	private DragListener dragListener;
	private ResizeListener resizeListener;

	@Override
	public void install() {
		super.install();

		dragListener = new DragListener(titleBar);

		titleBar.addEventHandler(MouseEvent.MOUSE_PRESSED, dragListener);
		titleBar.addEventHandler(MouseEvent.MOUSE_RELEASED, dragListener);
		titleBar.addEventHandler(MouseEvent.MOUSE_DRAGGED, dragListener);
		titleBar.addEventHandler(MouseEvent.MOUSE_EXITED, dragListener);

		resizeListener = new ResizeListener();
		boundary.addEventHandler(MouseEvent.MOUSE_MOVED, resizeListener);
		boundary.addEventHandler(MouseEvent.MOUSE_PRESSED, resizeListener);
		boundary.addEventHandler(MouseEvent.MOUSE_DRAGGED, resizeListener);
		boundary.addEventHandler(MouseEvent.MOUSE_EXITED, resizeListener);
		boundary.addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, resizeListener);
	}

	@Override
	public void dispose() {
		super.dispose();

		titleBar.removeEventFilter(MouseEvent.MOUSE_PRESSED, dragListener);
		titleBar.removeEventFilter(MouseEvent.MOUSE_RELEASED, dragListener);
		titleBar.removeEventFilter(MouseEvent.MOUSE_DRAGGED, dragListener);
		titleBar.removeEventFilter(MouseEvent.MOUSE_EXITED, dragListener);

		boundary.removeEventFilter(MouseEvent.MOUSE_MOVED, resizeListener);
		boundary.removeEventFilter(MouseEvent.MOUSE_PRESSED, resizeListener);
		boundary.removeEventFilter(MouseEvent.MOUSE_DRAGGED, resizeListener);
		boundary.removeEventFilter(MouseEvent.MOUSE_EXITED, resizeListener);
		boundary.removeEventFilter(MouseEvent.MOUSE_EXITED_TARGET, resizeListener);
	}

	@ActionProxy(id="restore", text="Restore", longText = "Return the window to its previous size and position.", graphic = "font>FontAwesome|SQUARE_ALT")
	public void restore() {
		getStage().setMaximized(false);
	}

	@ActionProxy(id="minimize", text="Minimize", longText = "Minimize the window to the taskbar.", graphic = "font>FontAwesome|MINUS")
	public void minimize() {
		getStage().setIconified(true);
	}

	@ActionProxy(id="maximize", text="Maximize", longText = "Expand the window to fill the screen.", graphic = "font>FontAwesome|SQUARE")
	public void maximize() {
		getStage().setMaximized(true);
		getStage().centerOnScreen();
	}

	@ActionProxy(id="close", text="Close", longText = "Close the window.", graphic = "font>FontAwesome|CLOSE")
	public void close() {
		getStage().close();
	}


	private class DragListener implements EventHandler<MouseEvent> {

		private final Node node;

		private double deltaX;
		private double deltaY;

		private DragListener(Node node) {
			this.node = node;
		}

		@Override
		public void handle(MouseEvent event) {
			EventType<? extends MouseEvent> eventType = event.getEventType();
			if (MouseEvent.MOUSE_PRESSED.equals(eventType)) {
				if (event.getTarget() != node) return;
				if (event.getClickCount() == 2) {
					getStage().setMaximized(!getStage().isMaximized());
				}
				deltaX = getStage().getX() - event.getScreenX();
				deltaY = getStage().getY() - event.getScreenY();
			} else if (MouseEvent.MOUSE_RELEASED.equals(eventType)) {
				if (event.getTarget() != node) return;
				node.setCursor(Cursor.DEFAULT);
			} else if (MouseEvent.MOUSE_DRAGGED.equals(eventType)) {
				if (event.getTarget() != node) return;
				node.setCursor(Cursor.MOVE);
				getStage().setX(event.getScreenX() + deltaX);
				getStage().setY(event.getScreenY() + deltaY);
			} else if (MouseEvent.MOUSE_EXITED.equals(eventType)) {
				if (event.getTarget() != node) return;
				if (!event.isPrimaryButtonDown()) {
					node.setCursor(Cursor.DEFAULT);
				}
			}
		}
	}

	private class ResizeListener implements EventHandler<MouseEvent> {
		private Cursor cursor = Cursor.DEFAULT;
		private double startX = 0;
		private double startY = 0;

		@Override
		public void handle(MouseEvent event) {
			EventType<? extends MouseEvent> eventType = event.getEventType();
			Scene scene = getNode().getScene();
			Stage stage = getStage();

			double eventX = event.getSceneX(),
					eventY = event.getSceneY(),
					sceneWidth = scene.getWidth(),
					sceneHeight = scene.getHeight();

			int borderWidth = 8;
			if (MouseEvent.MOUSE_MOVED.equals(eventType)) {

				EventTarget target = event.getTarget();
				if (target != boundary) {
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
				startX = stage.getWidth() - eventX;
				startY = stage.getHeight() - eventY;
			} else if (MouseEvent.MOUSE_DRAGGED.equals(eventType)) {
				if (!Cursor.DEFAULT.equals(cursor)) {
					if (!Cursor.W_RESIZE.equals(cursor) && !Cursor.E_RESIZE.equals(cursor)) {
						double minHeight = stage.getMinHeight() > (borderWidth * 2) ? stage.getMinHeight() : (borderWidth * 2);
						if (Cursor.NW_RESIZE.equals(cursor) || Cursor.N_RESIZE.equals(cursor) || Cursor.NE_RESIZE.equals(cursor)) {
							if (stage.getHeight() > minHeight || eventY < 0) {
								stage.setHeight(stage.getY() - event.getScreenY() + stage.getHeight());
								stage.setY(event.getScreenY());
							}
						} else {
							if (stage.getHeight() > minHeight || eventY + startY - stage.getHeight() > 0) {
								stage.setHeight(eventY + startY);
							}
						}
					}

					if (!Cursor.N_RESIZE.equals(cursor) && !Cursor.S_RESIZE.equals(cursor)) {
						double minWidth = stage.getMinWidth() > (borderWidth * 2) ? stage.getMinWidth() : (borderWidth * 2);
						if (Cursor.NW_RESIZE.equals(cursor) || Cursor.W_RESIZE.equals(cursor) || Cursor.SW_RESIZE.equals(cursor)) {
							if (stage.getWidth() > minWidth || eventX < 0) {
								stage.setWidth(stage.getX() - event.getScreenX() + stage.getWidth());
								stage.setX(event.getScreenX());
							}
						} else {
							if (stage.getWidth() > minWidth || eventX + startX - stage.getWidth() > 0) {
								stage.setWidth(eventX + startX);
							}
						}
					}
				}
			}
		}
	}

	private final ReadOnlyObjectWrapper<Stage> stage = new ReadOnlyObjectWrapper<>(this, "stage");

	public ReadOnlyObjectProperty<Stage> stageProperty() {
		return stage.getReadOnlyProperty();
	}

	public Stage getStage() {
		return stage.getValue();
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
