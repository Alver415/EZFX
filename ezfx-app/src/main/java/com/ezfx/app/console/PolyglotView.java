package com.ezfx.app.console;

import com.ezfx.controls.utils.Tabs;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Control;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TabPane;
import org.graalvm.polyglot.Language;

import java.util.Comparator;

public class PolyglotView extends Control {

	public PolyglotView(ManagedContext managedContext) {
		setManagedContext(managedContext);
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
	protected DefaultSkin createDefaultSkin() {
		return new DefaultSkin(this);
	}

	public static class DefaultSkin extends SkinBase<PolyglotView> {

		private final TabPane tabPane;

		protected DefaultSkin(PolyglotView control) {
			super(control);
			tabPane = new TabPane();
			control.managedContextProperty().subscribe(this::updateView);
			getChildren().setAll(tabPane);
		}

		private void updateView(ManagedContext managedContext) {
			if (managedContext == null) return;
			tabPane.getTabs().setAll(managedContext.getLanguages()
					.values().stream()
					.sorted(Comparator.comparing(Language::getName))
					.filter(Language::isInteractive)
					.map(language -> {
						MonoglotView monoglotView = new MonoglotView(managedContext, language);
						return Tabs.create(language.getName(), monoglotView);
					})
					.toList());
		}
	}

}
