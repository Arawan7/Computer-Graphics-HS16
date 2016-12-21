package assignment2;

import jrtr.*;
import jrtr.glrenderer.*;
import meshes.Primitives;
import transformations.Transformations;
import userInput.SimpleMouseMotionListener;

import java.io.IOException;

import javax.swing.*;

import javax.vecmath.*;

public class VirtualTrackballScene {
	private static RenderPanel renderPanel;
	private static RenderContext renderContext;
	private static SimpleSceneManager sceneManager;
	private static Shape steeredModel;
	private static Material material;
	private static Shader diffuseShader;

	/**
	 * An extension of {@link GLRenderPanel} or {@link SWRenderPanel} to 
	 * provide a call-back function for initialization. Here we construct
	 * a simple 3D scene and start a timer task to generate an animation.
	 */ 
	public final static class SimpleRenderPanel extends GLRenderPanel
	{
		/**
		 * Initialization call-back. We initialize our renderer here.
		 * 
		 * @param r	the render context that is associated with this render panel
		 */
		public void init(RenderContext r)
		{
			renderContext = r;
			
			try {
				steeredModel = new Shape(ObjReader.read("/home/simon/Documents/Computer-Graphics-HS16/obj/airplane.obj", 5, renderContext));
			} catch (IOException e) {
				e.printStackTrace();
			}
			steeredModel = Primitives.makeHouse(r);
			
			sceneManager.getCamera().setUpVector(new Vector3f(0,1,0));
			sceneManager.getCamera().setLookAtPoint(new Vector3f(0,0,0));
			sceneManager.getCamera().setCenterOfProjection(new Vector3f(0,0,50));
			
			Transformations.translateGlobal(steeredModel, new Vector3f(-1, -0.3f, 0), 10);

//			sceneManager.getCamera().setLookAtPoint(new Vector3f(steeredModel.getTransformation().m03,steeredModel.getTransformation().m13,steeredModel.getTransformation().m23));
			
			Shape xAxis = Primitives.makeCylinder(20, 5, 0.1f, r);
			Shape yAxis = Primitives.makeCylinder(20, 5, 0.1f, r);
			Shape zAxis = Primitives.makeCylinder(20, 5, 0.1f, r);
			Transformations.translate(yAxis, 5, 2.5f);
			Transformations.rotateRoll(xAxis, -90);
			Transformations.translate(xAxis, 4, 2.5f);
			Transformations.translate(xAxis, 3, 5f);
			Transformations.rotateTilt(zAxis, 90);
			Transformations.translate(zAxis, 4, 2.5f);
			Transformations.translate(zAxis, 1, 5f);
			
			sceneManager.addShape(xAxis);
			sceneManager.addShape(yAxis);
			sceneManager.addShape(zAxis);
			sceneManager.addShape(steeredModel);
			
			diffuseShader = renderContext.makeShader();
		    try {
		    	diffuseShader.load("../jrtr/shaders/diffuse.vert", "../jrtr/shaders/diffuse.frag");
		    } catch(Exception e) {
		    	System.out.print("Problem with shader:\n");
		    	System.out.print(e.getMessage());
		    }
			// Make a material that can be used for shading
 			material = new Material();
 			material.shader = diffuseShader;
 			material.diffuseMap = renderContext.makeTexture();
 			try {
 				material.diffuseMap.load("../textures/plant.jpg");
 			} catch(Exception e) {				
 				System.out.print("Could not load texture.\n");
 				System.out.print(e.getMessage());
 			}
// 			steeredModel.setMaterial(material);
			// Add the scene to the renderer
			renderContext.setSceneManager(sceneManager);
		}
	}
	
	public static Shape getSteeredModel() { return steeredModel;}
	
	public static void repaint()
	{
		renderPanel.getCanvas().repaint();
	}
	
	/**
	 * The main function opens a 3D rendering window, implemented by the class
	 * {@link SimpleRenderPanel}. {@link SimpleRenderPanel} is then called backed 
	 * for initialization automatically. It then constructs a simple 3D scene, 
	 * and starts a timer task to generate an animation.
	 */
	public static void main(String[] args)
	{
		// Make a render panel. The init function of the renderPanel
		// (see above) will be called back for initialization.
		renderPanel = new SimpleRenderPanel();
		
		// Make the main window of this application and add the renderer to it
		JFrame jframe = new JFrame("simple");
		jframe.setSize(1000, 1000);
		jframe.setLocationRelativeTo(null); // center of screen
		jframe.getContentPane().add(renderPanel.getCanvas());// put the canvas into a JFrame window

		// Add a mouse and key listener
		sceneManager = new SimpleSceneManager();
		SimpleMouseMotionListener mouseListener = new SimpleMouseMotionListener(sceneManager.getCamera(), jframe.getWidth(), jframe.getHeight());
	    renderPanel.getCanvas().addMouseMotionListener(mouseListener);
	    renderPanel.getCanvas().addMouseListener(mouseListener);
	    // renderPanel.getCanvas().addKeyListener(new SimpleKeyListener());
		renderPanel.getCanvas().setFocusable(true);   	    	    
	    
	    jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    jframe.setVisible(true); // show window
	}
}
