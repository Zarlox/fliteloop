package com.kifui.fliteloop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FliteLoopGame extends Game
{
	PlayScreen playScreen;
	UIMainScreen mainScreen;
	UIStageScreen stageScreen;

	public void stageSelect()
	{
		//stageScreen.newGame();
		setScreen(stageScreen);
	}
	
	public void NewGame()
	{
		playScreen.newGame();
		setScreen(playScreen);
	}
	
	public void MainMenu()
	{
		setScreen(mainScreen);
	}
	
	@Override
	public void create()
	{		
		playScreen = new PlayScreen(this);
		mainScreen = new UIMainScreen(this);
		stageScreen = new UIStageScreen(this);
		
		setScreen(mainScreen);
	}

	@Override
	public void dispose()
	{
		mainScreen.dispose();
		playScreen.dispose();
		stageScreen.dispose();
	}

	@Override
	public void render()
	{		
		super.render();
	}

	@Override
	public void resize(int width, int height)
	{
	}

	@Override
	public void pause()
	{
	}

	@Override
	public void resume()
	{
	}
}
