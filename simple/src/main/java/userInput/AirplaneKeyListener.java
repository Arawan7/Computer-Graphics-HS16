package userInput;


import java.awt.event.*;

import assignment2.FlightSimulatorScene;

/**
 * A key listener for the main window. Use this to process key events.
 * Currently this provides the following controls:
 * 'w': move the steeredModel and animatedModels up
 * 'a': move the steeredModel and animatedModels left
 * 's': move the steeredModel and animatedModels down
 * 'd': move the steeredModel and animatedModels right
 * 'q': rotate the steeredModel (and animatedModels relatively) around the z-axis (roll), leftwards
 * 'e': rotate the steeredModel (and animatedModels relatively) around the z-axis (roll), rightwards
 * 'y': rotate the steeredModel (and animatedModels relatively) around the x-axis (tilt), upwards
 * 'c': rotate the steeredModel (and animatedModels relatively) around the x-axis (tilt), downwards
 * 'p': play or pause animation
 * '+': accelerate animation
 * '-': slow down animation
 * 'x': default shader
 * 'n': shader using surface normals
 * 'm': use a material for shading
 */
public class AirplaneKeyListener implements KeyListener{

	private static final float DISTANCE_TO_FLY = 0.5f;
	private static final float DISTANCE_TO_AIRPLANE = 10f;
	
	public void keyPressed(KeyEvent e)
	{
		
		switch(e.getKeyChar())
		{
			case 'w': {
				FlightSimulatorScene.flyAndFollowAirplaneForward(DISTANCE_TO_FLY, DISTANCE_TO_AIRPLANE);
				break;
			}
			case 'a': {
				FlightSimulatorScene.flyAndFollowAirplaneRight(DISTANCE_TO_FLY, DISTANCE_TO_AIRPLANE);
				break;
			}
			case 's': {
				FlightSimulatorScene.flyAndFollowAirplaneForward(-DISTANCE_TO_FLY, DISTANCE_TO_AIRPLANE);
				break;
			}
			case 'd': {
				FlightSimulatorScene.flyAndFollowAirplaneRight(-DISTANCE_TO_FLY, DISTANCE_TO_AIRPLANE);
				break;
			}
		}
		
		// Trigger redrawing
		FlightSimulatorScene.repaint();
	}
	
	public void keyReleased(KeyEvent e)
	{
	}

	public void keyTyped(KeyEvent e)
    {
    }

}