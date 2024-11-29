package com.ezfx.spring.test.fxml;

import com.ezfx.spring.SpringFXProxyBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class CustomNodeBuilderFX extends SpringFXProxyBuilder<CustomNode> {
	private final ApplicationContext context;

	public CustomNodeBuilderFX(ApplicationContext context) {
		super(context, CustomNode.class);
		this.context = context;
	}

	@Override
	public CustomNode build() {
		CustomNode customNode = super.build();
		customNode.setText("Built by CustomNodeBuilderFX");
		return customNode;
	}
}
