package org.vinsert.insertion;

public interface IPlayer extends IActor {

	public String getUsername();
	
	public boolean isSpotAnimating();
	
	public IPlayerComposition getComposite();
	
	public IModel getModel();
	
	public IModel getThisModel();
	
	public void setThisModel(IModel model);
	
	public int getCombatLevel();
	
	public int getTotalLevel();
	
	public int getPrayerIcon();
	
	public int getSkullIcon();
	
}
