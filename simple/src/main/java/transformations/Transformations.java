package transformations;

import java.awt.Point;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import jrtr.Camera;
import jrtr.Shape;

/**
 * A class that provides affine transformations in 3D space.
 * @author Arawan//simon
 *
 */
public class Transformations {
	
	/**
	 * Rotates the given camera locally according to the rotation of moving from-point to to-point.
	 * @param camera The camera to rotate.
	 * @param from The from-mouse-position.
	 * @param to The to-mouse-position.
	 * @param width The screen's width.
	 * @param height The screen's height.
	 */
	public static void virtualTrackball(Camera camera, Point from, Point to, int width, int height)
	{
		Vector4f pivot = new Vector4f();
		camera.getCameraMatrix().getColumn(3, pivot);
		virtualTrackball(camera, pivot, from, to, width, height);
	}
	
	/**
	 * Rotates the given camera relatively to the model, according to the rotation of moving from-point to to-point. The camera is always looking at the model.
	 * @param camera The camera to rotate.
	 * @param model The model to rotate around and look at.
	 * @param from The from-mouse-position.
	 * @param to The to-mouse-position.
	 * @param width The screen's width.
	 * @param height The screen's height.
	 */
	public static void virtualTrackball(Camera camera, Shape model, Point from, Point to, int width, int height)
	{
		Matrix4f modelTrans = new Matrix4f();
		modelTrans.set(model.getTransformation());
		Vector4f modelPivot = new Vector4f();
		modelTrans.getColumn(3, modelPivot);
		virtualTrackball(camera, modelPivot, from, to, width, height);

//		Matrix4f cameraMatrix = new Matrix4f();
//		cameraMatrix.set(camera.getCameraMatrix());
//		Matrix4f rotation = getRotationWithPivot(cameraMatrix, modelPivot, rotationFromTo(projectPointToVector3f(from, width, height), projectPointToVector3f(to, width, height)) );
//		Vector4f cOP = new Vector4f();
//		rotation.getColumn(3, cOP);
//		
//		camera.setLookAtPoint(new Vector3f(modelPivot.x, modelPivot.y, modelPivot.z));
//		camera.setCenterOfProjection(new Vector3f(cOP.x,cOP.y,cOP.z));
	}
	
