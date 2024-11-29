package com.ezfx.controls.console;

import com.ezfx.base.io.IOConsole;
import com.ezfx.controls.utils.CssBoundStyleablePropertyFactory;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.List;

public class ConsoleView extends Control {

	protected final IOConsole console;
	protected final History<String> history;

	public ConsoleView() {
		this(new IOConsole());
	}

	public ConsoleView(IOConsole console) {
		this.console = console;
		this.history = new History<>();

		getStyleClass().add("console-view");
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new ConsoleViewSkin(this);
	}

	public IOConsole getConsole() {
		return console;
	}

	public void submit(String inputText) {
		ConsoleEvent event = new ConsoleEvent(ConsoleEvent.INPUT_SUBMITTED, inputText);
		history.addItem(event.getText());
		console.in.println(event.getText());
		fireEvent(event);
	}

	//region CSS
	protected static final CssBoundStyleablePropertyFactory<ConsoleView> STYLE_FACTORY =
			new CssBoundStyleablePropertyFactory<>(Control.getClassCssMetaData());

	public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
		return STYLE_FACTORY.getOrCreateCssMetaData();
	}

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
		return getClassCssMetaData();
	}
	//endregion
	//region Properties

	protected final StyleableObjectProperty<Paint> textFill = STYLE_FACTORY.createPaintProperty(this, "textFill",
			"-fx-text-fill", s -> s.textFill, Color.GREEN, true);

	public StyleableObjectProperty<Paint> textFillProperty() {
		return textFill;
	}

	public Paint getTextFill() {
		return textFill.getValue();
	}

	public void setTextFill(Paint paint) {
		textFill.setValue(paint);
	}

	protected final StyleableObjectProperty<Paint> textInFill = STYLE_FACTORY.createPaintProperty(this, "textInFill",
			"-fx-text-in-fill", s -> s.textInFill, Color.BLUE, true);

	public StyleableObjectProperty<Paint> textInFillProperty() {
		return textInFill;
	}

	public Paint getTextInFill() {
		return textInFill.getValue();
	}

	public void setTextInFill(Paint paint) {
		textInFill.setValue(paint);
	}

	protected final StyleableObjectProperty<Paint> textOutFill = STYLE_FACTORY.createPaintProperty(this, "textOutFill",
			"-fx-text-out-fill", s -> s.textOutFill, Color.BLACK, true);

	public StyleableObjectProperty<Paint> textOutFillProperty() {
		return textOutFill;
	}

	public Paint getTextOutFill() {
		return textOutFill.getValue();
	}

	public void setTextOutFill(Paint paint) {
		textOutFill.setValue(paint);
	}

	protected final StyleableObjectProperty<Paint> textErrFill = STYLE_FACTORY.createPaintProperty(this, "textErrFill",
			"-fx-text-err-fill", s -> s.textErrFill, Color.RED, true);

	public StyleableObjectProperty<Paint> textErrFillProperty() {
		return textErrFill;
	}

	public Paint getTextErrFill() {
		return textErrFill.getValue();
	}

	public void setTextErrFill(Paint paint) {
		textErrFill.setValue(paint);
	}


	protected final StyleableObjectProperty<Boolean> autoScroll = STYLE_FACTORY.createBooleanProperty(this,
			"autoScroll",
			"-fx-auto-scroll", s -> s.autoScroll, Boolean.TRUE, true);

	public StyleableObjectProperty<Boolean> autoScrollProperty() {
		return autoScroll;
	}

	public Boolean getAutoScroll() {
		return autoScroll.getValue();
	}

	public void setAutoScroll(Boolean value) {
		autoScroll.setValue(value);
	}

	//endregion
}
