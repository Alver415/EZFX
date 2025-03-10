package com.ezfx.controls.editor.introspective;

import com.ezfx.controls.editor.EditorBase;
import com.ezfx.controls.editor.factory.EditorFactory;
import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.option.*;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Skin;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionGroup;
import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.monadic.MonadicBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.ezfx.base.utils.EZFX.toObservableArrayList;
import static com.ezfx.controls.editor.factory.IntrospectingEditorFactory.DEFAULT_FACTORY;
import static com.ezfx.controls.editor.introspective.EZFXIntrospector.DEFAULT_INTROSPECTOR;
import static java.lang.reflect.Modifier.*;

@SuppressWarnings("unchecked")
public class IntrospectingEditor<T> extends ObjectEditor<T> implements DelegatingEditor<T> {

	private static final Logger log = LoggerFactory.getLogger(IntrospectingEditor.class);

	private final Binding<List<FieldOption<T>>> fieldOptions;
	private final Binding<List<MethodOption<T>>> methodOptions;
	private final Binding<List<? extends ConstructorOption<? extends T>>> constructorOptions;

	public IntrospectingEditor(Type type) {
		this(new SimpleObjectProperty<>(), type);
	}

	public IntrospectingEditor(
			Property<T> property,
			Type type) {
		this(property, type, DEFAULT_INTROSPECTOR, DEFAULT_FACTORY);
	}

	public IntrospectingEditor(
			Property<T> property,
			Type type,
			Introspector introspector,
			EditorFactory factory) {
		super(property);
		setType(type);
		setIntrospector(introspector);
		setEditorFactory(factory);

		fieldOptions = EasyBind.combine(introspectorProperty(), typeProperty(), Introspector::getFields)
				.map(fields -> fields.stream()
						.sorted(Comparator.comparing(Field::getName))
						.filter(field -> checkModifiers(field.getModifiers(), PUBLIC, STATIC, FINAL))
						.filter(field -> field.getType().isAssignableFrom((Class<T>) type))
						.map(this::build)
						.toList());

		methodOptions = EasyBind.combine(introspectorProperty(), typeProperty(), Introspector::getMethods)
				.map(methods -> methods.stream()
						.sorted(Comparator.comparing(Method::getParameterCount))
						.filter(method -> checkModifiers(method.getModifiers(), PUBLIC, STATIC))
						.filter(method -> method.getReturnType().isAssignableFrom((Class<T>) type))
						.map(this::build)
						.toList());

		MonadicBinding<List<? extends Constructor<T>>> combine =
				EasyBind.combine(introspectorProperty(), typeProperty(), Introspector::getConstructors);
		constructorOptions = combine.map(constructors -> constructors.stream()
				.sorted(Comparator.comparing(Constructor::getParameterCount))
				.filter(constructor -> checkModifiers(constructor.getModifiers(), PUBLIC))
				.filter(constructor -> ((Class<T>)type).isAssignableFrom(constructor.getDeclaringClass()))
				.map(this::build)
				.toList());

		ObjectBinding<ObservableList<ValueOption<T>>> valueOptions = Bindings.createObjectBinding(() -> {
			ObservableList<ValueOption<T>> list = FXCollections.observableArrayList();
			List<FieldOption<T>> value = fieldOptions.getValue();
			list.addAll(value);
			return list;
		}, fieldOptions);

		ObjectBinding<ObservableList<BuilderOption<T>>> builderOptions = Bindings.createObjectBinding(() -> {
			ObservableList<BuilderOption<T>> list = FXCollections.observableArrayList();
			list.addAll(methodOptions.getValue());
			list.addAll((Collection<? extends BuilderOption<T>>) constructorOptions.getValue());
			return list;
		}, methodOptions, constructorOptions);


		ActionGroup setValue = new ActionGroup("Value");
		ObservableValue<ObservableList<Action>> values = valueOptions.map(list -> list.stream()
				.map(option -> new Action(option.getName(), _ -> property.setValue(option.getValue())))
				.collect(toObservableArrayList()));
		values.subscribe(v -> setValue.getActions().setAll(v));

		ActionGroup setBuilder = new ActionGroup("Builders");
		ObservableValue<ObservableList<Action>> builders = builderOptions.map(list -> list.stream()
				.map(option -> new Action(option.getName(), _ -> {
					if (option instanceof ConstructorOption<T> constructorOption) {
						if (constructorOption.getConstructor().getParameterCount() == 0) {
							property.setValue((T) option.buildEditor().getValue());
							return;
						}
					}
					EditorDialog<T> dialog = new EditorDialog<>(property, option.buildEditor());
					dialog.setHeaderText(option.getType().getTypeName());
					dialog.show();
				}))
				.collect(toObservableArrayList()));
		builders.subscribe(v -> setBuilder.getActions().setAll(v));
		ObservableValue<ObservableList<Action>> items = Bindings.createObjectBinding(() -> {
			ObservableList<Action> actionGroups = FXCollections.observableArrayList();
			if (!setValue.getActions().isEmpty()){
				actionGroups.add(setValue);
			}
			if (!setBuilder.getActions().isEmpty()){
				actionGroups.add(setBuilder);
			}
			return actionGroups;
		}, setValue.getActions(), setBuilder.getActions());

		ActionGroup setValueGroup = new ActionGroup("Set Value");
		items.subscribe(v -> setValueGroup.getActions().setAll(v));

		actionsProperty().add(setValueGroup);

		knownValuesProperty().putAll((Map<String, T>) EasyBind.combine(introspectorProperty(), typeProperty(), Introspector::getFields)
				.map(fields -> fields.stream()
						.sorted(Comparator.comparing(Field::getName))
						.filter(field -> checkModifiers(field.getModifiers(), PUBLIC, STATIC, FINAL))
						.filter(field -> field.getType().isAssignableFrom((Class<T>) type))
						.collect(Collectors.toMap(
								Field::getName,
								IntrospectingEditor::getStaticFieldValue,
								(existing, _) -> existing,
								() -> FXCollections.observableMap(new TreeMap<>())))).get());

		property.subscribe(value -> {
			if (value == null) {
				setDelegate(null);
			} else if (!getIntrospector().getPropertyInfo(value.getClass()).isEmpty()) {
				setDelegate(new IntrospectingPropertiesEditor<>(value));
			} else if (!constructorOptions.getValue().isEmpty()){
				setDelegate((EditorBase<T>) constructorOptions.getValue().getFirst().buildEditor());
			}
		});

	}

