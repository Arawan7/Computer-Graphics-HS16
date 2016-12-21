package assignment5;

import jrtr.*;
import jrtr.Light.Type;
import jrtr.glrenderer.*;
import meshes.Primitives;
import transformations.Transformations;
import userInput.MouseHandler;
import userInput.RobotAnimationKeyListener;

import java.util.Timer;
import java.util.TimerTask;

import javax.swing.*;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

public class RobotScene {
	private static RenderPanel renderPanel;
	private static RenderContext renderContext;
	private static GraphSceneManager sceneManager;
	private static TransformGroup robotTrafo;
	private static int currentAnimationExecutionsPerSecond, animationExecutionsPerSecond, fps;
	private static boolean paused;
	static Shader diffuseShader;
	static Material materialWood;
	static boolean textured = false;
	static boolean withRobot = false;
	static boolean withCubes = true;
	static Shape cube0S;

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
		    if(textured)
		    	renderContext.useShader(diffuseShader);
			
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
			
			TransformGroup rootNode = new TransformGroup(new Vector3f(0,0,0));
			
		/* Robot construction */
			TransformGroup robotHeadTrafo, robotUpperArmLeftJointTrafo, robotUpperArmLeftTrafo, robotLowerArmLeftJointTrafo, robotLowerArmLeftTrafo,
				robotUpperArmRightJointTrafo, robotUpperArmRightTrafo, robotLowerArmRightJointTrafo, robotLowerArmRightTrafo, robotUpperLegLeftJointTrafo,
				robotUpperLegLeftTrafo, robotLowerLegLeftJointTrafo, robotLowerLegLeftTrafo, robotUpperLegRightJointTrafo, robotUpperLegRightTrafo,
				robotLowerLegRightJointTrafo, robotLowerLegRightTrafo, robotFootLeftTrafo, robotFootRightTrafo;
			
			ShapeNode robotBodySN, robotHeadSN, robotUpperArmLeftJointSN, robotUpperArmLeftSN, robotLowerArmLeftJointSN, robotLowerArmLeftSN,
				robotUpperArmRightJointSN, robotUpperArmRightSN, robotLowerArmRightJointSN, robotLowerArmRightSN, robotUpperLegLeftJointSN, robotUpperLegLeftSN,
				robotLowerLegLeftJointSN, robotLowerLegLeftSN, robotUpperLegRightJointSN, robotUpperLegRightSN, robotLowerLegRightJointSN, robotLowerLegRightSN,
				robotFootLeftSN, robotFootRightSN;
			
			Shape robotBodyS, robotUpperArmS, robotLowerArmS, robotUpperLegS, robotLowerLegS, robotFootS, robotSmallJointS, robotBigJointS, robotHeadS;
			
			LightNode blueLN;
			
			Light blueLight;
			
			blueLight = new Light();
			blueLight.type = Type.POINT;
			blueLight.diffuse = new Vector3f(0,0,1);
			blueLight.position = new Vector3f(0,0,0);
			
			blueLN = new LightNode(blueLight);
			
			robotBodyS = Primitives.makeCube(r);
			robotUpperArmS = Primitives.makeCylinder(30, 1f, 0.27f, r);
			robotLowerArmS = Primitives.makeCylinder(30, 1.2f, 0.22f, r);
			robotUpperLegS = Primitives.makeCylinder(30, 1.8f, 0.4f, r);
			robotLowerLegS = Primitives.makeCylinder(30, 2, 0.35f, r);
			robotFootS = Primitives.makeTorus(30, 30, 0.25f, 0.2f, r);
			robotSmallJointS = Primitives.makeTorus(30, 30, 0f, 0.35f, r);
			robotBigJointS = Primitives.makeTorus(30, 30, 0f, 0.45f, r);
			robotHeadS = Primitives.makeTorus(30, 30, 0.5f, 0.4f, r);
			
			if(textured && withRobot)
				robotLowerArmS.setMaterial(materialWood);
			
