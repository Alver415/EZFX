package com.ezfx.app.console;

import com.ezfx.base.utils.EZFX;
import com.ezfx.controls.utils.TableViews;
import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import org.fxmisc.easybind.EasyBind;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.management.ExecutionEvent;
import org.graalvm.polyglot.management.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class BindingsView extends Control {

	private static final Logger log = LoggerFactory.getLogger(BindingsView.class);

	public BindingsView(ManagedContext managedContext, Value binding) {
		setManagedContext(managedContext);
		setBinding(binding);

		//TODO: Determine if the order of operations is problematic here.
		// We create a new execution listener before closing the old one.
		EasyBind.monadic(managedContextProperty()).map(this::attachListener)
				.subscribe((oldListener, _) -> Optional.ofNullable(oldListener).ifPresent(ExecutionListener::close));
	}

	/**
	 * This sets up a listener against the Graal Polyglot Context so that whenever anything is executed,
	 * we pull the latest bindings and update the UI with their values.
	 */
	private ExecutionListener attachListener(ManagedContext managedContext) {
		Consumer<ExecutionEvent> listener = _ -> {
			try {
				bindings.setAll(Stream.of(binding.getValue())
						.flatMap(binding -> binding.getMemberKeys().stream()
								.map(key -> new ContextBinding(key, binding.getMember(key))))
						.collect(EZFX.toObservableArrayList()));
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		};
		//Run it once
		listener.accept(null);
		return managedContext.onReturn(listener);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new DefaultSkin(this);
	}

	private final Property<ManagedContext> managedContext = new SimpleObjectProperty<>(this, "managedContext");

	public Property<ManagedContext> managedContextProperty() {
		return this.managedContext;
	}

	public ManagedContext getManagedContext() {
		return this.managedContextProperty().getValue();
	}

	public void setManagedContext(ManagedContext value) {
		this.managedContextProperty().setValue(value);
	}

	private final Property<Value> binding = new SimpleObjectProperty<>(this, "binding");

	public Property<Value> bindingProperty() {
		return this.binding;
	}

	public Value getBinding() {
		return this.bindingProperty().getValue();
	}

	public void setBinding(Value value) {
		this.bindingProperty().setValue(value);
	}

	private final ListProperty<ContextBinding> bindings = new SimpleListProperty<>(this, "bindings", FXCollections.observableArrayList());

	public ListProperty<ContextBinding> bindingsProperty() {
		return this.bindings;
	}

	public ObservableList<ContextBinding> getBindings() {
		return this.bindingsProperty().getValue();
	}

	public void setBindings(ObservableList<ContextBinding> value) {
		this.bindingsProperty().setValue(value);
	}


	private static class DefaultSkin extends SkinBase<BindingsView> {

		public DefaultSkin(BindingsView control) {
			super(control);
			getChildren().setAll(TableViews.create(control.bindings, "key", "value"));
		}
	}
}
