package jrtr;

import javax.vecmath.Matrix4f;

/**
 * Stores the specification of a viewing frustum, or a viewing
 * volume. The viewing frustum is represented by a 4x4 projection
 * matrix. You will extend this class to construct the projection 
 * matrix from intuitive parameters.
 * <p>
 * A scene manager (see {@link SceneManagerInterface}, {@link SimpleSceneManager}) 
 * stores a frustum.
 */
public class Frustum {

	private Matrix4f projectionMatrix;
	
	/**
	 * Construct a default viewing frustum. The frustum is given by a 
	 * default 4x4 projection matrix.
	 */
	public Frustum()
	{
		createProjectionMatrix(1, 100, 1, 60);
	}
	
	/**
	 * Return the 4x4 projection matrix, which is used for example by 
	 * the renderer.
	 * 
	 * @return the 4x4 projection matrix
	 */
	public Matrix4f getProjectionMatrix()
	{
		return projectionMatrix;
	}
	
	/**
	 * Creates the projection matrix in respect to given arguments.
	 * @param nearPlane
	 * @param farPlane
	 * @param aspectRatio
	 * @param verticalFieldOfView FOV in degrees.
	 */
	private void createProjectionMatrix(float nearPlane, float farPlane, float aspectRatio, float verticalFieldOfView)
	{
		double FOV = Math.toRadians(verticalFieldOfView);
		projectionMatrix = new Matrix4f();
		projectionMatrix.setZero(); // has to be here or not?
		projectionMatrix.m00 = (float)( 1 / (aspectRatio*Math.tan(FOV/2)) );
		projectionMatrix.m11 = (float)(1 / Math.tan(FOV/2));
		projectionMatrix.m22 = (nearPlane+farPlane) / (nearPlane-farPlane);
		projectionMatrix.m32 = -1;
		projectionMatrix.m23 = 2*nearPlane*farPlane/(nearPlane-farPlane);
		
	}
	
	private void setDefaultProjection()
	{
		projectionMatrix = new Matrix4f();
		float f[] = {2.f, 0.f, 0.f, 0.f, 
					 0.f, 2.f, 0.f, 0.f,
				     0.f, 0.f, -1.02f, -2.02f,
				     0.f, 0.f, -1.f, 0.f};
		projectionMatrix.set(f);
	}
	
	private void createProjectionMatrix(float nearPlane, float farPlane, float right, float left, float top, float bottom)
	{		
		projectionMatrix = new Matrix4f();
		projectionMatrix.setZero(); // has to be here or not?
		projectionMatrix.m00 = 2*nearPlane / (right - left);
		projectionMatrix.m11 = 2*nearPlane / (top - bottom);
		projectionMatrix.m02 = (right+left) / (right-left);
		projectionMatrix.m12 = (top+bottom) / (top-bottom);
		projectionMatrix.m22 = (-farPlane-nearPlane) / (farPlane-nearPlane);
		projectionMatrix.m32 = -1;
		projectionMatrix.m23 = -2*farPlane*nearPlane / (farPlane-nearPlane);
	}
}
