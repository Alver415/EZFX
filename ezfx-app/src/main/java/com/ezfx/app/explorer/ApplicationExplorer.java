package com.ezfx.app.explorer;

import com.ezfx.app.console.ManagedContext;
import com.ezfx.app.console.PolyglotView;
import com.ezfx.base.utils.Nodes;
import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.EditorBase;
import com.ezfx.controls.info.NodeInfoHelper;
import com.ezfx.controls.popup.OverlayPopup;
import com.ezfx.controls.tree.SceneGraphTreeControl;
import com.ezfx.controls.utils.SplitPanes;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.reactfx.EventStreams;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.ezfx.base.utils.ObservableConstant.constant;
import static com.ezfx.base.utils.ScreenBounds.CACHED;
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

		private final Map<Node, Editor<?>> cache = new ConcurrentHashMap<>(new LinkedHashMap<>(5, 0.75f, true));

		private final ScrollPane scrollPane;
		private final SceneGraphTreeControl treeControl;
		private final PolyglotView polyglotView;

		private final OverlayPopup overlayPopup;
		private final ObservableValue<Node> target;

		protected DefaultSkin(ApplicationExplorer control) {
			super(control);

			treeControl = new SceneGraphTreeControl();
			treeControl.setRoot(new FakeNode.Application(control.getApplication()));
			treeControl.setChildrenProvider(FakeNode.CHILDREN_PROVIDER);

			polyglotView = new PolyglotView(control.getManagedContext());

			scrollPane = new ScrollPane();
			scrollPane.setFitToWidth(true);
			scrollPane.setFitToHeight(true);

			StackPane left = new StackPane(treeControl);
			StackPane right = new StackPane(scrollPane);
			StackPane bottom = new StackPane(polyglotView);

			target = Bindings.createObjectBinding(
					() -> treeControl.getHoveredItem() != null ? treeControl.getHoveredItem() : treeControl.getSelectedItem(),
					treeControl.hoveredItemProperty(), treeControl.selectedItemProperty());

			overlayPopup = new OverlayPopup(getNode().getScene().getWindow());
			overlayPopup.targetProperty().bind(target);
			overlayPopup.boundsProperty().bind(getBounds(target));
			overlayPopup.setBackground(Background.fill(Color.BLUE.interpolate(Color.TRANSPARENT, 0.75)));
			target.map(this::showPopup).subscribe(overlayPopup::setVisible);

//			treeView.selectedItemProperty().subscribe(item ->
//					polyglotView.getManagedContext().putPolyglotMember("selectedItem", item));

			ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
			EventStreams.valuesOf(treeControl.selectedItemProperty())
					.threadBridgeFromFx(executor)
					.filter(Objects::nonNull)
					.map(selected -> cache.computeIfAbsent(selected,
							_ -> {
								Object object = selected;
								if (selected instanceof FakeNode<?> fakeNode) {
									object = fakeNode.getActual();
								}
								return DEFAULT_FACTORY.buildEditor(object).orElseGet(EditorBase::new);
							}))
					.threadBridgeToFx(executor)
					.map(Editor::getNode)
					.subscribe(scrollPane::setContent);


			SplitPane top = SplitPanes.horizontal(left, right);
			getChildren().setAll(SplitPanes.vertical(top
//					, bottom
			));
		}

		private ObservableValue<Bounds> getBounds(ObservableValue<Node> target) {
			return target.flatMap(node -> {
				if (node instanceof FakeNode<?> fakeNode) {
					return CACHED.of(fakeNode.getActual());
				} else {
					return CACHED.ofNode(node);
				}
			});
		}

		private boolean showPopup(Node t) {
			boolean nonNull = t != null;
			// Make sure we don't draw the popup over the ApplicationExplorer itself while we're using it
			boolean isAncestor = t == getNode() ||
					Nodes.isAncestor(t, getNode()) ||
					t instanceof FakeNode.Scene scene && scene.getActual() == getNode().getScene() ||
					t instanceof FakeNode.Window window && window.getActual() == getNode().getScene().getWindow()||
					t instanceof FakeNode.Stage stage && stage.getActual() == getNode().getScene().getWindow();
			boolean shouldeBeVisible = nonNull && !isAncestor;
			return shouldeBeVisible;
		}
	}

	/**
	 * This is a hacky workaround to allow Application, Windows, Stages, and Scenes work in a TreeView<Node>, specifically SceneGraphTreeControl.
	 * This allows you to wrap those types in a Region, then map their properties to a natural counterpart.
	 * @param <T>
	 */
	private static class FakeNode<T> extends Region {

		private final T actual;

		protected FakeNode(T actual) {
			this.actual = actual;
		}

		public T getActual() {
			return actual;
		}

		protected final ReadOnlyListWrapper<Node> children = new ReadOnlyListWrapper<>(this, "children", FXCollections.observableArrayList());

		public ReadOnlyListProperty<Node> childrenProperty() {
			return this.children.getReadOnlyProperty();
		}

		public ObservableList<Node> getChildren() {
			return this.childrenProperty().getValue();
		}

		private static final Function<Node, ObservableList<Node>> CHILDREN_PROVIDER = node -> {
			if (node instanceof FakeNode<?> fakeNode)
				return fakeNode.getChildren();
			return SceneGraphTreeControl.CHILDREN_PROVIDER.apply(node);
		};

		/* === IMPLEMENTATIONS === */

		private static class Application extends FakeNode<javafx.application.Application> {
			public Application(javafx.application.Application actual) {
				super(actual);
				visibleProperty().bind(constant(true));
				getProperties().put("NAME_OVERRIDE", constant(NodeInfoHelper.CACHING.typeName(actual)));

				InvalidationListener listener = _ -> {
					Set<javafx.stage.Stage> stages = javafx.stage.Window.getWindows().stream()
							.filter(a -> a instanceof javafx.stage.Stage)
							.map(stage -> (javafx.stage.Stage) stage)
							.collect(Collectors.toSet());
					stagesProperty().addAll(stages);
				};
				javafx.stage.Window.getWindows().addListener(listener);
				stagesProperty().subscribe(set -> children.setAll(set.stream().map(Stage::new).collect(Collectors.toSet())));
				listener.invalidated(null);
			}

			private final SetProperty<javafx.stage.Stage> stages = new SimpleSetProperty<>(this, "stages", FXCollections.observableSet(new HashSet<>()));

			public SetProperty<javafx.stage.Stage> stagesProperty() {
				return this.stages;
			}

			public ObservableSet<javafx.stage.Stage> getStages() {
				return this.stagesProperty().getValue();
			}

			public void setStages(ObservableSet<javafx.stage.Stage> value) {
				this.stagesProperty().setValue(value);
			}
		}

		private static class Window extends FakeNode<javafx.stage.Window> {
			public Window(javafx.stage.Window actual) {
				super(actual);
				actual.sceneProperty().map(Scene::new).subscribe(scene -> children.setAll(scene));
			}
		}

		private static class Stage extends FakeNode<javafx.stage.Stage> {
			public Stage(javafx.stage.Stage actual) {
				super(actual);
				actual.sceneProperty().map(Scene::new).subscribe(scene -> children.setAll(scene));
				getProperties().put("NAME_OVERRIDE", actual.titleProperty());
				visibleProperty().subscribe(visible -> {
					if (visible) actual.show();
					else actual.hide();
				});
				actual.showingProperty().subscribe(showing -> {
					visibleProperty().set(showing);
				});
			}
		}

		private static class Scene extends FakeNode<javafx.scene.Scene> {
			public Scene(javafx.scene.Scene actual) {
				super(actual);
				visibleProperty().bind(constant(true));
				actual.rootProperty().subscribe(root -> children.setAll(root));
				idProperty().bind(actual.userAgentStylesheetProperty());
				actual.getStylesheets().addListener((ListChangeListener<? super String>) _ ->
						getStyleClass().setAll(actual.getStylesheets()));
			}
		}
	}
}
