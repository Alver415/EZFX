package com.ezfx.controls.editor.code;

import com.ezfx.base.utils.Resources;
import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.skin.EditorSkin;
import org.fxmisc.flowless.VirtualizedScrollPane;

import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ezfx.base.utils.EZFX.runFX;
import static com.ezfx.base.utils.EZFX.runOnVirtualThread;

public class XMLEditorSkin extends EditorSkin<Editor<String>, String> {
	private static final String STYLE_SHEET = Resources.css(XMLEditorSkin.class, "XMLEditorSkin.css");

	private static final Pattern XML_TAG = Pattern.compile(
			"(?<ELEMENT>(</?\\h*)(\\w+)([^<>]*)(\\h*/?>))|" +
					"(?<IMPORT>(<\\?/?\\h*)(\\w+)([^<>]*)(\\h*\\?>))|" +
					"(?<COMMENT><!--(.|\\v)+?-->)");

	private static final Pattern ATTRIBUTES = Pattern.compile("(\\w+\\h*)(=)(\\h*\"[^\"]+\")");

	private static final int GROUP_OPEN_BRACKET = 2;
	private static final int GROUP_ELEMENT_NAME = 3;
	private static final int GROUP_ATTRIBUTES_SECTION = 4;
	private static final int GROUP_CLOSE_BRACKET = 5;
	private static final int GROUP_ATTRIBUTE_NAME = 1;
	private static final int GROUP_EQUAL_SYMBOL = 2;
	private static final int GROUP_ATTRIBUTE_VALUE = 3;

	private final VirtualizedScrollPane<CodeArea> scrollPane;
	private final CodeArea codeArea;

	public XMLEditorSkin(Editor<String> editor) {
		super(editor);
		codeArea = new CodeArea();
		codeArea.getStylesheets().add(STYLE_SHEET);
		codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
		codeArea.textProperty().addListener((_, _, newText) -> runOnVirtualThread(() -> {
			StyleSpans<Collection<String>> styleSpans = computeHighlighting(newText);
			runFX(() -> codeArea.setStyleSpans(0, styleSpans));
		}));

		scrollPane = new VirtualizedScrollPane<>(codeArea);
		getChildren().setAll(scrollPane);
	}

	private static StyleSpans<Collection<String>> computeHighlighting(String text) {
		Matcher matcher = XML_TAG.matcher(text);
		int lastKwEnd = 0;
		StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
		while (matcher.find()) {

			spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
			if (matcher.group("IMPORT") != null) {
				spansBuilder.add(Collections.singleton("import"), matcher.end() - matcher.start());
			} else if (matcher.group("COMMENT") != null) {
				spansBuilder.add(Collections.singleton("comment"), matcher.end() - matcher.start());
			} else {
				if (matcher.group("ELEMENT") != null) {
					String attributesText = matcher.group(GROUP_ATTRIBUTES_SECTION);

					spansBuilder.add(
							Collections.singleton("tagmark"),
							matcher.end(GROUP_OPEN_BRACKET) - matcher.start(GROUP_OPEN_BRACKET));
					spansBuilder.add(
							Collections.singleton("anytag"),
							matcher.end(GROUP_ELEMENT_NAME) - matcher.end(GROUP_OPEN_BRACKET));

					if (!attributesText.isEmpty()) {

						lastKwEnd = 0;

						Matcher amatcher = ATTRIBUTES.matcher(attributesText);
						while (amatcher.find()) {
							spansBuilder.add(Collections.emptyList(), amatcher.start() - lastKwEnd);
							spansBuilder.add(
									Collections.singleton("attribute"),
									amatcher.end(GROUP_ATTRIBUTE_NAME) - amatcher.start(GROUP_ATTRIBUTE_NAME));
							spansBuilder.add(
									Collections.singleton("tagmark"),
									amatcher.end(GROUP_EQUAL_SYMBOL) - amatcher.end(GROUP_ATTRIBUTE_NAME));
							spansBuilder.add(
									Collections.singleton("avalue"),
									amatcher.end(GROUP_ATTRIBUTE_VALUE) - amatcher.end(GROUP_EQUAL_SYMBOL));
							lastKwEnd = amatcher.end();
						}
						if (attributesText.length() > lastKwEnd)
							spansBuilder.add(Collections.emptyList(), attributesText.length() - lastKwEnd);
					}

					lastKwEnd = matcher.end(GROUP_ATTRIBUTES_SECTION);

					spansBuilder.add(Collections.singleton("tagmark"), matcher.end(GROUP_CLOSE_BRACKET) - lastKwEnd);
				}
			}
			lastKwEnd = matcher.end();
		}
		spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
		return spansBuilder.create();
	}
}