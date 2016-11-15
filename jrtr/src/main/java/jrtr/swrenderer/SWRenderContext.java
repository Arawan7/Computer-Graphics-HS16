package jrtr.swrenderer;

import jrtr.Material;
import jrtr.RenderContext;
import jrtr.RenderItem;
import jrtr.SceneManagerInterface;
import jrtr.SceneManagerIterator;
import jrtr.Shader;
import jrtr.Texture;
import jrtr.VertexData;
import jrtr.glrenderer.GLRenderPanel;

import java.awt.image.*;
import java.util.ListIterator;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;


/**
 * A skeleton for a software renderer. It works in combination with
 * {@link SWRenderPanel}, which displays the output image. In project 3 
 * you will implement your own rasterizer in this class.
 * <p>
 * To use the software renderer, you will simply replace {@link GLRenderPanel} 
 * with {@link SWRenderPanel} in the user application.
 */
public class SWRenderContext implements RenderContext {

	private SceneManagerInterface sceneManager;
	private BufferedImage colorBuffer;
	private BufferedImage texture=null;
	private Material material=null;
		
	public void setSceneManager(SceneManagerInterface sceneManager)
	{
		this.sceneManager = sceneManager;
	}
	
	/**
	 * This is called by the SWRenderPanel to render the scene to the 
	 * software frame buffer.
	 */
	public void display()
	{
		if(sceneManager == null) return;
		
		beginFrame();
	
		SceneManagerIterator iterator = sceneManager.iterator();	
		while(iterator.hasNext())
		{
			draw(iterator.next());
		}		
		
		endFrame();
	}

	/**
	 * This is called by the {@link SWJPanel} to obtain the color buffer that
	 * will be displayed.
	 */
	public BufferedImage getColorBuffer()
	{
		return colorBuffer;
	}
	
	/**
	 * Set a new viewport size. The render context will also need to store
	 * a viewport matrix, which you need to reset here. 
	 */
	public void setViewportSize(int width, int height)
	{
		colorBuffer = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
	}
		
	/**
	 * Clear the framebuffer here.
	 */
	private void beginFrame()
	{
	}
	
	private void endFrame()
	{		
	}
	
