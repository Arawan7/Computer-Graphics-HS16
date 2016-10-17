package userInput;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import assignment2.VirtualTrackballScene;
import jrtr.Camera;
import transformations.Transformations;

/**
 * A mouse listener for the main window of this application. This can be
 * used to process mouse events.
 */
public class SimpleMouseMotionListener implements MouseMotionListener, MouseListener
{
	private Point previousPos;
	private int screenWidth, screenHeight;
	private Camera camera;
	private boolean isInside = false;
	
	public SimpleMouseMotionListener(Camera camera, int screenWidth, int screenHeight)
	{
		previousPos = new Point();
		this.camera = camera;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
	}
	
	
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1 && isInside)
		{
			previousPos = e.getPoint();
		}
	}
	public void mouseReleased(MouseEvent e) {
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {}


	@Override
	public void mouseEntered(MouseEvent arg0) { isInside = true;}


	@Override
	public void mouseExited(MouseEvent arg0) { isInside = false;}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(isInside)
		{
			Transformations.virtualTrackball(camera, previousPos, e.getPoint(), screenWidth, screenHeight);
			VirtualTrackballScene.repaint();
			previousPos = e.getLocationOnScreen();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {}
}