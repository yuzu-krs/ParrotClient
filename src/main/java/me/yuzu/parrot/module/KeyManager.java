package me.yuzu.parrot.module;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;

public class KeyManager {

	private final Minecraft mc=Minecraft.getInstance();
	private boolean[] keysPressed=new boolean[GLFW.GLFW_KEY_LAST];
	
	
	
	public void checkKeys(ModuleManager moduleManager) {
		long windowHandle=mc.getWindow().getWindow();
		
		for(Module module:moduleManager.getAllModules()) {
			int keyCode=module.getKeyCode();
			if(GLFW.glfwGetKey(windowHandle, keyCode)==GLFW.GLFW_PRESS) {
				if(!keysPressed[keyCode]) {
					module.toggle();
					keysPressed[keyCode]=true;
				}
					
			}else if(GLFW.glfwGetKey(windowHandle, keyCode)==GLFW.GLFW_RELEASE) {
					keysPressed[keyCode]=false;
			}
		}
	}

}
