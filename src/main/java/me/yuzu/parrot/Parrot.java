package me.yuzu.parrot;

import me.yuzu.parrot.module.KeyManager;
import me.yuzu.parrot.module.ModuleManager;
import net.minecraft.client.gui.GuiGraphics;

public class Parrot {
	
	public static String name="Parrot" , creator="Yuzu";
	
	public static Parrot instance=new Parrot();
	
	
	
	public static ModuleManager modManager;
	public static KeyManager keyManager;
	
	
	
	
	
	
	
	public static void startClient() {
		modManager=new ModuleManager();
		keyManager=new KeyManager();
	}

	
	public static void onTick() {
		modManager.onTick(keyManager);
		
	}
	
	public void onRender(GuiGraphics guiGraphics) {
		
	}
	
}