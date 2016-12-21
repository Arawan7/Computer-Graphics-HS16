package meshes;

import javax.vecmath.Vector2f;

import jrtr.RenderContext;
import jrtr.Shape;
import jrtr.VertexData;

public class BodiesOfRevolutions {
	
	/**
	 * Generates surfaces of revolution using Bézier curves.
	 * @param n Count of Bézier segments.
	 * @param controlPoints Array of the Bézier control points in the xy plane. need (n − 1) × 3 + 4 control points
	 * @param curveResolution Number of points which should be evaluated along the curve.
	 * @param rotationResolution Number of rotation steps used for construction.
	 * @param renderContext The render context of the scene.
	 * @return the shape that was generated.
	 */
	public static Shape createRotationalSurface(int n, Vector2f[] controlPoints, int curveResolution, int rotationResolution, RenderContext renderContext){
		assert n>0;
		assert controlPoints.length == (n-1)*3+4;
		assert curveResolution>1;
		assert rotationResolution>2;
		
		int numberOfVertices = curveResolution*rotationResolution;
		float[] v = new float[3*numberOfVertices];		// xyz per vertex
		
		float step = 1f/(curveResolution-1);
		for(int i=0; i<curveResolution; i++){
			int segmentNumber = (int) Math.floor(i*n/curveResolution);
			Vector2f[] p = new Vector2f[]{ controlPoints[3*segmentNumber+0], controlPoints[3*segmentNumber+1], controlPoints[3*segmentNumber+2], controlPoints[3*segmentNumber+3] };
			Vector2f point = cubicBézier(i*step, p);
			
			v[3*i] = point.x;
			v[3*i+1] = point.y;
			v[3*i+2] = 0;
		}
		double angle = 2*Math.PI / rotationResolution;
		double stepAngle = angle;
		for(int i=1; i<rotationResolution; i++)
		{
			for(int j=0; j<curveResolution; j++){
				// copy values and paste with rotated values about y axis
				v[3*(i*curveResolution+j)] = (float) ( v[3*j] * Math.cos(angle) - v[3*j+2] * Math.sin(angle) );
				v[3*(i*curveResolution+j)+1] = v[3*j+1];
				v[3*(i*curveResolution+j)+2] = (float) ( v[3*j] * Math.sin(angle) + v[3*j+2] * Math.cos(angle) );
			}
			angle += stepAngle;
		}
		
		int indices[] = new int[2*(numberOfVertices)*3];
		for(int i=0; i<rotationResolution; i++)
		{
			for(int j=0; j<curveResolution-1; j++)
			{
				indices[i*2*3*curveResolution + 6*j] = (i*curveResolution +j)%numberOfVertices;
				indices[i*2*3*curveResolution + 6*j +1] = (i*curveResolution +j+1)%numberOfVertices;
				indices[i*2*3*curveResolution + 6*j +2] = ((i+1)*curveResolution +j)%numberOfVertices;
				
				indices[i*2*3*curveResolution + 6*j +3] = ((i+1)*curveResolution +j)%numberOfVertices;
				indices[i*2*3*curveResolution + 6*j +4] = (i*curveResolution +j+1)%numberOfVertices;
				indices[i*2*3*curveResolution + 6*j +5] = ((i+1)*curveResolution +j+1)%numberOfVertices;
			}
		}
		
		float[] normals = new float[3*numberOfVertices];
		
		float c[] = new float[3*numberOfVertices];		// rgb per vertex
		for(int i=0; i<rotationResolution; i++)
		{
			float r,g,b;
			r = (float) Math.random();
			g = (float) Math.random();
			b = (float) Math.random();
			
			for(int j=0; j<3*curveResolution; j+=3)
			{					
				c[i*3*curveResolution + j] = r;
				c[i*3*curveResolution + j+1] = g;
				c[i*3*curveResolution + j+2] = b;
			}
		}
		
		VertexData vertexData = renderContext.makeVertexData(numberOfVertices);
		vertexData.addElement(c, VertexData.Semantic.COLOR, 3);
		vertexData.addElement(v, VertexData.Semantic.POSITION, 3);
		vertexData.addElement(normals, VertexData.Semantic.NORMAL, 3);
		vertexData.addIndices(indices);
		
		Shape fractalLandscape = new Shape(vertexData);
		return fractalLandscape;
	}
	
	private static Vector2f cubicBézier(float t, Vector2f[] p){
		assert p.length == 4;
		
		Vector2f x0 = new Vector2f(p[0]);
		x0.scale(-t*t*t + 3*t*t - 3*t + 1);
		
		Vector2f x1 = new Vector2f(p[1]);
		x1.scale(3*t*t*t - 6*t*t + 3*t);
		
		Vector2f x2 = new Vector2f(p[2]);
		x2.scale(-3*t*t*t + 3*t*t);
		
		Vector2f x3 = new Vector2f(p[3]);
		x3.scale(t*t*t);
		
		Vector2f x = x0;
		x.add(x1);
		x.add(x2);
		x.add(x3);

		return new Vector2f(x);
	}
}
