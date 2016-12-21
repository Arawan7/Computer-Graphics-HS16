package assignment6;

import jrtr.*;
import jrtr.glrenderer.*;
import meshes.BodiesOfRevolutions;
import meshes.Primitives;
import userInput.MouseHandler;

import java.util.Timer;
import java.util.TimerTask;

import javax.swing.*;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

public class SurfacesScene {
	private static RenderPanel renderPanel;
	private static RenderContext renderContext;
	private static GraphSceneManager sceneManager;
	private static int currentAnimationExecutionsPerSecond, animationExecutionsPerSecond, fps;
	private static boolean paused;
	static Shader diffuseShader;
	static Material materialWood;

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
			
			diffuseShader = renderContext.makeShader();
		    try {
	    		diffuseShader.load("../jrtr/shaders/diffusePointLights.vert", "../jrtr/shaders/diffusePointLights.frag");
		    } catch(Exception e) {
		    	System.out.print("Problem with shader:\n");
		    	System.out.print(e.getMessage());
		    }
			
		    materialWood = new Material();
			materialWood.shader = diffuseShader;
			materialWood.diffuseMap = renderContext.makeTexture();
			materialWood.diffuse = new Vector3f(1f,1f,1f);
			try {
				materialWood.diffuseMap.load("/home/simon/Documents/Computer-Graphics-HS16/textures/wood.jpg");
			} catch(Exception e) {
				System.out.print("Could not load texture.\n");
				System.out.print(e.getMessage());
			}
			
			
			
		/* surfaces construction */			
			Shape rotationSurfaceS = BodiesOfRevolutions.createRotationalSurface(2, new Vector2f[]{new Vector2f(0,0), new Vector2f(0.75f,0), new Vector2f(1.25f,0),
					new Vector2f(2,2), new Vector2f(2,2.75f), new Vector2f(2,3.25f), new Vector2f(4,4)}, 30, 30, r);
			
			ShapeNode rotationSurfaceSN = new ShapeNode(rotationSurfaceS);
			
			TransformGroup rootNode = new TransformGroup(new Vector3f(0,0,0), new Node[]{rotationSurfaceSN});
			
			// add the graph of the robot to the graph scene manager
			sceneManager.setRootNode(rootNode);
			
			// Add the scene to the renderer
			renderContext.setSceneManager(sceneManager);
		    
		    // Register a timer task
		    Timer timer = new Timer();
		    
		    fps = 100;
		    animationExecutionsPerSecond = 25;
		    currentAnimationExecutionsPerSecond = animationExecutionsPerSecond;
		    timer.scheduleAtFixedRate(new AnimationTask(), 0, (long)(1000/fps));
		}
	}
	
	/**
	 * A timer task that generates an animation. This task triggers
	 * the redrawing of the 3D scene every time the animation is executed.
	 */
	public static class AnimationTask extends TimerTask
	{
		int counterToFireAnimation = 0;
		int counterToInvertRotations = 0;
		public void run()
		{
			if(paused)
				counterToFireAnimation = 0;
			else
			{
				counterToFireAnimation++;
				if(counterToFireAnimation >= fps/currentAnimationExecutionsPerSecond)
				{
					robotAnimation();
					// Trigger redrawing of the render window
					renderPanel.getCanvas().repaint(); 
					counterToFireAnimation = 0;
				}
			}
		}
		
		private void robotAnimation()
		{
			
		}
	}
	
	public static void repaint()
	{
		renderPanel.getCanvas().repaint();
	}
	
	public static void togglePaused()
	{
		paused = !paused;
	}
	
	public static void increaseAnimationSpeed()
	{
		currentAnimationExecutionsPerSecond = currentAnimationExecutionsPerSecond + 1 < fps ? currentAnimationExecutionsPerSecond + 1 : fps;
	}
	
	public static void decreaseAnimationSpeed()
	{
		currentAnimationExecutionsPerSecond = currentAnimationExecutionsPerSecond - 1 > 1 ? currentAnimationExecutionsPerSecond - 1 : 1;
	}
	
	public static void moveCamera(float x, float z){
		Camera cam = sceneManager.getCamera();
		cam.getCenterOfProjection().add(new Vector3f(x, 0, z));
		cam.getLookAtPoint().add(new Vector3f(x, 0, z));
		
		cam.setCenterOfProjection(cam.getCenterOfProjection());
		repaint();
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
		sceneManager = new GraphSceneManager();
		
		// Make the main window of this application and add the renderer to it
		JFrame jframe = new JFrame("robot");
		jframe.setSize(1000, 1000);
		jframe.setLocationRelativeTo(null); // center of screen
		jframe.getContentPane().add(renderPanel.getCanvas());// put the canvas into a JFrame window  	    	    
	    
		MouseHandler mouseListener = new MouseHandler(sceneManager.getCamera());
	    renderPanel.getCanvas().addMouseMotionListener(mouseListener);
	    renderPanel.getCanvas().addMouseListener(mouseListener);
	    renderPanel.getCanvas().addMouseWheelListener(mouseListener);
	    
//	     renderPanel.getCanvas().addKeyListener(new RobotAnimationKeyListener());
		
	    jframe.setVisible(true); // show window
	}
}