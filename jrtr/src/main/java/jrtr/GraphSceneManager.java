package jrtr;

import java.util.Stack;

import javax.vecmath.Matrix4f;

import java.util.Iterator;

public class GraphSceneManager implements SceneManagerInterface {
	
	private Camera camera;
	private Frustum frustum;
	private Node root;
	
	public GraphSceneManager()
	{
		camera = new Camera();
		frustum = new Frustum();
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
			return new RenderItem((Shape)element.get3dObject(), (Matrix4f)parentTransf.clone());
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
}
