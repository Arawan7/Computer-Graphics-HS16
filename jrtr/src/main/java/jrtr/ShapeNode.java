package jrtr;

public class ShapeNode extends Leaf {
	private Shape shape;
	
	public ShapeNode(Shape shape) {
		this.shape = shape;
	}
	
	public void setShape(Shape shape){
		this.shape = shape;
	}
	
	@Override
	public Shape get3dObject() {
		return shape;
	}
}