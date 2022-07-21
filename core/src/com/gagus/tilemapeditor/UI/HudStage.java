package com.gagus.tilemapeditor.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.gagus.tilemapeditor.Map.MapManager;
import com.gagus.tilemapeditor.UI.Actors.TileActor;
import com.gagus.tilemapeditor.UI.Interfaces.HudTileSelector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HudStage extends Stage implements HudTileSelector{
	//OrthographicCamera cameraMenu;
	Table leftTable, rightTable, tilesTable;
	public static int menuWidth = Gdx.graphics.getWidth()/5, menuHeight = Gdx.graphics.getHeight();
	ShapeRenderer sp;
	MapManager mapManager;

	Pixmap pixmap;
	BitmapFont bitmapFont;
	//Actors
	TextButton newTileButton, saveButton, loadButton, eraseButton;
	TextButton.TextButtonStyle textButtonStyle;
	TileActor tileSelected;
	CheckBox collidableCheckbox;
	CheckBox.CheckBoxStyle checkBoxStyle;




	public HudStage(MapManager mapManager){
		/*cameraMenu = new OrthographicCamera();
		cameraMenu.viewportWidth = Gdx.graphics.getWidth();
		cameraMenu.viewportHeight = Gdx.graphics.getHeight();
		cameraMenu.position.set(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2, 0);
		cameraMenu.update();*/

		this.mapManager = mapManager;

		pixmap = new Pixmap( menuWidth, menuHeight, Pixmap.Format.RGBA8888 );
		pixmap.setColor( 0.7f, 0.7f, 0.7f, 1f );
		pixmap.fillRectangle( 0, 0, menuWidth,menuHeight );
		TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable( new TextureRegion(new Texture(pixmap ))); //relaché

		leftTable = new Table();
		leftTable.background(backgroundDrawable);
		leftTable.setSize(menuWidth, menuHeight);
		leftTable.setPosition(0,0);
		leftTable.setDebug(true);
		rightTable = new Table();
		rightTable.background(backgroundDrawable);
		rightTable.setSize(menuWidth, menuHeight);
		rightTable.setPosition(Gdx.graphics.getWidth()-menuWidth, 0);
		rightTable.setDebug(true);

		addActor(leftTable);
		addActor(rightTable);
		leftTable.clear();

		sp = new ShapeRenderer();

		pixmap = new Pixmap( 64, 64, Pixmap.Format.RGBA8888 );
		pixmap.setColor( 1, 1, 1, 1f );
		pixmap.fillRectangle( 0, 0, 64,64 );
		TextureRegionDrawable greyLight = new TextureRegionDrawable( new TextureRegion(new Texture(pixmap ))); //relaché
		pixmap.setColor( 0.3f, 0.3f, 0.3f, 1f );
		pixmap.fillRectangle( 0, 0, 64,64 );
		TextureRegionDrawable greyDark = new TextureRegionDrawable( new TextureRegion(new Texture(pixmap ))); //appuyé
		bitmapFont = new BitmapFont(Gdx.files.internal("font.fnt"));
		textButtonStyle = new TextButton.TextButtonStyle(greyLight,greyDark,null, bitmapFont);
		newTileButton = new TextButton("New TileWith", textButtonStyle);
		//newTileButton.setPosition(20,600);
		newTileButton.addListener(new InputListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				Gdx.app.log("button new tile touch down","");
				makeNewTile();
				return super.touchDown(event, x, y, pointer, button);
			}
		});
		leftTable.add(newTileButton);
		leftTable.row();

		//backgroundLeft.setZIndex(0);
		//backgroundRight.setZIndex(0);
		//newTileButton.setZIndex(1);
		tilesTable = new Table();
		tilesTable.setDebug(true);
		ScrollPane.ScrollPaneStyle paneStyle = new ScrollPane.ScrollPaneStyle();
		ScrollPane pane = new ScrollPane(tilesTable, paneStyle);

		leftTable.add(pane).size(menuWidth,menuHeight/3);
		leftTable.row();

		saveButton = new TextButton("SAVE", textButtonStyle);
		saveButton.addListener(new InputListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				saveMap();
				return false;
			}
		});
		rightTable.add(saveButton).pad(5);

		loadButton = new TextButton("LOAD", textButtonStyle);
		loadButton.addListener(new InputListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				loadMap();
				return false;
			}
		});
		rightTable.add(loadButton).pad(10);

		/*eraseButton = new TextButton("Gomme", textButtonStyle);
		//newTileButton.setPosition(20,600);
		eraseButton.addListener(new InputListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				Gdx.app.log("button select erase touch down","");
				selectErase();
				return super.touchDown(event, x, y, pointer, button);
			}
		});
		leftTable.add(eraseButton);*/
		pixmap.setColor( 0, 1, 0, 1f );
		pixmap.fill();
		TextureRegionDrawable green = new TextureRegionDrawable( new TextureRegion(new Texture(pixmap ))); //relaché
		pixmap.setColor( 1, 0, 0, 1f );
		pixmap.fill();
		TextureRegionDrawable red = new TextureRegionDrawable( new TextureRegion(new Texture(pixmap ))); //relaché

		checkBoxStyle = new CheckBox.CheckBoxStyle(red, green, bitmapFont, Color.GREEN);
		collidableCheckbox = new CheckBox("Collidable", checkBoxStyle);
		collidableCheckbox.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onClickSetTileCollidable();
			}
		});
		leftTable.add(collidableCheckbox).pad(10);


	}

	public void selectTile(TileActor tileActor){
		tileSelected = tileActor;
		collidableCheckbox.setChecked(tileSelected.collidable);
		mapManager.setTileSelected(tileSelected.imageName);
	}

	@Override
	public void dispose() {
		pixmap.dispose();
		super.dispose();
	}

	@Override
	public void draw() {
		super.draw();
		drawSelectedTile();
	}

	public void drawSelectedTile(){
		if(tileSelected != null){
			Vector2 position = tileSelected.localToStageCoordinates(new Vector2());
			Rectangle border = new Rectangle(position.x, position.y, tileSelected.getWidth(), tileSelected.getHeight());
			sp.begin(ShapeRenderer.ShapeType.Line);
			sp.setColor(Color.GREEN);
			sp.rect(border.x, border.y, border.width, border.height);
			sp.end();
		}

	}

	public void makeNewTile(){
		String imageName = Gdx.app.getClipboard().getContents();
		if(!tileExist(imageName)){
			Gdx.app.log("make new tile", imageName);
			TileActor actor = null;
			try{
				Pixmap pixmap200 = new Pixmap(Gdx.files.internal(imageName));
				Vector2 resolution = findResolution(pixmap200);
				Pixmap pixmap100 = new Pixmap((int)resolution.x, (int)resolution.y, pixmap200.getFormat());
				pixmap100.drawPixmap(pixmap200,
					0, 0, pixmap200.getWidth(), pixmap200.getHeight(),
					0, 0, pixmap100.getWidth(), pixmap100.getHeight()
				);
				TextureRegionDrawable imageTile = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap100)));
				pixmap100.dispose();
				pixmap200.dispose();
				actor = new TileActor(imageName, imageTile, this, false);
				if(tilesTable.getCells().size%4==0) tilesTable.row();
				tilesTable.add(actor).pad(5);
			}
			catch(Exception e){
				Gdx.app.log("chargement de l'image échoué","nom de fichier incorrect");
				return;
			}
		}
	}

	public boolean tileExist(String imageName){
		boolean exist = false;
		for(Cell actor : tilesTable.getCells()){
			TileActor tile = (TileActor) actor.getActor();
			if(tile.imageName.equals(imageName) ){
				exist = true;
				break;
			}
		}
		return exist;
	}

	public Vector2 findResolution(Pixmap pixmap){
		int width = pixmap.getWidth();
		int height = pixmap.getHeight();
		while(width>55 || height>55){
			float ratio = width/height;
			width-=ratio;
			height-=1;
		}
		Gdx.app.log("find size", "width : "+width+" height : "+height);
		return new Vector2(width, height);
	}

	public void saveMap(){
		String mapName = Gdx.app.getClipboard().getContents();
		Map<String, Integer> tilesImages = new HashMap<String, Integer>();
		ArrayList tilesCollidables = new ArrayList();
		int index = 0;
		for(Cell cell : tilesTable.getCells()){
			TileActor actor = (TileActor)cell.getActor();
			tilesImages.put(actor.imageName, index);
			if(actor.collidable) tilesCollidables.add(index);
			index++;
		}
		mapManager.saveMap(tilesImages, mapName, tilesCollidables);
	}

	public void loadMap(){
		String mapName = Gdx.app.getClipboard().getContents();
		mapManager.loadMap(mapName);
	}

	public void selectErase(){
		tileSelected = null;
		mapManager.selectErase();
	}

	public void onClickSetTileCollidable(){
		tileSelected.collidable = collidableCheckbox.isChecked();
	}
}
