package assignment2;

import jrtr.*;
import jrtr.glrenderer.*;
import primitiveMeshes.Primitives;

import javax.swing.*;
import javax.vecmath.Vector3f;

public class HouseScene {
	private static RenderPanel renderPanel;
	private static RenderContext renderContext;
	private static SimpleSceneManager sceneManager;
	private static Shape house;

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
			
			// create the object
			house = Primitives.makeHouse(r);
			
			// Make a scene manager and add the objects
			sceneManager = new SimpleSceneManager();
			sceneManager.addShape(house);
			sceneManager.getCamera().setUpVector(new Vector3f(0,1,0));
			
			sceneManager.getCamera().setLookAtPoint(new Vector3f(0,0,0));
			sceneManager.getCamera().setCenterOfProjection(new Vector3f(0,0,40));
			
//			sceneManager.getCamera().setLookAtPoint(new Vector3f(-5,0,0));
//			sceneManager.getCamera().setCenterOfProjection(new Vector3f(-10,40,40));
			

			// Add the scene to the renderer
			renderContext.setSceneManager(sceneManager);
		}
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
	    
	    jframe.setVisible(true); // show window
	}
}
