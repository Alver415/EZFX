package com.ezfx.app.explorer;

import com.ezfx.app.console.ManagedContext;
import com.ezfx.app.console.PolyglotView;
import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.explorer.GenericTreeView;
import com.ezfx.controls.utils.SplitPanes;
import javafx.application.Application;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.StackPane;
import org.reactfx.EventStreams;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.ezfx.controls.editor.factory.EditorFactory.DEFAULT_FACTORY;

public class ApplicationExplorer extends Control {

	public ApplicationExplorer(Application application) {
		setApplication(application);
		//TODO: Cleanup
		try {
			ManagedContext managedContext = ManagedContext.newBuilder()
					.permittedLanguages("js", "python")
					.allowAllAccess(true)
					.build();
			managedContext.getContext().getPolyglotBindings().putMember("application", application);
			setManagedContext(managedContext);
		} catch (ExecutionException | InterruptedException e) {
			throw new RuntimeException(e);
		}
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

		private final GenericTreeView<Object, Object> treeView;
		private final PolyglotView polyglotView;

		private final Map<Object, Editor<?>> factoryCache = new ConcurrentHashMap<>();

		protected DefaultSkin(ApplicationExplorer control) {
			super(control);
			treeView = new GenericTreeView<>();
			treeView.setRoot(control.getApplication());

			polyglotView = new PolyglotView(control.getManagedContext());

			StackPane left = new StackPane(treeView);
			StackPane right = new StackPane();
			StackPane bottom = new StackPane(polyglotView);

			treeView.selectedItemProperty().subscribe(item -> {
				polyglotView.getManagedContext().putPolyglotMember("selectedItem", item);
			});

			ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
			EventStreams.valuesOf(treeView.selectedItemProperty())
					.threadBridgeFromFx(executor)
					.filter(Objects::nonNull)
					.map(selected -> {
						Object value = selected.getValue().getValue();
						return factoryCache.computeIfAbsent(value, _ -> DEFAULT_FACTORY.buildEditor(value).orElseGet(Editor::new));
					})
					.threadBridgeToFx(executor)
					.subscribe(editor -> right.getChildren().setAll(editor));


			getChildren().setAll(SplitPanes.vertical(SplitPanes.horizontal(left, right), bottom));
		}
	}

}
