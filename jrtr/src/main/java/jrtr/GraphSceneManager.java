package jrtr;

import java.util.Stack;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector4f;

import java.util.Iterator;

public class GraphSceneManager implements SceneManagerInterface {
	
	private Camera camera;
	private Frustum frustum;
	private Node root;
	private Vector4f[] planeNormals;
	private float[] d;
	
	public GraphSceneManager()
	{
		camera = new Camera();
		frustum = new Frustum();
		d = new float[6];
		planeNormals = new Vector4f[]{new Vector4f(), new Vector4f(), new Vector4f(), new Vector4f(), new Vector4f(), new Vector4f()};
		Matrix4f viewProjMat = new Matrix4f(frustum.getProjectionMatrix());
		viewProjMat.invert();
		viewProjMat.transpose();
		createViewFrustumFromMatrix(viewProjMat);
	}
	
	public Camera getCamera()
	{
		return camera;
	}
	
	public Frustum getFrustum()
	{
		return frustum;
	}
	
	public void setRootNode(Node root){
		this.root = root;
	}
	
	public void addNode(Node node, Group parent)
	{
		
	}
	
	public Iterator<Light> lightIterator()
	{
		return new GraphSceneLightItr();
	}
	
	public SceneManagerIterator iterator()
	{
		return new GraphSceneManagerItr();
	}
	
	private class GraphSceneManagerItr implements SceneManagerIterator {
		
		private Stack<Node> stack;
		private Stack<Matrix4f> transfStack;
		
		public GraphSceneManagerItr()
		{
			stack = new Stack<Node>();
			stack.push(root);
			
			transfStack = new Stack<Matrix4f>();
			transfStack.push(root.getTransformation());
		}
		
		public boolean hasNext()
		{
			return !stack.isEmpty();
		}
		
		public RenderItem next()
		{
			Node element = stack.pop();
			Matrix4f parentTransf = transfStack.pop();
			while(!(element instanceof ShapeNode)){	
				if(element instanceof Group){
					for(Node node : element.getChildren()){
						stack.push(node);
						Matrix4f newParentTransf = new Matrix4f();
						newParentTransf.setIdentity();
						newParentTransf.mul(parentTransf, node.getTransformation());
						transfStack.push(newParentTransf);
					};
				}
				if(!stack.isEmpty()){
					element = stack.pop();
					parentTransf = transfStack.pop();
				}
				else // if stack ended with a non ShapeNode, break
					break;
			}
			Matrix4f objToWorld = (Matrix4f)parentTransf.clone();
			Matrix4f objToCam = new Matrix4f(camera.getCameraMatrix());
			objToCam.mul(objToWorld);
//			objToCam = objToWorld;
			return new RenderItem(((ShapeNode)element).sphereIsInFrustum(planeNormals, d, objToCam) ? (Shape)element.get3dObject() : null, objToWorld);
//			return new RenderItem((Shape)element.get3dObject(), (Matrix4f)parentTransf.clone());
		}
	}
	
	private class GraphSceneLightItr implements Iterator<Light>{
		
		private Stack<Node> stack;
		private Stack<Matrix4f> transfStack;
		
		public GraphSceneLightItr()
		{
			stack = new Stack<Node>();
			stack.push(root);
			
			transfStack = new Stack<Matrix4f>();
			transfStack.push(root.getTransformation());
		}
		
		public boolean hasNext()
		{			
			return !stack.isEmpty();
		}
		
		public Light next()
		{			
			Node element = stack.pop();
			Matrix4f parentTransf = transfStack.pop();
			while(!(element instanceof LightNode)){
				if(element instanceof Group){
					for(Node node : element.getChildren()){
						stack.push(node);
						Matrix4f newParentTransf = new Matrix4f();
						newParentTransf.setIdentity();
						newParentTransf.mul(parentTransf, node.getTransformation());
						transfStack.push(newParentTransf);
					};
				}
				if(!stack.isEmpty()){
					element = stack.pop();
					parentTransf = transfStack.pop();
				} else // if stack ended with a non LightNode, break
					return null;
			}
			
			Light light = (Light)element.get3dObject();
			
			light = light.clone();
			light.position.x = parentTransf.m03;
			light.position.y = parentTransf.m13;
			light.position.z = parentTransf.m23;
			
			return light;
		}
	}
	
