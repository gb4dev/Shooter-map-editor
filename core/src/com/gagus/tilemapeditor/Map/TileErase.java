package com.gagus.tilemapeditor.Map;


import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.utils.ShortArray;

public class TileErase extends Tile {
	PolygonRegion polyReg;
	EarClippingTriangulator triangulator;
	ShortArray triangles;
	PolygonSpriteBatch polygonSpriteBatch;
	TextureRegion texture;

	public TileErase(int x, int y, int tileWidth, int tileHeight, PolygonSpriteBatch polygonSpriteBatch){
		super(x,y,tileWidth,tileHeight,null, null);
		float[] vectrices = polygon.getTransformedVertices();
		Pixmap pixmap = new Pixmap( tileWidth, tileHeight, Pixmap.Format.RGBA8888 );
		pixmap.setColor( 0.5f, 0.5f, 0.5f, 1f );
		pixmap.fillRectangle( 0, 0, tileWidth,tileHeight );
		texture = new TextureRegion(new Texture(pixmap ));
		triangulator = new EarClippingTriangulator();
		triangles = triangulator.computeTriangles(vectrices);
		polyReg = new PolygonRegion(texture, vectrices, triangles.toArray());
		this.polygonSpriteBatch = polygonSpriteBatch;
	}

	@Override
	public void drawTile(SpriteBatch batch) {
		polygon.setPosition(posX,posY);
		float[] vectrices = polygon.getTransformedVertices();
		polyReg = new PolygonRegion(texture, vectrices, triangles.toArray());
		PolygonSprite poly = new PolygonSprite(polyReg);
		polygonSpriteBatch.setProjectionMatrix(batch.getProjectionMatrix());
		polygonSpriteBatch.begin();
		poly.draw(polygonSpriteBatch);
		polygonSpriteBatch.end();
	}
}
