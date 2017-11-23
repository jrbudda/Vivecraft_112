/**
 * Copyright 2013 Mark Browning, StellaArtois
 * Licensed under the LGPL 3.0 or later (See LICENSE.md for details)
 */
package com.mtbs3d.minecrift.gui;

import java.io.IOException;
import java.util.Set;

import com.mtbs3d.minecrift.control.ButtonTuple;
import com.mtbs3d.minecrift.control.ButtonType;
import com.mtbs3d.minecrift.control.ControllerType;
import com.mtbs3d.minecrift.control.VRButtonMapping;
import com.mtbs3d.minecrift.gui.framework.BaseGuiSettings;
import com.mtbs3d.minecrift.gui.framework.GuiButtonEx;
import com.mtbs3d.minecrift.provider.MCOpenVR;
import com.mtbs3d.minecrift.settings.VRSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiKeyBindingList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;

public class GuiVRControls extends BaseGuiSettings {

	public VRButtonMapping mapping; 
	public Set<ButtonTuple> mappingButtons;
	public boolean selectionMode = false;
	private boolean waitingForKey = false;
	private boolean keyboardHoldSelect = false;
	private boolean keyboardHold = false;
    
	private GuiVRControlsList guiList;
	private GuiKeyBindingSelection guiSelection;
    private GuiButton btnDefaults;
    private GuiButton btnDone;
    private GuiButton btnCancel;
    private GuiButton btnAddKey;
    private GuiButton btnKeyboardPress;
    private GuiButton btnKeyboardHold;

	public GuiVRControls(GuiScreen par1GuiScreen, VRSettings par2vrSettings) {
		super(par1GuiScreen, par2vrSettings);
        screenTitle = "VR Control Remapping";
	}

