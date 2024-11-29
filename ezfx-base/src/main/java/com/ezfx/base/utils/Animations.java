package com.ezfx.base.utils;

import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.util.Duration;

public interface Animations {

	private static void exposeOnHover(Region hoverRegion, Node animationTarget) {
		Duration duration = Duration.millis(300);

		// Set up the scale transition for the child node
		ScaleTransition scaleTransition = new ScaleTransition(duration, animationTarget);
		scaleTransition.setFromX(0);  // Start at 0 scale (invisible)
		scaleTransition.setToX(1);    // End at full scale

		// Set up the translate transition for the child node
		TranslateTransition translateTransition = new TranslateTransition(duration, animationTarget);
		translateTransition.setFromX(-30); // Start offset above its position
		translateTransition.setToX(0);     // End at its original position

		// Combine both transitions in a ParallelTransition
		ParallelTransition pullOpenTransition = new ParallelTransition(scaleTransition, translateTransition);


		// Play the animation when the mouse enters
		hoverRegion.setOnMouseEntered(event -> {
			pullOpenTransition.setRate(1);  // Play forward
			pullOpenTransition.play();
		});

		// Hide the child node and reset scale when mouse exits
		hoverRegion.setOnMouseExited(event -> {
			pullOpenTransition.setRate(-1); // Play backward
			pullOpenTransition.play(); // Reverse animation from current position
		});
	}

}
