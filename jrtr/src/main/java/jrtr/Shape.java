package jrtr;
import java.util.ListIterator;

import javax.vecmath.*;

import jrtr.VertexData.Semantic;

/**
 * Represents a 3D object. The shape references its geometry, 
 * that is, a triangle mesh stored in a {@link VertexData} 
 * object, its {@link Material}, and a transformation {@link Matrix4f}.
 */
public class Shape {

	private Material material;
	private VertexData vertexData;
	private Matrix4f t;
	private float boundingSphereRadius;
	private Vector4f boundingSphereCenter;
	
	/**
	 * Make a shape from {@link VertexData}. A shape contains the geometry 
	 * (the {@link VertexData}), material properties for shading (a 
	 * refernce to a {@link Material}), and a transformation {@link Matrix4f}.
	 *  
	 *  
	 * @param vertexData the vertices of the shape.
	 */
	public Shape(VertexData vertexData)
	{
		this.vertexData = vertexData;
		t = new Matrix4f();
		t.setIdentity();
		
		material = null;
		computeBoundingSphere();
	}
	
	private void computeBoundingSphere(){
		ListIterator<VertexData.VertexElement> itr = vertexData.getElements()
				.listIterator(0);
		
		int numberOfVertices = vertexData.getNumberOfVertices();
		float[] verticesData = new float[3*numberOfVertices];
		while (itr.hasNext()) {
			VertexData.VertexElement e = itr.next();
			if (e.getSemantic().equals(Semantic.POSITION))
				verticesData = e.getData();
		}
		float centerX = 0f, centerY = 0f, centerZ = 0f;
		for(int i=0; i<numberOfVertices; i++){
			centerX += verticesData[3*i];
			centerY += verticesData[3*i+1];
			centerZ += verticesData[3*i+2];
		}
		centerX /= (float)numberOfVertices;
		centerY /= (float)numberOfVertices;
		centerZ /= (float)numberOfVertices;
		
		boundingSphereCenter = new Vector4f(centerX, centerY, centerZ, 1);
		
		float maxDist = 0f;
		for(int i=0; i<numberOfVertices; i++){
			Vector4f vertex = new Vector4f(verticesData[3*i], verticesData[3*i+1], verticesData[3*i+2], 1);
			vertex.sub(boundingSphereCenter);
			maxDist = vertex.length() > maxDist ? vertex.length() : maxDist;
		}
		boundingSphereRadius = maxDist;
	}
	
	public float getBoundingSphereRadius() {
		return boundingSphereRadius;
	}

	public Vector4f getBoundingSphereCenter() {
		return boundingSphereCenter;
	}

	public VertexData getVertexData()
	{
		return vertexData;
	}
	
	public void setTransformation(Matrix4f t)
	{
		this.t = t;
	}
	
	public Matrix4f getTransformation()
	{
		return t;
	}
	
	/**
	 * Set a reference to a material for this shape.
	 * 
	 * @param material
	 * 		the material to be referenced from this shape
	 */
	public void setMaterial(Material material)
	{
		this.material = material;
	}

	/**
	 * To be implemented in the "Textures and Shading" project.
	 */
	public Material getMaterial()
	{
		return material;
	}

}
