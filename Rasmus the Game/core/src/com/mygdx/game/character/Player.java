package com.mygdx.game.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.awt.*;

public class Player extends Sprite{
    public Rectangle hitbox = new Rectangle(16,16);
    public Player(Texture texture){
        super(texture);
    }
    public Player(){}

    public enum State{
        IDLE, WALKING, JUMPING, DYING
    }

    State state = State.IDLE;

    public void setState(State newState){
        this.state = newState;
    }
    public State getState(){
        return state;
    }


}
