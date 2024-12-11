package com.ezfx.controls.console;

import com.ezfx.base.io.IOStream;
import com.ezfx.base.io.StringConsumingOutputStream;
import com.ezfx.base.utils.Resources;
import com.ezfx.controls.editor.code.CodeArea;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.EventType;
import javafx.scene.control.SkinBase;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.fxmisc.flowless.VirtualizedScrollPane;

import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyledDocument;
import org.fxmisc.richtext.util.UndoUtils;

import java.util.Collection;
import java.util.Optional;

import static com.ezfx.base.io.FileSystemIO.transfer;
import static com.ezfx.base.utils.EZFX.runFX;


public class ConsoleViewSkin extends SkinBase<ConsoleView> {

	public static final String STYLE_SHEET = Resources.css(ConsoleViewSkin.class, "ConsoleViewSkin.css");

	protected final VirtualizedScrollPane<CodeArea> scrollPane;
	protected final CodeArea codeArea;

	protected final IntegerProperty inputTextIndex = new SimpleIntegerProperty(0);
	protected final StringProperty inputText = new SimpleStringProperty();
	protected final StringProperty outputText = new SimpleStringProperty();

	protected ConsoleViewSkin(ConsoleView control) {
		super(control);

		codeArea = new CodeArea() {
			// Override replace so that all writing is moved to the beginning of the inputText section.
			// Effectively makes outputText section non-editable, but only appendable.
			@Override
			public void replace(
					int start,
					int end,
					StyledDocument<Collection<String>, String, Collection<String>> replacement) {
				start = Math.max(start, inputTextIndex.get());
				end = Math.max(end, inputTextIndex.get());
				super.replace(start, end, replacement);
			}

			@Override
			public void paste() {
				Clipboard clipboard = Clipboard.getSystemClipboard();
				if (clipboard.hasString()) {
					String text = clipboard.getString();
					if (text != null) {
						replaceSelection(text);
					}
				}
			}
		};
		codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
		codeArea.setUndoManager(UndoUtils.noOpUndoManager());
		codeArea.setOnSelectionDrag(_ -> {
		});
		codeArea.setOnSelectionDropped(_ -> {
		});


		outputText.bind(codeArea.textProperty().map(s -> s.substring(0, inputTextIndex.get())));
		inputText.bind(codeArea.textProperty().map(s -> s.substring(inputTextIndex.get())));

		scrollPane = new VirtualizedScrollPane<>(codeArea);
		ChangeListener<Double> listener = (_, oldValue, newValue) -> {
			boolean singleLine = (codeArea.getTotalHeightEstimate() - codeArea.getViewportHeight()) - newValue < 12;
			control.setAutoScroll(singleLine);
		};
		scrollPane.estimatedScrollYProperty().addListener(listener);
		getChildren().setAll(scrollPane);

		connect(control.console.in, Type.in);
		connect(control.console.out, Type.out);
		connect(control.console.err, Type.err);
	}

	private void connect(IOStream console, Type in) {
		transfer(console.getInputStream(), new StringConsumingOutputStream(s -> appendOutputText(s, in)));
	}

	@Override
	public void install() {
		super.install();
		ConsoleView control = getSkinnable();

		control.getStylesheets().add(STYLE_SHEET);
		control.addEventFilter(KeyEvent.KEY_PRESSED, this::keyPressedFilter);
	}

	@Override
	public void dispose() {
		super.dispose();
		ConsoleView control = getSkinnable();

		control.getStylesheets().remove(STYLE_SHEET);
		control.removeEventFilter(KeyEvent.KEY_PRESSED, this::keyPressedFilter);
	}

	protected void keyPressedFilter(KeyEvent event) {
		if (event.isConsumed()) {
			return;
		}

		if (event.isControlDown()) {
			if (event.getCode().equals(KeyCode.ENTER)) {
				submitInputText();
				event.consume();
			}
			if (event.getCode().equals(KeyCode.UP)) {
				String command = Optional.ofNullable(getSkinnable().history.step(-1)).orElse("");
				setInputText(command);
				event.consume();
			}
			if (event.getCode().equals(KeyCode.DOWN)) {
				String command = Optional.ofNullable(getSkinnable().history.step(1)).orElse("");
				setInputText(command);
				event.consume();
			}
		}
	}


	public void submitInputText() {
		String command = inputText.get();
		int start = outputText.length().get();
		int end = codeArea.textProperty().getValue().length();
		codeArea.deleteText(start, end);
		getSkinnable().submit(command);
	}

	private void appendOutputText(String string, Type type) {
		if (string.isBlank()) {
			return;
		}
		String text = string.replace('\r', '\0');
		runFX(() -> {
			int caret = codeArea.caretPositionProperty().getValue();
			int anchor = codeArea.getCaretSelectionBind().getAnchorPosition();
			int inputIndex = inputTextIndex.get();
			caret = caret < inputIndex ? caret : caret + text.length();
			anchor = anchor < inputIndex ? anchor : anchor + text.length();
			codeArea.insert(inputIndex, text, type.styleClass);
			if (getSkinnable().getAutoScroll()) {
				codeArea.showParagraphInViewport(codeArea.getCurrentParagraph());
			}
			inputTextIndex.set(inputTextIndex.get() + text.length());
			codeArea.getCaretSelectionBind().selectRangeExpl(anchor, caret);
			getSkinnable().fireEvent(new ConsoleEvent(type.eventType, text));
		});
	}

	private void setInputText(String command) {
		runFX(() -> {
			int start = inputTextIndex.get();
			int end = codeArea.textProperty().getValue().length();
			codeArea.replaceText(start, end, command);
			codeArea.requestFollowCaret();
		});
	}

	private enum Type {
		in("in-stream", ConsoleEvent.IN_PRINTED), out("out-stream", ConsoleEvent.OUT_PRINTED), err(
				"err-stream", ConsoleEvent.ERR_PRINTED);

		final String styleClass;
		final EventType<ConsoleEvent> eventType;

		Type(String styleClass, EventType<ConsoleEvent> eventType) {
			this.styleClass = styleClass;
			this.eventType = eventType;
		}
	}
}
