package me.yuzu.parrot.module;

import net.minecraft.client.Minecraft;

public abstract class Module {
	public String name;
	public String description;
	public Category category;
	public boolean toggled;
	public int keyCode;
	public Minecraft mc;
	
	public Module(String name,String description,Category category,int keyCode) {
		this.name=name;
		this.description=description;
		this.category=category;
		this.toggled=false;
		this.keyCode=keyCode;
		this.mc=Minecraft.getInstance();
		
	}
	
	public int getKeyCode() {
		return keyCode;
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	public void onUpdate() {
		
	}
	
	public void toggle() {
		this.toggled=!this.toggled;
		if(this.toggled) {
			onEnable();
		}else {
			onDisable();
		}
	}
	
	public String getName() {
		return name;
	}
	
	
	
	
	public Category getCategory() {
		return category;
	}
	
	public boolean isToggled() {
		return toggled;
	}
	
	
	public void setToggled(boolean toggled) {
		this.toggled=toggled;
	}
	
	public void onRender() {
		if(this.isToggled()){
			renderLogic();
		}
	}
	
	
	protected void renderLogic() {}

	
}
