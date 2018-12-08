package com.mtbs3d.minecrift.control;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import com.google.common.base.Joiner;
import com.mtbs3d.minecrift.provider.MCOpenVR;
import com.mtbs3d.minecrift.utils.KeyboardSimulator;
import com.mtbs3d.minecrift.utils.MCReflection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.optifine.reflect.Reflector;

public class VRButtonMapping implements Comparable<VRButtonMapping> {
	public final String functionId;
	public final String functionDesc;
	public final char functionExt;
	public KeyBinding keyBinding;
	public Set<ButtonTuple> buttons;
	protected int unpress;
	protected boolean pressed;
	
	public VRButtonMapping(String functionId, ButtonTuple... buttons) {
		this.functionId = functionId;
		String[] split = functionId.split("_");
		if (split.length == 1 || !functionId.startsWith("keyboard")) {
			this.functionDesc = functionId;
            this.functionExt = 0;
        } else {
        	this.functionDesc = split[0];
            this.functionExt = (char)split[1].getBytes()[0];
        }
		this.buttons = new HashSet<>(Arrays.asList(buttons));
	}
	
	public VRButtonMapping(String functionDesc, char functionExt, ButtonTuple... buttons) {
		this.functionId = functionDesc + (functionExt != 0 ? "_" + functionExt : "");
		this.functionDesc = functionDesc;
		this.functionExt = functionExt;
		this.buttons = new HashSet<>(Arrays.asList(buttons));
	}
	
	@Override
	public String toString() {
		return "vrmapping_" + functionId + ":" + (!buttons.isEmpty() ? Joiner.on(',').join(buttons) : "none");
	}

	@Override
	public int compareTo(VRButtonMapping other) {
		if (keyBinding != null && other.keyBinding != null)
			return keyBinding.compareTo(other.keyBinding);
		if (functionId.startsWith("keyboard") && !other.functionId.startsWith("keyboard"))
			return 1;
		if (!functionId.startsWith("keyboard") && other.functionId.startsWith("keyboard"))
			return -1;
		if (keyBinding != null)
			return I18n.format(keyBinding.getKeyDescription()).compareTo(other.functionId);
		if (other.keyBinding != null)
			return functionId.compareTo(I18n.format(other.keyBinding.getKeyDescription()));
		return functionId.compareTo(other.functionId);
	}
	
	public boolean isGUIBinding() {
		if (keyBinding == Minecraft.getMinecraft().gameSettings.keyBindInventory)
			return Minecraft.getMinecraft().currentScreen instanceof GuiContainer; // dirty hack
		return keyBinding != null && keyBinding.getKeyCategory().startsWith("Vivecraft") && keyBinding.getKeyDescription().startsWith("GUI");
	}
	
	public boolean isKeyboardBinding() {
		return functionDesc.startsWith("keyboard") && keyBinding == null;
	}
	
	public void tick() {
		if (this.unpress > 0) {
			this.unpress -= 1;
			if(this.unpress <= 0)
				actuallyUnpress();
		}
	}
	
	public boolean isPressed() {
		return this.pressed;
	}

	public void press(){	
		this.unpress = 0;
		if (this.pressed) return;
		if(keyBinding != null){
			pressKey(keyBinding);
			this.pressed = true;
			return;
		}
		if(functionExt != 0){
			if(functionDesc.contains("(hold)")){
				KeyboardSimulator.press(functionExt);
			} else {
				KeyboardSimulator.type(functionExt);	
			}		
			this.pressed = true;
			return;
		}	
		if(functionDesc.equals("keyboard-shift")){
			KeyboardSimulator.robot.keyPress(KeyEvent.VK_SHIFT);
			this.pressed = true;
			return;
		}
		if(functionDesc.equals("keyboard-ctrl")){
			KeyboardSimulator.robot.keyPress(KeyEvent.VK_CONTROL);
			this.pressed = true;
			return;
		}
		if(functionDesc.equals("keyboard-alt")){
			KeyboardSimulator.robot.keyPress(KeyEvent.VK_ALT);
			this.pressed = true;
			return;
		}
	}
	
