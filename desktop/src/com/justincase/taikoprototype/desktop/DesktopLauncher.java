package com.justincase.taikoprototype.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.justincase.taikoprototype.TaikoPrototype;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Prototype";
		config.width = 540;
		config.height = 960;
		config.samples=3;
		new LwjglApplication(new TaikoPrototype(), config);
	}
}
