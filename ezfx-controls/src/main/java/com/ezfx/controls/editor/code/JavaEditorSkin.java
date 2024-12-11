package com.ezfx.controls.editor.code;

import com.ezfx.base.utils.Resources;
import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.impl.standard.StringEditor;
import com.ezfx.controls.editor.skin.EditorSkin;
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

public class JavaEditorSkin extends EditorSkin<Editor<String>, String> {
	private static final String STYLE_SHEET = Resources.css(JavaEditorSkin.class, "JavaEditorSkin.css");

	private static final String[] KEYWORDS = new String[]{
			"abstract", "assert", "boolean", "break", "byte",
			"case", "catch", "char", "class", "const",
			"continue", "default", "do", "double", "else",
			"enum", "extends", "final", "finally", "float",
			"for", "goto", "if", "implements", "import",
			"instanceof", "int", "interface", "long", "native",
			"new", "package", "private", "protected", "public",
			"return", "short", "static", "strictfp", "super",
			"switch", "synchronized", "this", "throw", "throws",
			"transient", "try", "void", "volatile", "while"
	};

	private static final String KEYWORD_PATTERN = "\\b(%s)\\b".formatted(String.join("|", KEYWORDS));
	private static final String PAREN_PATTERN = "[()]";
	private static final String BRACE_PATTERN = "[{}]";
	private static final String BRACKET_PATTERN = "[\\[\\]]";
	private static final String SEMICOLON_PATTERN = ";";
	private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
	private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

	private static final Pattern PATTERN = Pattern.compile(
			"(?<KEYWORD>%s)|(?<PAREN>%s)|(?<BRACE>%s)|(?<BRACKET>%s)|(?<SEMICOLON>%s)|(?<STRING>%s)|(?<COMMENT>%s)"
					.formatted(KEYWORD_PATTERN, PAREN_PATTERN, BRACE_PATTERN, BRACKET_PATTERN, SEMICOLON_PATTERN, STRING_PATTERN, COMMENT_PATTERN));
	private static final Logger log = LoggerFactory.getLogger(JavaEditorSkin.class);

	private final CodeArea codeArea;
	private final ExecutorService executor = Executors.newSingleThreadExecutor();

	public JavaEditorSkin(Editor<String> editor) {
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

		VirtualizedScrollPane<CodeArea> scrollPane = new VirtualizedScrollPane<>(codeArea);
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
					matcher.group("KEYWORD") != null ? "keyword" :
							matcher.group("PAREN") != null ? "paren" :
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