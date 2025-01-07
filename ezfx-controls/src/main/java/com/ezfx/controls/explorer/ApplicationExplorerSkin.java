package com.ezfx.controls.explorer;

import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.PropertiesEditor;
import javafx.application.Application;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.SkinBase;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import org.reactfx.EventStreams;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.ezfx.base.utils.EZFX.printTime;
import static com.ezfx.controls.editor.factory.IntrospectingEditorFactory.DEFAULT_FACTORY;

public class ApplicationExplorerSkin extends SkinBase<ApplicationExplorer> {

	private final ApplicationTreeView<Object, Object> treeView;

	private final Map<Object, Editor<?>> cache = new ConcurrentHashMap<>();

	protected ApplicationExplorerSkin(ApplicationExplorer control) {
		super(control);
		treeView = new ApplicationTreeView<>();
		Application application = control.getApplication();
		treeView.setRoot(new GenericTreeItem(application));

		StackPane left = new StackPane(treeView);
		StackPane right = new StackPane();

		ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
		PropertiesEditor<Object> EMPTY = new PropertiesEditor<>();
		EventStreams.valuesOf(treeView.selectionModelProperty()
						.flatMap(SelectionModel::selectedItemProperty))
				.threadBridgeFromFx(executor)
				.filter(Objects::nonNull)
				.map(selected -> {
					Object value = selected.getValue().getValue();
					return cache.computeIfAbsent(value, _ -> DEFAULT_FACTORY.buildEditor(value).orElseGet(Editor::new));
				})
				.threadBridgeToFx(executor)
				.subscribe(editor -> printTime(() -> right.getChildren().setAll(editor)));


		getChildren().setAll(new SplitPane(left, right));
	}
}