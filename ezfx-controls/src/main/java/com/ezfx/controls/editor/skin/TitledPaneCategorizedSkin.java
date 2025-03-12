package com.ezfx.controls.editor.skin;

import com.ezfx.controls.editor.CategorizedMultiEditor;
import com.ezfx.controls.editor.EditorBase;
import com.ezfx.controls.editor.EditorSkinBase;
import com.ezfx.controls.editor.PropertiesEditor;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;

public class TitledPaneCategorizedSkin<E extends EditorBase<T> & CategorizedMultiEditor<T, PropertiesEditor<T>>, T> extends EditorSkinBase<E, T> {

	private final Accordion accordion = new Accordion();

	public TitledPaneCategorizedSkin(E control) {
		super(control);
		setChildren(accordion);

		control.categorizedEditorsProperty().map(categorizedEditors -> categorizedEditors.entrySet().stream()
						.map(entry -> new TitledPane(entry.getKey().title(), entry.getValue())).toList())
				.subscribe(content -> {
					accordion.getPanes().setAll(content);
					content.stream().findFirst().ifPresent(accordion::setExpandedPane);
				});
	}

}
