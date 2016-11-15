package userInput;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Stack;

import javax.vecmath.Point2f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import assignment3.MathHelper;
import assignment3.RasterizerScene;
import assignment4.ShaderProgramming;
import jrtr.Camera;

public class MouseHandler implements MouseListener, MouseMotionListener, MouseWheelListener
{
	Camera cam;
	Point2f mousePos;
	private static Vector3f origin;
	public static Vector3f getOrigin() {
		if (origin==null){
			return new Vector3f(0,0,0);
		}else{
			return new Vector3f(origin);
		}
	}
	private static Vector3f camPos;
	private static Vector3f vecUp;
	private static Quat4f camOri;
	int cnt = 0;
	float STEP_SZ = (float)Math.PI/360.0f;
	float WHEEL_SCALE = 1.0f;
	private static Vector3f planeAxis = new Vector3f(0,1,0);
	public enum mouseState {left,right,both}
	private Stack<Integer> mouseButton = new Stack<>();

	public MouseHandler(){
		setCamera(new Camera());
		origin = new Vector3f(0,0,0);
	}
	public MouseHandler(Camera cam){
		setCamera(cam);
		origin = new Vector3f(0,0,0);
	}

	public void setCamera(Camera cam){
		this.cam = cam;
		reset();
		updateCam();
	}
	
	public void reset(){
//		origin = new Vector3f(0,0,0);
//		camPos=new Vector3f(50,0,0);
//		planeAxis = new Vector3f(0,1,0);planeAxis.normalize();
//		vecUp=new Vector3f(0,0,1);
//		Vector3f temp = new Vector3f();
//		temp.set(vecUp);
//		camOri = MathHelper.getQuatDiff(new Vector3f(0,0,1.0f),vecUp);
//		vecUp.normalize();

		//image 1
		origin = new Vector3f(0.0f,0.0f,0.0f);
		camPos=new Vector3f(0.0f,0.0f,15.0f);
		planeAxis = new Vector3f(1.0f,0.0f,0.0f);planeAxis.normalize();

		//image 2

//		origin = new Vector3f(-5,0,0);
//		camPos=new Vector3f(-10,40,40);
//		planeAxis = new Vector3f(1.0f,0.0f,0.0f);planeAxis.normalize();
		

		vecUp=new Vector3f(0.0f,1.0f,0.0f);
		camOri = MathHelper.getQuatDiff(new Vector3f(0.0f,1.0f,0.0f),vecUp);
		
		updateCam();
	}

	public static Vector3f getCamPos() {
		if (camPos!=null){
			return new Vector3f(camPos);
		}else{
			return new Vector3f(0,0,0);
		}
	}
	private void updateCam(){
//		Vector3f cop = new Vector3f(origin);
//		Vector3f cop = new Vector3f(0,0,0);
//		cop.set(camPos);
		this.cam.setUpCopLook(vecUp, camPos, origin);
	}


	public void mousePressed(MouseEvent e) {
		Component rc = (Component) e.getSource();
		System.out.println("Mouse pressed: "+e.getButton());
		int t1 = mouseButton.isEmpty() ? 0:mouseButton.peek();
		if (t1+e.getButton()==4){
			mouseButton.pop();mouseButton.push(4);
			rc.removeMouseMotionListener(this);
		}else{
			mouseButton.push(e.getButton());				
		}
		rc.addMouseMotionListener(this);
		mousePos = new Point2f((float)e.getX(), (float)e.getY());
		cnt++;

	}
	public void mouseReleased(MouseEvent e) {
		Component rc = (Component) e.getSource();
		System.out.println("Mouse released " + e.getButton());
		if (mouseButton.pop()==4){
			mouseButton.push(4-e.getButton());
    		rc.addMouseMotionListener(this);
		}
		rc.removeMouseMotionListener(this);
	}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseDragged(MouseEvent e) {
		switch (mouseButton.peek()){
			case 1:
				rotateCam(e);
				break;
			case 4:
				shiftCam(e);
				break;
		}
		mousePos = new Point2f(e.getX(),e.getY());
//		RasterizerScene.repaint();
		ShaderProgramming.repaint();

	}
	
	private void shiftCam(MouseEvent e){
		Point2f diff = new Point2f(-((float)e.getX()-mousePos.x),(float)e.getY()-mousePos.y);
		mousePos.x += diff.x; mousePos.y += diff.y;
		Vector3f xShift = new Vector3f(planeAxis);xShift.normalize();
		Vector3f temp = new Vector3f(camPos);temp.sub(origin);temp.normalize();
		temp.cross(temp, xShift);temp.normalize();
//		Vector3f yShift = new Vector3f(0,0,1);
		Vector3f yShift = new Vector3f(temp);
		
		xShift.scale(diff.x/30);
		yShift.scale(diff.y/30);
		Vector3f shift = new Vector3f(xShift);
		shift.add(yShift);
		origin.add(shift);
		camPos.add(shift);
		Vector3f projTo = new Vector3f();
		projTo.set(camPos);
		projTo.sub(origin);
//		updateCam();
		updateCamRotation(new Point2f(0.0f,0.0f));
	}
	
	/**
	 * set camera orientation according the clicked mouse movement. It's done by the use of 
	 * quaternions, which are calculated by the half-angle formulation
	 * @param e
	 */
	private void rotateCam(MouseEvent e){
		Point2f diff = new Point2f(-((float)e.getX()-mousePos.x),(float)e.getY()-mousePos.y);
		mousePos.x += diff.x; mousePos.y += diff.y;
		updateCamRotation(diff);		
	}
	
	private void updateCamRotation(Point2f diff){
		Quat4f tempQ = new Quat4f();
		Vector3f temp = new Vector3f();
		Quat4f zQ = new Quat4f(0.0f,(float)Math.sin(diff.x*STEP_SZ/2.0f),0.0f,(float)Math.cos(diff.x*STEP_SZ/2.0f));
		planeAxis.set(MathHelper.QuaternionVectorRotation(zQ, planeAxis));planeAxis.normalize();
		Vector3f plAx = new Vector3f(planeAxis);
		plAx.scale((float)Math.sin(-diff.y*STEP_SZ/2.0f));
		Quat4f pQ = new Quat4f(plAx.x,plAx.y,plAx.z,(float)Math.cos(-diff.y*STEP_SZ/2.0f));
		
		Vector3f relCamPos = new Vector3f(camPos);
		relCamPos.sub(origin);
		Quat4f rot = new Quat4f(pQ);
		rot.mul(zQ);
		tempQ.set(rot);
		tempQ.mul(camOri);
		temp.set(0.0f,1.0f,0.0f);
		vecUp = MathHelper.QuaternionVectorRotation(camOri,temp);vecUp.normalize();			
		camOri.set(tempQ);
		relCamPos = MathHelper.QuaternionVectorRotation(rot, relCamPos);
		camPos.set(origin);
		camPos.add(relCamPos);
		temp.set(camPos);temp.sub(origin);
		
		updateCam();
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
	}
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		Vector3f temp = new Vector3f();
		Vector3f projTo = new Vector3f();
		temp.set(camPos);
		temp.sub(origin);
		projTo.set(temp);
//		float len = temp.length();
		temp.normalize();
		temp.scale(e.getWheelRotation()*WHEEL_SCALE);
		camPos.add(temp);
		if (camPos.length()<1)
			camPos.sub(temp);
		updateCam();
	}
}