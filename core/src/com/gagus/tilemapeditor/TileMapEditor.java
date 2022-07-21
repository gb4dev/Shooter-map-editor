package com.gagus.tilemapeditor;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.gagus.tilemapeditor.Map.MapManager;
import com.gagus.tilemapeditor.UI.HudStage;

public class TileMapEditor extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	MapManager mapmanager;
	OrthographicCamera cameraMap;
	OrthoCamController orthoCamController;
	HudStage hudStage;
	FitViewport fitViewport;
	public static final float VERSION = 1;


	@Override
	public void create () {
		batch = new SpriteBatch();
		cameraMap = new OrthographicCamera();
		cameraMap.zoom = 3;
		fitViewport = new FitViewport(1280, 720, cameraMap);
		fitViewport.apply();
		//cameraMap.viewportWidth = Gdx.graphics.getWidth()/2;
		//cameraMap.viewportHeight = Gdx.graphics.getHeight();
		//cameraMap.setToOrtho(false,Gdx.graphics.getWidth()*3/5, Gdx.graphics.getHeight());

		//camera.setToOrtho(false, 10, 10);
		img = new Texture("badlogic.jpg");
		Gdx.app.log("main", cameraMap.position.toString());

		mapmanager= new MapManager(256, 128, 15, 15, cameraMap, batch);
		cameraMap.position.set(mapmanager.tileWidth*2, mapmanager.tileHeight/2,0);
		orthoCamController = new OrthoCamController(cameraMap, mapmanager);
		//Gdx.input.setInputProcessor(orthoCamController);
		cameraMap.update();

		hudStage = new HudStage(mapmanager);
		//Gdx.input.setInputProcessor(hudStage);

		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(hudStage);
		inputMultiplexer.addProcessor(orthoCamController);
		Gdx.input.setInputProcessor(inputMultiplexer);

	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		cameraMap.update();
		mapmanager.drawPolygons();
		mapmanager.drawMap();
		hudStage.act();
		hudStage.draw();
		/*batch.begin();
		batch.setProjectionMatrix(cameraMenu.combined);
		batch.draw(img, 0, 0);
		batch.end();*/
	}

	@Override
	public void dispose () {
		batch.dispose();
		//img.dispose();
	}

	@Override
	public void resize(int width, int height) {
		fitViewport.update(width, height);
		//cameraMap.setToOrtho(false, width, height);
	}
}
