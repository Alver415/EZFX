package com.ezfx.controls.editor.code;

import com.ezfx.base.utils.Resources;
import com.ezfx.controls.editor.EditorBase;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CSSEditorSkin extends CodeEditorSkin {
	private static final String STYLE_SHEET = Resources.css(CSSEditorSkin.class, "CSSEditorSkin.css");

	private static final Pattern CSS_SYNTAX = Pattern.compile("""
			(?<SELECTOR>[.#]?[a-zA-Z_][\\w\\-]*)|(?<PROPERTY>[a-zA-Z\\-]+)(?=\\s*:)|(?<VALUE>#[0-9a-fA-F]{3,6}|\\b[0-9.]+(px|em|%|rem)?\\b|\"[^\"]*\"|'.*?')|(?<COMMENT>/\\*.*?\\*/)
			""", Pattern.DOTALL);

	public CSSEditorSkin(EditorBase<String> editor) {
		super(editor);
	}

	@Override
	public StyleSpans<Collection<String>> computeHighlighting(String text) {
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

	@Override
	public void install() {
		super.install();
		getStylesheets().add(STYLE_SHEET);
	}

	@Override
	public void dispose() {
		super.dispose();
		getStylesheets().remove(STYLE_SHEET);
	}
}