			robotBodySN = new ShapeNode(robotBodyS);
			robotHeadSN = new ShapeNode(robotHeadS);
			robotUpperArmLeftJointSN = new ShapeNode(robotBigJointS);
			robotUpperArmLeftSN = new ShapeNode(robotUpperArmS);
			robotLowerArmLeftJointSN = new ShapeNode(robotSmallJointS);
			robotLowerArmLeftSN = new ShapeNode(robotLowerArmS);
			robotUpperArmRightJointSN = new ShapeNode(robotBigJointS);
			robotUpperArmRightSN = new ShapeNode(robotUpperArmS);
			robotLowerArmRightJointSN = new ShapeNode(robotSmallJointS);
			robotLowerArmRightSN = new ShapeNode(robotLowerArmS);
			robotUpperLegLeftJointSN = new ShapeNode(robotBigJointS);
			robotUpperLegLeftSN = new ShapeNode(robotUpperLegS);
			robotLowerLegLeftJointSN = new ShapeNode(robotBigJointS);
			robotLowerLegLeftSN = new ShapeNode(robotLowerLegS);
			robotUpperLegRightJointSN = new ShapeNode(robotBigJointS);
			robotUpperLegRightSN = new ShapeNode(robotUpperLegS);
			robotLowerLegRightJointSN = new ShapeNode(robotBigJointS);
			robotLowerLegRightSN = new ShapeNode(robotLowerLegS);
			robotFootLeftSN = new ShapeNode(robotFootS);
			robotFootRightSN = new ShapeNode(robotFootS);
			
			robotHeadTrafo = new TransformGroup(new Vector3f(0,1.4f,0), new Node[]{robotHeadSN});
			robotLowerArmLeftTrafo = new TransformGroup(new Vector3f(0,-0.5f,0), new Node[]{robotLowerArmLeftSN});
			robotLowerArmLeftJointTrafo = new TransformGroup(new Vector3f(0,-0.45f,0), new Node[]{robotLowerArmLeftTrafo, robotLowerArmLeftJointSN});
			robotUpperArmLeftTrafo = new TransformGroup(new Vector3f(0,-0.8f,0), new Node[]{robotLowerArmLeftJointTrafo, robotUpperArmLeftSN});
			robotUpperArmLeftJointTrafo = new TransformGroup(new Vector3f(-1.35f,0.25f,0), new Node[]{robotUpperArmLeftTrafo, robotUpperArmLeftJointSN});
			robotLowerArmRightTrafo = new TransformGroup(new Vector3f(0,-0.5f,0), new Node[]{robotLowerArmRightSN, blueLN});
			robotLowerArmRightJointTrafo = new TransformGroup(new Vector3f(0,-0.45f,0), new Node[]{robotLowerArmRightTrafo, robotLowerArmRightJointSN});
			robotUpperArmRightTrafo = new TransformGroup(new Vector3f(0,-0.8f,0), new Node[]{robotLowerArmRightJointTrafo, robotUpperArmRightSN});
			robotUpperArmRightJointTrafo = new TransformGroup(new Vector3f(1.35f,0.25f,0), new Node[]{robotUpperArmRightTrafo, robotUpperArmRightJointSN});
			robotFootLeftTrafo = new TransformGroup(new Vector3f(0,-1,0), new Node[]{robotFootLeftSN});
			robotLowerLegLeftTrafo = new TransformGroup(new Vector3f(0,-1.2f,0), new Node[]{robotFootLeftTrafo, robotLowerLegLeftSN});
			robotLowerLegLeftJointTrafo = new TransformGroup(new Vector3f(0,-1,0), new Node[]{robotLowerLegLeftTrafo, robotLowerLegLeftJointSN});
			robotUpperLegLeftTrafo = new TransformGroup(new Vector3f(0,-1,0), new Node[]{robotLowerLegLeftJointTrafo, robotUpperLegLeftSN});
			robotUpperLegLeftJointTrafo = new TransformGroup(new Vector3f(-0.5f,-1.2f,0), new Node[]{robotUpperLegLeftTrafo, robotUpperLegLeftJointSN});
			robotFootRightTrafo = new TransformGroup(new Vector3f(0,-1,0), new Node[]{robotFootRightSN});
			robotLowerLegRightTrafo = new TransformGroup(new Vector3f(0,-1.2f,0), new Node[]{robotFootRightTrafo, robotLowerLegRightSN});
			robotLowerLegRightJointTrafo = new TransformGroup(new Vector3f(0,-1,0), new Node[]{robotLowerLegRightTrafo, robotLowerLegRightJointSN});
			robotUpperLegRightTrafo = new TransformGroup(new Vector3f(0,-1,0), new Node[]{robotLowerLegRightJointTrafo, robotUpperLegRightSN});
			robotUpperLegRightJointTrafo = new TransformGroup(new Vector3f(0.5f,-1.2f,0), new Node[]{robotUpperLegRightTrafo, robotUpperLegRightJointSN});
			
			robotTrafo = new TransformGroup(new Vector3f(5,0,0), new Node[]{robotHeadTrafo, robotUpperArmLeftJointTrafo, robotUpperArmRightJointTrafo,
					robotUpperLegLeftJointTrafo, robotUpperLegRightJointTrafo, robotBodySN});
			
			if(withRobot)
				rootNode.addNode(robotTrafo);
		/* Robot construction END */
			