    /**
     * Fired when a key is typed (except F11 who toggle full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode)
    {
        if (waitingForKey)
        {
        	if (!Character.isISOControl(typedChar)) {
	        	String function = "keyboard ";
	        	if (keyboardHold) function += "(hold)_";
	        	else function += "(press)_";
	        	function += typedChar;
	        	if (!mc.vrSettings.buttonMappings.containsKey(function)) {
	        		mc.vrSettings.buttonMappings.put(function, new VRButtonMapping(function));
	        		guiList.buildList();
	        	}
	            waitingForKey = false;
        	}
        }
        else
        {
            try {
				super.keyTyped(typedChar, keyCode);
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }


    
    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui() {
    	this.guiList = new GuiVRControlsList(this, mc);
    	this.guiSelection = new GuiKeyBindingSelection(this, mc);
        this.buttonList.clear();
        btnDefaults = (new GuiButtonEx(ID_GENERIC_DEFAULTS, this.width / 2 - 155 ,  this.height -25 ,100,20, "Defaults"));
        btnDone = (new GuiButtonEx(ID_GENERIC_DONE, this.width / 2 + 55, this.height -25,100,20, "Done"));
        btnCancel = (new GuiButtonEx(99, this.width / 2 - 155  + 80, this.height -25,150,20, "Cancel"));
        btnAddKey = (new GuiButtonEx(100, this.width / 2 - 50, this.height -25,100,20, "Add Key"));
        btnKeyboardPress = (new GuiButtonEx(101, this.width / 2 - 155, this.height -25,100,20, "Press"));
        btnKeyboardHold = (new GuiButtonEx(102, this.width / 2 - 50, this.height -25,100,20, "Hold"));
        this.buttonList.add(btnDefaults);
        this.buttonList.add(btnDone);
        this.buttonList.add(btnCancel);
        this.buttonList.add(btnAddKey);
        this.buttonList.add(btnKeyboardPress);
        this.buttonList.add(btnKeyboardHold);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3) {
    	if (reinit) {
    		initGui();
    		reinit = false;
    	}    

    	if(waitingForKey){
    		screenTitle = "Press keyboard key...";
    		btnCancel.visible = true;
    		btnDone.visible = false;
    		btnDefaults.visible = false;
    		btnAddKey.visible = false;
    		btnKeyboardPress.visible = false;
    		btnKeyboardHold.visible = false;
			btnCancel.x = this.width / 2 - 155 + 80;
			btnCancel.setWidth(150);
    	}else {
    		if(this.selectionMode && this.mapping != null){
    			btnCancel.visible = true;
    			btnDone.visible = true;
    			btnDefaults.visible = true;
    			btnAddKey.visible = false;
        		btnKeyboardPress.visible = false;
        		btnKeyboardHold.visible = false;
    			btnCancel.x = this.width / 2 - 50;
    			btnCancel.setWidth(100);
    			btnDefaults.displayString = "Clear All";
    			screenTitle = "Choose buttons for " + I18n.format(this.mapping.functionId);
    			this.guiSelection.drawScreen(par1, par2, par3);
    		}
    		else if (this.keyboardHoldSelect) {
    			btnCancel.visible = true;
    			btnDone.visible = false;
    			btnDefaults.visible = false;
    			btnAddKey.visible = false;
        		btnKeyboardPress.visible = true;
        		btnKeyboardHold.visible = true;
    			btnCancel.x = this.width / 2 + 55;
    			btnCancel.setWidth(100);
    			screenTitle = "Choose keyboard key mode";
    		}
    		else{
    			btnCancel.visible = false;
    			btnDone.visible = true;
    			btnDefaults.visible = true;
    			btnAddKey.visible = true;
        		btnKeyboardPress.visible = false;
        		btnKeyboardHold.visible = false;
    			btnDefaults.displayString = "Defaults";
    			this.selectionMode = false;
    			screenTitle = "VR Control Remapping";
    			this.guiList.drawScreen(par1, par2, par3);       
    		}
    	}
    	super.drawScreen(par1,par2,par3,false);

    }
    
    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton) {
    	if (par1GuiButton.id == ID_GENERIC_DONE) {
    		if (this.selectionMode && this.mapping != null) {
    			if (this.mapping.functionId.equals("GUI Left Click")) { // Gross mess to stop people screwing themselves
    				boolean bound = false;
    				outer: for (int i = 0; i < 2; i++) {
    					for (ButtonType button : MCOpenVR.controllers[i].getActiveButtons()) {
    						if (mappingButtons.contains(new ButtonTuple(button, ControllerType.values()[i]))) {
    							bound = true;
    							break outer;
    						}
    					}
    				}
    				if (!bound) return;
    			}
    			this.mapping.buttons.clear();
    			this.mapping.buttons.addAll(mappingButtons);
    			this.mappingButtons = null;
    			this.selectionMode = false;
    		} else {
	            this.guivrSettings.saveOptions();
	            this.mc.displayGuiScreen(this.parentGuiScreen);
    		}
        } else if (par1GuiButton.id == ID_GENERIC_DEFAULTS){
        	if (this.selectionMode && this.mapping != null) {
        		for (int i = 0; i < 2; i++) {
					for (ButtonType button : MCOpenVR.controllers[i].getActiveButtons()) {
						mappingButtons.remove(new ButtonTuple(button, ControllerType.values()[i]));
					}
				}
        	} else {
	        	mc.vrSettings.resetBindings();
	        	guiList.buildList();
        	}
        }   else if (par1GuiButton.id == 99){ //selection cancel
        	this.selectionMode = false;
        	this.waitingForKey = false;
        	this.keyboardHoldSelect = false;
        	this.mappingButtons = null;
        } else if (par1GuiButton.id == 100){
        	this.keyboardHoldSelect = true;
        } else if (par1GuiButton.id == 101){
        	this.keyboardHold = false;
        	this.waitingForKey = true;
        	this.keyboardHoldSelect = false;
        } else if (par1GuiButton.id == 102){
        	this.keyboardHold = true;
        	this.waitingForKey = true;
        	this.keyboardHoldSelect = false;
        }
    }
    
    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
      
        boolean flag = false;
        
        if(this.selectionMode){
        		flag = this.guiSelection.mouseClicked(mouseX, mouseY, mouseButton);
        		this.guiList.setEnabled(true);
        }else if (!this.keyboardHoldSelect && !this.waitingForKey){
        		flag = this.guiList.mouseClicked(mouseX, mouseY, mouseButton);
        		this.guiSelection.setEnabled(true);
        }
      if (!flag)
        {
            try {
				super.mouseClicked(mouseX, mouseY, mouseButton);
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }

    /**
     * Called when a mouse button is released.  Args : mouseX, mouseY, releaseButton\n \n@param state Will be negative
     * to indicate mouse move and will be either 0 or 1 to indicate mouse up.
     */
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (state != 0 || !this.guiList.mouseReleased(mouseX, mouseY, state))
        {
            super.mouseReleased(mouseX, mouseY, state);
        }
    }

    
}
