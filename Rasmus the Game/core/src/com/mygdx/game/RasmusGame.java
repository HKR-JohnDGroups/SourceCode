package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.character.KeyboardController;
import com.mygdx.game.character.Player;
import com.mygdx.game.world.TiledGameMap;

import java.awt.*;
import java.util.ArrayList;

public class RasmusGame extends ApplicationAdapter {

	private SpriteBatch batch;
	private OrthographicCamera camera;
	private KeyboardController controller;

	//private GameMap gameMap;
	private Player player;
	private TiledGameMap tiledMap;
	private int mapWidth, mapHeight;
	private ArrayList<Rectangle> collisionRectangles;
	//CREATE ANIMATION
	private TextureRegion[] walkDownFrames,walkLeftFrames,walkRightFrames,walkUpFrames;
	private TextureRegion idle;
	private Animation<TextureRegion> currentFrame, walkDownAnimation,walkUpAnimation,walkLeftAnimation,walkRightAnimation; // Must declare frame type (TextureRegion)
	private static final int FRAME_COLS = 4, FRAME_ROWS = 4;
	private float stateTime;


	@Override
	public void create () {

		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		controller = new KeyboardController();
		batch = new SpriteBatch();

		//create player
		player = new Player();
		player.setPosition(w/2 -player.getWidth()/2-10, h/2 - player.getHeight()/2-24);
		player.hitbox.setLocation((int)(player.getX()-player.getWidth()/2),(int)(player.getY()-player.getHeight()/2));


		//create map
		tiledMap = new TiledGameMap("worldMap.tmx");

		//create collision map
		collisionRectangles = new ArrayList<>();
		MapLayer collisionMapLayer = tiledMap.getLayers("Mountains");
		TiledMapTileLayer collisionLayer = (TiledMapTileLayer) collisionMapLayer;
		for(int i=0;i<tiledMap.getWidth();i++){
			for(int j=0;j<tiledMap.getHeight();j++){
				TiledMapTileLayer.Cell cell = collisionLayer.getCell(i, j);
				if (cell == null) continue; // There is no cell
				if (cell.getTile() == null) continue; // No tile inside cell
				if (cell.getTile().getId() != 0){
					Rectangle temp = new Rectangle(i*16,j*16,16,16);
					collisionRectangles.add(temp);
				}
			}
		}
		//Create camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, w,h);
		Viewport viewport = new FitViewport(404,227,camera);
		viewport.apply();
		camera.position.set(player.getX(),player.getY(),0);
		camera.update();

		//Create animation
		Texture walkSheet = new Texture(Gdx.files.internal("knight.png"));
		TextureRegion[][] tmp = TextureRegion.split(walkSheet,
				walkSheet.getWidth() / FRAME_COLS,
				walkSheet.getHeight() / FRAME_ROWS);
		walkDownFrames = tmp[0];
		walkLeftFrames = tmp[1];
		walkRightFrames = tmp[2];
		walkUpFrames = tmp[3];

		walkDownAnimation = new Animation<>(0.25f, walkDownFrames);
		walkUpAnimation = new Animation<>(0.25f, walkUpFrames);
		walkLeftAnimation = new Animation<>(0.25f, walkLeftFrames);
		walkRightAnimation = new Animation<>(0.25f, walkRightFrames);
		currentFrame = walkDownAnimation;
		idle = walkDownFrames[3];
		stateTime = 0f;
	}

	@Override
	public void render () {


		stateTime += Gdx.graphics.getDeltaTime();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.input.setInputProcessor(controller);

		playerMovement();

		camera.position.set(player.getX(),player.getY(),0);
		player.hitbox.setLocation((int)(player.getX()-player.getWidth()/2),(int)(player.getY()-player.getHeight()/2));
		cameraInBounds();
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		tiledMap.render(camera);
		batch.begin();
		if (player.getState().equals(Player.State.WALKING)) {
			batch.draw(currentFrame.getKeyFrame(stateTime, true), player.getX(), player.getY());
		}else {
			batch.draw(idle,player.getX(),player.getY());
		}
		batch.end();

	}

	private void playerMovement() {
		float speed = 1f;
		Rectangle tempHitBoxLocation = new Rectangle(16,16);

		if (controller.left && player.getX() - player.getWidth() / 2 > 0) {
			tempHitBoxLocation.setLocation((int)(-speed + player.getX()-player.getWidth()/2),(int)(player.getY()-player.getHeight()/2));
			if (!collisionDetector(tempHitBoxLocation)) {
				player.translateX(-speed);
			}
			currentFrame = walkLeftAnimation;
			idle = walkLeftFrames[3];

		}
		if (controller.right && player.getX() + 21< mapWidth) {
			tempHitBoxLocation.setLocation((int)(speed + player.getX()-player.getWidth()/2),(int)(player.getY()-player.getHeight()/2));
			if (!collisionDetector(tempHitBoxLocation)){
				player.translateX(speed);
			}
				currentFrame = walkRightAnimation;
				idle = walkRightFrames[3];

		}
		if (controller.down && player.getY() - player.getHeight() / 2 > 0) {
			tempHitBoxLocation.setLocation((int)(player.getX()-player.getWidth()/2),(int)(-speed + player.getY()-player.getHeight()/2));

			if (!collisionDetector(tempHitBoxLocation)) {
				player.translateY(-speed);
			}
			currentFrame = walkDownAnimation;
			idle = walkDownFrames[3];
		}
		if (controller.up && player.getY() + 29 <= mapHeight) {
			tempHitBoxLocation.setLocation((int)(player.getX()-player.getWidth()/2),(int)(speed + player.getY()-player.getHeight()/2));

			if (!collisionDetector(tempHitBoxLocation)) {
				player.translateY(speed);
			}
			currentFrame = walkUpAnimation;
			idle = walkUpFrames[3];
		}
		if (!controller.left && !controller.right && !controller.up && !controller.down) {
			player.setState(Player.State.IDLE);
		} else {
			player.setState(Player.State.WALKING);
		}
	}

	private boolean collisionDetector(Rectangle playerRectangle){
		boolean isCollision = false;
		for(Rectangle rectangle : collisionRectangles){
			if(rectangle.intersects(playerRectangle)){
				 isCollision = true;
			}
		}
		return isCollision;
	}

	private void cameraInBounds() {
		MapProperties prop = tiledMap.getProperties();

		mapWidth = prop.get("width", Integer.class)*16;
		mapHeight = prop.get("height", Integer.class)*16;
		float cameraX = camera.position.x;
		float cameraY = camera.position.y;
		float viewportHalfHeight = camera.viewportHeight/2;
		float viewportHalfWidth = camera.viewportWidth/2;

		//Left Clamp
		if(cameraX < viewportHalfWidth) {
			camera.position.x = viewportHalfWidth;
		}
		//Right Clamp
		if(cameraX+viewportHalfWidth>mapWidth){
			camera.position.x = mapWidth-viewportHalfWidth;
		}
		//Top Clamp
		if(cameraY+viewportHalfHeight>mapHeight){
			camera.position.y = mapHeight-viewportHalfHeight;
		}
		//Bottom Clamp
		if(cameraY<viewportHalfHeight){
			camera.position.y = viewportHalfHeight;
		}
	}

	@Override
	public void dispose () {
		batch.dispose();
		tiledMap.dispose();
	}


}
