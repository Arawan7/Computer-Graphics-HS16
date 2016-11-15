package primitiveMeshes;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import javax.vecmath.Vector3f;

import jrtr.RenderContext;
import jrtr.Shape;
import jrtr.VertexData;

public class Primitives {
	
	static Random rnd = new Random(System.currentTimeMillis());
	
	/**
	 * Creates a fractal landscape using Squares & Diamonds algorithm.
	 * @param n The exponent of 2 which gives the length of the fractal landscape when increased by one. Has to be greater than zero.
	 * @param maxNoise The maximum random noise that is applied.
	 * @param renderContext the render context to create the VertexData.
	 * @return The shape of a fractal landscape.
	 */
	public static final Shape makeFractalLandscape(int n, float maxNoise, RenderContext renderContext)
	{
		assert 0 < n;
		
		int size = (int)Math.pow(2, n) + 1;
		float[][] heightValues = new float[size][size];
		computeHeightValues(heightValues, size, maxNoise);
		float maxHight = 0.1f;
		
		for(int x=0; x<size; x++)
			for(int y=0; y<size; y++)
			{
				maxHight = Float.max(maxHight, heightValues[x][y]);
			}
		
		int numberOfVertices = size*size;
		float[] v = new float[3*numberOfVertices]; // xyz per vertex
		
		for(int x=0; x<size; x++)
			for(int y=0; y<size; y++)
			{
				v[3*(x*size + y)] = x-(int)size/2;
				v[3*(x*size + y)+1] = y-(int)size/2;
				v[3*(x*size + y)+2] = heightValues[x][y];
			}
		
		// The vertex colors
		float c[] = new float[3*numberOfVertices]; // rgb per vertex
		for(int x=0; x<size; x++)
			for(int y=0; y<size; y++)
			{
				Color color = getLandscapeColor(heightValues[x][y], maxHight);
				float r = color.getRed();
				float g = color.getGreen();
				float b = color.getBlue();
				
				c[3*(x*size + y)] = r/255;
				c[3*(x*size + y)+1] = g/255;
				c[3*(x*size + y)+2] = b/255;
			}
		
		// The triangles
		int indices[] = new int[3*2*(int)Math.pow(4, n)]; // three vertex indices for each triangle, two triangles per square, 4^n squares
		for(int x=0; x<size-1; x++)
			for(int y=0; y<size-1; y++)
			{
				indices[6*(x*(size-1) + y)] = x*size + y;
				indices[6*(x*(size-1) + y)+1] = (x+1)*size + y;
				indices[6*(x*(size-1) + y)+2] = (x+1)*size + y+1;
				
				indices[6*(x*(size-1) + y)+3] = (x+1)*size + y+1;
				indices[6*(x*(size-1) + y)+4] = x*size + y+1;
				indices[6*(x*(size-1) + y)+5] = x*size + y;
			}
		
		float[] normals = new float[3*numberOfVertices];
		for(int x=0; x<size; x++)
			for(int y=0; y<size; y++)
			{
				int indexOfVertex = 3*(x*size + y);
				int indexOfNextRowVertex = x==(size-1) ? 3*((x-1)*size + y) : 3*((x+1)*size + y);
				int indexOfNextColumnVertex = y==(size-1) ? 3*(x*size + y-1) : 3*(x*size + y+1);
				
				Vector3f vertex = new Vector3f(v[indexOfVertex],v[indexOfVertex+1],v[indexOfVertex+2]);
				Vector3f rowVertex = new Vector3f(v[indexOfNextRowVertex],v[indexOfNextRowVertex+1],v[indexOfNextRowVertex+2]);
				Vector3f columnVertex = new Vector3f(v[indexOfNextColumnVertex],v[indexOfNextColumnVertex+1],v[indexOfNextColumnVertex+2]);
				Vector3f crossP = new Vector3f();
				rowVertex.sub(vertex);
				columnVertex.sub(vertex);
				if(x == size-1 || y == size-1)
					crossP.cross(columnVertex,rowVertex);
				else
					crossP.cross(rowVertex,columnVertex);
				
				normals[3*(x*size + y)] = crossP.x;
				normals[3*(x*size + y)+1] = crossP.y;
				normals[3*(x*size + y)+2] = crossP.z;
			}
		
		VertexData vertexData = renderContext.makeVertexData(numberOfVertices);
		vertexData.addElement(c, VertexData.Semantic.COLOR, 3);
		vertexData.addElement(v, VertexData.Semantic.POSITION, 3);
		vertexData.addElement(normals, VertexData.Semantic.NORMAL, 3);
		vertexData.addIndices(indices);
		
		Shape fractalLandscape = new Shape(vertexData);
		return fractalLandscape;
	}
	
