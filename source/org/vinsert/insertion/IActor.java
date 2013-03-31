package org.vinsert.insertion;

public interface IActor extends IRenderable {
	
	public int getAnimation();

	public int getGridX();
	
	public int getGridY();
	
	public int getOrientation();
	
	public int getInteracting();
	
	public String getSpeech();
	
	public int getQueueXPos();
	
	public int getQueueYPos();
	
	public int[] getQueueX();
	
	public int[] getQueueY();
	
	public boolean isMoving();
	
	public int[] getHitDamages();
	
	public int[] getHitCycles();
	
	public int[] getHitTypes();
	
	public int getCycle();
	
	public int getHealth();
	
	public int getMaxHealth();
	
	public int getStandAnimation();
	
	public int getTurnAnimation();
	
	public int getWalkAnimation();
	
	public int getTurn180();
	
	public int getTurnCW90();
	
}
