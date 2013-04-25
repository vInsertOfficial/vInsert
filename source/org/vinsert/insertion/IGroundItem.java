package org.vinsert.insertion;

public interface IGroundItem extends IRenderable {

    public int getId();

    public int getX();

    public int getY();

    public int getSpawnTime();

    public int getDuration();

    public boolean isNonStackable();

}
