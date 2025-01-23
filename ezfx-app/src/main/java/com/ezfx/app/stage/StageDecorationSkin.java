package com.ezfx.app.stage;

import com.ezfx.controls.editor.introspective.ActionIntrospector;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.Rectangle2D;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.SkinBase;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionProxy;
import org.controlsfx.control.action.ActionUtils;

import java.util.List;
import java.util.function.Function;

import static com.ezfx.base.utils.Screens.getScreen;
import static com.ezfx.controls.icons.SVGs.*;

public class StageDecorationSkin<T extends StageDecoration> extends SkinBase<T> {

	protected final StackPane window;
	protected final BorderPane stagePane;
	protected final ContextMenu contextMenu;

	protected final BorderPane titleBar;
	protected final HBox titleHeader;
	protected final ImageView iconView;
	protected final Text titleText;

	protected final HBox buttonBar;
	protected final Button settingsButton;
	protected final Button minimizeButton;
	protected final Button maximizeButton;
	protected final Group centerButton;
	protected final Button restoreButton;
	protected final Button closeButton;

	protected final SubScene subScene;

	private final DoubleBinding horizontalPadding;
	private final DoubleBinding verticalPadding;


	public StageDecorationSkin(T control) {
		super(control);
		stage.bind(control.sceneProperty().flatMap(Scene::windowProperty).map(window -> (Stage) window));

		window = new StackPane();
		window.getStyleClass().add("window");
		window.setPickOnBounds(true);
		window.setMouseTransparent(false);
		stage.flatMap(Stage::focusedProperty).orElse(false).subscribe(focused -> {
			window.pseudoClassStateChanged(PseudoClass.getPseudoClass("focused"), focused);
			window.pseudoClassStateChanged(PseudoClass.getPseudoClass("unfocused"), !focused);
		});


		stagePane = new BorderPane();
		stagePane.getStyleClass().add("stage");
		window.getChildren().setAll(stagePane);

		ActionIntrospector.register(this);
		Action settingsAction = ActionIntrospector.action("settings");
		Action minimizeAction = ActionIntrospector.action("minimize");
		Action maximizeAction = ActionIntrospector.action("maximize");
		Action restoreAction = ActionIntrospector.action("restore");
		Action closeAction = ActionIntrospector.action("close");
		closeAction.getStyleClass().add("action-close");
		List<Action> actions = List.of(minimizeAction, maximizeAction, restoreAction, closeAction);


		settingsAction.setGraphic(GEAR.svg());
		minimizeAction.setGraphic(MINIMIZE.svg());
		maximizeAction.setGraphic(MAXIMIZE.svg());
		restoreAction.setGraphic(RESTORE.svg());
		closeAction.setGraphic(CLOSE.svg());

		Function<Boolean, Boolean> invert = b -> !b;
		restoreAction.disabledProperty().bind(stageProperty().flatMap(Stage::maximizedProperty).map(invert));
		maximizeAction.disabledProperty().bind(stageProperty().flatMap(Stage::maximizedProperty));
		minimizeAction.disabledProperty().bind(stageProperty().flatMap(Stage::iconifiedProperty));

		settingsButton = createActionButton(settingsAction);
		minimizeButton = createActionButton(minimizeAction);
		maximizeButton = createActionButton(maximizeAction);
		restoreButton = createActionButton(restoreAction);
		closeButton = createActionButton(closeAction);

		centerButton = new Group();
		stageProperty().flatMap(Stage::maximizedProperty).orElse(false).subscribe(maximized -> {
			centerButton.getChildren().setAll(maximized ? restoreButton : maximizeButton);
		});


		contextMenu = ActionUtils.createContextMenu(actions);
		buttonBar = new HBox();
		buttonBar.getStyleClass().add("button-bar");
		buttonBar.getChildren().setAll(settingsButton, minimizeButton, centerButton, closeButton);

		titleBar = new BorderPane();
		titleBar.setBackground(Background.fill(Color.WHITE.interpolate(Color.TRANSPARENT, 0.5)));
		titleBar.getStyleClass().add("title-bar");

		iconView = new ImageView();
		iconView.getStyleClass().add("icon");
		iconView.setPickOnBounds(true);
		iconView.imageProperty().bind(stageProperty().flatMap(stage -> {
			SimpleObjectProperty<Image> property = new SimpleObjectProperty<>();
			stage.getIcons().addListener((ListChangeListener<? super Image>) change -> {
				property.set(change.getList().stream().findFirst().orElse(null));
			});
			property.set(stage.getIcons().stream().findFirst().orElse(null));
			return property;
		}));
		iconView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> contextMenu.show(iconView, event.getScreenX(), event.getScreenY()));

		titleText = new Text();
		titleText.getStyleClass().add("title");
		titleText.textProperty().bind(stageProperty().flatMap(Stage::titleProperty));

		titleHeader = new HBox(iconView, titleText);
		titleHeader.getStyleClass().add("title-header");
		titleBar.setLeft(titleHeader);
		titleBar.setRight(buttonBar);
		stagePane.setTop(titleBar);

		subScene = new SubScene(new Pane(), 600, 400);
		subScene.getStyleClass().add("scene");
		subScene.fillProperty().bind(control.sceneFillProperty());
		subScene.userAgentStylesheetProperty().bind(control.sceneProperty().flatMap(Scene::userAgentStylesheetProperty));

		horizontalPadding = Bindings.createDoubleBinding(() -> window.getPadding().getLeft() + window.getPadding().getRight(), window.paddingProperty());
		verticalPadding = Bindings.createDoubleBinding(() -> window.getPadding().getTop() + window.getPadding().getBottom(), window.paddingProperty());