	/**
	 * The main rendering method. You will need to implement this to draw
	 * 3D objects.
	 */
	private void draw(RenderItem renderItem)
	{
		clear();
		setMaterial(renderItem.getShape().getMaterial());
		
		// calculate 3D object coordinates to 2D pixel coordinates projection matrix
		Matrix4f objMatrix = renderItem.getT();
		Matrix4f cam = sceneManager.getCamera().getCameraMatrix();
		Matrix4f projectionMatrix = sceneManager.getFrustum().getProjectionMatrix();
		Matrix4f viewportMatrix = new Matrix4f();
		int imageOriginX = colorBuffer.getMinX();
		int imageOriginY = colorBuffer.getMinY();
		int height = colorBuffer.getHeight();
		int width = colorBuffer.getWidth();
		viewportMatrix.setRow(0, width/2f, 0, 0, (2*imageOriginX+width)/2f);
		viewportMatrix.setRow(1, 0, -height/2f, 0, (2*imageOriginY+height)/2f);
		viewportMatrix.setRow(2, 0, 0, 0.5f, 0.5f);
		viewportMatrix.setRow(3, 0, 0, 0, 1);
		
		Matrix4f objectToPixelCoordProj = new Matrix4f(objMatrix);
		objectToPixelCoordProj.mul(cam, objectToPixelCoordProj);
		objectToPixelCoordProj.mul(projectionMatrix, objectToPixelCoordProj);
		objectToPixelCoordProj.mul(viewportMatrix, objectToPixelCoordProj);
		
		// collect data from renderItem
		SWVertexData vertexData = (SWVertexData) renderItem.getShape().getVertexData();
		
		ListIterator<VertexData.VertexElement> itr = vertexData.getElements().listIterator(0);
		int numberOfVertices = vertexData.getNumberOfVertices();

		int[] indices = vertexData.getIndices();
		float[] verticesData = new float[3*numberOfVertices];
		float[] colorsData = new float[3*numberOfVertices];
		float[] normalsData = new float[3*numberOfVertices];
		float[] uvsData = new float[2*numberOfVertices];
		
		while(itr.hasNext())
		{
			VertexData.VertexElement e = itr.next();
			switch(e.getSemantic())
			{
				case  POSITION:
				{
					verticesData = e.getData();
					break;
				}
				case COLOR:
				{
					colorsData = e.getData();
					break;
				}
				case NORMAL:
				{
					normalsData = e.getData();
					break;
				}
				case TEXCOORD:
				{
					uvsData = e.getData();
					break;
				}
			}
		}
		
		// The zBuffer initialized with zero values
		double[][] zBuffer = new double[colorBuffer.getWidth()][colorBuffer.getHeight()];
		for(int x=0; x<zBuffer.length; x++)
		{
			for(int y=0; y<zBuffer[0].length; y++)
			{
				zBuffer[x][y]=1;
			}
		}

		// arrays for the three vertices of the triangle to be rasterized
		float[][] positions = new float[3][4];
		float[][] colors = new float[3][3];
		float[][] normals = new float[3][3];
		float[][] uvs = new float[3][2];
		
		int k = 0;
		for(int j=0; j<indices.length; j++)
		{

			int i = indices[j];

			Vector4f vertex = new Vector4f(verticesData[3*i],verticesData[3*i+1],verticesData[3*i+2], 1f);
			objectToPixelCoordProj.transform(vertex);
			
			positions[k][0] = vertex.x;
			positions[k][1] = vertex.y;
			positions[k][2] = vertex.z;
			positions[k][3] = vertex.w;
			
			colors[k][0] = colorsData[3*i];
			colors[k][1] = colorsData[3*i+1];
			colors[k][2] = colorsData[3*i+2];
			
			normals[k][0] = normalsData[3*i];
			normals[k][1] = normalsData[3*i+1];
			normals[k][2] = normalsData[3*i+2];
			
			uvs[k][0] = uvsData[2*i];
			uvs[k][1] = uvsData[2*i+1]; 
			
			k++;
			
			// Exercise 1
			if(vertex.w>0)
			{
				Vector2f imageVertex = new Vector2f(vertex.x/vertex.w, vertex.y/vertex.w);
				if(imageVertex.x>=0 && imageVertex.x<colorBuffer.getWidth() && imageVertex.y>=0 && imageVertex.y<colorBuffer.getHeight())
					colorBuffer.setRGB((int) imageVertex.x, (int) imageVertex.y, (int) (Math.pow(2, 24)-1));
			}
			
			// Exercise 2
			if(k==3)
			{
				Matrix3f triangMatrix = new Matrix3f();
				triangMatrix.setColumn(0, positions[0][0], positions[0][1], positions[0][3]);
				triangMatrix.setColumn(1, positions[1][0], positions[1][1], positions[1][3]);
				triangMatrix.setColumn(2, positions[2][0], positions[2][1], positions[2][3]);
				if(triangMatrix.determinant()>=0)
				{
					rasterizeTriangle(positions, colors, normals, uvs, zBuffer);
				}
				k=0;
			}
		}
	}
	
	private void rasterizeTriangle(float[][] positions, float[][] colors, float[][] normals, float[][] uvs, double[][] zBuffer)
	{
		// homogeneous 2D coordinates - ignore z coordinate
		float[][] hom2DCoords = new float[3][3];
		for(int i=0; i<3; i++){
			hom2DCoords[i][0] = positions[i][0]; // x
			hom2DCoords[i][1] = positions[i][1]; // y
			hom2DCoords[i][2] = positions[i][3]; // w
		}
		
		// calculate barycentric coordinates matrix - edge functions
		Matrix3f barCoordMatrix = new Matrix3f();
		barCoordMatrix.setRow(0, hom2DCoords[0]);
		barCoordMatrix.setRow(1, hom2DCoords[1]);
		barCoordMatrix.setRow(2, hom2DCoords[2]);
		barCoordMatrix.invert();
				
		// all w's positive
		if(hom2DCoords[0][2]>0 && hom2DCoords[1][2]>0 && hom2DCoords[2][2]>0)
		{
			float[][] pixelsOnImagePlane = new float[3][2];
			for(int i=0; i<3; i++)
			{
				// homogeneous division
				pixelsOnImagePlane[i][0]=hom2DCoords[i][0]/hom2DCoords[i][2]; // x/w
				pixelsOnImagePlane[i][1]=hom2DCoords[i][1]/hom2DCoords[i][2]; // y/w
			}
			
			int minX=getPixelMinCoord(pixelsOnImagePlane,0);
			int minY=getPixelMinCoord(pixelsOnImagePlane,1);
			int maxX=getPixelMaxCoord(pixelsOnImagePlane,0);
			int maxY=getPixelMaxCoord(pixelsOnImagePlane,1);
			
			// test pixels inside bounding box of the triangle
			for(int x=minX; x<=maxX; x++)
			{
				for(int y=minY; y<=maxY; y++)
				{
					drawPixel(x, y, barCoordMatrix, colors, uvs, zBuffer);
				}
			}
		}
		else if(hom2DCoords[0][2]<0 && hom2DCoords[1][2]<0 && hom2DCoords[2][2]<0)
		{
			// do nothing because triangle is completely behind camera
		}
		else
		{
			// test all pixels
			for(int x=0; x<colorBuffer.getWidth(); x++)
			{
				for(int y=0; y<colorBuffer.getHeight(); y++)
				{
					drawPixel(x, y, barCoordMatrix, colors, uvs, zBuffer);
				}
			}
		}
	}
	
