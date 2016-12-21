package assignment4;

import jrtr.*;
import jrtr.glrenderer.*;
import jrtr.swrenderer.SWRenderPanel;
import meshes.Primitives;
import transformations.Transformations;
import userInput.MouseHandler;

import java.io.IOException;

import javax.swing.*;
import javax.vecmath.Vector3f;


public class ShaderProgramming {
	private static RenderPanel renderPanel;
	private static RenderContext renderContext;
	private static SimpleSceneManager sceneManager;
	private static Shape cylinder, cylinder2, cylinder3;
	static Shader normalShader;
	static Shader diffuseShader;
	static Material materialCylinder, materialCylinder2, materialCylinder3;
	static int exercise = 2;

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
			
			// Load some more shaders
		    normalShader = renderContext.makeShader();
		    try {
		    	normalShader.load("../jrtr/shaders/normal.vert", "../jrtr/shaders/normal.frag");
		    } catch(Exception e) {
		    	System.out.print("Problem with shader:\n");
		    	System.out.print(e.getMessage());
		    }
		    
		    if(exercise == 1)
		    {
		    	diffuseShader = renderContext.makeShader();
			    try {
			    	diffuseShader.load("../jrtr/shaders/diffusePointLights.vert", "../jrtr/shaders/diffusePointLights.frag");
			    } catch(Exception e) {
			    	System.out.print("Problem with shader:\n");
			    	System.out.print(e.getMessage());
			    }

			    // Make a materials that can be used for shading
				materialCylinder = new Material();
				materialCylinder.shader = diffuseShader;
				materialCylinder.diffuseMap = renderContext.makeTexture();
				materialCylinder.diffuse = new Vector3f(0.5f,1f,0.5f);
				try {
					materialCylinder.diffuseMap.load("/home/simon/Documents/Computer-Graphics-HS16/textures/wood.jpg");
				} catch(Exception e) {
					System.out.print("Could not load texture.\n");
					System.out.print(e.getMessage());
				}
				
				materialCylinder2 = new Material();
				materialCylinder2.shader = diffuseShader;
				materialCylinder2.diffuseMap = renderContext.makeTexture();
				materialCylinder2.diffuse = new Vector3f(1f,0f,0.5f);
				try {
					materialCylinder2.diffuseMap.load("/home/simon/Documents/Computer-Graphics-HS16/textures/wood.jpg");
				} catch(Exception e) {
					System.out.print("Could not load texture.\n");
					System.out.print(e.getMessage());
				}
				
				materialCylinder3 = new Material();
				materialCylinder3.shader = diffuseShader;
				materialCylinder3.diffuseMap = renderContext.makeTexture();
				materialCylinder3.diffuse = new Vector3f(0f,0f,1f);
				try {
					materialCylinder3.diffuseMap.load("/home/simon/Documents/Computer-Graphics-HS16/textures/wood.jpg");
				} catch(Exception e) {
					System.out.print("Could not load texture.\n");
					System.out.print(e.getMessage());
				}
				
				// create the objects and assign materials
				cylinder = Primitives.makeCylinder(30, 10, 1, r);
				cylinder.setMaterial(materialCylinder);
				
				cylinder2 = Primitives.makeCylinder(30, 10, 1, r);
				Transformations.translate(cylinder2, 0, 3);
				cylinder2.setMaterial(materialCylinder2);
				
				cylinder3 = Primitives.makeCylinder(30, 10, 1, r);
				Transformations.translate(cylinder3, 1, 3);
				cylinder3.setMaterial(materialCylinder3);
				
				// Make a scene manager and add the objects
				sceneManager.addShape(cylinder);
				sceneManager.addShape(cylinder2);
				sceneManager.addShape(cylinder3);
				
				// red light source
				Light redLight = new Light();
				redLight.type = Light.Type.POINT;
				redLight.position = new Vector3f(0,6,0);
				redLight.diffuse = new Vector3f(1f,0,0);
				sceneManager.addLight(redLight);
				
				// blue light source
				Light blueLight = new Light();
				blueLight.type = Light.Type.POINT;
				blueLight.position = new Vector3f(0,-6,-10);
				blueLight.diffuse = new Vector3f(0f,0,1f);
				sceneManager.addLight(blueLight);
		    } else if (exercise == 2)
		    {
		    	diffuseShader = renderContext.makeShader();
			    try {
			    	diffuseShader.load("../jrtr/shaders/phongPointLights.vert", "../jrtr/shaders/phongPointLights.frag");
			    } catch(Exception e) {
			    	System.out.print("Problem with shader:\n");
			    	System.out.print(e.getMessage());
			    }

			    // Make a materials that can be used for shading
				materialCylinder = new Material();
				materialCylinder.shader = diffuseShader;
				materialCylinder.diffuseMap = renderContext.makeTexture();
				materialCylinder.diffuse = new Vector3f(1f,1f,1f);
				materialCylinder.specular = new Vector3f(0.5f,0.5f,0.5f);
				materialCylinder.shininess = 100f;
				
				try {
					cylinder = new Shape(ObjReader.read("/home/simon/Documents/Computer-Graphics-HS16/obj/teapot.obj", 4, r));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
				cylinder.setMaterial(materialCylinder);
				sceneManager.addShape(cylinder);
				
				// red light source
				Light redLight = new Light();
				redLight.type = Light.Type.POINT;
				redLight.position = new Vector3f(0,5,0);
				redLight.diffuse = new Vector3f(1f,0,0);
				sceneManager.addLight(redLight);
				
				// white light source
				Light whiteLight = new Light();
				whiteLight.type = Light.Type.POINT;
				whiteLight.position = new Vector3f(0,5,-5);
				whiteLight.diffuse = new Vector3f(1f,1,1f);
				sceneManager.addLight(whiteLight);
		    } else if (exercise == 3)
		    {
		    	diffuseShader = renderContext.makeShader();
			    try {
			    	diffuseShader.load("../jrtr/shaders/phongTexturingPointLights.vert", "../jrtr/shaders/phongTexturingPointLights.frag");
			    } catch(Exception e) {
			    	System.out.print("Problem with shader:\n");
			    	System.out.print(e.getMessage());
			    }

			    // Make a materials that can be used for shading
				materialCylinder = new Material();
				materialCylinder.shader = diffuseShader;
				materialCylinder.diffuseMap = renderContext.makeTexture();
				materialCylinder.specularMap = renderContext.makeTexture();
				materialCylinder.diffuse = new Vector3f(1,1f,1f);
				materialCylinder.specular = new Vector3f(0f,0f,0f);
				materialCylinder.shininess = 100f;
				try {
					materialCylinder.diffuseMap.load("/home/simon/Documents/Computer-Graphics-HS16/textures/wood.jpg");
					materialCylinder.specularMap.load("/home/simon/Documents/Computer-Graphics-HS16/textures/stone.jpeg");
				} catch(Exception e) {
					System.out.print("Could not load texture.\n");
					System.out.print(e.getMessage());
				}
				
				materialCylinder2 = new Material();
				materialCylinder2.shader = diffuseShader;
				materialCylinder2.diffuseMap = renderContext.makeTexture();
				materialCylinder2.specularMap = renderContext.makeTexture();
				materialCylinder2.diffuse = new Vector3f(1f,1f,1f);
				materialCylinder2.specular = new Vector3f(0f,0f,0f);
				materialCylinder2.shininess = 100f;
				try {
					materialCylinder2.diffuseMap.load("/home/simon/Documents/Computer-Graphics-HS16/textures/stone.jpeg");
					materialCylinder2.specularMap.load("/home/simon/Documents/Computer-Graphics-HS16/textures/sky.png");
				} catch(Exception e) {
					System.out.print("Could not load texture.\n");
					System.out.print(e.getMessage());
				}
				
//				try {
//					cylinder = new Shape(ObjReader.read("/home/simon/Documents/Computer-Graphics-HS16/obj/teapot.obj", 3, r));
//					cylinder2 = new Shape(ObjReader.read("/home/simon/Documents/Computer-Graphics-HS16/obj/teapot.obj", 3, r));
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				};
				cylinder = Primitives.makeCylinder(30, 10, 1, r);
				cylinder2 = Primitives.makeCylinder(30, 10, 1, r);
				
				cylinder.setMaterial(materialCylinder);
				cylinder2.setMaterial(materialCylinder2);
				
				Transformations.translate(cylinder, 3, 3.3f);
				Transformations.translate(cylinder2, 2, 3.3f);
				
				sceneManager.addShape(cylinder);
				sceneManager.addShape(cylinder2);
				
				// red light source
				Light redLight = new Light();
				redLight.type = Light.Type.POINT;
				redLight.position = new Vector3f(0,6,3);
				redLight.diffuse = new Vector3f(1f,0,0);
				sceneManager.addLight(redLight);
				
				// white light source
				Light whiteLight = new Light();
				whiteLight.type = Light.Type.POINT;
				whiteLight.position = new Vector3f(0,6,-3);
				whiteLight.diffuse = new Vector3f(1f,1,1f);
				sceneManager.addLight(whiteLight);
		    }

			// Add the scene to the renderer
			renderContext.setSceneManager(sceneManager);
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
		sceneManager = new SimpleSceneManager();
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
