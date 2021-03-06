package com.mygdx.game.world;

import com.badlogic.gdx.graphics.OrthographicCamera;

public abstract class GameMap {

    public abstract void render(OrthographicCamera camera);
    public abstract void update(float delta);
    public abstract void dispose();

    public abstract int getWidth();
    public abstract int getHeight();
    public abstract int getLayers();


}
