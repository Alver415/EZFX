package com.ezfx.controls.editor;

import com.ezfx.controls.editor.impl.javafx.NodeEditor;
import javafx.application.Platform;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DevTest {

	@Test
	public void test() throws Exception {


Property<Object> property = new SimpleObjectProperty<>(new Object());
BiFunction<String, Object, Object> function = (string, value) -> {
	System.out.printf("%s", string);
	return value;
};
property.map(value -> function.apply("\nA", value))
		.map(value -> function.apply("B", value))
		.map(value -> function.apply("C", value))
		.map(value -> function.apply("D", value))
		.map(value -> function.apply("E", value))
		.addListener((_, _, _) -> {});

System.out.println();



		//Var<?> property = Var.newSimpleVar("A");

		//if property is null here, then nothing is executed.
		//if property has a value here, you get a series of cascading executions of the print statements
//		property.setValue("A");

		//If you set breakpoints on each lambda expression you'll see it goes
		// 1, 2, 3, 4, 5
		// 1, 2, 3, 4
		// 1, 2, 3
		// 1, 2,
		// 1,

//		first.map(oldValue -> {
//					int newValue = oldValue + 1;
//					System.out.println(oldValue + " -> " + newValue);
//					return newValue;
//				})
//				.map(oldValue -> {
//					int newValue = oldValue + 1;
//					System.out.println(oldValue + " -> " + newValue);
//					return newValue;
//				})
//				.subscribe(value -> System.out.println("Value: " + value));

//		MappedBinding<Integer, Integer> map1 = map(first, value -> print("1", value));
//		MappedBinding<Integer, Integer> map2 = map(map1, value -> print("2", value));
//		MappedBinding<Integer, Integer> map3 = map(map2, value -> print("2", value));
//
//		map3.subscribe(value -> System.out.println("Value: " + value));


//		map.subscribe(value -> System.out.println("FINAL: " + value));

///		System.out.println(map.getValue());
		//map.subscribe(a -> System.out.println("Value: " + a));
		//map.subscribe(() -> System.out.println("Value: " ));
//		SimpleObjectProperty<Object> second = new SimpleObjectProperty<>();
//		second.bind(map);
//
//		var text = new SimpleStringProperty("abcd");
//		ObservableValue<String> upperCase = text.map(String::toUpperCase);
//		text.set("xyz");
//
//		upperCase.subscribe(v -> System.out.println(v));


	}


	@Test
	public void fx() {
		Platform.startup(() -> {
		});

		NodeEditor editor = new NodeEditor();
		editor.setValue(new StackPane());
	}


	public static <S, T> MappedBinding<S, T> map(ObservableValue<S> source, Function<? super S, ? extends T> mapper) {
		return new MappedBinding<>(source, mapper);
	}

	public static class MappedBinding<S, T> extends ObjectBinding<T> {

		private final ObservableValue<S> source;
		private final Function<? super S, ? extends T> mapper;

		public MappedBinding(ObservableValue<S> source, Function<? super S, ? extends T> mapper) {
			this.source = Objects.requireNonNull(source, "source cannot be null");
			this.mapper = Objects.requireNonNull(mapper, "mapper cannot be null");
		}

		@Override
		protected T computeValue() {
			S value = source.getValue();
			return value == null ? null : mapper.apply(value);
		}
	}

}
