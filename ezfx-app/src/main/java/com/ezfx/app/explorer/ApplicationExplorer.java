package com.ezfx.app.explorer;

import com.ezfx.app.console.ManagedContext;
import com.ezfx.app.console.PolyglotView;
import com.ezfx.base.utils.Nodes;
import com.ezfx.controls.editor.FXItemEditor;
import com.ezfx.controls.item.FXItem;
import com.ezfx.controls.item.FXItemFactory;
import com.ezfx.controls.item.FXItemTreeControl;
import com.ezfx.controls.popup.OverlayPopup;
import com.ezfx.controls.utils.SplitPanes;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import static com.ezfx.base.utils.ScreenBounds.CACHED;

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

		private final ScrollPane scrollPane;
		private final FXItemEditor<FXItem<?, ?>> editor;
		private final FXItemTreeControl treeControl;
		private final PolyglotView polyglotView;

		private final OverlayPopup overlayPopup;
		private final ObservableValue<FXItem<?, ?>> target;

		protected DefaultSkin(ApplicationExplorer control) {
			super(control);

			treeControl = new FXItemTreeControl();
			treeControl.setRoot(FXItemFactory.CACHED.create(control.getApplication()));

			polyglotView = new PolyglotView(control.getManagedContext());

			editor = new FXItemEditor<>();
			scrollPane = new ScrollPane(editor);
			scrollPane.setFitToWidth(true);
			scrollPane.setFitToHeight(true);

			StackPane left = new StackPane(treeControl);
			StackPane right = new StackPane(scrollPane);
			StackPane bottom = new StackPane(polyglotView);

			target = Bindings.createObjectBinding(() ->
//							treeControl.getHoveredItem() != null ?
//							treeControl.getHoveredItem() :
							treeControl.getSelectedItem(),
					treeControl.hoveredItemProperty(), treeControl.selectedItemProperty());

			overlayPopup = new OverlayPopup(getNode().getScene().getWindow());
			overlayPopup.targetProperty().bind(target);
			overlayPopup.boundsProperty().bind(target.map(FXItem::get).flatMap(CACHED::of));
			overlayPopup.setBackground(Background.fill(Color.BLUE.interpolate(Color.TRANSPARENT, 0.75)));
			target.map(this::showPopup).subscribe(overlayPopup::setVisible);

//			treeView.selectedItemProperty().subscribe(item ->
//					polyglotView.getManagedContext().putPolyglotMember("selectedItem", item));

			treeControl.selectedItemProperty().subscribe(editor::setValue);


			SplitPane top = SplitPanes.horizontal(left, right);
			getChildren().setAll(SplitPanes.vertical(top
//					, bottom
			));
		}

		private boolean showPopup(FXItem<?, ?> item) {
			if (true) return true;
			Object object = item.get();
			if (object instanceof Node node) {
				boolean nonNull = node != null;
				// Make sure we don't draw the popup over the ApplicationExplorer itself while we're using it
				boolean isAncestor = node == getNode() ||
						Nodes.isAncestor(node, getNode())
//					||
//					node instanceof FakeNode.Scene scene && scene.getActual() == getNode().getScene() ||
//					node instanceof FakeNode.Window window && window.getActual() == getNode().getScene().getWindow()||
//					node instanceof FakeNode.Stage stage && stage.getActual() == getNode().getScene().getWindow()
						;
				return nonNull && !isAncestor;
			}
			return true;
		}
	}
}
