package com.gagus.tilemapeditor.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ShortArray;
import com.gagus.tilemapeditor.TileMapEditor;
import com.gagus.tilemapeditor.UI.Actors.TileActor;
import com.gagus.tilemapeditor.UI.HudStage;
import com.gagus.tilemapeditor.UI.Interfaces.HudTileSelector;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Gaetan on 27/07/2018.
 */

public class MapManager {
	public static int tileWidth, tileHeight, mapWidth, mapHeight, mapPixelsWidth, mapPixelsHeight;
	SpriteBatch batch;
	ShapeRenderer shapeRenderer;
	OrthographicCamera camera;

	Tile[][] map;
	public Polygon[][] polygons;
	PolygonSprite poly;
	PolygonSpriteBatch polyBatch;
	Texture textureSolid;

	int tileColumnOffset = 128; // pixels
	int tileRowOffset = 64; // pixels
	Rectangle areaMap;
	Tile tileSelected; // tile selectionné pour remplir la map

	EarClippingTriangulator triangulator;
	PolygonRegion polyReg;

	public MapManager(int tileWidth, int tileHeight, int mapWidth, int mapHeight, OrthographicCamera camera, SpriteBatch batch){
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		this.mapPixelsWidth = tileWidth * mapWidth;
		this.mapPixelsHeight = tileHeight * mapHeight;
		this.areaMap = new Rectangle(HudStage.menuWidth, 0, HudStage.menuWidth*3, HudStage.menuHeight);
		this.batch = batch;
		shapeRenderer = new ShapeRenderer();
		this.camera = camera;
		map = new Tile[mapHeight][mapWidth];
		createPolygons();

		// Creating the color filling (but textures would work the same way)
		Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		pix.setColor(0.5f,0.5f,0.5f,1); // DE is red, AD is green and BE is blue.
		pix.fill();
		textureSolid = new Texture(pix);

		triangulator = new EarClippingTriangulator();
		polyReg = new PolygonRegion(new TextureRegion(textureSolid), new float[8], new short[4]);
		poly = new PolygonSprite(polyReg);
		polyBatch = new PolygonSpriteBatch();
	}

	public void drawMap(){
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		for(int y = 0 ; y< polygons.length; y++) {
			for (int x = polygons[y].length-1; x >= 0 ; x--) {
				if(map[y][x] != null){
					map[y][x].drawTile(batch);
				}

			}
		}
		if(tileSelected != null){
			tileSelected.drawTile(batch);
		}
		else{
			polyBatch.setProjectionMatrix(batch.getProjectionMatrix());
			polyBatch.begin();
			poly.draw(polyBatch);
			polyBatch.end();
		}
		batch.end();
	}

	public void drawPolygons(){
		//Color[] colors = new Color[]{ Color.BLUE, Color.BROWN, Color.YELLOW, Color.RED, Color.GREEN};
		//int a = 0;
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		//shapeRenderer.setColor(Color.BLACK);
		for(int y = 0 ; y< polygons.length; y++) {
			shapeRenderer.setColor(Color.DARK_GRAY);
			//a = ++a>=5?0:a;
			for (int x = 0; x < polygons[y].length; x++) {
				shapeRenderer.polygon(polygons[y][x].getTransformedVertices());
			}
		}
		//shapeRenderer.rect(100,100,200,200);
		shapeRenderer.end();
	}

	public void createPolygons(){
		/* map rectangulaire
		for(int y = 0 ; y< polygons.length; y++){
			for(int x = 0; x< polygons[y].length; x++){
				int posX = x*tileWidth - x*tileWidth/2;
				int posY = y*tileHeight + ((x%2)*tileHeight/2);
				float [] vectrices = new float[8];
				vectrices[0] = posX;
				vectrices[1] = posY + tileHeight/2;

				vectrices[2] = posX + tileWidth/2;
				vectrices[3] = posY + tileHeight;

				vectrices[4] = posX + tileWidth;
				vectrices[5] = posY + tileHeight/2;

				vectrices[6] = posX + tileWidth/2;
				vectrices[7] = posY;

				Polygon polygon = new Polygon(vectrices);
				polygon.setOrigin(posX, posY);
				polygons[y][x] = polygon;
			}
		}
		*/
		polygons = new Polygon[mapWidth][mapHeight];
		for(int y = 0 ; y< polygons.length; y++){
			for(int x = 0; x< polygons[y].length; x++){
				int posX = x*tileWidth/2 + y*tileWidth/2;
				int posY = x*tileHeight/2 - y*tileHeight/2;
				float [] vectrices = new float[8];
				vectrices[0] = posX;
				vectrices[1] = posY + tileHeight/2;

				vectrices[2] = posX + tileWidth/2;
				vectrices[3] = posY + tileHeight;

				vectrices[4] = posX + tileWidth;
				vectrices[5] = posY + tileHeight/2;

				vectrices[6] = posX + tileWidth/2;
				vectrices[7] = posY;

				Polygon polygon = new Polygon(vectrices);
				polygon.setOrigin(posX, posY);
				polygons[y][x] = polygon;
			}
		}

	}

