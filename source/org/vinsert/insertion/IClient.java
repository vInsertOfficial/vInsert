package org.vinsert.insertion;

public interface IClient {

	public IPlayer getLocalPlayer();
	
	public IPlayer[] getPlayers();
	
	public INpc[] getNpcs();
	
	public int[] getNpcIndices();
	
	public int[][][] getVertexHeights();
	
	public byte[][][] getSceneFlags();

	public int getCamPitch();

	public int getCamYaw();

	public int getCameraX();

	public int getCameraY();

	public int getCameraZ();

	public int getPlane();
	
	public int getOriginX();
	
	public int getOriginY();
	
	public IWidget[][] getWidgets();

	public String[] getMenuActions();
	
	public String[] getMenuTargets();
	
	public ISceneGraph getScene();

	public int getMapScale();

	public int getMapOffset();
	
	public int getCompassAngle();

	public int getMenuX();
	
	public int getMenuY();
	
	public int getMenuWidth();

	public int getMenuHeight();
	
	public int getMenuSize();
	
	public int getCycle();
	
	public INodeDeque[][][] getGroundItems();
	
	public int getGameState();
	
	public int[] getBitConfigs();
	
	public int[] getSkillLevels();
	
	public int[] getSkillExperiences();
	
	public int[] getSkillBases();
	
	public int[] getWidgetSettings();
	
	public int[] getPaneXPos();
	
	public int[] getPaneYPos();

	public boolean isMenuOpen();
	
	public INodeMultiSet getInterfaceNodes();
	
}
