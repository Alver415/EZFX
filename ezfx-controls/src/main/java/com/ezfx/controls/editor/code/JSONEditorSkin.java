package com.ezfx.controls.editor.code;

import com.ezfx.base.utils.Resources;
import com.ezfx.controls.editor.EditorBase;
import com.ezfx.controls.editor.EditorSkinBase;
import javafx.concurrent.Task;
import org.fxmisc.flowless.VirtualizedScrollPane;

import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONEditorSkin extends EditorSkinBase<EditorBase<String>, String> {

	private static final String STYLE_SHEET = Resources.css(JSONEditorSkin.class, "JSONEditorSkin.css");

	private static final String BRACE_PATTERN = "[{}]";
	private static final String BRACKET_PATTERN = "[\\[\\]]";
	private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
	private static final Pattern PATTERN = Pattern.compile("|(?<BRACE>%s)|(?<BRACKET>%s)|(?<STRING>%s)"
			.formatted(BRACE_PATTERN, BRACKET_PATTERN, STRING_PATTERN));
	private static final Logger log = LoggerFactory.getLogger(JSONEditorSkin.class);

	private final ExecutorService executor = Executors.newSingleThreadExecutor();

	private final CodeArea codeArea;
	private final VirtualizedScrollPane<CodeArea> scrollPane;

	public JSONEditorSkin(EditorBase<String> editor) {
		super(editor);
		codeArea = new CodeArea();
		codeArea.getStylesheets().add(STYLE_SHEET);
		codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

		Subscription cleanupWhenDone = codeArea.multiPlainChanges()
				.successionEnds(Duration.ofMillis(500))
				.retainLatestUntilLater(executor)
				.supplyTask(this::computeHighlightingAsync)
				.awaitLatest(codeArea.multiPlainChanges())
				.filterMap(t -> {
					if (t.isSuccess()) {
						return Optional.of(t.get());
					} else {
						log.warn(t.getFailure().getMessage(), t.getFailure());
						return Optional.empty();
					}
				})
				.subscribe(this::applyHighlighting);
		scrollPane = new VirtualizedScrollPane<>(codeArea);
		getChildren().setAll(scrollPane);
	}

	private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
		String text = codeArea.getText();
		Task<StyleSpans<Collection<String>>> task = new Task<>() {
			@Override
			protected StyleSpans<Collection<String>> call() {
				return computeHighlighting(text);
			}
		};
		executor.execute(task);
		return task;
	}

	private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
		codeArea.setStyleSpans(0, highlighting);
	}

	private static StyleSpans<Collection<String>> computeHighlighting(String text) {
		Matcher matcher = PATTERN.matcher(text);
		int lastKwEnd = 0;
		StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
		while (matcher.find()) {
			String styleClass =
					matcher.group("BRACE") != null ? "brace" :
							matcher.group("BRACKET") != null ? "bracket" :
									matcher.group("SEMICOLON") != null ? "semicolon" :
											matcher.group("STRING") != null ? "string" :
													matcher.group("COMMENT") != null ? "comment" :
															null; /* never happens */
			assert styleClass != null;
			spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
			spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
			lastKwEnd = matcher.end();
		}
		spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
		return spansBuilder.create();
	}
}