	public void scheduleUnpress(int unpressInTicks){
		this.unpress = unpressInTicks;
	}
	
	public void actuallyUnpress() {
		if (!this.pressed) return;
		this.pressed = false;
		if(keyBinding != null) {
			unpressKey(keyBinding);
			return;
		}
		if(functionExt != 0){
			if(functionDesc.contains("(hold)")){
				KeyboardSimulator.unpress(functionExt);
			} else {
				//nothing
			}		
			return;
		}	
		if(functionDesc.equals("keyboard-shift")){
			KeyboardSimulator.robot.keyRelease(KeyEvent.VK_SHIFT);
			return;
		}
		if(functionDesc.equals("keyboard-ctrl")){
			KeyboardSimulator.robot.keyRelease(KeyEvent.VK_CONTROL);
			return;
		}
		if(functionDesc.equals("keyboard-alt")){
			KeyboardSimulator.robot.keyRelease(KeyEvent.VK_ALT);
			return;
		}
	}

    public static void setKeyBindState(KeyBinding kb, boolean pressed) {
        if (kb != null) {
			MCReflection.KeyBinding_pressed.set(kb, pressed); //kb.pressed = pressed;
			MCReflection.KeyBinding_pressTime.set(kb, (Integer)MCReflection.KeyBinding_pressTime.get(kb) + 1); //++kb.pressTime;
        }       
    }
    
    public static void pressKey(KeyBinding kb) {
    	int awtCode = KeyboardSimulator.translateToAWT(kb.getKeyCode());
    	boolean flag = Display.isActive() && awtCode != Keyboard.KEY_NONE && !MCOpenVR.isVivecraftBinding(kb) && (!Reflector.forgeExists() || Reflector.call(kb, Reflector.ForgeKeyBinding_getKeyModifier) == Reflector.getFieldValue(Reflector.KeyModifier_NONE));
    	if (flag) {
    		try { // because apparently java is just stupid
    		//	System.out.println("Keyboard Simulator keyPress: " + ", LWJGL code: " + kb.getKeyCode() + ", AWT code: " + awtCode);
    			KeyboardSimulator.robot.keyPress(awtCode);
    		} catch (Exception e) {
    			System.out.println("Key error: " + e.toString() + ", LWJGL code: " + kb.getKeyCode() + ", AWT code: " + awtCode);
    			flag = false;
    		}
    	}
    	if (!flag) {
		//	System.out.println("setKeyBindState true: " + ", LWJGL code: " + kb.getKeyCode() + ", AWT code: " + awtCode);
    		setKeyBindState(kb, true);
    	}
    }
    
    public static void unpressKey(KeyBinding kb) {
    	int awtCode = KeyboardSimulator.translateToAWT(kb.getKeyCode());
    	boolean flag = Display.isActive() && awtCode != Keyboard.KEY_NONE && !MCOpenVR.isVivecraftBinding(kb) && (!Reflector.forgeExists() || Reflector.call(kb, Reflector.ForgeKeyBinding_getKeyModifier) == Reflector.getFieldValue(Reflector.KeyModifier_NONE));
    	if (flag) {
    		try { // because apparently java is just stupid
    		//	System.out.println("Keyboard Simulator keyRelease: " + ", LWJGL code: " + kb.getKeyCode() + ", AWT code: " + awtCode);
    			KeyboardSimulator.robot.keyRelease(awtCode);
    		} catch (Exception e) {
    		//	System.out.println("Key error: " + e.toString() + ", LWJGL code: " + kb.getKeyCode() + ", AWT code: " + awtCode);
    			flag = false;
    		}
    	}
    	
    	if (!flag) {
			//System.out.println("unpressKey: " + ", LWJGL code: " + kb.getKeyCode() + ", AWT code: " + awtCode);
			MCReflection.KeyBinding_unpressKey.invoke(kb);
    	}
    }
}