		stage.subscribe(stage -> {
			if (stage == null) return;
			stage.minWidthProperty().bind(
					buttonBar.widthProperty()
							.add(titleHeader.widthProperty())
							.add(horizontalPadding));
			stage.minHeightProperty().bind(
					titleBar.heightProperty().map(h -> (control.getShowTitleBar() ? h.doubleValue() : 0.0) + verticalPadding.get())
			);
		});
		ObservableValue<Number> stageWidth = stage.flatMap(Stage::widthProperty).orElse(0);
		ObservableValue<Number> stageHeight = stage.flatMap(Stage::heightProperty).orElse(0);
		subScene.widthProperty().bind(Bindings.createDoubleBinding(
				() -> -horizontalPadding.get() + stageWidth.getValue().doubleValue(),
				horizontalPadding, stageWidth));
		subScene.heightProperty().bind(Bindings.createDoubleBinding(
				() -> {
					double titleBarHeight = control.getShowTitleBar() ? titleBar.heightProperty().get() : 0;
					return -verticalPadding.get() - titleBarHeight + stageHeight.getValue().doubleValue();
				},
				verticalPadding,
				control.showTitleBarProperty(),
				titleHeader.heightProperty(), stageHeight));
		stagePane.setCenter(subScene);

		subScene.rootProperty().bind(control.rootProperty());

		control.showTitleBarProperty()
				.map(showTitleBar -> showTitleBar ? titleBar : null)
				.subscribe(stagePane::setTop);

		getChildren().setAll(window);

	}

	private Button createActionButton(Action action) {
		Button button = new Button();
		button.getStyleClass().addAll(action.getStyleClass());
		button.graphicProperty().bind(action.graphicProperty());
		button.setOnAction(action);
		return button;
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
		window.addEventHandler(MouseEvent.MOUSE_MOVED, resizeListener);
		window.addEventHandler(MouseEvent.MOUSE_PRESSED, resizeListener);
		window.addEventHandler(MouseEvent.MOUSE_DRAGGED, resizeListener);
		window.addEventHandler(MouseEvent.MOUSE_EXITED, resizeListener);
		window.addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, resizeListener);
	}

	@Override
	public void dispose() {
		super.dispose();

		titleBar.removeEventFilter(MouseEvent.MOUSE_PRESSED, dragListener);
		titleBar.removeEventFilter(MouseEvent.MOUSE_RELEASED, dragListener);
		titleBar.removeEventFilter(MouseEvent.MOUSE_DRAGGED, dragListener);
		titleBar.removeEventFilter(MouseEvent.MOUSE_EXITED, dragListener);

		window.removeEventFilter(MouseEvent.MOUSE_MOVED, resizeListener);
		window.removeEventFilter(MouseEvent.MOUSE_PRESSED, resizeListener);
		window.removeEventFilter(MouseEvent.MOUSE_DRAGGED, resizeListener);
		window.removeEventFilter(MouseEvent.MOUSE_EXITED, resizeListener);
		window.removeEventFilter(MouseEvent.MOUSE_EXITED_TARGET, resizeListener);
	}

	@ActionProxy(id = "close", text = "Close", longText = "Close the window.", graphic = "com/ezfx/controls/icons/mycons/close.png")
	public void close() {
		getStage().close();
	}


	@ActionProxy(id = "settings", text = "Settings", longText = "Open Application Settings.", graphic = "com/ezfx/controls/icons/mycons/gear.png")
	public void settings() {
	}

	@ActionProxy(id = "restore", text = "Restore", longText = "Return the window to its previous size and position.", graphic = "com/ezfx/controls/icons/mycons/restore.png")
	public void restore() {
		getStage().setMaximized(false);
	}

	@ActionProxy(id = "minimize", text = "Minimize", longText = "Minimize the window to the taskbar.", graphic = "com/ezfx/controls/icons/mycons/minimize.png")
	public void minimize() {
		getStage().setIconified(true);
	}

	@ActionProxy(id = "maximize", text = "Maximize", longText = "Expand the window to fill the screen.", graphic = "com/ezfx/controls/icons/mycons/maximize.png")
	public void maximize() {
		Stage stage = getStage();
		stage.setMaximized(true);

		Screen screen = getScreen(stage);
		Rectangle2D bounds = screen.getVisualBounds();

		double dx = window.getWidth() - stagePane.getWidth();
		double dy = window.getHeight() - stagePane.getHeight();

		stage.setX(bounds.getMinX() - (dx / 2));
		stage.setY(bounds.getMinY() - (dy / 2));
		stage.setWidth(bounds.getWidth() + dx);
		stage.setHeight(bounds.getHeight() + dy);
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
				if (event.getClickCount() == 2) {
					if (getStage().isMaximized()) {
						restore();
					} else {
						maximize();
					}
				}
				deltaX = getStage().getX() - event.getScreenX();
				deltaY = getStage().getY() - event.getScreenY();
			} else if (MouseEvent.MOUSE_RELEASED.equals(eventType)) {
				node.setCursor(Cursor.DEFAULT);
			} else if (MouseEvent.MOUSE_DRAGGED.equals(eventType)) {
				node.setCursor(Cursor.MOVE);
				getStage().setX(event.getScreenX() + deltaX);
				getStage().setY(event.getScreenY() + deltaY);
			} else if (MouseEvent.MOUSE_EXITED.equals(eventType)) {
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

			int borderWidth = 4;
			if (MouseEvent.MOUSE_MOVED.equals(eventType)) {

				EventTarget target = event.getTarget();
				if (target != window) {
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
