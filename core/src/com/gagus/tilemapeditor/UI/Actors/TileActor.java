package com.gagus.tilemapeditor.UI.Actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.gagus.tilemapeditor.UI.Interfaces.HudTileSelector;

public class TileActor extends ImageButton {
	public String imageName;
	public boolean collidable;
	HudTileSelector selector;

	public TileActor(String imageName, TextureRegionDrawable texture, final HudTileSelector selector, boolean collidable){
		super(texture,texture);
		this.imageName = imageName;
		this.selector = selector;
		this.collidable = collidable;
		addListener(new InputListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				selectTile();
				Gdx.app.log("click tile actor","");
				return false;
			}
		});
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
	}

	public void selectTile(){
		selector.selectTile(this);
	}
}
