package userInput;

import java.awt.event.*;

import assignment5.RobotScene;

public class RobotAnimationKeyListener implements KeyListener {
	private static final float DISTANCE = 5f;
	
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
			case 'a': {
				RobotScene.moveCamera(-DISTANCE, 0);
				break;
			}
			case 'd': {
				RobotScene.moveCamera(DISTANCE, 0);
				break;
			}case 'w': {
				RobotScene.moveCamera(0, DISTANCE);
				break;
			}case 's': {
				RobotScene.moveCamera(0, -DISTANCE);
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