	public void drawOverPolygon(int x, int y){
		//dessine un rectangle en rouge sur la case en x,y
		/*float[] vectrices = polygons[y][x].getTransformedVertices();
		EarClippingTriangulator triangulator = new EarClippingTriangulator();
		ShortArray triangles = triangulator.computeTriangles(vectrices);
		PolygonRegion polyReg = new PolygonRegion(new TextureRegion(textureSolid), vectrices, triangles.toArray());
		poly = new PolygonSprite(polyReg);*/
		if(tileSelected != null){ // si aucune tile n'est selectionné
			tileSelected.posX = (int)polygons[x][y].getOriginX();
			tileSelected.posY = -(int)polygons[x][y].getOriginY();
			tileSelected.visible = true;
			Gdx.app.log("set tileselect position", "x : "+tileSelected.posX+" y : "+tileSelected.posY );
		}
		else{
			int posX = (int)polygons[x][y].getOriginX();
			int posY = -(int)polygons[x][y].getOriginY();
			poly.setPosition(posX,posY);
		}


	}

	public void setTileSelectedInvisible(){
		if(tileSelected != null) tileSelected.visible = false;
	}

	public Vector2 getTileWithPoint(Vector2 pos){
		int tileX = Math.round(pos.x/tileColumnOffset + pos.y/tileRowOffset)/2; // get tile x
		int tileY = Math.round(pos.x/tileColumnOffset - pos.y/tileRowOffset)/2; // get tile y

		//Gdx.app.log("controller mouse position ","X : "+pos.x+" Y : "+pos.y);
		//Gdx.app.log("controller mouse colided ","X : "+tileX+" Y : "+tileY);
		if(tileY>=-0 && tileY<= this.polygons.length && tileX>=0 && tileX<=this.polygons[0].length) { // if mouse over tile

			//methode de delimitation des points a tester selon la position de la souris
			int startX = tileX - 1;
			int startY = tileY - 1;
			int endX = tileX + 1;
			int endY = tileY + 1;
			//Gdx.app.log("controller mouse position ","X : "+posX+" Y : "+posY);
			//Gdx.app.log("controller mouse position ","X : "+pos.x+" Y : "+pos.y);
			startX = startX < 0 ? 0 : startX;
			startY = startY < 0 ? 0 : startY;
			endY = endY >= this.polygons.length ? this.polygons.length - 1 : endY;
			endX = endX >= this.polygons[endY].length ? this.polygons[endY].length - 1 : endX;

			//methode brute pour trouver la lite en fonction de la position de la sourie et de la tile trouvé aproximativement
			for (int y = startY; y <= endY; y++) {
				for (int x = startX; x <= endX; x++) {
					if (this.polygons[y][x].contains(pos.x, pos.y)) {
						//Gdx.app.log("controller mouse search tile ", "X : " + x + " Y : " + y);
						return new Vector2(x,y);
					}
				}
			}
		}
		return null;
	}

	public boolean pointInMap(float x, float y){
		//Gdx.app.log("point in map", areaMap.toString());
		if(areaMap.contains(x,y)) return true;
		return false;
	}

	public void setTileSelected(String imageName) {
		Texture texture = new Texture(imageName);
		tileSelected = new Tile(0,0,tileWidth, tileHeight, texture, imageName);
		tileSelected.visible = false;
		Gdx.app.log("set tile in map manager","");
	}

	public void placeTile(int x, int y){
		if(tileSelected != null){
			if(tileSelected.imageName != null){
				map[y][x] = new Tile(x, y, tileWidth, tileHeight, tileSelected.img, tileSelected.imageName);
				Gdx.app.log("place tile mapmanager", "tile placed !");
			}
			else{
				map[y][x] = null;
			}
		}

	}

