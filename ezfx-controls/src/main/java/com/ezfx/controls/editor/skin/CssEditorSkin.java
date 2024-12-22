package com.ezfx.controls.editor.skin;

import com.ezfx.controls.editor.impl.javafx.CssEditor;
import com.ezfx.controls.editor.impl.standard.StringEditor;
import com.ezfx.controls.utils.LazyTreeItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.*;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
public class CssEditorSkin extends EditorSkin<CssEditor, Styleable> {

	private TreeTableView<CssMetaData<? extends Styleable, ?>> treeTableView;

	public CssEditorSkin(CssEditor editor) {
		super(editor);

		treeTableView = new TreeTableView<>();
		treeTableView.setShowRoot(false);
		treeTableView.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

		TreeTableColumn<CssMetaData<? extends Styleable, ?>, String> keyColumn = new TreeTableColumn<>("Key");
		keyColumn.setCellValueFactory(cdf -> cdf.getValue().valueProperty().map(CssMetaData::getProperty));

		TreeTableColumn<CssMetaData<? extends Styleable, ?>, StringEditor> valueColumn = new TreeTableColumn<>("Value");
		valueColumn.setCellValueFactory(cdf -> cdf.getValue().valueProperty().map(this::buildEditor));

		TreeTableColumn<CssMetaData<? extends Styleable, ?>, StyleOrigin> originColumn = new TreeTableColumn<>("Origin");
		originColumn.setCellValueFactory(cdf -> cdf.getValue().valueProperty()
				.map(CssEditorSkin::getCssMetaData)
				//TODO: Make sure this works with observable property(), instead of calling getValue()
				.map(cssMetaData -> cssMetaData.getStyleableProperty(property().getValue()))
				.map(StyleableProperty::getStyleOrigin));


		treeTableView.getColumns().add(keyColumn);
		treeTableView.getColumns().add(valueColumn);
		treeTableView.getColumns().add(originColumn);


		CssMetaDataTreeItem root = new CssMetaDataTreeItem(null, _ -> FXCollections.observableArrayList(property().getValue().getCssMetaData()));
		treeTableView.setRoot(root);

		getChildren().setAll(treeTableView);
	}

	private <S extends Styleable, T> StringEditor buildEditor(CssMetaData<S, T> cssMetaData) {
		S styleable = (S) editor().valueProperty().getValue();
		StyleableProperty<T> property = cssMetaData.getStyleableProperty(styleable);
		if (property == null) return null;

		StringEditor stringEditor = new StringEditor();
		stringEditor.setValue(String.valueOf(property.getValue()));
		stringEditor.setDisable(!cssMetaData.isSettable(styleable));
		stringEditor.valueProperty().addListener((_, _, value) -> {
			try {
				String css = "*{%s: %s;}".formatted(cssMetaData.getProperty(), value);
				StyleConverter<?, T> converter = cssMetaData.getConverter();
				Stream.of(parser)
						.map(parser -> parser.parse(css))
						.map(Stylesheet::getRules)
						.flatMap(Collection::stream)
						.map(Rule::getDeclarations)
						.flatMap(Collection::stream)
						.map(Declaration::getParsedValue)
						.map(parsedValue -> (T) converter.convert(parsedValue, null))
						.reduce(CssEditorSkin::ensureExactlyOne)
						.ifPresent(converted -> property.applyStyle(StyleOrigin.USER, converted));
			} catch (Exception e) {
				property.applyStyle(StyleOrigin.USER, null);
			}
		});
		return stringEditor;
	}

	private static <T> T ensureExactlyOne(T a, T b) {
		throw new RuntimeException();
	}

	CssParser parser = new CssParser();

	private static class ParsedValueImpl<V, T> extends ParsedValue<V, T> {
		protected ParsedValueImpl(V value, StyleConverter<V, T> converter) {
			super(value, converter);
		}
	}

	private static <T extends Styleable, V> CssMetaData<T, V> getCssMetaData(CssMetaData<? extends Styleable, ?> v) {
		return (CssMetaData<T, V>) v;
	}

	private static class CssMetaDataTreeItem extends LazyTreeItem<CssMetaData<? extends Styleable, ?>> {

		public CssMetaDataTreeItem(CssMetaData<? extends Styleable, ?> child) {
			this(child, cssMetaData -> {
				List<CssMetaData<? extends Styleable, ?>> subProperties = cssMetaData.getSubProperties();
				return FXCollections.observableArrayList(subProperties == null ? List.of() : subProperties);
			});
		}

		public CssMetaDataTreeItem(CssMetaData<? extends Styleable, ?> child, Function<CssMetaData<?, ?>, ObservableList<CssMetaData<? extends Styleable, ?>>> childrenProvider) {
			super(child, childrenProvider);
		}

		@Override
		protected LazyTreeItem<CssMetaData<?, ?>> create(CssMetaData<? extends Styleable, ?> child, Function<CssMetaData<?, ?>, ObservableList<CssMetaData<? extends Styleable, ?>>> childrenProvider) {
			return new CssMetaDataTreeItem(child);
		}
	}
}