	/**
	 * Calculates the min pixel coordinate of the positions in the specified dimension.
	 * @param positions The positions to take the min from.
	 * @param dimension The dimension to take the min from (0: dimension x, 1: dimension y).
	 * @return the smallest pixel coordinate of the pixels as an integer. The smallest value that will be returned is 0.
	 */
	private int getPixelMinCoord(float[][] positions, int dimension)
	{
		float min = positions[0][dimension];
		for(int i=1; i<positions.length; i++)
		{
			min = Float.min(min, positions[i][dimension]);
		}
		
		return min < 0 ? 0 : (int)min;
	}
	
	/**
	 * Calculates the max pixel coordinate of the positions in the specified dimension.
	 * @param positions The positions to take the max from.
	 * @param dimension The dimension to take the max from (0: dimension x, 1: dimension y).
	 * @return the biggest pixel coordinate of the pixels as an integer. The biggest value that will be returned is colorBuffer.getWidth()-1.
	 */
	private int getPixelMaxCoord(float[][] positions, int dimension)
	{
		float max = positions[0][dimension];
		for(int i=1; i<positions.length; i++)
		{
			max= Float.max(max, positions[i][dimension]);
		}
		
		return max+1 >= colorBuffer.getWidth() ? colorBuffer.getWidth()-1 : (int)max+1;
	}
	
	private void drawPixel(int x, int y, Matrix3f barCoordMatrix, float[][] colors, float[][] uvs, double[][] zBuffer)
	{
		// barycentric coordinates matrix transposed for finding a_w, b_w, c_w
		Matrix3f barCoordMatrixTranspose = new Matrix3f();
		barCoordMatrixTranspose.transpose(barCoordMatrix);
		
		Vector3f pixel = new Vector3f(x,y,1);
		// apply edge functions
		barCoordMatrixTranspose.transform(pixel);
		float alpha_w = pixel.x;
		float bita_w = pixel.y;
		float gamma_w = pixel.z;
		// pixel is inside triangle
		if(alpha_w>0 && bita_w>0 && gamma_w>0){
			double oneOverW = getOneOverW(new Vector3f(x,y,1), new Matrix3f(barCoordMatrix));
			double z = zBuffer[x][y];
			if(z > oneOverW)
			{
				zBuffer[x][y]=oneOverW;
				int color=0;
				if(texture!=null)
					color = getTextureColor(new Vector3f(x,y,1), uvs, new Matrix3f(barCoordMatrix));
				else
					color = getColor(new Vector3f(x,y,1), colors, new Matrix3f(barCoordMatrix));
				colorBuffer.setRGB(x, y, color);
			}
		}
	}
	
	private int getTextureColor(Vector3f pixel, float[][] textures, Matrix3f barCoord)
	{
		// u component
		double uCoord = getColorCoord(pixel, textures, barCoord, 0);
		// v component
		double vCoord =  getColorCoord(pixel, textures, barCoord, 1);
		
		
		int textHeight = texture.getHeight();
		int textWidth = texture.getWidth();
		double u = uCoord*textWidth;
		double v = vCoord*textHeight;
		
//		int color = getNearestNeighbourColor(u, v);
		int color = getBilinearInterpolationColor(u, v);
		
		return color;
	}
	
