package primitiveMeshes;

import jrtr.RenderContext;
import jrtr.VertexData;

public class Primitives {
	/**
	 * Generates a triangle mesh for a torus.
	 * @param innerResolution How many segments are used to construct the inner circle, must be greater or equals to 3.
	 * @param outerResolution How many segments are used to construct the outer circle, must be greater or equals to 3.
	 * @param innerRadius Radius of the inner circle.
	 * @param outerRadius Radius of the outer circle.
	 * @param renderContext the render context to create the VertexData.
	 * @return the VertexData of a torus.
	 */
	public static final VertexData getTorus(int innerResolution, int outerResolution, float innerRadius, float outerRadius, RenderContext renderContext)
	{
		assert innerResolution >= 3;
		assert outerResolution >= 3;
		
		int numberOfVertices = innerResolution * outerResolution;
		
		// The vertex positions
		float[] v = new float[3*numberOfVertices]; // xyz per vertex
		double angleBetweenTwoInnerSegments = (2*Math.PI)/innerResolution;
		double angleBetweenTwoOuterSegments = (2*Math.PI)/outerResolution;
		double currentInnerAngle = 0;
		double currentOuterAngle = 0;
		
		for(int i=0; i<innerResolution; i++) // loop over inner segments
		{
			for(int j=0; j<3*outerResolution; j+=3) // loop over outer segments
			{
				float x = (float) ( (innerRadius + outerRadius * Math.cos(currentOuterAngle)) * Math.cos(currentInnerAngle) );
				float y = (float) ( outerRadius * Math.sin(currentOuterAngle) );
				float z = (float) ( (innerRadius + outerRadius * Math.cos(currentOuterAngle)) * Math.sin(currentInnerAngle) );
				
				v[i*3*outerResolution + j] = x;
				v[i*3*outerResolution + j+1] = y;
				v[i*3*outerResolution + j+2] = z;
				
				currentOuterAngle += angleBetweenTwoOuterSegments; // reach next outer segment
			}
			currentOuterAngle = 0; // start building the circle again
			currentInnerAngle += angleBetweenTwoInnerSegments; // reach next inner segment
		}
		
		// The vertex colors
		float c[] = new float[3*numberOfVertices]; // rgb per vertex
		
		for(int i=0; i<innerResolution; i++) // loop over inner segments
		{
			float r,g,b;
			r = (float) Math.random();
			g = (float) Math.random();
			b = (float) Math.random();
			
			for(int j=0; j<3*outerResolution; j+=3) // loop over outer segments
			{					
				c[i*3*outerResolution + j] = r;
				c[i*3*outerResolution + j+1] = g;
				c[i*3*outerResolution + j+2] = b;
			}
		}
		
		// The triangles
		int indices[] = new int[2*(innerResolution * outerResolution)*3]; // two triangles per face, inner- times outer resolution faces and three vertex indices for each triangle
		for(int i=0; i<innerResolution; i++) // loop over inner resolution
		{
			for(int j=0; j<outerResolution; j++) // loop over outer resolution
			{
				// build one side
				indices[i*2*3*outerResolution + 6*j] = (i*outerResolution +j)%numberOfVertices; // two triangles, each three indices, outer resolution many faces of segment
				indices[i*2*3*outerResolution + 6*j +1] = (i*outerResolution +j+1)%numberOfVertices;
				indices[i*2*3*outerResolution + 6*j +2] = ((i+1)*outerResolution +j)%numberOfVertices;
				
				indices[i*2*3*outerResolution + 6*j +3] = ((i+1)*outerResolution +j)%numberOfVertices;
				indices[i*2*3*outerResolution + 6*j +4] = (i*outerResolution +j+1)%numberOfVertices;
				indices[i*2*3*outerResolution + 6*j +5] = ((i+1)*outerResolution +j+1)%numberOfVertices;
			}
		}
		
		VertexData vertexData = renderContext.makeVertexData(numberOfVertices);
		vertexData.addElement(c, VertexData.Semantic.COLOR, 3);
		vertexData.addElement(v, VertexData.Semantic.POSITION, 3);
		vertexData.addIndices(indices);
		
		return vertexData;
	}
	
