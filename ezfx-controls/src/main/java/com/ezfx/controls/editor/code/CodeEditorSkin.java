package com.ezfx.controls.editor.code;

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
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CodeEditorSkin extends EditorSkinBase<EditorBase<String>, String> {
	private static final Logger log = LoggerFactory.getLogger(CodeEditorSkin.class);

	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private Subscription highlightingSubscription;

	private VirtualizedScrollPane<CodeArea> scrollPane;
	private CodeArea codeArea;

	public CodeEditorSkin(EditorBase<String> editor) {
		super(editor);
	}

	@Override
	public void install() {
		super.install();
		codeArea = new CodeArea();
		codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
		scrollPane = new VirtualizedScrollPane<>(codeArea);

		highlightingSubscription = codeArea.multiPlainChanges()
				.successionEnds(Duration.ofMillis(500))
				.retainLatestUntilLater(executor)
				.supplyTask(() -> {
					String text = codeArea.getText();
					Task<StyleSpans<Collection<String>>> task = new Task<>() {
						@Override
						protected StyleSpans<Collection<String>> call() {
							return computeHighlighting(text);
						}
					};
					executor.execute(task);
					return task;
				})
				.awaitLatest(codeArea.multiPlainChanges())
				.filterMap(t -> {
					if (t.isSuccess()) {
						return Optional.of(t.get());
					} else {
						log.warn(t.getFailure().getMessage(), t.getFailure());
						return Optional.empty();
					}
				})
				.subscribe(highlighting -> codeArea.setStyleSpans(0, highlighting));

		//Binding
		codeArea.replaceText(Optional.ofNullable(getValue()).orElse(""));
		valueProperty().addListener((_, _, text) -> {
			if (codeArea.getText().equals(text)) return;
			codeArea.replaceText(Optional.ofNullable(text).orElse(""));
		});
		codeArea.textProperty().addListener((_, _, text) -> {
			if (Objects.equals(getValue(), text)) return;
			setValue(text);
		});

		getChildren().setAll(scrollPane);
	}

	@Override
	public void dispose() {
		super.dispose();
		highlightingSubscription.unsubscribe();
		getChildren().remove(scrollPane);
	}

	/**
	 * This should be implemented by the extending classes for text highlighting.
	 */
	protected StyleSpans<Collection<String>> computeHighlighting(String text) {
		StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
		spansBuilder.add(Collections.singleton("none"), text.length());
		return spansBuilder.create();
	}
}