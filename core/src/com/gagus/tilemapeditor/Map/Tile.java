package com.gagus.tilemapeditor.Map;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;

/**
 * Created by Gaetan on 27/07/2018.
 */

public class Tile {
	public int x, y, posX, posY, tileWidth, tileHeight;
	Polygon polygon;
	Texture img;
	String imageName;
	public boolean visible;

	public Tile(int x, int y, int tileWidth, int tileHeight, Texture img, String imageName){
		this.x = x;
		this.y = y;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		createPolygon();

		this.img = img;
		visible = true;
		this.imageName = imageName;
	}

	public void drawTile(SpriteBatch batch){
		if(visible) batch.draw(img,posX, posY);
	}

	public void drawPolygon(ShapeRenderer shapeRenderer){
		shapeRenderer.polygon(polygon.getTransformedVertices());
	}

	public void createPolygon(){
		this.posX = x*tileWidth/2 + y*tileWidth/2;
		this.posY = x*tileHeight/2 - y*tileHeight/2;
		float [] vectrices = new float[8];
		vectrices[0] = posX;
		vectrices[1] = posY + tileHeight/2;

		vectrices[2] = posX + tileWidth/2;
		vectrices[3] = posY + tileHeight;

		vectrices[4] = posX + tileWidth;
		vectrices[5] = posY + tileHeight/2;

		vectrices[6] = posX + tileWidth/2;
		vectrices[7] = posY;

		this.polygon = new Polygon(vectrices);
	}
}
