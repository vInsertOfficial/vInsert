package org.vinsert.insertion;

public interface IModel extends IRenderable {

	public int getVertices();
	
	public int[] getVerticesX();
	
	public int[] getVerticesY();

	public int[] getVerticesZ();
	
	public int[] getVertexSkins();
	
	public int getTriangles();
	
	public int[] getTriViewX();

	public int[] getTriViewY();

	public int[] getTriViewZ();
	
	public int getTexTriangles();
	
	public int[] getTexMapX();

	public int[] getTexMapY();

	public int[] getTexMapZ();
	
}
