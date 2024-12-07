package me.yuzu.parrot.module.movement;

import org.lwjgl.glfw.GLFW;

import me.yuzu.parrot.module.Category;
import me.yuzu.parrot.module.Module;

public class Flight extends Module{

	public Flight() {
		super("Flight", "Enables creative flight in survival", Category.MOVEMENT, GLFW.GLFW_KEY_F);
	}
	
	@Override
	public void onEnable() {
		if(mc.player!=null) {
			mc.player.getAbilities().flying=true;
			mc.player.getAbilities().mayfly=true;
		}
		super.onEnable();
	}
	
	@Override
	public void onDisable() {
		if(mc.player!=null) {
			mc.player.getAbilities().flying=false;
			mc.player.getAbilities().mayfly=false;
		}
		super.onDisable();
	}
	
	@Override
	public void onUpdate() {
		if(this.isToggled()) {
			
			mc.player.getAbilities().mayfly=true;
		}
	}

}