		/* culling objects construction */
			cube0S = Primitives.makeCube(r);
			if(textured)
				cube0S.setMaterial(materialWood);
			int dim = textured ? 8 : 300;
			TransformGroup[][] cubesTrafo = new TransformGroup[dim][dim];
			ShapeNode[][] cubesSN = new ShapeNode[dim][dim];	
			
			if(withCubes){
				for(int i=0; i<dim; i++)
					for(int j=0; j<dim; j++){
						cubesSN[i][j] = new ShapeNode(cube0S);
						cubesTrafo[i][j] = new TransformGroup(new Vector3f(3*(i-dim/2), -6.5f, 3*(j-dim/2)), new Node[]{cubesSN[i][j]});
						rootNode.addNode(cubesTrafo[i][j]);
					}
			}
			
		/* culling objects construction end */
			
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
					rotateCube();
					// Trigger redrawing of the render window
					renderPanel.getCanvas().repaint(); 
					counterToFireAnimation = 0;
				}
			}
		}
		
		private void rotateCube(){
			Transformations.rotatePan(cube0S, 2f);
		}
		
		private void robotAnimation()
		{
			// move robot in a circle
			Matrix4f rotCircle = new Matrix4f();
			rotCircle.rotY((float)(Math.PI/128));
			rotCircle.mul(robotTrafo.getTransformation());
			robotTrafo.setTransformation(rotCircle);
			
			TransformGroup leftLeg = (TransformGroup) robotTrafo.getChildren().get(3);
			TransformGroup rightLeg = (TransformGroup) robotTrafo.getChildren().get(4);
			
			TransformGroup leftKnee = (TransformGroup) leftLeg.getChildren().get(0).getChildren().get(0);
			TransformGroup rightKnee = (TransformGroup) rightLeg.getChildren().get(0).getChildren().get(0);
			
			TransformGroup leftArm = (TransformGroup) robotTrafo.getChildren().get(1);
			TransformGroup rightArm = (TransformGroup) robotTrafo.getChildren().get(2);
			
			TransformGroup leftElbow = (TransformGroup) leftArm.getChildren().get(0).getChildren().get(0);
			TransformGroup rightElbow = (TransformGroup) rightArm.getChildren().get(0).getChildren().get(0);
			
			Matrix4f rotLeftArm = new Matrix4f();
			rotLeftArm.setIdentity();
			Matrix4f rotRightArm = new Matrix4f();
			rotRightArm.setIdentity();
			
			Matrix4f rotLeftElbow = new Matrix4f();
			rotLeftElbow.setIdentity();
			Matrix4f rotRightElbow = new Matrix4f();
			rotRightElbow.setIdentity();
			
			Matrix4f rotKnees = new Matrix4f();
			rotKnees.setIdentity();
			
			float rotAmount = (float)(Math.PI/64);
			
			counterToInvertRotations++;
			if(counterToInvertRotations < 20){
				rotLeftArm.rotX(-rotAmount);
				rotRightArm.rotX(rotAmount);
				
				rotRightElbow.rotX(rotAmount);
				
				rotKnees.rotX(-rotAmount);
			} else if (counterToInvertRotations < 40){
				rotLeftArm.rotX(rotAmount);
				rotRightArm.rotX(-rotAmount);
				
				rotRightElbow.rotX(-rotAmount);
				
				rotKnees.rotX(rotAmount);
			} else if (counterToInvertRotations < 60){
				rotLeftArm.rotX(rotAmount);
				rotRightArm.rotX(-rotAmount);
				
				rotLeftElbow.rotX(rotAmount);
				
				rotKnees.rotX(-rotAmount);
			} else if(counterToInvertRotations < 80){
				rotLeftArm.rotX(-rotAmount);
				rotRightArm.rotX(rotAmount);
				
				rotLeftElbow.rotX(-rotAmount);
				
				rotKnees.rotX(rotAmount);
			} else
				counterToInvertRotations = -1;
		
			leftArm.getTransformation().mul(rotLeftArm);
			rightArm.getTransformation().mul(rotRightArm);
			
			leftElbow.getTransformation().mul(rotLeftElbow);
			rightElbow.getTransformation().mul(rotRightElbow);
			
			leftLeg.getTransformation().mul(rotRightArm);
			rightLeg.getTransformation().mul(rotLeftArm);
			
			rightKnee.getTransformation().mul(rotKnees);
			leftKnee.getTransformation().mul(rotKnees);
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
	    
	     renderPanel.getCanvas().addKeyListener(new RobotAnimationKeyListener());
		
	    jframe.setVisible(true); // show window
	}
}
