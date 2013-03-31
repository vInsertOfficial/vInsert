package org.vinsert.insertion;

public interface IWall {
	
	public IRenderable getRenderable();

	public int getObjectHash();

	public int getFlags();

	public int getPlane();

	public int getGridX();

	public int getGridY();

	public int getHeight();

	public int getOrientation();
	
}