	private static Color getLandscapeColor(float height, float maxHight)
	{
		assert maxHight > 0f;
		
		Color sand = new Color(237, 201, 175);
		Color leaf = new Color(30, 147, 45);
		Color mountain = new Color(150, 141, 153);
		Color snow = new Color(255,255,255);
		
		float relativeHeight = height / maxHight; // between 0 and 1
		
		if(relativeHeight < 0.65f)
			return sand;
		else if(relativeHeight < 0.75f)
			return leaf;
		else if(relativeHeight < 0.9f)
			return mountain;
		else
			return snow;
	}
	
	/**
	 * Applies Squares & Diamonds algorithm recursively to heightValues.
	 * @param heightValues The 2D array to fill with height values. Dimensions have to be 2^currentSize + 1.
	 * @param size The exponent of 2 which gives the heightValues.length when increased by one.
	 * @param noise The maximum random noise that is applied.
	 */
	private static void computeHeightValues(final float[][] heightValues, int size, float noise) {
		
		int arrayPosOfHalfSize = size/2;
		int currentSize = size;
		float currentNoise = noise;
		
		// initialization
		heightValues[0][0] = (float) Math.random()*noise;
		heightValues[size-1][0] = (float) Math.random()*noise;
		heightValues[0][size-1] = (float) Math.random()*noise;
		heightValues[size-1][size-1] = (float) Math.random()*noise;
		
		ArrayList<Point> iterationPos = new ArrayList<Point>();
		ArrayList<Point> newIterationPos = new ArrayList<Point>();
		iterationPos.add(new Point(0,0));
		
		while(currentSize > 2){
			for(Point point : iterationPos){
				applySquare(heightValues, point.x, point.y, arrayPosOfHalfSize, currentSize, currentNoise);
			}
			for(Point point : iterationPos){
				applyDiamond(heightValues, point.x, point.y, arrayPosOfHalfSize, currentSize, currentNoise);
				newIterationPos.add(new Point(point.x, point.y+arrayPosOfHalfSize));
				newIterationPos.add(new Point(point.x+arrayPosOfHalfSize, point.y));
				newIterationPos.add(new Point(point.x+arrayPosOfHalfSize, point.y+arrayPosOfHalfSize));
			}
			currentSize = arrayPosOfHalfSize+1;
			arrayPosOfHalfSize = currentSize/2;
			iterationPos.addAll(newIterationPos);
			newIterationPos.clear();
			currentNoise *= 0.65f;
		}
	}
	
	private static void applySquare(final float[][] heightValues, int originX, int originY, int arrayPosOfHalfSize, int size, float noise){
		heightValues[originX+arrayPosOfHalfSize][originY+arrayPosOfHalfSize] = getMeanWithNoise(new float[]{heightValues[originX][originY],
				heightValues[originX+size-1][originY],
				heightValues[originX][originY+size-1],
				heightValues[originX+size-1][originY+size-1]},
				noise);
	}
	
