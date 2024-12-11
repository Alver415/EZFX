package com.ezfx.controls.editor.code;

import com.ezfx.base.utils.Resources;
import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.option.Option;
import com.ezfx.controls.editor.skin.EditorSkin;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ezfx.base.utils.EZFX.runFX;
import static com.ezfx.base.utils.EZFX.runOnVirtualThread;

public class CSSEditorSkin extends EditorSkin<Editor<String>, String> {
	private static final String STYLE_SHEET = Resources.css(CSSEditorSkin.class, "CSSEditorSkin.css");

	private static final Pattern CSS_SYNTAX = Pattern.compile(
			"(?<SELECTOR>[.#]?[a-zA-Z_][\\w\\-]*)|(?<PROPERTY>[a-zA-Z\\-]+)(?=\\s*:)|(?<VALUE>#[0-9a-fA-F]{3,6}|\\b[0-9.]+(px|em|%|rem)?\\b|\"[^\"]*\"|'.*?')|(?<COMMENT>/\\*.*?\\*/)",
			Pattern.DOTALL);

	private final VirtualizedScrollPane<CodeArea> scrollPane;
	private final CodeArea codeArea;

	public CSSEditorSkin(Editor<String> editor) {
		super(editor);
		codeArea = new CodeArea();
		codeArea.getStylesheets().add(STYLE_SHEET);
		codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
		codeArea.textProperty().addListener((_, _, newText) -> runOnVirtualThread(() -> {
			StyleSpans<Collection<String>> styleSpans = computeHighlighting(newText);
			runFX(() -> codeArea.setStyleSpans(0, styleSpans));
		}));

		codeArea.replaceText(Optional.ofNullable(editor.getValue()).orElse(""));
		editor.property().addListener((_, _, text) -> codeArea.replaceText(Optional.ofNullable(text).orElse("")));
		codeArea.textProperty().addListener((_, _, text) -> editor.property().setValue(text));


		scrollPane = new VirtualizedScrollPane<>(codeArea);
		getChildren().setAll(scrollPane);
	}

	private static StyleSpans<Collection<String>> computeHighlighting(String text) {
		Matcher matcher = CSS_SYNTAX.matcher(text);
		int lastKwEnd = 0;
		StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

		while (matcher.find()) {
			spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);

			if (matcher.group("SELECTOR") != null) {
				spansBuilder.add(Collections.singleton("selector"), matcher.end() - matcher.start());
			} else if (matcher.group("PROPERTY") != null) {
				spansBuilder.add(Collections.singleton("property"), matcher.end() - matcher.start());
			} else if (matcher.group("VALUE") != null) {
				spansBuilder.add(Collections.singleton("value"), matcher.end() - matcher.start());
			} else if (matcher.group("COMMENT") != null) {
				spansBuilder.add(Collections.singleton("comment"), matcher.end() - matcher.start());
			}
			lastKwEnd = matcher.end();
		}
		spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
		return spansBuilder.create();
	}
}
