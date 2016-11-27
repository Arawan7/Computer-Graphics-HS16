package jrtr;

import java.util.LinkedList;

import javax.vecmath.Matrix4f;

public abstract class Leaf implements Node {
	
	@Override
	public LinkedList<Node> getChildren() {
		return null;
	}
	
	@Override
	public Matrix4f getTransformation() {
		Matrix4f eye = new Matrix4f();
		eye.setIdentity();
		return eye;
	}
}