	// calculate the view frustum from the view-projection matrix
	// (projection-matrix -> view-space; view-projection-matrix -> world-space etc...)
	private void createViewFrustumFromMatrix(Matrix4f viewToCameraSpaceProjMat)
	{
		
		
	    // left
	    planeNormals[0].x = viewToCameraSpaceProjMat.m03 + viewToCameraSpaceProjMat.m00;
	    planeNormals[0].y = viewToCameraSpaceProjMat.m13 + viewToCameraSpaceProjMat.m10;
	    planeNormals[0].z = viewToCameraSpaceProjMat.m23 + viewToCameraSpaceProjMat.m20;
	    d[0]        	  = viewToCameraSpaceProjMat.m33 + viewToCameraSpaceProjMat.m30;
	    
	    // right
	    planeNormals[1].x = viewToCameraSpaceProjMat.m03 - viewToCameraSpaceProjMat.m00;
	    planeNormals[1].y = viewToCameraSpaceProjMat.m13 - viewToCameraSpaceProjMat.m10;
	    planeNormals[1].z = viewToCameraSpaceProjMat.m23 - viewToCameraSpaceProjMat.m20;
	    d[1]       	   	  = viewToCameraSpaceProjMat.m33 - viewToCameraSpaceProjMat.m30;
	    
	    // bottom
	    planeNormals[2].x = viewToCameraSpaceProjMat.m03 + viewToCameraSpaceProjMat.m01;
	    planeNormals[2].y = viewToCameraSpaceProjMat.m13 + viewToCameraSpaceProjMat.m11;
	    planeNormals[2].z = viewToCameraSpaceProjMat.m23 + viewToCameraSpaceProjMat.m21;
	    d[2]        	  = viewToCameraSpaceProjMat.m33 + viewToCameraSpaceProjMat.m31;
	    
	    // top
	    planeNormals[3].x = viewToCameraSpaceProjMat.m03 - viewToCameraSpaceProjMat.m01;
	    planeNormals[3].y = viewToCameraSpaceProjMat.m13 - viewToCameraSpaceProjMat.m11;
	    planeNormals[3].z = viewToCameraSpaceProjMat.m23 - viewToCameraSpaceProjMat.m21;
	    d[3]        	  = viewToCameraSpaceProjMat.m33 - viewToCameraSpaceProjMat.m31;
	    
	    // near
	    planeNormals[4].x = viewToCameraSpaceProjMat.m03 + viewToCameraSpaceProjMat.m02;
	    planeNormals[4].y = viewToCameraSpaceProjMat.m13 + viewToCameraSpaceProjMat.m12;
	    planeNormals[4].z = viewToCameraSpaceProjMat.m23 + viewToCameraSpaceProjMat.m22;
	    d[4]        	  = viewToCameraSpaceProjMat.m33 + viewToCameraSpaceProjMat.m32;
	    
	    // far
	    planeNormals[5].x = viewToCameraSpaceProjMat.m03 - viewToCameraSpaceProjMat.m02;
	    planeNormals[5].y = viewToCameraSpaceProjMat.m13 - viewToCameraSpaceProjMat.m12;
	    planeNormals[5].z = viewToCameraSpaceProjMat.m23 - viewToCameraSpaceProjMat.m22;
	    d[5]			  = viewToCameraSpaceProjMat.m33 - viewToCameraSpaceProjMat.m32;
	    
	    // normalize
	    for(int i=0; i<6; i++)
	    {
	        float length = planeNormals[i].length();
	        planeNormals[i].scale(1/length);
	        d[i] 		/= length; 			// d also has to be divided by the length of the normal
	    }
	}
	
}