	public void saveMap(Map<String, Integer> images, String mapName, ArrayList tilesCollidables){
		final GsonBuilder builder = new GsonBuilder();
		final Gson gson = builder.create();
		final Map valeurs = new HashMap();
		valeurs.put("version", TileMapEditor.VERSION);
		valeurs.put("mapWidth", mapWidth);
		valeurs.put("mapHeight", mapHeight);
		valeurs.put("tileWidth", tileWidth);
		valeurs.put("tileHeight", tileHeight);
		valeurs.put("imagesTiles", images);
		valeurs.put("collidableTiles",tilesCollidables);
		//ArrayList<ArrayList<String>> mapNumbers = new ArrayList<ArrayList<String>>();
		int[][] mapNumbers = new int[mapHeight][mapWidth];
		for(int y = 0; y<map.length; y++){
			//ArrayList<String> line = new ArrayList<String>();
			for(int x = 0; x<map[y].length; x++){
				if(map[y][x] != null){
					Tile tile = map[y][x];
					mapNumbers[y][x] = images.get(tile.imageName);
					//line.add(String.valueOf(images.indexOf(tile.imageName)));
				}
				else{
					//line.add("-1");
					mapNumbers[y][x] = -1;
				}
			}
			//mapNumbers.add(line);
		}
		valeurs.put("tileMap", mapNumbers);

		String jsonText = gson.toJson(valeurs);
		Gdx.app.log("save map json",jsonText);

		//String locRoot = Gdx.files.getLocalStoragePath();
		Gdx.app.log("files :", Gdx.files.getLocalStoragePath());
		FileHandle handle = Gdx.files.local(mapName);
		handle.writeString(jsonText, false);
	}

	public void loadMap(String mapName){
		String jsonText;
		try{
			FileHandle mapFile = Gdx.files.local(mapName);
			jsonText = mapFile.readString();
		}
		catch(Exception e){
			Gdx.app.log("load map fail","error when open file");
			Gdx.app.log("load map fail",e.getMessage());
			return;
		}

		final GsonBuilder builder = new GsonBuilder();
		final Gson gson = builder.create();
		Map valeurs;
		try{
			valeurs = gson.fromJson(jsonText, Map.class);
		}
		catch(Exception e){
			Gdx.app.log("load map fail","error when reading json");
			Gdx.app.log("load map fail",e.getMessage());
			return;
		}

		float mapWidth = Float.parseFloat(valeurs.get("mapWidth").toString());
		float mapHeight = Float.parseFloat(valeurs.get("mapHeight").toString());
		float tileWidth = Float.parseFloat(valeurs.get("tileWidth").toString());
		float tileHeight = Float.parseFloat(valeurs.get("tileHeight").toString());
		Map images = (Map<String, Integer>)valeurs.get("imagesTiles");
		//ArrayList<ArrayList<String>> mapNumbers = (ArrayList<ArrayList<String>>)jsonObject.get("tileMap");
		Gdx.app.log(valeurs.get("tileMap").getClass().toString(),"");
		ArrayList mapNumbers = (ArrayList) valeurs.get("tileMap");
		Gdx.app.log("get objects json mapNumber", mapNumbers.toString());
		Tile[][] mapLoaded = new Tile[(int)mapHeight][(int)mapWidth]; // new map
		Texture[] textures = new Texture[images.size()];
		//set textures in array
		Gdx.app.log("load images", Gdx.files.getLocalStoragePath()+" | "+ Gdx.files.getExternalStoragePath());
		Set<String> keys = images.keySet();
		for(String key : keys){
			Texture texture = new Texture(key);
			textures[(int)Float.parseFloat(images.get(key).toString())] = texture;
		}

		ArrayList tilesCollidables = (ArrayList)valeurs.get("collidableTiles");
		for(int y = 0 ; y< mapLoaded.length; y++) {
			for (int x = 0; x < mapLoaded[y].length; x++) {
				Gdx.app.log("create tile", "");
				//int tileImage = Integer.parseInt(mapNumbers.get(y).get(x));
				float tileImage = Float.parseFloat(((ArrayList)mapNumbers.get(y)).get(x).toString());
				if((int)tileImage != -1){
					mapLoaded[y][x] = new Tile(x, y, (int)tileWidth, (int)tileHeight, textures[(int)tileImage], (String)images.get((int)tileImage));
				}
				else{
					mapLoaded[y][x] = null;
				}
			}
		}

		this.tileWidth = (int)tileWidth;
		this.tileHeight = (int)tileHeight;
		this.mapWidth = (int)mapWidth;
		this.mapHeight = (int)mapHeight;
		this.mapPixelsWidth = (int)tileWidth * (int)mapWidth;
		this.mapPixelsHeight = (int)tileHeight * (int)mapHeight;
		createPolygons();
		this.map = mapLoaded;
	}

	public void selectErase(){
		this.tileSelected = null;
	}
}
