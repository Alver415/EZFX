package com.ezfx.controls.editor.skin;

import com.ezfx.base.linkable.LinkableProperty;
import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.EditorView;
import javafx.beans.binding.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LinkableEditorSkin<T, C extends Editor<T>> extends EditorView.DefaultSkin<T, C> {

	private static final Insets INSETS = new Insets(2);
	private static final ObjectProperty<LinkableProperty<?>> selected = new SimpleObjectProperty<>();
	private static final ObservableValue<Boolean> isLinkStateActive = selected.map(Objects::nonNull).orElse(false);

	public LinkableEditorSkin(EditorView<T, C> editor) {
		super(editor);
		Label name = new Label();
		name.textProperty().bind(editor.nameProperty());

		Editor<T> control = editor.getEditor();
		LinkableProperty<T> linkable = LinkableProperty.wrap(control.valueProperty());

		ReadOnlyObjectProperty<LinkableProperty<T>> linkedTo = linkable.linkedToProperty();
		ReadOnlyListProperty<LinkableProperty<T>> linkedBy = linkable.linkedByProperty();

		Button linkButton = new Button();
		ObjectBinding<Color> color = Bindings.createObjectBinding(() -> {
			boolean isLinking = isLinkStateActive.getValue();
			if (isLinking) {
				if (linkable == selected.get()) {
					return Color.LIGHTGRAY;
				} else {
					return Color.GREEN;
				}
			} else {
				if (linkable.isLinkedTo()) {
					return Color.BLUE;
				} else {
					return Color.LIGHTGRAY;
				}
			}
		}, linkable.isLinkedToProperty(), isLinkStateActive);
		ObservableValue<Background> background = color.map(Color::desaturate).map(Background::fill);
		linkButton.backgroundProperty().bind(background);


		BooleanBinding disabled = Bindings.createBooleanBinding(() -> linkable == selected.get(), selected);
		linkButton.disableProperty().bind(disabled);


		ObservableValue<String> linkedToText = linkable.lastLinkedToProperty()
				.map(LinkableProperty::getName)
				.map("Linked to: %s"::formatted);

		ObservableValue<String> linkedDependenciesText = linkable.dependenciesProperty()
				.map(l -> l.isEmpty() ? null : l.stream()
						.map(LinkableProperty::getName)
						.collect(Collectors.joining("\n - ", "\n - ", "")))
				.map("Linked via path: %s"::formatted);

		ObservableValue<String> linkedByText = linkable.linkedByProperty()
				.map(l -> l.isEmpty() ? null : l)
				.map(l -> l.stream().map(LinkableProperty::getName).collect(Collectors.joining(",")))
				.map("Linked by: [%s]"::formatted);

		StringExpression reminder = Bindings.createStringBinding(() -> Stream.of(linkedToText, linkedByText)
						.map(ObservableValue::getValue)
						.filter(Objects::nonNull)
						.collect(Collectors.joining(" | ")),
				linkedToText, linkedByText);

		Text text = new Text();
		text.textProperty().bind(reminder);

		Tooltip tooltip = new Tooltip();
		tooltip.textProperty().bind(linkedDependenciesText);
		linkButton.setTooltip(tooltip);

		linkButton.setOnAction(a -> {
			LinkableProperty source = selected.get();
			LinkableProperty target = linkable;
			Boolean isLinking = isLinkStateActive.getValue();
			if (!isLinking) {
				if (target.isLinkedTo()) {
					target.unlink();
				} else {
					selected.set(target);
				}
			} else {
				source.link(target);
				selected.set(null);
			}
		});
		DoubleBinding size = name.heightProperty().subtract(2);
		linkButton.maxWidthProperty().bind(size);
		linkButton.maxHeightProperty().bind(size);
		linkButton.minWidthProperty().bind(size);
		linkButton.minHeightProperty().bind(size);
		HBox.setMargin(linkButton, INSETS);

		Button biLink = new Button();
		biLink.setOnAction(a -> selected.set(null));
		biLink.maxWidthProperty().bind(size);
		biLink.maxHeightProperty().bind(size);
		biLink.minWidthProperty().bind(size);
		biLink.minHeightProperty().bind(size);
		HBox.setMargin(biLink, INSETS);
		HBox buttons = new HBox(text, linkButton, biLink);

		BorderPane borderPane = new BorderPane();
		borderPane.setLeft(name);
		borderPane.setRight(buttons);

		HBox hBox = new HBox(control.getNode());
		HBox.setHgrow(control.getNode(), Priority.ALWAYS);
		VBox vBox = new VBox(borderPane, hBox);
		getChildren().setAll(vBox);


		hBox.disableProperty().bind(linkable.isLinkedToProperty());
	}
}