	private static void applyDiamond(final float[][] heightValues, int originX, int originY, int arrayPosOfHalfSize, int size, float noise){
		heightValues[originX+arrayPosOfHalfSize][originY] = getMeanWithNoise(new float[]{heightValues[originX][originY],
				heightValues[originX+size-1][originY],
				heightValues[originX+arrayPosOfHalfSize][originY+arrayPosOfHalfSize],
				(originY-arrayPosOfHalfSize) < 0 ? Float.NaN : heightValues[originX+arrayPosOfHalfSize][originY-arrayPosOfHalfSize]},
				noise);
		
		heightValues[originX][originY+arrayPosOfHalfSize] = getMeanWithNoise(new float[]{heightValues[originX][originY],
				heightValues[originX][originY+size-1],
				heightValues[originX+arrayPosOfHalfSize][originY+arrayPosOfHalfSize],
				(originX-arrayPosOfHalfSize) < 0 ? Float.NaN : heightValues[originX-arrayPosOfHalfSize][originY+arrayPosOfHalfSize]},
				noise);
		
		heightValues[originX+arrayPosOfHalfSize][originY+size-1] = getMeanWithNoise(new float[]{heightValues[originX][originY+size-1],
				heightValues[originX+size-1][originY+size-1],
				heightValues[originX+arrayPosOfHalfSize][originY+arrayPosOfHalfSize],
				(originY+size-1+arrayPosOfHalfSize) >= heightValues.length ? Float.NaN : heightValues[originX+arrayPosOfHalfSize][originY+size-1+arrayPosOfHalfSize]},
				noise);
		
		heightValues[originX+size-1][originY+arrayPosOfHalfSize] = getMeanWithNoise(new float[]{heightValues[originX+size-1][originY],
				heightValues[originX+size-1][originY+size-1],
				heightValues[originX+arrayPosOfHalfSize][originY+arrayPosOfHalfSize],
				(originX+size-1+arrayPosOfHalfSize) >= heightValues.length ? Float.NaN : heightValues[originX+size-1+arrayPosOfHalfSize][originY+arrayPosOfHalfSize]},
				noise);
	}
	
	private static float getMeanWithNoise(float[] heights, float maxNoise)
	{
		float mean = 0f;
		int numberOfFloats=0;
		
		for(int i=0; i<heights.length; i++){
			mean += Float.isNaN(heights[i]) ? 0f : heights[i];
			numberOfFloats += Float.isNaN(heights[i]) ? 0 : 1;
		}
		return mean/numberOfFloats + rnd.nextFloat()*maxNoise;
	}

	/**
	 * Generates a triangle mesh for a torus.
	 * @param innerResolution How many segments are used to construct the inner circle, must be greater or equals to 3.
	 * @param outerResolution How many segments are used to construct the outer circle, must be greater or equals to 3.
	 * @param innerRadius Radius of the inner circle.
	 * @param outerRadius Radius of the outer circle.
	 * @param renderContext the render context to create the VertexData.
	 * @return the Shape of a torus.
	 */
	public static final Shape makeTorus(int innerResolution, int outerResolution, float innerRadius, float outerRadius, RenderContext renderContext)
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
		
