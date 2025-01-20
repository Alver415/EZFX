package com.ezfx.app.console;

import com.ezfx.base.io.IOConsole;
import com.ezfx.base.io.StringConsumingOutputStream;
import com.ezfx.controls.console.ConsoleView;
import com.ezfx.controls.utils.SplitPanes;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.SplitPane;
import org.graalvm.polyglot.Language;
import org.graalvm.polyglot.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonoglotView extends Control {

	private static final Logger log = LoggerFactory.getLogger(MonoglotView.class);

	public MonoglotView(ManagedContext managedContext, Language language) {
		setManagedContext(managedContext);
		setLanguage(language);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new DefaultSkin(this);
	}

	private static class DefaultSkin extends SkinBase<MonoglotView> {

		public DefaultSkin(MonoglotView control) {
			super(control);
			ConsoleView consoleView = new ConsoleView();

			String languageId = control.getLanguage().getId();
			ManagedContext managedContext = control.getManagedContext();
			Value languageBinding = managedContext.getContext().getBindings(languageId);
			Value polyglotBinding = managedContext.getContext().getPolyglotBindings();
			BindingsView languageBindings = new BindingsView(managedContext, languageBinding);
			BindingsView polyglotBindings = new BindingsView(managedContext, polyglotBinding);

			SplitPane splitPane = new SplitPane(consoleView, SplitPanes.vertical(polyglotBindings, languageBindings));
			getChildren().setAll(splitPane);

			IOConsole console = consoleView.getConsole();
			console.in.subscribe(new StringConsumingOutputStream(input -> {
				managedContext.evalAsync(languageId, input)
						.thenAcceptAsync(result -> {
//							String string = String.valueOf(result);
//							log.info(string);
//							console.out.println(string);
						}, Platform::runLater).exceptionally(e -> {
							log.warn(e.getMessage(), e);
							console.err.println(e.getMessage());
							return null;
						});
			}));
		}
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

	private final Property<Language> language = new SimpleObjectProperty<>(this, "language");

	public Property<Language> languageProperty() {
		return this.language;
	}

	public Language getLanguage() {
		return this.languageProperty().getValue();
	}

	public void setLanguage(Language value) {
		this.languageProperty().setValue(value);
	}
}
