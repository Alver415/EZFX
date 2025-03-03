package com.ezfx.app.explorer;

import com.ezfx.app.console.ManagedContext;
import com.ezfx.app.console.PolyglotView;
import com.ezfx.base.utils.ScreenBounds;
import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.explorer.GenericTreeView;
import com.ezfx.controls.popup.OverlayPopup;
import com.ezfx.controls.utils.SplitPanes;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
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
		private static final Property<Bounds> DEFAULT_BOUNDS = new SimpleObjectProperty<>(DEFAULT_BOUNDING_BOX);

		private final ScrollPane scrollPane;
		private final GenericTreeView<Object, Object> treeView;
		private final PolyglotView polyglotView;

		private final Map<Object, Editor<?>> cache = new ConcurrentHashMap<>(new LinkedHashMap<>(5, 0.75f, true));
		private final OverlayPopup overlayPopup;

		protected DefaultSkin(ApplicationExplorer control) {
			super(control);

			treeView = new GenericTreeView<>();
			treeView.setRoot(control.getApplication());

			polyglotView = new PolyglotView(control.getManagedContext());

			scrollPane = new ScrollPane();
			scrollPane.setFitToWidth(true);
			scrollPane.setFitToHeight(true);

			StackPane left = new StackPane(treeView);
			StackPane right = new StackPane(scrollPane);
			StackPane bottom = new StackPane(polyglotView);

			ObservableValue<Object> highlighted = Bindings.createObjectBinding(
					() -> treeView.getHoveredItem() != null ? treeView.getHoveredItem() : treeView.getSelectedItem(),
					treeView.hoveredItemProperty(), treeView.selectedItemProperty());

			ObservableValue<Node> target = highlighted.map(object -> object instanceof Node node ? node : null);

			overlayPopup = new OverlayPopup();
			overlayPopup.targetProperty().bind(target);
			overlayPopup.boundsProperty().bind(target.flatMap(ScreenBounds.CACHED::ofNode));
			overlayPopup.setBackground(Background.fill(Color.BLUE.interpolate(Color.TRANSPARENT, 0.75)));
			overlayPopup.show(this.getNode().getScene().getWindow());

//			treeView.selectedItemProperty().subscribe(item ->
//					polyglotView.getManagedContext().putPolyglotMember("selectedItem", item));

			ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
			EventStreams.valuesOf(treeView.selectedItemProperty())
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
