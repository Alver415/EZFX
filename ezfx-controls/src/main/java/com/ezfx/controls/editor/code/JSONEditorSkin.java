package com.ezfx.controls.editor.code;

import com.ezfx.base.utils.Resources;
import com.ezfx.controls.editor.EditorBase;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONEditorSkin extends CodeEditorSkin {

	private static final String STYLE_SHEET = Resources.css(JSONEditorSkin.class, "JSONEditorSkin.css");

	private static final String BRACE_PATTERN = "[{}]";
	private static final String BRACKET_PATTERN = "[\\[\\]]";
	private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
	private static final Pattern PATTERN = Pattern.compile("|(?<BRACE>%s)|(?<BRACKET>%s)|(?<STRING>%s)"
			.formatted(BRACE_PATTERN, BRACKET_PATTERN, STRING_PATTERN));

	public JSONEditorSkin(EditorBase<String> editor) {
		super(editor, STYLE_SHEET);
	}

	@Override
	protected StyleSpans<Collection<String>> computeHighlighting(String text) {
		Matcher matcher = PATTERN.matcher(text);
		int lastKwEnd = 0;
		StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
		while (matcher.find()) {
			String styleClass =
					matcher.group("BRACE") != null ? "brace" :
							matcher.group("BRACKET") != null ? "bracket" :
									matcher.group("STRING") != null ? "string" :
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