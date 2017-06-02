package com.justincase.taikoprototype;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;


public class TaikoPrototype extends Game {
	public SpriteBatch batch;
	OrthographicCamera camera;
	FitViewport viewport;
	BitmapFont font;

	public int V_WIDTH = 1500;
	public int V_HEIGHT = 2668;

	public LoadingScreen loadingScreen;
	public PlayMode playScreen;

	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, V_WIDTH, V_HEIGHT);

		loadingScreen = new LoadingScreen(this);

		this.setScreen(loadingScreen);
	}

	@Override
	public void render () {
		super.render();

		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			Gdx.app.exit();
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}

	@Override
	public void resize(int width, int height){

	}
}
