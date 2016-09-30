package com.kifui.fliteloop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.BatchTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class PlayScreen implements Screen, InputProcessor
{
	private final int sceneWidth = 800;
	private final int sceneHeight = 480;
	
	private SpriteBatch batch;
	private OrthographicCamera mapCamera;
	private OrthographicCamera hudCamera;
	private FliteLoopGame game;
	
	private ShapeRenderer debugDraw;
	
	private ButtonObj btnMenu;
	
	Array<String> imgList;
	
	// Main textures
	private Texture t;
	private Texture mapt;
	
	// background box 9 patch
	private TextureRegion backBoxTR;
	private NinePatch backboxNP;
	
	// Plane sprite
	private TextureRegion planeTR;
	
	// Left/Right arrow sprites
	private TextureRegion leftArrowTR;
	private TextureRegion rightArrowTR;
	
	// Background and parallax
	private TextureRegion backgroundTR;
	private TextureRegion parallax1TR;
	private TextureRegion parallax2TR;
	
	// plane position
	private float planeX, planeY;	// center of rotation position
	private float planeAng;			// 0-2PI around the circle of rotation
	private Vector2 planeBoxSize;
	private Vector2 planeBoxPts[];

	// Base linear and rotation speed. Will change over time
	private float planeLinVel;	// Speed in units per seconds
	private float planeRotVel;	// Speed in degrees per seconds
	
	// Keep track of user input
	private boolean bInputLeft;
	private boolean bInputRight;
	
	// Acceleration rate (per seconds) when not turning
	private float planeLinAccel;
	private float planeLinDecel;
	
	// Min/Max velocity
	private float planeLinVelMin;
	private float planeLinVelMax;
	
	// Amount of time at lower velocity to drift into new rotation
	private float planeDriftBaseTime;
	
	// Velocity at which there is no drift anymore
	private float planeLinVelNoDrift;

	// Gravity force
	private Vector2 gravity;
	
	// HUD stuff
	private float score;
	private String sScore;
	private int scoreCount;

	private float timer;
	private String sTimer;
	
	private String sSpeed;
	
	// Plane explosion
	private float explosionTimer;
	TextureRegion explosionTR;
	
	// direction of rotation
	enum EDirRot
	{
		CLW,
		CCLW
	};
	
	// Font
	private BitmapFont font;
	
	// Working vector usage
	private Vector2 v0,v1;
	
	// Last momentum vector at beginning of turn
	private Vector2 lastDirVec;
	
	// When a turn is engaged, we reset a timer
	// and a vector length. It will take some time
	// to reach that final length on the new angle.
	private float turnTimeDelta;
	private boolean newTurn;
	
	// Tiled map object
	private TiledMap map;
	private TiledMapRenderer mapRenderer;
	
	// Map position and max position (X)
	private int mapPos;
	private int mapMaxPos;
	private int tileWidth, tileHeight;
	
	// Play mode
	// 1 : Ready
	// 2 : Play
	// 3 : GameOver
	private enum EMode
	{
		Ready,		// About to start
		Play,		// Playing (user input)
		Explode,	// Dying animation
		GameOver	// Game Over, score shown
	};
	private EMode eMode;
	
	public PlayScreen(FliteLoopGame game)
	{
		this.game = game;

		// Create "Menu" button
		btnMenu = new ButtonObj(
				"data/upbtn1.9.png", "data/dnbtn1.9.png",
				"data/button.fnt",
				0, 0, 64, 64,
				0, 0, 64, 64,
				18, 18, 18, 18,
				350, 0, 100, 32);
		btnMenu.setText("MENU");
		btnMenu.SetColor(1, 1, 0, 1);
		btnMenu.SetTextColor(0, 1, 0, 1);
		
		// Widget texture
		t = new Texture(Gdx.files.internal("data/widgets.png"));
		
		// map texture
		mapt = new Texture(Gdx.files.internal("data/tileset_1.png"));

		// Back box ninepatch
		backBoxTR = new TextureRegion(t,214,27,68,68);
		backboxNP = new NinePatch(backBoxTR, 18, 18, 18, 18);
		
		// Plane
		planeTR = new TextureRegion(t, 217, 0, 64, 20);
		
		// Explosion
		explosionTR = new TextureRegion(t,284,0,148,92);

		// Left/Right arrows
		leftArrowTR  = new TextureRegion(t,0,0,81,133);
		rightArrowTR = new TextureRegion(t,80,0,81,133);
		
		// Background and parallax
		backgroundTR = new TextureRegion(mapt, 0  , 904, 200, 120);
		parallax1TR  = new TextureRegion(mapt, 200, 904, 200, 120);
		parallax2TR  = new TextureRegion(mapt, 400, 904, 200, 120);

		// Other initialization
		batch = new SpriteBatch();
		
		debugDraw = new ShapeRenderer();

		mapCamera = new OrthographicCamera();
		mapCamera.setToOrtho(false, sceneWidth, sceneHeight);
		mapCamera.update();

		hudCamera = new OrthographicCamera();
		hudCamera.setToOrtho(false, sceneWidth, sceneHeight);
		hudCamera.update();
		
		planeBoxSize = new Vector2();
		
		planeBoxPts = new Vector2[4];
		for(int i=0; i<4; i++)
			planeBoxPts[i] = new Vector2();
		v0 = new Vector2();
		v1 = new Vector2();
		
		gravity = new Vector2();
		
		lastDirVec = new Vector2();
		newTurn = false;
		
		font = new BitmapFont(Gdx.files.internal("data/button.fnt"), false);
		
		sTimer = new String();
		sScore = new String();
		sSpeed = new String();

		map = new TmxMapLoader().load("data/map_1.tmx");
		mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);
		
		eMode = EMode.Ready;

	}
	
	public void newGame()
	{
		if (map!=null)
			map.dispose();
		map = new TmxMapLoader().load("data/map_1.tmx");
		mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);
		
		mapCamera.position.set(sceneWidth/2,sceneHeight/2,0);
		mapCamera.update();
		
		hudCamera.position.set(sceneWidth/2,sceneHeight/2,0);
		hudCamera.update();
		
		mapPos = 0;
		
		lastDirVec.set(1,0);
		turnTimeDelta = 100f;
		
		MapProperties prop = map.getProperties();
		mapMaxPos = prop.get("width", Integer.class);
		tileWidth = prop.get("tilewidth", Integer.class);
		tileHeight = prop.get("tileheight", Integer.class);
		
		// Adjust max width with tile width
		mapMaxPos *= tileWidth;

		planeX = 200.0f;
		planeY = 240.0f;
		planeAng = 0.0f;

		planeLinAccel = 25.0f;		// Linear acceleration per seconds
		planeLinDecel = 35.0f;		// Linear deceleration per seconds
		planeLinVelMin = 180.0f;	// Minimum linear velocity
		planeLinVelMax = 600.0f;	// Maximum linear velocity

		planeDriftBaseTime = .5f;	// seconds to complete drift
		planeLinVelNoDrift = 400f;	// Speed where there wont be any drift anymore
		
		gravity.set(0f,-25f);
		
		planeLinVel = planeLinVelMin;
		planeRotVel = 240.0f;
		
		planeBoxSize.set(36.0f, 8.0f);
		
		score = 0f;
		scoreCount = 0;
		timer = 0.0f;

		bInputLeft = false;
		bInputRight = false;
		
		eMode = EMode.Play;
	}	
	
	@Override
	public void render(float delta)
	{
		// Updates done only when in playing mode
		if (eMode == EMode.Play)
		{
			// Update timer
			timer += delta;
			sTimer = String.format("Time: %.2f", timer);
			
			// Update score
			sScore = String.format("Score: %.0f", score);
			
			// Update speed
			if (!bInputLeft && !bInputRight)
			{
				// Accelerate plane
				planeLinVel += (delta * planeLinAccel);
			}
			else if (planeLinVel > planeLinVelMin)
			{
				// Decelerate plane
				planeLinVel -= (delta * planeLinDecel);
				if (planeLinVel < planeLinVelMin)
					planeLinVel = planeLinVelMin;
			}
			sSpeed = String.format("Speed: %.1f MPH", planeLinVel);
			
			// If turning
			if (bInputLeft || bInputRight)
			{
				// Reset new direction velocity and acceleration time
				if (newTurn)
				{
					lastDirVec.set(1,0);
					lastDirVec.setAngle(planeAng);
					lastDirVec.scl(planeLinVel*delta);
				
					turnTimeDelta = 0f;
					newTurn = false;
				}
				
				// Update plane angle if currently turning
				if (bInputLeft)
					planeAng += (delta * planeRotVel);
				else if (bInputRight)
					planeAng -= (delta * planeRotVel);
				
				// Keep angle in 0-360 range
				if (planeAng < 0.0f)
					planeAng += 360.0f;
				if (planeAng > 360.0f)
					planeAng -= 360.0f;
			}
			
			// Update the plane position and rotation if needed
			updatePlane(delta);
			
			// Update map position
			UpdateMapPos();
			
			// Check collisions
			CheckCollisions();
		}
		
		// Start Render
		Gdx.gl.glClearColor(0.3f, 0.3f, 1.0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.setProjectionMatrix(hudCamera.combined);
		batch.begin();
		batch.draw(backgroundTR, 0,  0,  0,  0,  200,  120,  4,  4,  0);
		
		// slow parallax
		float pos = (mapPos*.5f) % sceneWidth;
		batch.draw(parallax2TR, -pos,  0,  0,  0,  sceneWidth,  sceneHeight,  1,  1,  0);
		if (pos > 0)
			batch.draw(parallax2TR, sceneWidth-pos,  0,  0,  0,  sceneWidth,  sceneHeight,  1,  1,  0);
		// Fast parallax
		pos = (mapPos*2f) % sceneWidth;
		batch.draw(parallax1TR, -pos,  0,  0,  0,  sceneWidth,  sceneHeight,  1,  1,  0);
		if (pos > 0)
			batch.draw(parallax1TR, sceneWidth-pos,  0,  0,  0,  sceneWidth,  sceneHeight,  1,  1,  0);
		
		batch.end();
		
		
		// Render map
		mapRenderer.setView(mapCamera);
		mapRenderer.render();
		
		// Render widgets
		batch.setProjectionMatrix(mapCamera.combined);
		batch.begin();
		
		// render plane or explosion
		if (eMode == EMode.Play)
		{
			// Draw plane
			batch.draw(planeTR,
					planeX-32, planeY-10,	// x,y
					32.0f, 10.0f,			// Origin
					64, 20,					// Width, height
					1.0f, 1.0f,				// Scale
					planeAng);				// Rotation
		}
		else if (eMode == EMode.Explode)
		{
			// Draw explosion
			batch.draw(explosionTR,
					planeX-(explosionTR.getRegionWidth()/2),
					planeY-(explosionTR.getRegionHeight()/2));
			// Decrease explosion timer
			explosionTimer -= delta;
			if (explosionTimer < 0f)
			{
				eMode = EMode.GameOver;
				CalculateFinalScore();
			}
		}
	
		batch.end();

		// Render HUD
		batch.setProjectionMatrix(hudCamera.combined);
		batch.begin();
		btnMenu.render(batch);
		if (eMode != EMode.GameOver)
		{
			font.setColor(1f, 1f, 0f, 1f);
			font.draw(batch, sScore, 10, 475);
			font.draw(batch, sTimer, 200, 475);
			font.draw(batch, sSpeed, 400, 475);
			
			batch.setColor(1f,  1f,  1f,  .3f);
			batch.draw(leftArrowTR, 5, (sceneHeight-leftArrowTR.getRegionHeight())/2);
			batch.draw(rightArrowTR, sceneWidth-rightArrowTR.getRegionWidth()-5, (sceneHeight-rightArrowTR.getRegionHeight())/2);
			batch.setColor(1f,  1f,  1f,  1f);
		}

		if (eMode == EMode.GameOver)
		{
			// final score box
			backboxNP.draw(batch,(sceneWidth/2)-(300/2), (sceneHeight/2)-(200/2), 300, 200);
			font.setColor(0f, .7f, 0f, 1f);
			font.draw(batch, "FINAL SCORE", (sceneWidth/2)-90, (sceneHeight/2)+20);
			font.draw(batch, sScore, (sceneWidth/2)-30, (sceneHeight/2)-20);
		}
		batch.end();
		
		// Debug drawing
		/*
		debugDraw.setProjectionMatrix(mapCamera.combined);
		debugDraw.begin(ShapeType.Line);
		debugDraw.setColor(1, 1, 0, 1);
		debugDraw.line(planeBoxPts[0], planeBoxPts[1]);
		debugDraw.line(planeBoxPts[1], planeBoxPts[3]);
		debugDraw.line(planeBoxPts[3], planeBoxPts[2]);
		debugDraw.line(planeBoxPts[2], planeBoxPts[0]);
		debugDraw.circle(planeX, planeY, 4);
		debugDraw.end();
		*/
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
	}

	@Override
	public void pause()
	{
	}

	@Override
	public void resume()
	{
	}

	@Override
	public void dispose()
	{
		map.dispose();
	}
	
	/********************************************
	 *  Utility
	 *******************************************/
	
	private void CheckCollisions()
	{
		// Only done if in play mode
		if (eMode == EMode.Play)
		{
			TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(0);
			
			// Check collision for the 4 collision box corners
			boolean hasCollided = false;
			for(int i=0; i<4; i++)
			{
				int col = (int)planeBoxPts[i].x/32;
				int row = (int)planeBoxPts[i].y/32;
				Cell cell = layer.getCell(col, row);
				if (cell != null)
				{
					MapProperties mp = cell.getTile().getProperties();
					// Check collision against wall
					if (mp.get("col_id")!=null)
					{
						int offX = ((int)planeBoxPts[i].x) - (col*32);
						int offY = ((int)planeBoxPts[i].y) - (row*32);
						if (mp.get("col_id").equals("1"))
						{
							if (offX > offY)
							{
								hasCollided = true;
								Gdx.app.log("collision", "1");
								break;
							}
						}
						else if (mp.get("col_id").equals("2"))
						{
							if (32-offX > offY)
							{
								hasCollided = true;
								Gdx.app.log("collision", "2");
								break;
							}
						}
						else if (mp.get("col_id").equals("3"))
						{
							if (32-offX < offY)
							{
								hasCollided = true;
								Gdx.app.log("collision", "3");
								break;
							}
						}
						else if (mp.get("col_id").equals("4"))
						{
							if (offX < offY)
							{
								hasCollided = true;
								Gdx.app.log("collision", "4");
								break;
							}
						}
						else if (mp.get("col_id").equals("5"))
						{
							//  full tile, instant death
							hasCollided = true;
							break;
						}
					}
					// Check collision against target
					if (mp.get("score")!=null)
					{
						score++;
						scoreCount++;
						layer.setCell(col, row, null);
						break;
					}
					else if (mp.get("goal")!=null)
					{
						score += 2;
						eMode = EMode.GameOver;
						CalculateFinalScore();
						break;
					}
					else if (mp.get("bonus")!=null)
					{
						// TODO
						//int bonus = Integer.valueOf(mp.get("score").toString());
						layer.setCell(col, row, null);
						break;
					}
				}
			}
			
			// Handle collision
			if (hasCollided)
			{
				eMode = EMode.Explode;
				explosionTimer = 2f;
			}
		}
	}
	
	private void UpdateMapPos()
	{
		int lastMapPos = mapPos;
		if (planeX > mapPos + 400)
			mapPos = (int)planeX - 400;
		else if (planeX < mapPos + 300)
			mapPos = (int)planeX - 300;
		if (mapPos < 0)
			mapPos = 0;
		else if (mapPos > mapMaxPos)
			mapPos = mapMaxPos;

		if (mapPos != lastMapPos)
		{
			mapCamera.translate(mapPos-lastMapPos, 0);
			mapCamera.update();
		}
	}
	
	private void updatePlane(float delta)
	{
		// Update target rotation vector
		v0.set(1,0);
		v0.setAngle(planeAng);
		v0.scl(planeLinVel*delta);

		
		if (turnTimeDelta > 0.5f && turnTimeDelta < 0.6f)
		{
			int rtrt=0;
		}
		
		// Calculate drift multiplier from 1 (full drift) to 0 (no drift)
		// 1 = planeLinVelMin;
		// 0 = planeLinVelNoDrift;
		float reducedDrift = planeLinVelNoDrift - ((planeLinVel>=planeLinVelNoDrift) ? planeLinVelNoDrift : planeLinVel);
		reducedDrift = reducedDrift / (planeLinVelNoDrift-planeLinVelMin);
		
		if (reducedDrift>0.01f && turnTimeDelta < (planeDriftBaseTime*reducedDrift))
		{
			v1.set(lastDirVec);
			
			float lerpTime = turnTimeDelta / (planeDriftBaseTime*reducedDrift);
			
			Gdx.app.log("lerpTime: ", String.valueOf(lerpTime));
			
			v1.lerp(v0, lerpTime);
			turnTimeDelta += delta;
		}
		else
			v1.set(v0);
		
		// Apply gravity
		v1.x += (gravity.x * delta);
		v1.y += (gravity.y * delta);
		
		// Update position
		planeX += (v1.x);
		planeY += (v1.y);
		
		Gdx.app.log("reducedDrift: ", String.valueOf(reducedDrift));
		
		// 2. Place points of box around plane center
		planeBoxPts[0].set(planeX - (planeBoxSize.x / 2.0f), planeY + (planeBoxSize.y / 2.0f)); // Top left
		planeBoxPts[1].set(planeX + (planeBoxSize.x / 2.0f), planeY + (planeBoxSize.y / 2.0f)); // Top right
		planeBoxPts[2].set(planeX - (planeBoxSize.x / 2.0f), planeY - (planeBoxSize.y / 2.0f)); // Bottom left
		planeBoxPts[3].set(planeX + (planeBoxSize.x / 2.0f), planeY - (planeBoxSize.y / 2.0f)); // Bottom right

		// 3. Rotate plane box points to their final position
		for(int i=0; i<4; i++)
		{
			// Box corners
			v0.set(planeBoxPts[i].x-planeX, planeBoxPts[i].y-planeY);
			
			// Distance from center
			float len = planeBoxPts[i].dst(planeX,  planeY);
			
			// Normalize
			v0.nor();
			
			// Get angle from X axis
			float ang = v0.angle();

			// Move to position
			v0.setAngle(ang + planeAng);
			
			planeBoxPts[i].set(v0.x*len, v0.y*len);
			planeBoxPts[i].x += planeX;
			planeBoxPts[i].y += planeY;
		}
		
		// Bounce plane back down if it reach the top of the screen
		if (planeBoxPts[1].y > sceneHeight-32.0f && planeAng < 90.0f)
			planeAng = -planeAng;
		else if (planeBoxPts[3].y > sceneHeight-32.0f && planeAng > 90.0f)
			planeAng = 270.0f - (planeAng - 90.0f);

		// Bounce plane back up if it reach the bottom of the screen
		if (planeBoxPts[3].y < 32.0f && planeAng > 270.0f)
			planeAng = 90 - (planeAng - 270.0f);
		else if (planeBoxPts[1].y < 32.0f && planeAng < 270.0f)
			planeAng = 90 + (270.0f - planeAng);
	}
	
	private void CalculateFinalScore()
	{
		float finalscore = 0f;
		
		// Include up to 1/100 precision on the time
		timer *= 100;

		// Score goes to 10000 numbers
		score *= 100000;
		
		// Add both
		score += timer;
		
		sScore = String.format("%d", (int)score);
	}
	
	
	/********************************************
	 *  Input processing
	 *******************************************/

	@Override
	public boolean keyDown(int keycode)
	{
		if(keycode == Keys.DOWN)
		{
			// TEST
	
		}
		if(keycode == Keys.LEFT)
		{
			bInputLeft = true;
			newTurn = true;
		}
		else if(keycode == Keys.RIGHT)
		{
			bInputRight = true;
			newTurn = true;
		}
		else if(keycode == Keys.BACK)
		{
			// go back to main menu
			game.MainMenu();
		}		
		return false;
	}


	@Override
	public boolean keyUp(int keycode)
	{
		if(keycode == Keys.LEFT)
		{
			bInputLeft = false;
		}
		else if(keycode == Keys.RIGHT)
		{
			bInputRight = false;
		}
		return false;
	}


	@Override
	public boolean keyTyped(char character)
	{
		return false;
	}


	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		Vector3 touchPoint=new Vector3(screenX, screenY, 0);
		hudCamera.unproject(touchPoint);		

		// Update buttons
		if (btnMenu.touchDown((int)touchPoint.x, (int)touchPoint.y))
		{
		}
		else if (touchPoint.x < sceneWidth/2)
		{
			bInputLeft = true;
			newTurn = true;
		}
		else if (touchPoint.x > sceneWidth/2)
		{
			bInputRight = true;
			newTurn = true;
		}
		
		return false;
	}


	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button)
	{
		Vector3 touchPoint=new Vector3(screenX, screenY, 0);
		hudCamera.unproject(touchPoint);		

		// Update buttons
		if (btnMenu.touchUp((int)touchPoint.x, (int)touchPoint.y))
		{
			game.MainMenu();
		}
		else if (touchPoint.x < sceneWidth/2)
		{
			bInputLeft = false;
		}
		else if (touchPoint.x > sceneWidth/2)
		{
			bInputRight = false;
		}
		
		return false;
	}


	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer)
	{
		return false;
	}


	@Override
	public boolean mouseMoved(int screenX, int screenY)
	{
		return false;
	}


	@Override
	public boolean scrolled(int amount)
	{
		return false;
	}

}
