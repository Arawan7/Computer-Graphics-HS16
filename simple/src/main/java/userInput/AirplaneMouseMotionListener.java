package userInput;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import assignment2.FlightSimulatorScene;
import jrtr.Camera;
import transformations.Transformations;

public class AirplaneMouseMotionListener implements MouseMotionListener, MouseListener {
	private Point previousPos;
	private final int screenWidth, screenHeight;
	private Camera camera;
	private boolean isInside = false;
	
	public AirplaneMouseMotionListener(Camera camera, int screenWidth, int screenHeight)
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
			Point toPosition = e.getPoint();
			Transformations.virtualTrackball(FlightSimulatorScene.getAirplane(), previousPos, toPosition, screenWidth, screenHeight);
			FlightSimulatorScene.repaint();
			previousPos = toPosition;
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {}
}