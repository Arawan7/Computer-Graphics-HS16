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
	 * Rotates the model by #angle degrees counter clockwise about the specified x axis (tilt).
	 * @param model The model to rotate.
	 * @param angle The angle to rotate in degrees.
	 * @param relativeModel The model to relate the rotation to.
	 */
	public static void rotateRelativeTilt(Shape model, float angle, Shape relativeModel)
	{
		float angleInRadians = (float)((angle * Math.PI) / 180);
		Matrix4f transformationRelativeModel = relativeModel.getTransformation();
		
		Vector4f pivot = new Vector4f();
		transformationRelativeModel.getColumn(3, pivot);
		
		Vector4f relativeXAxis = new Vector4f();
		transformationRelativeModel.getColumn(0, relativeXAxis);
		Matrix4f rotationMatrix = getRotationAboutAxis(relativeXAxis, angleInRadians);
		
		rotateWithPivot(model, pivot, rotationMatrix);
	}
	
	/**
	 * Rotates the model by #angle degrees counter clockwise about the specified y axis (pan).
	 * @param model The model to rotate.
	 * @param angle The angle to rotate in degrees.
	 * @param relativeModel The model to relate the rotation to.
	 */
	public static void rotateRelativePan(Shape model, float angle, Shape relativeModel)
	{
		float angleInRadians = (float)((angle * Math.PI) / 180);
		Matrix4f transformationRelativeModel = relativeModel.getTransformation();
		
		Vector4f pivot = new Vector4f();
		transformationRelativeModel.getColumn(3, pivot);
		
		Vector4f relativeYAxis = new Vector4f();
		transformationRelativeModel.getColumn(1, relativeYAxis);
		Matrix4f rotationMatrix = getRotationAboutAxis(relativeYAxis, angleInRadians);
		
		rotateWithPivot(model, pivot, rotationMatrix);
	}
	
	/**
	 * Rotates the model by #angle degrees counter clockwise about the specified z axis (roll).
	 * @param model The model to rotate.
	 * @param angle The angle to rotate in degrees.
	 * @param relativeModel The model to relate the rotation to.
	 */
	public static void rotateRelativeRoll(Shape model, float angle, Shape relativeModel)
	{
		float angleInRadians = (float)((angle * Math.PI) / 180);
		Matrix4f transformationRelativeModel = relativeModel.getTransformation();
		
		Vector4f pivot = new Vector4f();
		transformationRelativeModel.getColumn(3, pivot);
		
		Vector4f relativeZAxis = new Vector4f();
		transformationRelativeModel.getColumn(2, relativeZAxis);
		Matrix4f rotationMatrix = getRotationAboutAxis(relativeZAxis, angleInRadians);
		
		rotateWithPivot(model, pivot, rotationMatrix);
	}
	
	/**
	 * Rotates the model by #angle degrees counter clockwise about its x axis (tilt).
	 * @param model The model to rotate.
	 * @param angle The angle to rotate in degrees.
	 */
	public static void rotateTilt(Shape model, float angle)
	{
		rotateRelativeTilt(model, angle, model);
	}
	
	/**
	 * Rotates the model by #angle degrees counter clockwise about its y axis (pan).
	 * @param model The model to rotate.
	 * @param angle The angle to rotate in degrees.
	 */
	public static void rotatePan(Shape model, float angle)
	{
		rotateRelativePan(model, angle, model);
	}
	
	/**
	 * Rotates the model by #angle degrees counter clockwise about its z axis (roll).
	 * @param model The model to rotate.
	 * @param angle The angle to rotate in degrees.
	 */
	public static void rotateRoll(Shape model, float angle)
	{
		rotateRelativeRoll(model, angle, model);
	}
	
	/**
	 * Translates the model relatively.
	 * @param model The model to move.
	 * @param direction The direction (0: forward, 1: backward, 2: left, 3: right, 4: up, 5: down)
	 * @param distance How far to move the model.
	 */
	public static void translate(Shape model, int direction, float distance)
	{
		assert direction >= 0;
		assert direction < 6;
		
		Matrix4f transformation = model.getTransformation();
		Vector4f axis = new Vector4f();
		
		switch(direction)
		{
			case 0:
				transformation.getColumn(2, axis);
				axis.scale(-1);
				break;
			case 1:
				transformation.getColumn(2, axis);
				break;
			case 2:
				transformation.getColumn(0, axis);
				axis.scale(-1);
				break;
			case 3:
				transformation.getColumn(0, axis);
				break;
			case 4:
				transformation.getColumn(1, axis);
				break;
			case 5:
				transformation.getColumn(1, axis);
				axis.scale(-1);
				break;
		}
		Vector3f relativeDirection = new Vector3f(axis.x, axis.y, axis.z);
		translateGlobal(model, relativeDirection, distance);
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
		
		direction.normalize();
		direction.scale(distance);
		
		Matrix4f transformation = model.getTransformation();
		Vector3f relativeOrigin = new Vector3f(transformation.m03,transformation.m13,transformation.m23);
		relativeOrigin.add(new Vector3f(direction.x,direction.y,direction.z));
		transformation.setTranslation(relativeOrigin);
	}
	
	
	private static Matrix4f getRotationAboutAxis(Vector4f axis, float angle)
	{		
		Matrix4f rotation = new Matrix4f();
		rotation.m00 = axis.x*axis.x+(float)(Math.cos(angle)*(1-axis.x*axis.x));
		rotation.m01 = axis.x*axis.y*( 1-(float)(Math.cos(angle)) ) - axis.z*(float)(Math.sin(angle));
		rotation.m02 = axis.x*axis.z*( 1-(float)(Math.cos(angle)) ) + axis.y*(float)(Math.sin(angle));
		rotation.m03 = 0;
		
		rotation.m10 = axis.x*axis.y*( 1-(float)(Math.cos(angle)) ) + axis.z*(float)(Math.sin(angle));
		rotation.m11 = axis.y*axis.y+(float)(Math.cos(angle)*(1-axis.y*axis.y));
		rotation.m12 = axis.y*axis.z*( 1-(float)(Math.cos(angle)) ) - axis.x*(float)(Math.sin(angle));
		rotation.m13 = 0;
		
		rotation.m20 = axis.x*axis.z*( 1-(float)(Math.cos(angle)) ) - axis.y*(float)(Math.sin(angle));
		rotation.m21 = axis.y*axis.z*( 1-(float)(Math.cos(angle)) ) + axis.x*(float)(Math.sin(angle));
		rotation.m22 = axis.z*axis.z+(float)(Math.cos(angle)*(1-axis.z*axis.z));
		rotation.m23 = 0;
		
		rotation.m30 = 0;
		rotation.m31 = 0;
		rotation.m32 = 0;
		rotation.m33 = 1;
		
		return rotation;
	}
	
	/**
	 * Rotates the model around given pivot.
	 * @param model The model to rotate.
	 * @param pivot The pivot to rotate about.
	 * @param rotation The rotation to apply.
	 */
	private static void rotateWithPivot(Shape model, Vector4f pivot, Matrix4f rotation)
	{
		Matrix4f translationToOrigin = new Matrix4f();
		translationToOrigin.setIdentity();
		translationToOrigin.setColumn(3, new Vector4f(-pivot.x,-pivot.y,-pivot.z,1));
		
		Matrix4f translationToModel = (Matrix4f)translationToOrigin.clone();
		translationToModel.invert();
		
		// construct ((((T^-1)R)T)(transformation))
		translationToModel.mul(rotation);
		translationToModel.mul(translationToOrigin);
		translationToModel.mul(model.getTransformation());
		
		// apply result
		model.setTransformation(translationToModel);
	}
}