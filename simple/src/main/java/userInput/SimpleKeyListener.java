package userInput;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.vecmath.Matrix4f;

import jrtr.RenderPanel;
import jrtr.Shape;

/**
 * A key listener for the main window. Use this to process key events.
 * Currently this provides the following controls:
 * 'q': rotates the model leftwards
 * 'w': rotates the model rightwards
 * 's': stop animation
 * 'p': play animation
 * '+': accelerate rotation
 * '-': slow down rotation
 * 'd': default shader
 * 'n': shader using surface normals
 * 'm': use a material for shading
 */
public class SimpleKeyListener implements KeyListener
{
	
	private Shape model;
	private RenderPanel renderPanel;
	
	public SimpleKeyListener(Shape model, RenderPanel renderPanel)
	{
		this.model = model;
		this.renderPanel = renderPanel;
	}
	
	
	public void keyPressed(KeyEvent e)
	{/*
		switch(e.getKeyChar())
		{
			case 'q': {
				Matrix4f t = model.getTransformation();
	    		Matrix4f rotX = new Matrix4f();
	    		rotX.rotX(0.1f);
	    		t.mul(rotX);
	    		model.setTransformation(t);
	    		renderPanel.getCanvas().repaint(); 
				break;
			}
			case 'w': {
				Matrix4f t = model.getTransformation();
	    		Matrix4f rotX = new Matrix4f();
	    		rotX.rotX(-0.1f);
	    		t.mul(rotX);
	    		model.setTransformation(t);
	    		renderPanel.getCanvas().repaint(); 
				break;
			}
			case 's': {
				// Stop animation
				currentstep = 0;
				break;
			}
			case 'p': {
				// Resume animation
				currentstep = basicstep;
				break;
			}
			case '+': {
				// Accelerate roation
				currentstep += basicstep;
				break;
			}
			case '-': {
				// Slow down rotation
				currentstep -= basicstep;
				break;
			}
			case 'n': {
				// Remove material from shape, and set "normal" shader
				model.setMaterial(null);
				renderContext.useShader(normalShader);
				break;
			}
			case 'd': {
				// Remove material from shape, and set "default" shader
				model.setMaterial(null);
				renderContext.useDefaultShader();
				break;
			}
			case 'm': {
				// Set a material for more complex shading of the shape
				if(model.getMaterial() == null) {
					model.setMaterial(material);
				} else
				{
					model.setMaterial(null);
					renderContext.useDefaultShader();
				}
				break;
			}
		}
		
		// Trigger redrawing
		renderPanel.getCanvas().repaint();
		*/
	}
	
	public void keyReleased(KeyEvent e)
	{
	}

	public void keyTyped(KeyEvent e)
    {
    }

}