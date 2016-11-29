package jrtr;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector4f;

public class ShapeNode extends Leaf {
	private Shape shape;
	
	public ShapeNode(Shape shape) {
		this.shape = shape;
	}
	
	public void setShape(Shape shape){
		this.shape = shape;
	}
	
	@Override
	public Shape get3dObject() {
		return shape;
	}
	
	public boolean sphereIsInFrustum(Vector4f[] planeNormals, float[] d, Matrix4f objectToWorldCamera)
	{
		Vector4f center = (Vector4f)shape.getBoundingSphereCenter().clone();
		objectToWorldCamera.transform(center);
		float radius = shape.getBoundingSphereRadius();
		
	    for(int i=0; i<6; i++)
	    {
	        if(signedDistanceToPoint(center, planeNormals[i], d[i]) < -radius)
	        {
	            return false;
	        }
	    }
	    return true;
	}
	
	private float signedDistanceToPoint(Vector4f pt, Vector4f normal, float d)
	{
		assert normal.length() == 1;
		
	    return normal.dot(pt) + d;
	}

}