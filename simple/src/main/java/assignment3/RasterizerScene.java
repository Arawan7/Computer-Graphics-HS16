package assignment3;

import jrtr.*;
import jrtr.glrenderer.*;
import jrtr.swrenderer.SWRenderPanel;
import jrtr.swrenderer.SWTexture;
import meshes.Primitives;
import userInput.MouseHandler;

import javax.swing.*;

public class RasterizerScene {
	private static RenderPanel renderPanel;
	private static RenderContext renderContext;
	private static SimpleSceneManager sceneManager;
	private static Shape cylinder;
	static Shader normalShader;
	static Shader diffuseShader;
	static Material material;

	/**
	 * An extension of {@link GLRenderPanel} or {@link SWRenderPanel} to 
	 * provide a call-back function for initialization. Here we construct
	 * a simple 3D scene and start a timer task to generate an animation.
	 */ 
	public final static class SimpleRenderPanel extends SWRenderPanel
	{
		/**
		 * Initialization call-back. We initialize our renderer here.
		 * 
		 * @param r	the render context that is associated with this render panel
		 */
		public void init(RenderContext r)
		{
			renderContext = r;
			
			// Load some more shaders
		    normalShader = renderContext.makeShader();
		    try {
		    	normalShader.load("../jrtr/shaders/normal.vert", "../jrtr/shaders/normal.frag");
		    } catch(Exception e) {
		    	System.out.print("Problem with shader:\n");
		    	System.out.print(e.getMessage());
		    }
	
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
			material.swTexture = (SWTexture) renderContext.makeTexture();
			try {
				material.swTexture.load("/home/simon/Documents/Computer-Graphics-HS16/textures/wood.jpg");
//				material.swTexture.load("/home/simon/Documents/Computer-Graphics-HS16/textures/cylinder.jpg");
//				material.swTexture.load("/home/simon/Documents/Computer-Graphics-HS16/textures/sky.png");
//				material.swTexture.load("/home/simon/Documents/Computer-Graphics-HS16/textures/stone.jpeg");
//				material.swTexture.load("/home/simon/Documents/Computer-Graphics-HS16/textures/cylinderPixel.png");
			} catch(Exception e) {
				System.out.print("Could not load texture.\n");
				System.out.print(e.getMessage());
			}
			
			// create the object
			cylinder = Primitives.makeCylinder(30, 10, 1, r);
//			cylinder = Primitives.makeTorus(3, 3, 2, 2, r);
//			cylinder = Primitives.makeCube(r);
			
			// Make a scene manager and add the objects
			sceneManager = new SimpleSceneManager();
			sceneManager.addShape(cylinder);

			// Add the scene to the renderer
			renderContext.setSceneManager(sceneManager);
			cylinder.setMaterial(material);
		}
	}
	
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
		
		MouseHandler mouseListener = new MouseHandler(sceneManager.getCamera());
	    renderPanel.getCanvas().addMouseMotionListener(mouseListener);
	    renderPanel.getCanvas().addMouseListener(mouseListener);
	    renderPanel.getCanvas().addMouseWheelListener(mouseListener);
	    
	    jframe.setVisible(true); // show window
	}
}