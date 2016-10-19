package assignment2;

import jrtr.*;
import jrtr.glrenderer.*;
import primitiveMeshes.Primitives;
import transformations.Transformations;
import userInput.AirplaneKeyListener;
import userInput.AirplaneMouseMotionListener;

import java.io.IOException;

import javax.swing.*;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;	

public class FlightSimulatorScene {
	private static RenderPanel renderPanel;
	private static RenderContext renderContext;
	private static SimpleSceneManager sceneManager;
	private static Shape fractalLandscape;
	private static Shape airplane;

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
			fractalLandscape = Primitives.makeFractalLandscape(6, 8f, r);
			
			sceneManager.addShape(fractalLandscape);
			
			try {
				airplane = new Shape(ObjReader.read("/home/simon/Documents/Computer-Graphics-HS16/obj/airplane.obj", 4, renderContext));
			} catch (IOException e) {
				e.printStackTrace();
			}
			sceneManager.addShape(airplane);
			
			airplane.getTransformation().setTranslation(new Vector3f(sceneManager.getCamera().getCenterOfProjection()));
			Transformations.rotateTilt(airplane, 90);
			Transformations.rotatePan(airplane, 180);
			Transformations.translateGlobal(airplane, new Vector3f(-2f,0,1), 20);
			
			Vector4f airplaneOrigin = new Vector4f();
			airplane.getTransformation().getColumn(3, airplaneOrigin);
			
			sceneManager.getCamera().setUpVector(new Vector3f(0,0,1));				
			sceneManager.getCamera().setLookAtPoint(new Vector3f(airplaneOrigin.x, airplaneOrigin.y, airplaneOrigin.z));
			sceneManager.getCamera().setCenterOfProjection(new Vector3f(-50,0,30));

			// Add the scene to the renderer
			renderContext.setSceneManager(sceneManager);
		    
		    Shader lsShader = renderContext.makeShader();
		    try {
		        lsShader.load("../jrtr/shaders/landscape.vert", "../jrtr/shaders/landscape.frag");
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		    renderContext.useShader(lsShader);
		}
	}
	
	public static void repaint()
	{
		renderPanel.getCanvas().repaint();
	}
	
	
	
	public static void flyAndFollowAirplaneForward(float distance, float distanceToAirplane){
		// move airplane
		Transformations.translate(airplane, 2, distance);
		placeCameraBehindAirplane(distanceToAirplane);
	}
	
	public static void flyAndFollowAirplaneRight(float distanceToFly, float distanceToAirplane){
		// move airplane
		Transformations.translate(airplane, 1, distanceToFly);
		placeCameraBehindAirplane(distanceToAirplane);		
	}
	
	private static void placeCameraBehindAirplane(float distanceToAirplane)
	{
		// look at airplane
		Vector4f airplaneOrigin = new Vector4f();
		airplane.getTransformation().getColumn(3, airplaneOrigin);
		sceneManager.getCamera().setLookAtPoint(new Vector3f(airplaneOrigin.x, airplaneOrigin.y, airplaneOrigin.z));
		// get direction of airplane
		Vector4f direction = new Vector4f();
		airplane.getTransformation().getColumn(0, direction);
		// calculate scaled direction vector
		Vector3f transformedDirection = new Vector3f(direction.x, direction.y, direction.z);
		transformedDirection.normalize();
		transformedDirection.scale(-distanceToAirplane);
		Vector3f cOP = new Vector3f(airplaneOrigin.x, airplaneOrigin.y, airplaneOrigin.z);
		cOP.sub(transformedDirection);
		cOP.add(new Vector3f(0,0,3));
		sceneManager.getCamera().setCenterOfProjection(cOP);
	}
	
	public static Shape getAirplane(){ return airplane; }
	
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
		sceneManager = new SimpleSceneManager();
		
		// Make the main window of this application and add the renderer to it
		JFrame jframe = new JFrame("simple");
		jframe.setSize(1000, 1000);
		jframe.setLocationRelativeTo(null); // center of screen
		jframe.getContentPane().add(renderPanel.getCanvas());// put the canvas into a JFrame window  	    	    
	    
		AirplaneMouseMotionListener mouseListener = new AirplaneMouseMotionListener(sceneManager.getCamera(), jframe.getWidth(), jframe.getHeight());
	    renderPanel.getCanvas().addMouseMotionListener(mouseListener);
	    renderPanel.getCanvas().addMouseListener(mouseListener);
	    renderPanel.getCanvas().addKeyListener(new AirplaneKeyListener());
		
	    jframe.setVisible(true); // show window
	}
}