	private int getNearestNeighbourColor(double u, double v)
	{
		int uInt = (int) u;
		int vInt = (int) v;
		
		int uNearest = uInt;
		int vNearest = vInt;
		
		double distance=1;
		for(int i=0; i<2; i++)
		{
			for(int j=0; j<2; j++)
			{
				double distanceTmp = Math.pow(u-(uInt+i), 2)+Math.pow(v-(vInt+j), 2);
				if((uInt+i)<texture.getWidth()&&(vInt+j)<texture.getHeight())
				{
					if(distanceTmp<distance)
					{
						distance=distanceTmp;
						uNearest = uInt+i;
						vNearest = vInt+j;
					}
				}
			}
		}
		
		int color = texture.getRGB(uNearest, vNearest);
		return color;
	}
	
	private int getBilinearInterpolationColor(double u, double v)
	{
		int uInt = (int) u;
		int vInt = (int) v;
		
		int c=0;
		
		if((uInt+1)<texture.getWidth()&&(vInt+1)<texture.getHeight())
		{
			double w_u = u-uInt;
			double w_u1 = 1-w_u;
						
			// for blue, green, red
			for(int i=0; i<3; i++){
				
				int tex00 = (texture.getRGB(uInt, vInt)>> 8*i)&0xFF;
				int tex01 = (texture.getRGB(uInt+1, vInt)>>8*i)&0xFF;
				int tex10 = (texture.getRGB(uInt, vInt+1)>>8*i)&0xFF;
				int tex11 = (texture.getRGB(uInt+1, vInt+1)>>8*i)&0xFF;
				
				double c_b = tex00*w_u1+tex01*w_u;
				double c_t = tex10*w_u1+tex11*w_u;
				
				double w_v = (v-vInt);
				double w_v1 = 1-w_v; 
				double c1 = c_b*w_v1+c_t*w_v;
				int cInt =(int) c1;
				c += (cInt<<8*i);
			}
		}
		else
			c = texture.getRGB(texture.getWidth()-1, texture.getHeight()-1);
			
		return  c;
	}
	
	
		
	private int getColor(Vector3f pixel, float[][] colors, Matrix3f barCoord)
	{
		// red component
		int redColor = (int) (getColorCoord(pixel, colors, barCoord, 0)*(Math.pow(2, 8)-1))<<16;
		// green component
		int greenColor = (int) (getColorCoord(pixel, colors, barCoord, 1)*(Math.pow(2, 8)-1))<<8;
		// blue component
		int blueColor = (int) (getColorCoord(pixel, colors, barCoord, 2)*(Math.pow(2,8)-1));
		
		return redColor+greenColor+blueColor;
	}
	
	private double getColorCoord(Vector3f pixel, float[][] colors, Matrix3f barCoord, int coord)
	{		
		float u0 = colors[0][coord];
		float u1 = colors[1][coord];
		float u2 = colors[2][coord];
		
		Vector3f barColorCoord = new Vector3f(u0, u1, u2);
		barCoord.transform(barColorCoord);
		
		float u = barColorCoord.dot(pixel);
		double oneOverW = getOneOverW(pixel, barCoord);
		
		double color = u/oneOverW;
		if(color<0 || color>1)
			;//System.out.println("color: "+color);
			
		return color;
	}
	
	private double getOneOverW(Vector3f pixel, Matrix3f barCoord)
	{
		Vector3d pixel2 = new Vector3d(pixel);
		Vector3d constantFunction = new Vector3d(1,1,1);
		Matrix3d barCoord2 = new Matrix3d(barCoord);
		barCoord2.transform(constantFunction);
		
		return constantFunction.dot(pixel2);
	}
	
	/**
	 * Does nothing. We will not implement shaders for the software renderer.
	 */
	public Shader makeShader()	
	{
		return new SWShader();
	}
	
	/**
	 * Does nothing. We will not implement shaders for the software renderer.
	 */
	public void useShader(Shader s)
	{
	}
	
	/**
	 * Does nothing. We will not implement shaders for the software renderer.
	 */
	public void useDefaultShader()
	{
	}

	/**
	 * Does nothing. We will not implement textures for the software renderer.
	 */
	public Texture makeTexture()
	{
		return new SWTexture();
	}
	
	public VertexData makeVertexData(int n)
	{
		return new SWVertexData(n);		
	}
	
	private void setMaterial(Material m)
	{
		material=m;
		if(material!=null)
			texture=material.swTexture.texture;
		else
			texture=null;
			
	}
	
	private void clear()
	{
		int width = colorBuffer.getWidth();
		int height = colorBuffer.getHeight();
		setViewportSize(width, height);
	}
}
