package org.vinsert.insertion;

public interface IWidget {
	
	public IWidget[] getChildren();
	
	public IWidget getRoot();
	
	public int[][] getOpcodes();
	
	public String getTooltip();
	
	public String[] getActions();

	public int getWidth();
	
	public int getHeight();

	public int getX();

	public int getY();
	
	public int getRelativeX();
	
	public int getRelativeY();
	
	public int getParentId();
	
	public int getId();
	
	public int[] getSlotContents();
	
	public int[] getSlotSizes();

	public int getScrollVMax();
	
	public int getScrollHMax();
	
	public String getName();
	
	public int getStaticPos();
	
	public String getSelectedAction();
	
	public String getSpellName();
	
	public String getText();
	
	public int getModelId();
	
	public String getString1();
	
	public String getString2();
	
}
