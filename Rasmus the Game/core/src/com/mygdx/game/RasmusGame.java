package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.character.KeyboardController;
import com.mygdx.game.world.TiledGameMap;

public class RasmusGame extends ApplicationAdapter {

	private SpriteBatch batch;
	private OrthographicCamera camera;
	private KeyboardController controller;
	//private GameMap gameMap;
	private Sprite player;
	private TiledGameMap tiledMap;
	int mapWidth, mapHeight;
	//CREATE ANIMATION
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
		player = new Sprite();
		player.setPosition(w/2 -player.getWidth()/2-10, h/2 - player.getHeight()/2-24);

		//create map
		tiledMap = new TiledGameMap("worldMap.tmx");

		//Create camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, w,h);
		Viewport viewport = new FitViewport(683,384,camera);
		viewport.apply();
		camera.position.set(player.getX(),player.getY(),0);
		camera.update();

		//Create animation
		Texture walkSheet = new Texture(Gdx.files.internal("knight.png"));
		TextureRegion[][] tmp = TextureRegion.split(walkSheet,
				walkSheet.getWidth() / FRAME_COLS,
				walkSheet.getHeight() / FRAME_ROWS);
		TextureRegion[] walkDownFrames = tmp[0];
		TextureRegion[] walkLeftFrames = tmp[1];
		TextureRegion[] walkRightFrames = tmp[2];
		TextureRegion[] walkUpFrames = tmp[3];

		walkDownAnimation = new Animation<>(0.25f, walkDownFrames);
		walkUpAnimation = new Animation<>(0.25f, walkUpFrames);
		walkLeftAnimation = new Animation<>(0.25f, walkLeftFrames);
		walkRightAnimation = new Animation<>(0.25f, walkRightFrames);
		currentFrame = walkDownAnimation;
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
		cameraInBounds();
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		tiledMap.render(camera);
		batch.begin();
		batch.draw(currentFrame.getKeyFrame(stateTime,true),player.getX(),player.getY());
		batch.end();

	}

	private void playerMovement() {
		float speed = 1f;
		if(controller.left && player.getX()-player.getWidth()/2>0){
			player.translateX(-speed);
			currentFrame = walkLeftAnimation;
		}
		if(controller.right && player.getX()+player.getWidth()/2<mapWidth) {
			player.translateX(speed);
			currentFrame = walkRightAnimation;
		}
		if(controller.down && player.getY()-player.getHeight()/2>0){
			player.translateY(-speed);
			currentFrame = walkDownAnimation;
		}
		if(controller.up && player.getY()+player.getHeight()/2<mapHeight){
			player.translateY(speed);
			currentFrame = walkUpAnimation;
		}
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
