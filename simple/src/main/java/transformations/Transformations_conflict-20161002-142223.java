package transformations;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import jrtr.Shape;

public class Transformations {
	/**
	 * Scales the model by the scaling factors.
	 * @param model The model to apply the scaling.
	 * @param scalingFactors the factors to use in the scaling (x,y,z).
	 */
	public static void scale(Shape model, Vector3f scalingFactors)
	{
		Matrix4f transformation = model.getTransformation();
		Matrix4f translation = new Matrix4f(new float[]{ scalingFactors.x,0,0,0, 0,scalingFactors.y,0,0, 0,0,scalingFactors.z,0, 0,0,0,1 });
		transformation.mul(translation);
	}
	
	/**
	 * Translates the model relatively.
	 * @param model The model to move.
	 * @param direction The direction in world space to move towards. Mustn't have zero length.
	 * @param distance How far to move the model.
	 */
	public static void translate(Shape model, Vector3f direction, float distance)
	{
		assert direction.length() != 0;
		
		direction.normalize();
		direction.scale(distance);
		
	}
	
	/**
	 * Translates the model in world space.
	 * @param model The model to move.
	 * @param direction The direction in world space to move towards. Mustn't have zero length.
	 * @param distance How far to move the model.
	 */
	public static void translateGlobal(Shape model, Vector3f direction, float distance)
	{
		assert direction.length() != 0;
		
		System.out.println("m30: " + model.getTransformation().m30);
		System.out.println("m31: " + model.getTransformation().m31);
		System.out.println("m32: " + model.getTransformation().m32);
		System.out.println("m33: " + model.getTransformation().m33 + "\n After:");
		
		direction.normalize();
		direction.scale(distance);
		
		Matrix4f transformation = model.getTransformation();
		
		
		/*
		Vector4f previousLastColumn = new Vector4f();
		transformation.getColumn(3, previousLastColumn);
		
		Matrix4f translation = new Matrix4f(new float[]{ 1,0,0,0, 0,1,0,0, 0,0,1,0, direction.x,direction.y,direction.z,1 });
		translation.mul(transformation);
		translation.setColumn(3, previousLastColumn);
		model.setTransformation(translation);
		
		System.out.println("Previous last column: " + previousLastColumn);
		Vector4f tmp = new Vector4f();
		translation.getColumn(3, tmp);
		System.out.println("last column: " + tmp);
		
		System.out.println("m30: " + model.getTransformation().m30);
		System.out.println("m31: " + model.getTransformation().m31);
		System.out.println("m32: " + model.getTransformation().m32);
		System.out.println("m33: " + model.getTransformation().m33);
		*/
	}
}