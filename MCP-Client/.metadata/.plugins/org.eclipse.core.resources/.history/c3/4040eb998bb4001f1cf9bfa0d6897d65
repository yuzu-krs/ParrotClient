package me.yuzu.parrot.module;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import me.yuzu.parrot.module.movement.Flight;

public class ModuleManager {
	
	private static List<Module> modules;
	
	
	public ModuleManager() {
		
		modules=new ArrayList<>();
		
		
		//Combat
		
		
		
		
		
		//Movement
		addMod(new Flight());
		
		
		
		//Player
		
		
		
		//Render
		
		
		//Misc
		
		
		//Gui
		
		
		
		
		
		
	}
	
	
	public void addMod(Module module) {
		modules.add(module);
	}
	
	public static List<Module> getAllModules(){
		return modules;
	}
	
	
	public List<Module> getEnabledModules(){
		return modules.stream().filter(Module::isToggled).collect(Collectors.toList());
	}
	
	
	public static List<Module> getModulesByCategory(Category category){
		return modules.stream().filter(module->module.getCategory()==category).collect(Collectors.toList());
		
	}
	
	
	public Module getModuleByName(String name) {
		for(Module module:modules) {
			
			if(module.getName().equalsIgnoreCase(name)) {
				return module;
			}
		}
		return null;
	}
	
	
	public void toggledModule(String name) {
		Module module=getModuleByName(name);
		if(module!=null) {
			module.toggle();
		}
	}
	
	public static void onUpdate() {
		for(Module module:modules) {
			module.onRender();
		}
	}
	
	public void onRender() {
		for(Module module:modules) {
			module.onRender();
		}
	}
	
	public void onTick(KeyManager keyManager) {
		keyManager.checkKeys(this);
		
		for(Module module:modules) {
			if(module.isToggled()) {
				module.onUpdate();
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
