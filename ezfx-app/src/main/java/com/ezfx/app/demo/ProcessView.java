package com.ezfx.app.demo;

import com.ezfx.base.io.ContinuousInputStream;
import com.ezfx.controls.console.ConsoleView;

import static com.ezfx.base.io.FileSystemIO.transfer;

public class ProcessView extends ConsoleView {

	protected final Process process;

	public ProcessView(Process process) {
		this.process = process;
		this.console.in.subscribe(process.getOutputStream());
		transfer(new ContinuousInputStream(process.getInputStream()), this.console.out.getOutputStream());
		transfer(new ContinuousInputStream(process.getErrorStream()), this.console.err.getOutputStream());
	}

	public Process getProcess() {
		return process;
	}

}
