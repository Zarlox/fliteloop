package com.kifui.fliteloop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ButtonObj
{
	private Texture ut,dt;
	private TextureRegion utr, dtr;
	private NinePatch unp, dnp;
	private float x, y, w, h;
	private String text;
	
	private BitmapFont bf;
	private TextBounds textBounds;
	
	private boolean btnIsDown;
	
	public ButtonObj(
			String upTexPath, String dnTexPath, String fontPath,
			int urgnX, int urgnY, int urgnWidth, int urgnHeight,	// Button UP region
			int drgnX, int drgnY, int drgnWidth, int drgnHeight,	// Button DOWN region
			int npLeft, int npTop, int npRight, int npBottom,		// Ninepatch margins
			float btnX, float btnY, float btnWidth, float btnHeight)// Button position
	{
		ut = new Texture(Gdx.files.internal(upTexPath));
		ut.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		utr = new TextureRegion(ut, urgnX, urgnY, urgnWidth, urgnHeight);
		unp = new NinePatch(utr, npLeft, npTop, npRight, npBottom);

		dt = new Texture(Gdx.files.internal(dnTexPath));
		dt.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		dtr = new TextureRegion(dt, drgnX, drgnY, drgnWidth, drgnHeight);
		dnp = new NinePatch(dtr, npLeft, npTop, npRight, npBottom);
		
		bf = new BitmapFont(Gdx.files.internal(fontPath), false);
		textBounds = new TextBounds();
		
		x = btnX;
		y = btnY;
		w = btnWidth;
		h = btnHeight;
		
		btnIsDown = false;
	}
	
	public void setText(String s)
	{
		text = s;
		bf.getBounds(text, textBounds);
	}
	
	public void SetTextColor(float r, float g, float b, float a)
	{
		Color c = new Color(r,g,b,a);
		bf.setColor(c);
	}

	public void SetUpColor(float r, float g, float b, float a)
	{
		Color c = new Color(r,g,b,a);
		unp.setColor(c);
	}
	
	public void SetDownColor(float r, float g, float b, float a)
	{
		Color c = new Color(r,g,b,a);
		dnp.setColor(c);
	}
	
	public void SetColor(float r, float g, float b, float a)
	{
		Color c = new Color(r,g,b,a);
		unp.setColor(c);
		dnp.setColor(c);
	}
	
	public boolean touchDown(int tx, int ty)
	{
		if (tx >= x && tx <= x+w &&
			ty >= y && ty <= y+h)
			btnIsDown = true;
		return btnIsDown;
	}

	public boolean touchUp(int tx, int ty)
	{
		boolean bWasDown = btnIsDown; 
		btnIsDown = false;
		return bWasDown;
	}
	
	public void render(SpriteBatch b)
	{
		if (btnIsDown)
			dnp.draw(b, x, y, w, h);
		else
			unp.draw(b, x, y, w, h);
		bf.draw(b, text, x + (w-textBounds.width)/2, y + ((h-textBounds.height)/2)+textBounds.height);
	}
	
	
	
}
