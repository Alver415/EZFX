package com.ezfx.controls.editor.introspective;

import com.ezfx.base.observable.ObservableObjectArrayImpl;
import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.EditorBase;
import com.ezfx.controls.editor.ListEditor;
import com.ezfx.controls.editor.MultiEditor;
import com.ezfx.controls.editor.skin.MultiEditorSkin;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Skin;
import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.monadic.MonadicBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

import static com.ezfx.base.utils.FXCollectors.toObservableArrayList;

@SuppressWarnings("unchecked")
public class FunctionParameterEditor<T> extends IntrospectingEditor<T> implements MultiEditor<T> {

	private static final Logger log = LoggerFactory.getLogger(FunctionParameterEditor.class);
	private final ObjectBinding<T> valueBinding;

	public FunctionParameterEditor(Class<T> type, Parameter[] parameters, Function<Object[], T> function) {
		this(new SimpleObjectProperty<>(), type, parameters, function);
	}
	public FunctionParameterEditor(Property<T> property, Class<T> type, Parameter[] parameters, Function<Object[], T> function) {
		super(property, type);

		setEditors(Arrays.stream(parameters)
				.map(this::buildEditor)
				.collect(toObservableArrayList()));
		editorsProperty().subscribe(editors ->
				getChildren().setAll(editors.stream().map(Editor::getNode).toList()));

		MonadicBinding<T> defaultValue = EasyBind.combine(introspectorProperty(), typeProperty(), Introspector::getDefaultValueForType);

		Property<?>[] properties = getEditors().stream().map(Editor::valueProperty).toArray(Property[]::new);
		valueBinding = Bindings.createObjectBinding(() -> {
			Object[] arguments = Arrays.stream(properties).map(Property::getValue)
					.map(FunctionParameterEditor::handleArrays)
					.toArray();
			try {
				return function.apply(arguments);
			} catch (Exception e) {
				log.warn("Failed to build value of type: {}", type.getSimpleName(), e);
				return defaultValue.get();
			}
		}, properties);
		valueBinding.addListener((_, _, value) -> property.setValue(value));

	}


	private static Object handleArrays(Object value) {
		return value instanceof ObservableObjectArrayImpl<?> array ? toArray(array) : value;
	}

	private static <T> T[] toArray(ObservableObjectArrayImpl<T> array) {
		T[] newArray = (T[]) Array.newInstance(array.getComponentType(), array.size());
		array.copyTo(0, newArray, 0, array.size());
		return newArray;
	}

	private <R> Editor<R> buildEditor(Parameter parameter) {
		Class<R> type = (Class<R>) parameter.getType();
		String parameterName = getIntrospector().getParameterName(parameter);
		String parameterType = getIntrospector().getParameterTypeName(parameter);
		String name = "%s (%s)".formatted(parameterName, parameterType);
		R value = getIntrospector().getDefaultValueForType(type);
		Property<R> property = new SimpleObjectProperty<>(this, name, value);
		Editor<R> editor = getEditorFactory()
				.buildEditor(type, property)
				.orElseGet(EditorBase::new);

		//TODO: Cleanup
		handleList(parameter, editor);

		return editor;
	}

	private static <O, R> void handleList(Parameter parameter, Editor<R> editor) {
		//noinspection unchecked
		Optional.ofNullable(editor)
				.filter(e -> e instanceof ListEditor<?>)
				.map(e -> ((ListEditor<O>) e))
				.ifPresent(listEditor -> {
					Optional.ofNullable(parameter).stream()
							.map(Parameter::getParameterizedType)
							.filter(param -> param instanceof ParameterizedType)
							.map(e -> (ParameterizedType) e)
							.map(ParameterizedType::getActualTypeArguments)
							.flatMap(Arrays::stream)
							.filter(type -> type instanceof Class<?>)
							.map(type -> (Class<O>) type)
							.findFirst()
							.ifPresent(listEditor::setGenericType);
				});
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new MultiEditorSkin.VerticalEditorSkin<>(this);
	}


	private final ListProperty<Editor<?>> editors = new SimpleListProperty<>(this, "editors", FXCollections.observableArrayList());

	public ListProperty<Editor<?>> editorsProperty() {
		return this.editors;
	}

	public ObservableList<Editor<?>> getEditors() {
		return this.editorsProperty().get();
	}

	public void setEditors(ObservableList<Editor<?>> value) {
		this.editorsProperty().set(value);
	}
}
