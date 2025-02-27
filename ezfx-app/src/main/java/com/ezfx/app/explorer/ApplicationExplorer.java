package com.ezfx.app.explorer;

import com.ezfx.app.console.ManagedContext;
import com.ezfx.app.console.PolyglotView;
import com.ezfx.base.utils.ScreenBounds;
import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.explorer.GenericTreeView;
import com.ezfx.controls.explorer.TreeValue;
import com.ezfx.controls.utils.SplitPanes;
import javafx.application.Application;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.reactfx.EventStreams;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.ezfx.controls.editor.factory.EditorFactory.DEFAULT_FACTORY;

public class ApplicationExplorer extends Control {

	public ApplicationExplorer(Application application) {
		setApplication(application);
		//TODO: Cleanup
		//FIXME: Not working with installation
//		try {
//			ManagedContext managedContext = ManagedContext.newBuilder()
//					.permittedLanguages("js", "python")
//					.allowAllAccess(true)
//					.build();
//			managedContext.getContext().getPolyglotBindings().putMember("application", application);
//			setManagedContext(managedContext);
//		} catch (ExecutionException | InterruptedException e) {
//			throw new RuntimeException(e);
//		}
	}

	private final Property<Application> application = new SimpleObjectProperty<>(this, "application");

	public Property<Application> applicationProperty() {
		return this.application;
	}

	public Application getApplication() {
		return this.applicationProperty().getValue();
	}

	public void setApplication(Application value) {
		this.applicationProperty().setValue(value);
	}

	private final Property<ManagedContext> managedContext = new SimpleObjectProperty<>(this, "managedContext");

	public Property<ManagedContext> managedContextProperty() {
		return this.managedContext;
	}

	public ManagedContext getManagedContext() {
		return this.managedContextProperty().getValue();
	}

	public void setManagedContext(ManagedContext value) {
		this.managedContextProperty().setValue(value);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new DefaultSkin(this);
	}

	public static class DefaultSkin extends SkinBase<ApplicationExplorer> {

		private static final BoundingBox DEFAULT_BOUNDING_BOX = new BoundingBox(0, 0, 0, 0);

		private final ScrollPane scrollPane;
		private final GenericTreeView<Object, Object> treeView;
		private final PolyglotView polyglotView;
		private final Stage overlay;

		private final Map<Object, Editor<?>> cache = new ConcurrentHashMap<>(new LinkedHashMap<>(5, 0.75f, true));

		protected DefaultSkin(ApplicationExplorer control) {
			super(control);

			overlay = new Stage();
			overlay.initStyle(StageStyle.TRANSPARENT);
			overlay.setAlwaysOnTop(true);
			overlay.show();
			Canvas canvas = new Canvas(0, 0);
			Scene scene = new Scene(new Group(canvas));
			scene.setFill(Color.TRANSPARENT);
			overlay.setScene(scene);

			treeView = new GenericTreeView<>();
			treeView.setRoot(control.getApplication());

			polyglotView = new PolyglotView(control.getManagedContext());

			scrollPane = new ScrollPane();
			scrollPane.setFitToWidth(true);
			scrollPane.setFitToHeight(true);

			StackPane left = new StackPane(treeView);
			StackPane right = new StackPane(scrollPane);
			StackPane bottom = new StackPane(polyglotView);


			ObservableValue<Node> selectedItem = treeView.getSelectionModel().selectedItemProperty()
					.flatMap(TreeItem::valueProperty)
					.flatMap(TreeValue::valueProperty)
					.map(value -> value instanceof Node node ? node : null);

			treeView.selectedProperty()
					.map(o -> {
						if (o instanceof Window w) {
							return w;
						} else if (o instanceof Scene s) {
							return s.getWindow();
						} else if (o instanceof Node n) {
							return n.getScene().getWindow();
						}
						return null;
					}).flatMap(ScreenBounds::ofWindow)
					.subscribe(bounds -> {
						if (bounds == null) return;
						overlay.setX(bounds.getMinX());
						overlay.setY(bounds.getMinY());
						overlay.setWidth(bounds.getWidth());
						overlay.setHeight(bounds.getHeight());
						canvas.setWidth(bounds.getWidth());
						canvas.setHeight(bounds.getHeight());
					});
			treeView.selectedProperty()
					.flatMap(ScreenBounds::of)
					.subscribe(screenBounds -> {
						if (screenBounds == null) return;
						Bounds local = canvas.screenToLocal(screenBounds);
						GraphicsContext gc = canvas.getGraphicsContext2D();
						gc.setFill(Color.GRAY.interpolate(Color.TRANSPARENT, 0.5));
						gc.clearRect(0, 0, 10000, 10000);
						gc.fillRect(0, 0, 10000, 10000);
						gc.clearRect(local.getMinX(), local.getMinY(), local.getWidth(), local.getHeight());
					});


//			treeView.selectedItemProperty().subscribe(item ->
//					polyglotView.getManagedContext().putPolyglotMember("selectedItem", item));


			ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
			EventStreams.valuesOf(treeView.selectedProperty())
					.threadBridgeFromFx(executor)
					.filter(Objects::nonNull)
					.map(selected -> cache.computeIfAbsent(selected,
							_ -> DEFAULT_FACTORY.buildEditor(selected).orElseGet(Editor::new)))
					.threadBridgeToFx(executor)
					.subscribe(scrollPane::setContent);


			SplitPane top = SplitPanes.horizontal(left, right);
			getChildren().setAll(SplitPanes.vertical(top
//					, bottom
			));
		}
	}
}
