package com.ezfx.base.io;

import java.io.InputStream;
import java.util.concurrent.LinkedBlockingQueue;

import static com.ezfx.base.io.FileSystemIO.transfer;


public class InputStreamSplitter {

	private final ObservableOutputStream outputSteam;

	public InputStreamSplitter(InputStream original) {
		this.outputSteam = new ObservableOutputStream();
		this.outputSteam.subscribe(new QueueOutputStream());
		transfer(original, this.outputSteam);
	}

	public InputStream split() {
		LinkedBlockingQueue<Integer> blockingQueue = new LinkedBlockingQueue<>();
		outputSteam.subscribe(new QueueOutputStream(blockingQueue));
		return new QueueInputStream(blockingQueue);
	}
}