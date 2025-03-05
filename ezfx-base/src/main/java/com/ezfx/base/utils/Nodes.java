package com.ezfx.base.utils;

import javafx.scene.Node;

public interface Nodes {

	static boolean isAncestor(Node node, Node ancestor){
		if (node == null) return false;
		while ((node = node.getParent()) != null){
			if (node == ancestor) {
				return true;
			}
		}
		return false;
	}
}