	/**
	 * Generates a triangle mesh for a cylinder with resolution many segments, specified height and radius.
	 * @param resolution How many segments are used to construct the cylinder, must be greater or equals to 3.
	 * @param height The height of the cylinder.
	 * @param radius The radius of the cylinder.
	 * @param renderContext the render context to create the VertexData.
	 * @return the VertexData of a cylinder.
	 */
	public static final VertexData getCylinder(int resolution, float height, float radius, RenderContext renderContext)
	{
		assert resolution >= 3;
		
		int numberOfVertices = 2+6+2*(resolution-3); // top and bottom center-vertices, at least three segments and each new segment adds 2 vertices
		int indexOfTopCenter = numberOfVertices/2;
		
		// The vertex positions
		float[] v = new float[3*numberOfVertices]; // xyz per vertex
		
		// bottom center-vertex
		v[0] = 0;
		v[1] = -height*0.5f;
		v[2] = 0;
		
		// top center-vertex
		v[3*indexOfTopCenter] = 0;
		v[3*indexOfTopCenter + 1] = height*0.5f;
		v[3*indexOfTopCenter + 2] = 0;
		
		double angleBetweenTwoSegments = (2*Math.PI)/resolution;
		double currentAngle = 0;
		
		for(int i=3; i<3*indexOfTopCenter; i += 3) // start at three because center-vertices are already set
		{
			float x = (float) ( radius * Math.cos(currentAngle) );
			float z = (float) ( radius * Math.sin(currentAngle) );
			
			// bottom circle
			v[i] = x;
			v[i+1] = -height*0.5f;
			v[i+2] = z;
			
			// top circle
			v[3*indexOfTopCenter + i] = x;
			v[3*indexOfTopCenter + i + 1] = height*0.5f;
			v[3*indexOfTopCenter + i + 2] = z;
			
			currentAngle += angleBetweenTwoSegments; // increase currentAngle to reach next segment
		}
		
		// The vertex colors
		float c[] = new float[3*numberOfVertices]; // rgb per vertex
		
		for(int i=3; i<3*resolution; i+=6)
		{
			c[i] = 0.3f;
			c[i+1] = 0.5f;
			c[i+2] = 1;
			
			c[3*indexOfTopCenter+i] = 0.3f;
			c[3*indexOfTopCenter+i+1] = 0.5f;
			c[3*indexOfTopCenter+i+2] = 1;
		}
		// set color of bottom center-vertex
		c[0] = 0;
		c[1] = 1;
		c[2] = 1;
		// set color of top center-vertex
		c[3*indexOfTopCenter] = 0;
		c[3*indexOfTopCenter+1] = 1;
		c[3*indexOfTopCenter+2] = 1;			
		
		// The triangles
		int indices[] = new int[3*4*resolution]; // four triangles per segment and three vertex indices for each triangle
		for(int i=0; i<resolution; i++)
		{
			// build bottom disk
			indices[12*i] = 0;
			indices[12*i+1] = i+1;
			indices[12*i+2] = i+2 == resolution+1 ? 1 : i+2; // omit "overflow"
			
			// build top disk
			indices[12*i+3] = indexOfTopCenter;
			indices[12*i+4] = indexOfTopCenter + i+1;
			indices[12*i+5] = (indexOfTopCenter + i+2) == (numberOfVertices) ? indexOfTopCenter + 1 : indexOfTopCenter + i+2; // omit "overflow"
			
			// build side of segment
			indices[12*i+6] = i+1 ;
			indices[12*i+7] = indexOfTopCenter+1 +i;
			indices[12*i+8] = i+2 == indexOfTopCenter ? 1 : i+2; // omit "overflow";
			
			indices[12*i+9] = i+2 == indexOfTopCenter ? 1 : i+2; // omit "overflow";
			indices[12*i+10] = indexOfTopCenter+1 +i;
			indices[12*i+11] = indexOfTopCenter+1 +i+1 == numberOfVertices ? indexOfTopCenter+1 : indexOfTopCenter+1 +i+1; // omit "overflow"
		}
		
		VertexData vertexData = renderContext.makeVertexData(numberOfVertices);
		vertexData.addElement(c, VertexData.Semantic.COLOR, 3);
		vertexData.addElement(v, VertexData.Semantic.POSITION, 3);
		vertexData.addIndices(indices);
		
		return vertexData;
	}
	
