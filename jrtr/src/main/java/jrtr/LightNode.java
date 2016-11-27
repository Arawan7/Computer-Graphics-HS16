package jrtr;

public class LightNode extends Leaf {
	private Light light;
	
	public LightNode(Light light) {
		this.light = light;
	}
	
	public void setLight(Light light){
		this.light = light;
	}
	
	@Override
	public Light get3dObject() {
		return light;
	}
}