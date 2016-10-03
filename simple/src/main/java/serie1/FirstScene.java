package serie1;

import jrtr.*;
import jrtr.glrenderer.*;
import primitiveMeshes.Primitives;
import transformations.Transformations;
import userInput.SimpleMouseListener;

import javax.swing.*;

import java.awt.event.*;
import javax.vecmath.*;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Implements a simple application that opens a 3D rendering window and 
 * shows a rotating cube.
 */
public class FirstScene
{	
	static RenderPanel renderPanel;
	static RenderContext renderContext;
	static Shader normalShader;
	static Shader diffuseShader;
	static Material material;
	static SimpleSceneManager sceneManager;
	static Shape cube, cylinder, rotor1, rotor2, steeredModel;
	static Shape[] animatedModels;
	static int currentAnimationExecutionsPerSecond, animationExecutionsPerSecond, fps;
	static boolean paused;

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
			cube = new Shape(Primitives.getCube(r));
			cylinder = new Shape( Primitives.getCylinder(50, 3, 1, r) );
			rotor1 = new Shape( Primitives.getTorus(30, 30, 0.7f, 0.3f, r) );
			rotor2 = new Shape( Primitives.getTorus(30, 30, 0.7f, 0.3f, r) );
			
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
		}
	}
	/**
	 * A key listener for the main window. Use this to process key events.
	 * Currently this provides the following controls:
	 * 'w': move the steeredModel up
	 * 'a': move the steeredModel left
	 * 's': move the steeredModel down
	 * 'd': move the steeredModel right
	 * 'q': rotate the steeredModel around the z-axis (roll), leftwards
	 * 'e': rotate the steeredModel around the z-axis (roll), rightwards
	 * 'y': rotate the steeredModel around the x-axis (tilt), upwards
	 * 'c': rotate the steeredModel around the x-axis (tilt), downwards
	 * 'p': play or pause animation
	 * '+': accelerate animation
	 * '-': slow down animation
	 * 'x': default shader
	 * 'n': shader using surface normals
	 * 'm': use a material for shading
	 */
	public static class SimpleKeyListener implements KeyListener
	{
		public void keyPressed(KeyEvent e)
		{
			float distance = 0.5f;
			float angleInDegrees = 10;
			
			switch(e.getKeyChar())
			{
				case 'w': {
					Transformations.translate(steeredModel, 4, distance);
					for(int i=0; i<animatedModels.length; i++){
						Transformations.translateGlobal(animatedModels[i], new Vector3f(cube.getTransformation().m01, cube.getTransformation().m11, cube.getTransformation().m21), distance);
					}
					break;
				}
				case 'a': {
					Transformations.translate(steeredModel, 2, distance);
					for(int i=0; i<animatedModels.length; i++){
						Transformations.translateGlobal(animatedModels[i], new Vector3f(-cube.getTransformation().m00, -cube.getTransformation().m10, -cube.getTransformation().m20), distance);
					}
					break;
				}
				case 's': {
					Transformations.translate(steeredModel, 5, distance);
					for(int i=0; i<animatedModels.length; i++){
						Transformations.translateGlobal(animatedModels[i], new Vector3f(-cube.getTransformation().m01, -cube.getTransformation().m11, -cube.getTransformation().m21), distance);
					}
					break;
				}
				case 'd': {
					Transformations.translate(steeredModel, 3, distance);
					for(int i=0; i<animatedModels.length; i++){
						Transformations.translateGlobal(animatedModels[i], new Vector3f(cube.getTransformation().m00, cube.getTransformation().m10, cube.getTransformation().m20), distance);
					}
					break;
				}
				case 'q': {
					Transformations.rotateRoll(steeredModel, angleInDegrees);
					for(int i=0; i<animatedModels.length; i++){
						Transformations.rotateRelativeRoll(animatedModels[i], angleInDegrees, cube);
					}
					break;
				}case 'e': {
					Transformations.rotateRoll(steeredModel, -angleInDegrees);
					for(int i=0; i<animatedModels.length; i++){
						Transformations.rotateRelativeRoll(animatedModels[i], -angleInDegrees, cube);
					}
					break;
				}
				case 'y': {
					Transformations.rotateTilt(steeredModel, angleInDegrees);
					for(int i=0; i<animatedModels.length; i++){
						Transformations.rotateRelativeTilt(animatedModels[i], angleInDegrees, cube);
					}
					break;
				}case 'c': {
					Transformations.rotateTilt(steeredModel, -angleInDegrees);
					for(int i=0; i<animatedModels.length; i++){
						Transformations.rotateRelativeTilt(animatedModels[i], -angleInDegrees, cube);
					}
					break;
				}
				case 'p': {
					// play or pause animation
					paused = !paused;
					break;
				}
				case '+': {
					// Accelerate animation by 10%
					currentAnimationExecutionsPerSecond = currentAnimationExecutionsPerSecond + 1 < fps ? currentAnimationExecutionsPerSecond + 1 : fps;
					break;
				}
				case '-': {
					// Slow down animation by 10%
					currentAnimationExecutionsPerSecond = currentAnimationExecutionsPerSecond - 1 > 1 ? currentAnimationExecutionsPerSecond - 1 : 1;
					break;
				}
				case 'n': {
					// Remove material from shape, and set "normal" shader
					steeredModel.setMaterial(null);
					renderContext.useShader(normalShader);
					break;
				}
				case 'x': {
					// Remove material from shape, and set "default" shader
					steeredModel.setMaterial(null);
					renderContext.useDefaultShader();
					break;
				}
				case 'm': {
					// Set a material for more complex shading of the shape
					if(steeredModel.getMaterial() == null) {
						steeredModel.setMaterial(material);
					} else
					{
						steeredModel.setMaterial(null);
						renderContext.useDefaultShader();
					}
					break;
				}
			}
			
			// Trigger redrawing
			renderPanel.getCanvas().repaint();
		}
		
		public void keyReleased(KeyEvent e)
		{
		}

		public void keyTyped(KeyEvent e)
        {
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

		// Add a mouse and key listener
	    renderPanel.getCanvas().addMouseListener(new SimpleMouseListener());
	    renderPanel.getCanvas().addKeyListener(new SimpleKeyListener());
		renderPanel.getCanvas().setFocusable(true);   	    	    
	    
	    jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    jframe.setVisible(true); // show window
	}
}
