package com.soccer.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.soccer.FieldData;
import com.soccer.Soccer;


public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = FieldData.SCREEN_WIDTH;
		config.height = FieldData.SCREEN_HEIGHT;
		config.samples=3;

		new LwjglApplication(new Soccer(), config);

	}
}
