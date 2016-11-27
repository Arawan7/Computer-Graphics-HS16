package jrtr;

import java.util.LinkedList;

import javax.vecmath.Matrix4f;

public interface Node {
	Matrix4f getTransformation();
	
	Object get3dObject();
	
	LinkedList<Node> getChildren();
}