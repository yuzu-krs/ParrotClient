package me.yuzu.parrot.util;

import java.awt.Color;

public class RainbowUtil {

	public static int getRainbowColor(float offset) {

		float hue=(System.currentTimeMillis()%10000L/10000F+offset);
		return Color.HSBtoRGB(hue, 1.0f, 1.0f);
		
	}
						
}