	private FieldOption<T> build(Field field) {
		String typeName = field.getType().getSimpleName();
		String fieldName = field.getName();
		String name = "%s.%s".formatted(typeName, fieldName);
		return new FieldOption<>(name, getType(), field);
	}

	private MethodOption<T> build(Method method) {
		Class<?> returnType = method.getReturnType();
		String typeName = returnType.getSimpleName();
		String methodName = method.getName();
		String parameterTypeNames = Arrays.stream(method.getParameters())
				.map(getIntrospector()::getParameterTypeName)
				.collect(Collectors.joining(", "));
		String name = "%s.%s(%s)".formatted(typeName, methodName, parameterTypeNames);
		return new MethodOption<>(name, getType(), method);
	}

	private <R extends T> ConstructorOption<R> build(Constructor<R> constructor) {
		Class<R> type = constructor.getDeclaringClass();
		String typeName = type.getSimpleName();
		String parameterTypeNames = Arrays.stream(constructor.getParameters())
				.map(getIntrospector()::getParameterTypeName)
				.collect(Collectors.joining(", "));
		String name = "new %s(%s)".formatted(typeName, parameterTypeNames);
		return new ConstructorOption<>(name, type, constructor);
	}


	@Override
	protected Skin<?> createDefaultSkin() {
		return new DelegatingEditorSkin<>(this);
	}

	// endregion Introspection


	private final ObjectProperty<Type> type = new SimpleObjectProperty<>(this, "type");

	public ObjectProperty<Type> typeProperty() {
		return this.type;
	}

	public Type getType() {
		return this.typeProperty().get();
	}

	public void setType(Type value) {
		this.typeProperty().set(value);
	}

	private final ObjectProperty<Introspector> introspector = new SimpleObjectProperty<>(this, "introspector");

	public ObjectProperty<Introspector> introspectorProperty() {
		return this.introspector;
	}

	public Introspector getIntrospector() {
		return this.introspectorProperty().get();
	}

	public void setIntrospector(Introspector value) {
		this.introspectorProperty().set(value);
	}

	private final ObjectProperty<EditorFactory> editorFactory = new SimpleObjectProperty<>(this, "editorFactory");

	public ObjectProperty<EditorFactory> editorFactoryProperty() {
		return this.editorFactory;
	}

	public EditorFactory getEditorFactory() {
		return this.editorFactoryProperty().get();
	}

	public void setEditorFactory(EditorFactory value) {
		this.editorFactoryProperty().set(value);
	}


	private static <T> T getStaticFieldValue(Field field) {
		try {
			return (T) field.get(null);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean checkModifiers(Member member, int... modifiers) {
		return checkModifiers(member.getModifiers(), modifiers);
	}

	public boolean checkModifiers(int modifier, int... modifiers) {
		return Arrays.stream(modifiers)
				.mapToObj(m -> (modifier & m) != 0)
				.reduce((a, b) -> a && b)
				.orElse(true);
	}

	private final Property<EditorBase<T>> delegate = new SimpleObjectProperty<>(this, "delegate");

	@Override
	public Property<EditorBase<T>> delegateProperty() {
		return this.delegate;
	}

	public EditorBase<T> getDelegate() {
		return this.delegateProperty().getValue();
	}

	public void setDelegate(EditorBase<T> value) {
		this.delegateProperty().setValue(value);
	}


}