	/**
	 * Generates a simple cube.
	 * @param renderContext the render context to create the VertexData.
	 * @return the VertexData of a cube.
	 */
	public static final VertexData getCube(RenderContext renderContext)
	{			
		// The vertex positions of the cube
		float v[] = {-1,-1,1, 1,-1,1, 1,1,1, -1,1,1,		// front face
			         -1,-1,-1, -1,-1,1, -1,1,1, -1,1,-1,	// left face
				  	 1,-1,-1,-1,-1,-1, -1,1,-1, 1,1,-1,		// back face
					 1,-1,1, 1,-1,-1, 1,1,-1, 1,1,1,		// right face
					 1,1,1, 1,1,-1, -1,1,-1, -1,1,1,		// top face
					-1,-1,1, -1,-1,-1, 1,-1,-1, 1,-1,1};	// bottom face

		// The vertex normals 
		float n[] = {0,0,1, 0,0,1, 0,0,1, 0,0,1,			// front face
			         -1,0,0, -1,0,0, -1,0,0, -1,0,0,		// left face
				  	 0,0,-1, 0,0,-1, 0,0,-1, 0,0,-1,		// back face
					 1,0,0, 1,0,0, 1,0,0, 1,0,0,			// right face
					 0,1,0, 0,1,0, 0,1,0, 0,1,0,			// top face
					 0,-1,0, 0,-1,0, 0,-1,0, 0,-1,0};		// bottom face

		// The vertex colors
		float c[] = {1,0,0, 1,0,0, 1,0,0, 1,0,0,
				     0,1,0, 0,1,0, 0,1,0, 0,1,0,
					 1,0,0, 1,0,0, 1,0,0, 1,0,0,
					 0,1,0, 0,1,0, 0,1,0, 0,1,0,
					 0,0,1, 0,0,1, 0,0,1, 0,0,1,
					 0,0,1, 0,0,1, 0,0,1, 0,0,1};

		// Texture coordinates 
		float uv[] = {0,0, 1,0, 1,1, 0,1,
				  0,0, 1,0, 1,1, 0,1,
				  0,0, 1,0, 1,1, 0,1,
				  0,0, 1,0, 1,1, 0,1,
				  0,0, 1,0, 1,1, 0,1,
				  0,0, 1,0, 1,1, 0,1};
		
		// The triangles (three vertex indices for each triangle)
		int indices[] = {0,2,3, 0,1,2,			// front face
						 4,6,7, 4,5,6,			// left face
						 8,10,11, 8,9,10,		// back face
						 12,14,15, 12,13,14,	// right face
						 16,18,19, 16,17,18,	// top face
						 20,22,23, 20,21,22};	// bottom face
		
		VertexData vertexData = renderContext.makeVertexData(v.length/3);
		vertexData.addElement(c, VertexData.Semantic.COLOR, 3);
		vertexData.addElement(v, VertexData.Semantic.POSITION, 3);
		vertexData.addElement(n, VertexData.Semantic.NORMAL, 3);
		vertexData.addElement(uv, VertexData.Semantic.TEXCOORD, 2);
		vertexData.addIndices(indices);
		
		return vertexData;
	}
}