		Shape torus = new Shape(vertexData);
		return torus;
	}
	
	/**
	 * Generates a triangle mesh for a cylinder with resolution many segments, specified height and radius.
	 * @param resolution How many segments are used to construct the cylinder, must be greater or equals to 3.
	 * @param height The height of the cylinder.
	 * @param radius The radius of the cylinder.
	 * @param renderContext the render context to create the VertexData.
	 * @return the Shape of a cylinder.
	 */
	public static final Shape makeCylinder(int resolution, float height, float radius, RenderContext renderContext)
	{
		assert resolution >= 3;
		
		float halfHeight = height/2;
		
		float[] vertices = new float[12*resolution + 6 + 6]; 	// 3 per vertex, n + 1 per disk, 2 disks
		float[] normals = new float[12*resolution + 6 + 6];
		
		// set vertices and normals
		// center points
		// top center
		vertices[12*resolution] = 0;
		vertices[12*resolution + 1] = halfHeight;
		vertices[12*resolution + 2] = 0;
		
		normals[12*resolution + 1] = 1;
		
		//bottom center
		vertices[12*resolution + 3] = 0;
		vertices[12*resolution + 4] = -halfHeight;
		vertices[12*resolution + 5] = 0;
		
		normals[12*resolution + 4] = -1;
		
		// disk vertices
		for (int k = 0; k < resolution; k++) {
			// top disk
			vertices[3*k] = radius * cos(k, resolution);
			vertices[3*k+1] = halfHeight;
			vertices[3*k+2] = radius * sin(k, resolution);
			
			normals[3*k + 1] = 1;
			
			// bottom disk
			vertices[3*resolution + 3*k] = radius * cos(k, resolution);
			vertices[3*resolution + 3*k + 1] = -halfHeight;
			vertices[3*resolution + 3*k + 2] = radius * sin(k, resolution);
			
			normals[3*resolution + 3*k + 1] = -1;
		}
		
		// side vertices
		for (int k = 0; k < resolution; k++) {
			// top disk
			vertices[6*resolution+3*k] = radius * cos(k, resolution);
			vertices[6*resolution+3*k+1] = halfHeight;
			vertices[6*resolution+3*k + 2] = radius * sin(k, resolution);
			
			normals[6*resolution+3*k-1] = cos(k, resolution);
			normals[6*resolution+3*k+1] = sin(k, resolution);
			
			// bottom disk
			vertices[9*resolution + 3*k] = radius * cos(k, resolution);
			vertices[9*resolution + 3*k + 1] = -halfHeight;
			vertices[9*resolution + 3*k + 2] = radius * sin(k, resolution);
			
			normals[9*resolution + 3*k-1] = cos(k, resolution);
			normals[9*resolution + 3*k+1] = sin(k, resolution);
		}
		
		// texturing vertices
		vertices[12*resolution + 6] = radius;
		vertices[12*resolution + 7] = halfHeight;
		vertices[12*resolution + 8] = 0;
		normals[12*resolution + 6] = 1;
		
		vertices[12*resolution + 9] = radius;
		vertices[12*resolution + 10] = -halfHeight;
		vertices[12*resolution + 11] = 0;
		normals[12*resolution + 9] = 1;
		
		// set colors
		float[] colors = new float[12*resolution + 6 + 6];
		for (int i = 0; i < 6*resolution+1; i+=6) {
			colors[i] = 1;
			colors[i+1] = 1;
			colors[i+2] = 1;
			colors[6*resolution + i] = 1;
			colors[6*resolution + i + 1] = 1;
			colors[6*resolution + i + 2] = 1;
		}
		
		// Texture coordinates - use texture with ratio 10:1
		float uv[] = new float[2*vertices.length/3]; // 2 per vertex (#vertices = vertices.length/3)
		
		// top disk center
		uv[2*4*resolution] = 0.95f;
		uv[2*4*resolution+1] = 0.5f;
		
		// bottom disk center
		uv[2*(4*resolution + 1)] = 0.95f;
		uv[2*(4*resolution + 1)+1] = 0.5f;
		
		for(int k = 0; k < resolution; k++) {
			// top disk circle
			uv[2*k] = 0.95f + cos(k, resolution)*0.05f;
			uv[2*k+1] = 0.5f + sin(k, resolution)*0.5f;
			
			// bottom disk circle
			uv[2*(resolution + k)] = 0.95f + cos(k, resolution)*0.05f;
			uv[2*(resolution + k)+1] = 0.5f + sin(k, resolution)*0.5f;
			
			// top side vertices
			uv[2*(2*resolution + k)] = 0;
			uv[2*(2*resolution + k)+1] = (float)k/(resolution-1);
			
			// bottom side vertices
			uv[2*(3*resolution + k)] = 0.9f;
			uv[2*(3*resolution + k)+1] = (float)k/(resolution-1);
		}
		// top disk texturing vertex
		uv[2*(4*resolution+2)] = 0;
		uv[2*(4*resolution+2)+1] = 0;
		
		// bottom disk texturing vertex
		uv[2*(4*resolution + 3)] = 0.9f;
		uv[2*(4*resolution + 3)+1] = 0;
		
		// set indices
		int[] indices = new int[12*resolution]; // 4n triangles, 3 per triangle
		
		for(int k = 0; k < resolution; k++) {
			// top disk
			indices[12*k] = 4*resolution;
			indices[12*k+1] = k;
			indices[12*k+2] = k + 1;
			
			// bottom disk
			indices[12*k+3] = 4*resolution + 1;
			indices[12*k+4] = resolution + k + 1;
			indices[12*k+5] = resolution + k;
			
			// side
			indices[12*k+6] = 2 * resolution + k + 1;
			indices[12*k+7] = 2 * resolution + k;
			indices[12*k+8] = 3 * resolution + k;
			
			indices[12*k+9] = 3 * resolution + k;
			indices[12*k+10] = 3 * resolution + k + 1;
			indices[12*k+11] = 2 * resolution + k + 1;
		}
		
		// finishing
		indices[12*(resolution-1)] = 4*resolution;
		indices[12*(resolution-1)+1] = resolution - 1;
		indices[12*(resolution-1)+2] = 0;
		
		indices[12*(resolution-1)+3] = 4*resolution + 1;
		indices[12*(resolution-1)+4] = resolution;
		indices[12*(resolution-1)+5] = 2*resolution - 1;
		
		indices[12*(resolution-1)+6] = 4*resolution+2; // 2*resolution; // top right
		indices[12*(resolution-1)+7] = 3*resolution - 1; // top left
		indices[12*(resolution-1)+8] = 4*resolution - 1; // bottom left
		
		indices[12*(resolution-1)+9] = 4*resolution - 1; // bottom left
		indices[12*(resolution-1)+10] = 4*resolution+3; // 3*resolution; // bottom right
		indices[12*(resolution-1)+11] = 4*resolution+2; // 2*resolution; // top right
		
		VertexData vertexData = renderContext.makeVertexData(4*resolution + 2 + 2);
		vertexData.addElement(vertices, VertexData.Semantic.POSITION, 3);
		vertexData.addElement(colors, VertexData.Semantic.COLOR, 3);
		vertexData.addElement(normals, VertexData.Semantic.NORMAL, 3);
		vertexData.addElement(uv, VertexData.Semantic.TEXCOORD, 2);
		vertexData.addIndices(indices);
		
		Shape cylinder = new Shape(vertexData);
		return cylinder;
	}
	
	/**
	 * Calculates sin(2pi/res * k)
	 * @param k the k. point on the circle.
	 * @param res the number of points on the circle
	 * @return sin(2pi/res * k)
	 */
	private static final float sin(int k, int res) {
		return ((Double)Math.sin((2*Math.PI/res)*k)).floatValue();
	}

	/**
	 * Calculates cos(2pi/res * k)
	 * @param k the k. point on the circle.
	 * @param res the number of points on the circle
	 * @return cos(2pi/res * k)
	 */
	private static final float cos(int k, int res) {
		return ((Double)Math.cos((2*Math.PI/res)*k)).floatValue();
	}
	
	public static final Shape makeCylinderWithoutNormalsAndUVs(int resolution, float height, float radius, RenderContext renderContext)
	{
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
		
		Shape cylinder = new Shape(vertexData);
		return cylinder;
	}
	
	/**
	 * Generates a simple cube.
	 * @param renderContext the render context to create the VertexData.
	 * @return the Shape of a cube.
	 */
	public static final Shape makeCube(RenderContext renderContext)
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
		
		Shape cube = new Shape(vertexData);
		return cube;
	}
	
	public static final Shape makeHouse(RenderContext renderContext)
	{
		// A house
		float vertices[] = {-4,-4,4, 4,-4,4, 4,4,4, -4,4,4,		// front face
							-4,-4,-4, -4,-4,4, -4,4,4, -4,4,-4, // left face
							4,-4,-4,-4,-4,-4, -4,4,-4, 4,4,-4,  // back face
							4,-4,4, 4,-4,-4, 4,4,-4, 4,4,4,		// right face
							4,4,4, 4,4,-4, -4,4,-4, -4,4,4,		// top face
							-4,-4,4, -4,-4,-4, 4,-4,-4, 4,-4,4, // bottom face
	
							-20,-4,20, 20,-4,20, 20,-4,-20, -20,-4,-20, // ground floor
							-4,4,4, 4,4,4, 0,8,4,				// the roof
							4,4,4, 4,4,-4, 0,8,-4, 0,8,4,
							-4,4,4, 0,8,4, 0,8,-4, -4,4,-4,
							4,4,-4, -4,4,-4, 0,8,-4};
	
		float normals[] = {0,0,1,  0,0,1,  0,0,1,  0,0,1,		// front face
						   -1,0,0, -1,0,0, -1,0,0, -1,0,0,		// left face
						   0,0,-1, 0,0,-1, 0,0,-1, 0,0,-1,		// back face
						   1,0,0,  1,0,0,  1,0,0,  1,0,0,		// right face
						   0,1,0,  0,1,0,  0,1,0,  0,1,0,		// top face
						   0,-1,0, 0,-1,0, 0,-1,0, 0,-1,0,		// bottom face
	
						   0,1,0,  0,1,0,  0,1,0,  0,1,0,		// ground floor
						   0,0,1,  0,0,1,  0,0,1,				// front roof
						   0.707f,0.707f,0, 0.707f,0.707f,0, 0.707f,0.707f,0, 0.707f,0.707f,0, // right roof
						   -0.707f,0.707f,0, -0.707f,0.707f,0, -0.707f,0.707f,0, -0.707f,0.707f,0, // left roof
						   0,0,-1, 0,0,-1, 0,0,-1};				// back roof
						   
		float colors[] = {1,0,0, 1,0,0, 1,0,0, 1,0,0,
						  0,1,0, 0,1,0, 0,1,0, 0,1,0,
						  1,0,0, 1,0,0, 1,0,0, 1,0,0,
						  0,1,0, 0,1,0, 0,1,0, 0,1,0,
						  0,0,1, 0,0,1, 0,0,1, 0,0,1,
						  0,0,1, 0,0,1, 0,0,1, 0,0,1,
		
						  0,0.5f,0, 0,0.5f,0, 0,0.5f,0, 0,0.5f,0,			// ground floor
						  0,0,1, 0,0,1, 0,0,1,							// roof
						  1,0,0, 1,0,0, 1,0,0, 1,0,0,
						  0,1,0, 0,1,0, 0,1,0, 0,1,0,
						  0,0,1, 0,0,1, 0,0,1,};
	
		// Set up the vertex data
		VertexData vertexData = renderContext.makeVertexData(42);
	
		// Specify the elements of the vertex data:
		// - one element for vertex positions
		vertexData.addElement(vertices, VertexData.Semantic.POSITION, 3);
		// - one element for vertex colors
		vertexData.addElement(colors, VertexData.Semantic.COLOR, 3);
		// - one element for vertex normals
		vertexData.addElement(normals, VertexData.Semantic.NORMAL, 3);
		
		// The index data that stores the connectivity of the triangles
		int indices[] = {0,2,3, 0,1,2,			// front face
						 4,6,7, 4,5,6,			// left face
						 8,10,11, 8,9,10,		// back face
						 12,14,15, 12,13,14,	// right face
						 16,18,19, 16,17,18,	// top face
						 20,22,23, 20,21,22,	// bottom face
		                 
						 24,26,27, 24,25,26,	// ground floor
						 28,29,30,				// roof
						 31,33,34, 31,32,33,
						 35,37,38, 35,36,37,
						 39,40,41};	
	
		vertexData.addIndices(indices);
	
		Shape house = new Shape(vertexData);
		
		return house;
	}
}
