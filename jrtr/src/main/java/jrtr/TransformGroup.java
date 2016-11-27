package jrtr;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

public class TransformGroup extends Group {
	private Matrix4f transformation;
	
	/**
	 * Creates a transform group and sets its transformation matrix to the identity.
	 */
	public TransformGroup() {
		super();
		
		Matrix4f transf = new Matrix4f();
		transf.setIdentity();
		transformation = transf;
	}
	
	/**
	 * Creates a transform group and sets its transformation matrix to the given transformation.
	 * @param transf the transformation Matrix4f to initialize with.
	 */
	public TransformGroup(Matrix4f transf) {
		super();
		
		transformation = transf;
	}
	
	/**
	 * Creates a transform group and sets its transformation matrix to the position in world space.
	 * @param position the position to place this transform group in world space.
	 */
	public TransformGroup(Vector3f position) {		
		super();
		
		Matrix4f transf = new Matrix4f();
		transf.setIdentity();
		transf.setTranslation(position);
		
		transformation = transf;
	}
	
	/**
	 * Creates a transform group and sets its transformation matrix to the position in world space.
	 * @param position the position to place this transform group in world space.
	 * @param children the children to add to this group.
	 */
	public TransformGroup(Vector3f position, Node[] children) {		
		super();
		
		Matrix4f transf = new Matrix4f();
		transf.setIdentity();
		transf.setTranslation(position);
		
		transformation = transf;
		
		for(Node node : children){
			this.children.addLast(node);
		}
	}
	
	public void setTransformation(Matrix4f transformation){
		this.transformation = transformation;
	}
	
	@Override
	public Matrix4f getTransformation() {
		return transformation;
	}
}