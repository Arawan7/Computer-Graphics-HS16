package assignment1;

import jrtr.*;
import jrtr.glrenderer.*;
import meshes.Primitives;
import transformations.Transformations;
import userInput.SimpleKeyListener;

import javax.swing.*;

import javax.vecmath.*;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Implements a simple application that opens a 3D rendering window and 
 * shows a rotating cube.
 */
public class FirstScene
{
	private static RenderPanel renderPanel;
	private static RenderContext renderContext;
	private static Shader normalShader;
	private static Shader diffuseShader;
	private static Material material;
	private static SimpleSceneManager sceneManager;
	private static Shape cube, cylinder, rotor1, rotor2, steeredModel;
	private static Shape[] animatedModels;
	private static ArrayList<Shape> shoots = new ArrayList<Shape>();
	private static int currentAnimationExecutionsPerSecond, animationExecutionsPerSecond, fps;
	private static boolean paused;

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
			
			// create the objects
			cube = Primitives.makeCube(r);
			cylinder = Primitives.makeCylinder(50, 3, 1, r);
			rotor1 = Primitives.makeTorus(30, 30, 0.7f, 0.3f, r);
			rotor2 = Primitives.makeTorus(30, 30, 0.7f, 0.3f, r);
			
			animatedModels = new Shape[3];
			animatedModels[0] = rotor1;
			animatedModels[1] = rotor2;
			animatedModels[2] = cylinder;
			
			steeredModel = cube;
			
			Transformations.rotateTilt(cylinder, 90);
			Transformations.translateGlobal(cylinder, new Vector3f(0,-1,0), 3.5f);
			//Transformations.translate(rotor1, 5, 1);
			Transformations.translate(rotor1, 3, 1.2f);
			//Transformations.translate(rotor2, 5, 1f);
			Transformations.translate(rotor2, 2, 1.2f);
			//Transformations.translate(cube, 5, 2.4f);
			Transformations.translate(cube, 5, 1.4f);
			//Transformations.translate(cube, 3, 3);
			//Transformations.translate(cube, 0, 4);
			
			
			// Make a scene manager and add the objects
			sceneManager = new SimpleSceneManager();
			for(int i=0; i<animatedModels.length; i++)
			{
				sceneManager.addShape(animatedModels[i]);
			}
			sceneManager.addShape(steeredModel);

			// Add the scene to the renderer
			renderContext.setSceneManager(sceneManager);
			
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
			material.diffuseMap = renderContext.makeTexture();
			try {
				material.diffuseMap.load("../textures/plant.jpg");
			} catch(Exception e) {				
				System.out.print("Could not load texture.\n");
				System.out.print(e.getMessage());
			}

			// Register a timer task
		    Timer timer = new Timer();
		    
		    fps = 100;
		    animationExecutionsPerSecond = 30;
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
		int counter = 0;
		public void run()
		{
			if(paused)
				counter = 0;
			else
			{
				counter++;
				if(counter >= fps/currentAnimationExecutionsPerSecond)
				{
					animation();
					// Trigger redrawing of the render window
					renderPanel.getCanvas().repaint(); 
					counter = 0;
				}
			}
		}
		
		private void animation()
		{
			
			for(int i=0; i < animatedModels.length; i++)
			{
				
				//Transformations.rotateRelativePan(animatedModels[i], 10, cube);
				//Transformations.rotateRelativeTilt(animatedModels[i], 10, cube);
				//Transformations.rotateRelativeRoll(animatedModels[i], 10, cube);
				Transformations.rotatePan(animatedModels[i], (float)Math.pow(-1, i) * -10);
				//Transformations.translateGlobal(animatedModels[i], new Vector3f(-cube.getTransformation().m02, -cube.getTransformation().m12, -cube.getTransformation().m22), 0.5f);
			}
			//Transformations.rotatePan(cube, 10);
			//Transformations.translate(cube, 0, 0.5f);
			
			for(int i=0; i<shoots.size(); i++)
			{
				Transformations.translate(shoots.get(i), 5, 0.3f);
				Transformations.rotatePan(shoots.get(i), -20);
			}
			
		}
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
	
