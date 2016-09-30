package com.kifui.fliteloop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class UIStageScreen implements Screen, InputProcessor
{
	private Skin skin;
    private Stage stage;
    private Table container;
	
	private FliteLoopGame game;
	
	UIStageScreen(FliteLoopGame game)
	{
		this.game = game;
		
		stage = new Stage(new FitViewport(800, 480));

		skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		skin.add("top", skin.newDrawable("default-round", Color.RED), Drawable.class);
		skin.add("star-filled", skin.newDrawable("white", Color.YELLOW), Drawable.class); 
		skin.add("star-unfilled", skin.newDrawable("white", Color.GRAY), Drawable.class);

		container = new Table();
		stage.addActor(container);
		container.setFillParent(true);
		
		PagedScrollPane scroll = new PagedScrollPane();
		scroll.setFlingTime(0.1f);
		scroll.setPageSpacing(25);
		scroll.setScrollingDisabled(false, true);
		int c = 1;
		for (int l = 0; l < 10; l++) {
			Table levels = new Table().pad(50);
			levels.defaults().pad(20, 40, 20, 40);
			for (int y = 0; y < 3; y++) {
				levels.row();
				for (int x = 0; x < 4; x++) {
					levels.add(getLevelButton(c++)).expand().fill();
				}
			}
			scroll.addPage(levels);
		}
		container.add(scroll).expand().fill();
		
	}

	public Button getLevelButton(int level) {
		Button button = new Button(skin);
		ButtonStyle style = button.getStyle();
		style.up = 	style.down = null;
		
		// Create the label to show the level number
		Label label = new Label(Integer.toString(level), skin);
		label.setFontScale(2f);
		label.setAlignment(Align.center);		
		
		// Stack the image and the label at the top of our button
		button.stack(new Image(skin.getDrawable("top")), label).expand().fill();

		// Randomize the number of stars earned for demonstration purposes
		/*
		int stars = MathUtils.random(-1, +3);
		Table starTable = new Table();
		starTable.defaults().pad(5);
		if (stars >= 0) {
			for (int star = 0; star < 3; star++) {
				if (stars > star) {
					starTable.add(new Image(skin.getDrawable("star-filled"))).width(20).height(20);
				} else {
					starTable.add(new Image(skin.getDrawable("star-unfilled"))).width(20).height(20);
				}
			}			
		}
		
		button.row();
		button.add(starTable).height(30);
		*/
		
		button.setName("Level" + Integer.toString(level));
		button.addListener(levelClickListener);		
		return button;
	}
	
	/**
	 * Handle the click - in real life, we'd go to the level
	 */
	public ClickListener levelClickListener = new ClickListener() {
		@Override
		public void clicked (InputEvent event, float x, float y) {
			System.out.println("Click: " + event.getListenerActor().getName());
		}
	};		

	

	@Override
	public void render(float delta)
	{
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	    stage.act(Gdx.graphics.getDeltaTime());
	    stage.draw();

	    // Debug draw
	    Table.drawDebug(stage);
	}

	@Override
	public void resize(int width, int height)
	{
		stage.getViewport().update(width, height, false);
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
		stage.dispose();
		skin.dispose();
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
		/*
		Vector3 touchPoint=new Vector3(screenX, screenY, 0);
		camera.unproject(touchPoint);		
		
		btnNewGame.touchDown((int)touchPoint.x, (int)touchPoint.y);
		btnQuit.touchDown((int)touchPoint.x, (int)touchPoint.y);
		*/
		return false;
	}


	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button)
	{
		/*
		Vector3 touchPoint=new Vector3(screenX, screenY, 0);
		camera.unproject(touchPoint);		
		
		if (btnNewGame.touchUp((int)touchPoint.x, (int)touchPoint.y))
		{
			game.NewGame();
		}
		else if (btnQuit.touchUp((int)touchPoint.x, (int)touchPoint.y))
		{
			Gdx.app.exit();
		}
		*/
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
