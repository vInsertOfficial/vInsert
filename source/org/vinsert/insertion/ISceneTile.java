package org.vinsert.insertion;

public interface ISceneTile {
	
	public ISceneObject[] getInteractableObjects();
	
	public IFloorDecoration getFloorDecoration();
	
	public IWallDecoration getWallDecoration();
	
	public IGroundLayer getGroundLayer();
	
	public IWall getBoundary();
	

}
