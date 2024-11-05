package com.fsfx.control.editor;

import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Subscription;

public abstract class BeanEditorSkinBase<B> extends SkinBase<BeanEditor<B>> {

	public BeanEditorSkinBase(BeanEditor<B> control) {
		super(control);
	}

	public static class VBoxSkin<B> extends BeanEditorSkinBase<B> {

		private final Subscription subscription;

		public VBoxSkin(BeanEditor<B> control) {
			super(control);

			subscription = control.editorsProperty().subscribe(editors -> {
				VBox vBox = new VBox();
				vBox.getChildren().setAll(editors);
				getChildren().setAll(vBox);
			});
		}

		@Override
		public void dispose() {
			super.dispose();
			subscription.unsubscribe();
		}

	}

	public static class HBoxSkin<B> extends BeanEditorSkinBase<B> {

		private final Subscription subscription;

		public HBoxSkin(BeanEditor<B> control) {
			super(control);

			subscription = control.editorsProperty().subscribe(editors -> {
				HBox hBox = new HBox();
				hBox.getChildren().setAll(editors);
				getChildren().setAll(hBox);
			});
		}

		@Override
		public void dispose() {
			super.dispose();
			subscription.unsubscribe();
		}

	}

	public static class ListSkin<B> extends BeanEditorSkinBase<B> {

		private final Subscription subscription;

		public ListSkin(BeanEditor<B> control) {
			super(control);

			subscription = control.editorsProperty().subscribe(editors -> {
				ListView<Node> listView = new ListView<>();
				listView.getItems().setAll(editors);
				getChildren().setAll(listView);
			});
		}

		@Override
		public void dispose() {
			super.dispose();
			subscription.unsubscribe();
		}
	}
	public static class TreeSkin<B> extends BeanEditorSkinBase<B> {

		private final Subscription subscription;

		public TreeSkin(BeanEditor<B> control) {
			super(control);

			subscription = control.editorsProperty().subscribe(editors -> {
				TreeView<Node> treeView = new TreeView<>();
				treeView.setRoot(new TreeItem<>(editors.getFirst()));
				getChildren().setAll(treeView);
			});
		}

		@Override
		public void dispose() {
			super.dispose();
			subscription.unsubscribe();
		}
	}
}
