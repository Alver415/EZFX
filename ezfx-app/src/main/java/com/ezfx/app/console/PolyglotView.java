package com.ezfx.app.console;

import com.ezfx.base.io.IOConsole;
import com.ezfx.base.io.StringConsumingOutputStream;
import com.ezfx.base.utils.EZFX;
import com.ezfx.controls.console.ConsoleView;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;

import java.io.PrintStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.ezfx.base.utils.EZFX.runAsync;

public class PolyglotView extends ConsoleView {

	protected final ManagedContext managedContext;
	protected final String languageId;

	public static PolyglotView build(String languageId) throws ExecutionException, InterruptedException {
		IOConsole console = new IOConsole();
		ManagedContext managedContext = ManagedContext.start(Context.newBuilder(languageId)
				.allowAllAccess(true)
				.in(console.in.getInputStream())
				.out(console.out.getPrintStream())
				.err(console.err.getPrintStream()));
		return new PolyglotView(console, languageId, managedContext);
	}

	public PolyglotView(IOConsole console, String languageId, ManagedContext managedContext) {
		super(console);
		this.managedContext = managedContext;
		this.languageId = languageId;
		this.console.in.subscribe(new StringConsumingOutputStream(string -> {
			CompletableFuture<Value> future = getManagedContext().evalAsync(getLanguageId(), string);
			EZFX.runAsync(() -> {
				try {
					future.get();
				} catch (ExecutionException e) {
					if (e.getCause() instanceof PolyglotException pe) {
						PrintStream printStream;
						if (pe.isGuestException()) {
							printStream = console.err.getPrintStream();
						} else if (pe.isHostException()) {
							printStream = System.err;
						} else {
							throw new RuntimeException(pe);
						}
						printStream.println(pe.getMessage());
					}
				}
			});
		}));
	}

	private String getLanguageId() {
		return languageId;
	}

	public ManagedContext getManagedContext() {
		return managedContext;
	}

}
