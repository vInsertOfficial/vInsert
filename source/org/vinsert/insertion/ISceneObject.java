package org.vinsert.insertion;

public interface ISceneObject {
	
	public IRenderable getRenderable();
	
	public int getObjectHash();
	
	public int getFlags();
	
	public int getPlane();
	
	public int getGridX();
	
	public int getGridY();
	
	public int getHeight();
	
	public int getOrientation();
	
	public int getReletiveX();
	
	public int getReletiveY();
	
	public int getOffsetX();
	
	public int getOffsetY();

}
