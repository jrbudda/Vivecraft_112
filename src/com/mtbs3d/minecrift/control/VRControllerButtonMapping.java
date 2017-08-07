package com.mtbs3d.minecrift.control;

import java.awt.event.KeyEvent;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import com.mtbs3d.minecrift.provider.MCOpenVR;
import com.mtbs3d.minecrift.utils.KeyboardSimulator;
import com.mtbs3d.minecrift.utils.MCReflection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.src.Reflector;

public class VRControllerButtonMapping {

	public ViveButtons Button;
	public String FunctionDesc = "none";
	public char FunctionExt = 0;
	public KeyBinding key;
	private boolean unpress;
	
	public VRControllerButtonMapping(ViveButtons button, String function) {
		this.Button = button;
		this.FunctionDesc = function;		
	}
	
	@Override
	public String toString() {
		return Button.toString() + ":" + FunctionDesc + ( FunctionExt !=0  ? "_" + FunctionExt:"");
	};
	
	public void tick() {
		if (this.unpress) {
			actuallyUnpress();
			this.unpress = false;
		}
	}

	public void press(){	
		this.unpress = false;
		if(this.FunctionDesc.equals("none")) return;
		if(key!=null){
			pressKey(key);
			return;
		}
		if(FunctionExt!=0){
			if(FunctionDesc.contains("(hold)")){
				KeyboardSimulator.press(FunctionExt);
			} else {
				KeyboardSimulator.type(FunctionExt);	
			}		
			return;
		}	
		if(FunctionDesc.equals("keyboard-shift")){
			KeyboardSimulator.robot.keyPress(KeyEvent.VK_SHIFT);
			return;
		}
		if(FunctionDesc.equals("keyboard-ctrl")){
			KeyboardSimulator.robot.keyPress(KeyEvent.VK_CONTROL);
			return;
		}
		if(FunctionDesc.equals("keyboard-alt")){
			KeyboardSimulator.robot.keyPress(KeyEvent.VK_ALT);
			return;
		}
	}
	
	public void unpress(){
		this.unpress = true;
	}
	
	public void actuallyUnpress() {
		if(this.FunctionDesc.equals("none")) return;
		if(key!=null) {
			 unpressKey(key);
			return ;
		}
		if(FunctionExt!=0){
			if(FunctionDesc.contains("(hold)")){
				KeyboardSimulator.unpress(FunctionExt);
			} else {
				//nothing
			}		
			return;
		}	
		if(FunctionDesc.equals("keyboard-shift")){
			KeyboardSimulator.robot.keyRelease(KeyEvent.VK_SHIFT);
			return;
		}
		if(FunctionDesc.equals("keyboard-ctrl")){
			KeyboardSimulator.robot.keyRelease(KeyEvent.VK_CONTROL);
			return;
		}
		if(FunctionDesc.equals("keyboard-alt")){
			KeyboardSimulator.robot.keyRelease(KeyEvent.VK_ALT);
			return;
		}
	}
	

    public static void setKeyBindState(KeyBinding kb, boolean pressed) {
        if (kb != null) {
            MCReflection.setField(MCReflection.KeyBinding_pressed, kb, pressed); //kb.pressed = pressed;
            MCReflection.setField(MCReflection.KeyBinding_pressTime, kb, (Integer)MCReflection.getField(MCReflection.KeyBinding_pressTime, kb) + 1); //++kb.pressTime;
        }       
    }
    
    public static void pressKey(KeyBinding kb) {
    	int awtCode = KeyboardSimulator.translateToAWT(kb.getKeyCode());
    	boolean flag = Display.isActive() && awtCode != Keyboard.KEY_NONE && !MCOpenVR.isVivecraftBinding(kb) && (!Reflector.forgeExists() || Reflector.call(kb, Reflector.ForgeKeyBinding_getKeyModifier) == Reflector.getFieldValue(Reflector.KeyModifier_NONE));
    	if (flag) {
    		try { // because apparently java is just stupid
    			KeyboardSimulator.robot.keyPress(awtCode);
    		} catch (Exception e) {
    			System.out.println("Key error: " + e.toString() + ", LWJGL code: " + kb.getKeyCode() + ", AWT code: " + awtCode);
    			flag = false;
    		}
    	}
    	if (!flag) setKeyBindState(kb, true);
    }
    
    public static void unpressKey(KeyBinding kb) {
    	int awtCode = KeyboardSimulator.translateToAWT(kb.getKeyCode());
    	boolean flag = Display.isActive() && awtCode != Keyboard.KEY_NONE && !MCOpenVR.isVivecraftBinding(kb) && (!Reflector.forgeExists() || Reflector.call(kb, Reflector.ForgeKeyBinding_getKeyModifier) == Reflector.getFieldValue(Reflector.KeyModifier_NONE));
    	if (flag) {
    		try { // because apparently java is just stupid
    			KeyboardSimulator.robot.keyRelease(awtCode);
    		} catch (Exception e) {
    			System.out.println("Key error: " + e.toString() + ", LWJGL code: " + kb.getKeyCode() + ", AWT code: " + awtCode);
    			flag = false;
    		}
    	}
    	if (!flag) MCReflection.invokeMethod(MCReflection.KeyBinding_unpressKey, kb);
    }
}