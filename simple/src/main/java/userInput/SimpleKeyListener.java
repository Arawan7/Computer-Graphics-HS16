package userInput;

import java.awt.event.*;

import assignment1.FirstScene;

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
	public class SimpleKeyListener implements KeyListener
	{
		private static final float DISTANCE = 0.5f;
		private static final float ANGLE_IN_DEGREES = 10;
		
		public void keyPressed(KeyEvent e)
		{
			
			switch(e.getKeyChar())
			{
				case 'w': {
					FirstScene.applyUpTranslation(DISTANCE);
					break;
				}
				case 'a': {
					FirstScene.applyLeftTranslation(DISTANCE);
					break;
				}
				case 's': {
					FirstScene.applyDownTranslation(DISTANCE);
					break;
				}
				case 'd': {
					FirstScene.applyRightTranslation(DISTANCE);
					break;
				}
				case 'q': {
					FirstScene.applyLeftRoll(ANGLE_IN_DEGREES);
					break;
				}case 'e': {
					FirstScene.applyRightRoll(ANGLE_IN_DEGREES);
					break;
				}
				case 'y': {
					FirstScene.applyUpTilt(ANGLE_IN_DEGREES);
					break;
				}case 'c': {
					FirstScene.applyDownTilt(ANGLE_IN_DEGREES);
					break;
				}
				case 'p': {
					// play or pause animation
					FirstScene.togglePaused();
					break;
				}
				case '+': {
					FirstScene.increaseAnimationSpeed();
					break;
				}
				case '-': {
					FirstScene.decreaseAnimationSpeed();
					break;
				}
				case 'n': {
					FirstScene.setNormalShader();
					break;
				}
				case 'x': {
					FirstScene.setDefaultShader();
					break;
				}
				case 'm': {
					FirstScene.setMaterial();
					break;
				}
			}
			
			// Trigger redrawing
			FirstScene.repaint();
		}
		
		public void keyReleased(KeyEvent e)
		{
		}

		public void keyTyped(KeyEvent e)
        {
        }

	}