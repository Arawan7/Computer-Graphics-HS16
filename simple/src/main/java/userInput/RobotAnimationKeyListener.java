package userInput;

import java.awt.event.*;

import assignment5.RobotScene;

public class RobotAnimationKeyListener implements KeyListener {
	private static final float DISTANCE = 0.5f;
	private static final float ANGLE_IN_DEGREES = 10;
	
	public void keyPressed(KeyEvent e)
	{
		
		switch(e.getKeyChar())
		{
			case 'p': {
				// play or pause animation
				RobotScene.togglePaused();
				break;
			}
			case '+': {
				RobotScene.increaseAnimationSpeed();
				break;
			}
			case '-': {
				RobotScene.decreaseAnimationSpeed();
				break;
			}
		}
		
		// Trigger redrawing
		RobotScene.repaint();
	}
	
	public void keyReleased(KeyEvent e)
	{
	}

	public void keyTyped(KeyEvent e)
    {
    }
}