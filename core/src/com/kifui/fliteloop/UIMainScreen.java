package com.kifui.fliteloop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

public class UIMainScreen implements Screen, InputProcessor
{
	private Texture t;
	private TextureRegion tr;
	
	ButtonObj btnNewGame;
	ButtonObj btnQuit;
	
	SpriteBatch batch;
	OrthographicCamera camera;
	
	FliteLoopGame game;
	
	public UIMainScreen(FliteLoopGame game)
	{
		this.game = game;
		
		t = new Texture(Gdx.files.internal("data/titlescreen.png"));
		t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		tr = new TextureRegion(t, 0, 0, 400, 240);
		
		// Start button
		btnNewGame = new ButtonObj(
				"data/upbtn1.9.png", "data/dnbtn1.9.png",
				"data/button.fnt",
				0, 0, 64, 64,
				0, 0, 64, 64,
				18, 18, 18, 18,
				300, 100, 200, 50);
		btnNewGame.setText("Start");
		btnNewGame.SetColor(1, 1, 0, 1);
		btnNewGame.SetTextColor(0, 1, 0, 1);

		// Quit Button
		btnQuit = new ButtonObj(
				"data/upbtn1.9.png", "data/dnbtn1.9.png",
				"data/button.fnt",
				0, 0, 64, 64,
				0, 0, 64, 64,
				18, 18, 18, 18,
				300, 30, 200, 50);
		btnQuit.setText("Exit");
		btnQuit.SetColor(1, 1, 0, 1);
		btnQuit.SetTextColor(0, 1, 0, 1);
		
		
		batch = new SpriteBatch();
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		camera.update();
	}
	
	@Override
	public void render(float delta)
	{
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(tr,0,0,800,480);
		btnNewGame.render(batch);
		btnQuit.render(batch);
		batch.end();
	}

	@Override
	public void resize(int width, int height)
	{
	}

	@Override
	public void show()
	{
		Gdx.input.setInputProcessor(this);
		Gdx.input.setCatchBackKey(true);
	}

	@Override
	public void hide()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void pause()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void resume()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose()
	{
		// TODO Auto-generated method stub

	}

	/********************************************
	 *  Input processing
	 *******************************************/

	@Override
	public boolean keyDown(int keycode)
	{
		if(keycode == Keys.BACK)
		{
			// Exit application
			Gdx.app.exit();
		}
       return false;
	}


	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		Vector3 touchPoint=new Vector3(screenX, screenY, 0);
		camera.unproject(touchPoint);		
		
		btnNewGame.touchDown((int)touchPoint.x, (int)touchPoint.y);
		btnQuit.touchDown((int)touchPoint.x, (int)touchPoint.y);
		return false;
	}


	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button)
	{
		Vector3 touchPoint=new Vector3(screenX, screenY, 0);
		camera.unproject(touchPoint);		
		
		if (btnNewGame.touchUp((int)touchPoint.x, (int)touchPoint.y))
		{
			game.NewGame();
			//game.stageSelect();
		}
		else if (btnQuit.touchUp((int)touchPoint.x, (int)touchPoint.y))
		{
			Gdx.app.exit();
		}
		return false;
	}


	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