	/**
	 * Rotates the given model locally, according to the rotation of moving from-point to to-point.
	 * @param model The model to rotate.
	 * @param from The from-mouse-position.
	 * @param to The to-mouse-position.
	 * @param width The screen's width.
	 * @param height The screen's height.
	 */
	public static void virtualTrackball(Shape model, Point from, Point to, int width, int height)
	{		
		Matrix4f rotation = rotationFromTo( projectPointToVector3f(from, width, height), projectPointToVector3f(to, width, height) );
		Vector4f pivot = new Vector4f();
		model.getTransformation().getColumn(3, pivot);
		// apply result
		model.setTransformation(getRotationWithPivot(model.getTransformation(), pivot, rotation));
	}
	
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
	 * Rotates the given camera around pivot, according to the rotation of moving from-point to to-point.
	 * @param camera The camera to rotate.
	 * @param pivot The pivot to rotate around.
	 * @param from The from-mouse-position.
	 * @param to The to-mouse-position.
	 * @param width The screen's width.
	 * @param height The screen's height.
	 */
	private static void virtualTrackball(Camera camera, Vector4f pivot, Point from, Point to, int width, int height)
	{		
		Matrix4f rotation = rotationFromTo( projectPointToVector3f(from, width, height), projectPointToVector3f(to, width, height) );
		
		// apply result
		camera.setCameraMatrix(getRotationWithPivot(camera.getCameraMatrix(), pivot, rotation));
		
//		Matrix4f rotationWithPivot = getRotationWithPivot(camera.getCameraMatrix(), pivot, rotation);
//		camera.setCenterOfProjection(new Vector3f(rotationWithPivot.m03, rotationWithPivot.m13, rotationWithPivot.m23));
//		camera.setLookAtPoint(new Vector3f(pivot.x, pivot.y, pivot.z));
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
	 * Translates the camera relatively.
	 * @param camera The camera to move.
	 * @param direction The direction (0: forward, 1: backward, 2: left, 3: right, 4: up, 5: down)
	 * @param distance How far to move the model.
	 */
	public static void translate(Camera camera, int direction, float distance)
	{
		assert direction >= 0;
		assert direction < 6;
		
		Matrix4f transformation = camera.getCameraMatrix();
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
		translateMatrix(camera.getCameraMatrix(), relativeDirection, distance);
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
		
		translateMatrix(model.getTransformation(), direction, distance);
	}
	
	/**
	 * Translates the camera in world space.
	 * @param camera The camera to move.
	 * @param direction The direction in world space to move towards. Mustn't have zero length.
	 * @param distance How far to move the model.
	 */
	public static void translateGlobal(Camera camera, Vector3f direction, float distance)
	{
		assert direction.length() != 0;
		
		translateMatrix(camera.getCameraMatrix(), direction, distance);
	}
	
	/**
	 * Translates the matrix in world space.
	 * @param matrix The matrix to apply the translation.
	 * @param direction The direction in world space to move towards. Mustn't have zero length.
	 * @param distance How far to move the model.
	 */
	private static void translateMatrix(Matrix4f modelMatrix, Vector3f direction, float distance){
		assert direction.length() != 0;
		
		direction.normalize();
		direction.scale(distance);
		
		Vector3f relativeOrigin = new Vector3f(modelMatrix.m03,modelMatrix.m13,modelMatrix.m23);
		relativeOrigin.add(new Vector3f(direction.x,direction.y,direction.z));
		modelMatrix.setTranslation(relativeOrigin);
		//return modelMatrix;
	}
	
	/**
	 * Calculates the rotation matrix about given axis by given angle.
	 * @param axis The axis to rotate about.
	 * @param angle The angle to rotate
	 * @return the rotation matrix about given axis by given angle.
	 */
	private static Matrix4f getRotationAboutAxis(Vector4f axis, float angle)
	{		
		Matrix4f rotation = new Matrix4f();
		AxisAngle4f rot = new AxisAngle4f(axis.x,axis.y,axis.z,angle);
		rotation.set(rot);
		return rotation;
	}
	
	/**
	 * Rotates the model around given pivot by given rotation.
	 * @param model The model to rotate.
	 * @param pivot The pivot to rotate about.
	 * @param rotation The rotation to apply.
	 */
	private static void rotateWithPivot(Shape model, Vector4f pivot, Matrix4f rotation)
	{
		model.setTransformation( getRotationWithPivot(model.getTransformation(), pivot, rotation) );
	}
	
	/**
	 * Calculates the rotation around given pivot by given rotation.
	 * @param modelMatrix The matrix to apply the rotation to.
	 * @param pivot The pivot to rotate about.
	 * @param rotation The applied rotation to set the transformation.
	 */
	private static Matrix4f getRotationWithPivot(Matrix4f modelMatrix, Vector4f pivot, Matrix4f rotation)
	{
		Matrix4f translationToOrigin = new Matrix4f();
		translationToOrigin.setIdentity();
		translationToOrigin.setColumn(3, new Vector4f(-pivot.x,-pivot.y,-pivot.z,1));
		
		Matrix4f translationToModel = new Matrix4f();
		translationToModel.set(translationToOrigin);
		translationToModel.invert();
		
		Matrix4f modelMatrixClone = new Matrix4f(modelMatrix);
		
		// construct ((((T^-1)R)T)(transformation))
		translationToModel.mul(rotation);
		translationToModel.mul(translationToOrigin);
		translationToModel.mul(modelMatrixClone);
		
		return translationToModel;
	}
	
	/**
	 * Calculates the rotation matrix that describes the rotation of moving vector from to vector to.
	 * @param from
	 * @param to
	 * @return the rotation matrix of rotating vector from to vector to.
	 */
	private static Matrix4f rotationFromTo(Vector3f from, Vector3f to)
	{
		Vector3f axis = new Vector3f();
		axis.cross(from, to);
		float angleBetweenVectors = from.angle(to);
		Vector4f axis4f = new Vector4f(new float[]{axis.x,axis.y,axis.z,0});
		
		return getRotationAboutAxis(axis4f, angleBetweenVectors);

	}
	
	/**
	 * Projects the given mousePos to 3D world space.
	 * @param mousePos
	 * @param width The with of the screen.
	 * @param height The height of the screen
	 * @return the projected point as a Vector3f.
	 */
	private static Vector3f projectPointToVector3f(Point mousePos, int width, int height)
	{
		// Scale bounds to [0,0] - [2,2]
		
		double x = mousePos.x * 0.5 / width;
		double y = mousePos.y * 0.5 / height;
		
		// Translate 0,0 to the center
		x = x - 1;
		// Flip so +Y is up instead of down
		y = 1 - y;
		
		double z2 = 1 - x * x - y * y;
		double z = z2 > 0 ? Math.sqrt(z2) : 0.001f;
		Vector3f projectedPoint = new Vector3f((float)x, (float)y, (float)z);
		projectedPoint.normalize();
		
		return projectedPoint;
	}
}