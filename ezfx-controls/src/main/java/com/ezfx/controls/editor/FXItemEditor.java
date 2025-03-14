package com.ezfx.controls.editor;

import com.ezfx.controls.editor.impl.javafx.ApplicationEditor;
import com.ezfx.controls.editor.impl.javafx.NodeEditor;
import com.ezfx.controls.editor.introspective.IntrospectingPropertiesEditor;
import com.ezfx.controls.info.FXItem;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;

public class FXItemEditor<T extends FXItem<?, ?>> extends ObjectEditor<T> {

	private final ApplicationEditor applicationEditor;
	private final NodeEditor nodeEditor;
	private final IntrospectingPropertiesEditor<Object> propertiesEditor;

	public FXItemEditor() {
		applicationEditor = new ApplicationEditor();
		nodeEditor = new NodeEditor();
		propertiesEditor = new IntrospectingPropertiesEditor<>();
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new DefaultSkin<>(this);
	}

	private static class DefaultSkin<T extends FXItem<?, ?>> extends SkinBase<FXItemEditor<T>> {

		public DefaultSkin(FXItemEditor<T> editor) {
			super(editor);
			Label notAvailableLabel = new Label("Not Available");
			editor.valueProperty().map(item -> switch (item) {
						case FXItem.FXApplicationItem<?> applicationItem -> {
							editor.applicationEditor.setValue(applicationItem.get());
							yield editor.applicationEditor;
						}
						case FXItem.FXNodeItem<?> nodeItem -> {
							editor.nodeEditor.setValue(nodeItem.get());
							yield editor.nodeEditor;
						}
						case FXItem<?, ?> fxItem -> {
							editor.propertiesEditor.setValue(fxItem.get());
							yield editor.propertiesEditor;
						}
					})
					.map(mapToNode -> (Node) mapToNode)
					.orElse(notAvailableLabel)
					.subscribe(subEditor -> getChildren().setAll(subEditor));
		}
	}
}