	public static void setNormalShader()
	{
		// Remove material from shape, and set "normal" shader
		steeredModel.setMaterial(null);
		renderContext.useShader(normalShader);
	}
	
	public static void setDefaultShader()
	{
		// Remove material from shape, and set "default" shader
		steeredModel.setMaterial(null);
		renderContext.useDefaultShader();
	}
	
	public static void setMaterial()
	{
		// Set a material for more complex shading of the shape
		if(steeredModel.getMaterial() == null) {
			steeredModel.setMaterial(material);
		} else
		{
			steeredModel.setMaterial(null);
			renderContext.useDefaultShader();
		}
	}
	
	public static void repaint()
	{
		renderPanel.getCanvas().repaint();
	}
	
	public static void applyDownTranslation(float distance)
	{
		Transformations.translate(steeredModel, 5, distance);
		for(int i=0; i<animatedModels.length; i++){
			Transformations.translateGlobal(animatedModels[i], new Vector3f(
					-cube.getTransformation().m01,
					-cube.getTransformation().m11,
					-cube.getTransformation().m21), distance);
		}
	}
	
	public static void applyUpTranslation(float distance)
	{
		Transformations.translate(steeredModel, 4, distance);
		for(int i=0; i<animatedModels.length; i++){
			Transformations.translateGlobal(animatedModels[i], new Vector3f(
					cube.getTransformation().m01,
					cube.getTransformation().m11,
					cube.getTransformation().m21), distance);
		}
	}
	
	public static void applyLeftTranslation(float distance)
	{
		Transformations.translate(steeredModel, 2, distance);
		for(int i=0; i<animatedModels.length; i++){
			Transformations.translateGlobal(animatedModels[i], new Vector3f(
					-cube.getTransformation().m00,
					-cube.getTransformation().m10,
					-cube.getTransformation().m20), distance);
		}
	}
	
	public static void applyRightTranslation(float distance)
	{
		Transformations.translate(steeredModel, 3, distance);
		for(int i=0; i<animatedModels.length; i++){
			Transformations.translateGlobal(animatedModels[i], new Vector3f(
					cube.getTransformation().m00,
					cube.getTransformation().m10,
					cube.getTransformation().m20), distance);
		}
	}
	
	public static void applyLeftRoll(float angleInDegrees)
	{
		Transformations.rotateRoll(steeredModel, angleInDegrees);
		for(int i=0; i<animatedModels.length; i++){
			Transformations.rotateRelativeRoll(animatedModels[i], angleInDegrees, cube);
		}
	}
	
	public static void applyRightRoll(float angleInDegrees)
	{
		Transformations.rotateRoll(steeredModel, -angleInDegrees);
		for(int i=0; i<animatedModels.length; i++){
			Transformations.rotateRelativeRoll(animatedModels[i], -angleInDegrees, cube);
		}
	}
	
	public static void applyUpTilt(float angleInDegrees)
	{
		Transformations.rotateTilt(steeredModel, angleInDegrees);
		for(int i=0; i<animatedModels.length; i++){
			Transformations.rotateRelativeTilt(animatedModels[i], angleInDegrees, cube);
		}
	}
	
	public static void applyDownTilt(float angleInDegrees)
	{
		Transformations.rotateTilt(steeredModel, -angleInDegrees);
		for(int i=0; i<animatedModels.length; i++){
			Transformations.rotateRelativeTilt(animatedModels[i], -angleInDegrees, cube);
		}
	}
	
	public static void shoot()
	{
		Shape shot = Primitives.makeCylinder(50, 1, 0.4f, renderContext);
		sceneManager.addShape(shot);
		Matrix3f b = new Matrix3f();
		cube.getTransformation().getRotationScale(b);;
		shot.getTransformation().setRotation(b);
		Vector4f h = new Vector4f();
		cube.getTransformation().getColumn(3, h);
		shot.getTransformation().setTranslation(new Vector3f(h.x, h.y, h.z));
		Transformations.rotateTilt(shot, 90);
		shoots.add(shot);
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

		// Add a key listener
	    renderPanel.getCanvas().addKeyListener(new SimpleKeyListener());
		renderPanel.getCanvas().setFocusable(true);   	    	    
	    
	    jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    jframe.setVisible(true); // show window
	}
}
