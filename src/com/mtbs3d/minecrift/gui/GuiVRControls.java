/**
 * Copyright 2013 Mark Browning, StellaArtois
 * Licensed under the LGPL 3.0 or later (See LICENSE.md for details)
 */
package com.mtbs3d.minecrift.gui;

import java.io.IOException;

import com.mtbs3d.minecrift.control.VRControllerButtonMapping;
import com.mtbs3d.minecrift.gui.framework.BaseGuiSettings;
import com.mtbs3d.minecrift.gui.framework.GuiButtonEx;
import com.mtbs3d.minecrift.settings.VRSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiKeyBindingList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.settings.KeyBinding;

public class GuiVRControls extends BaseGuiSettings {

	private GuiVRControlsList guiList;
	public VRControllerButtonMapping buttonId; 
	
	public GuiVRControls(GuiScreen par1GuiScreen, VRSettings par2vrSettings) {
		super(par1GuiScreen, par2vrSettings);
        screenTitle = "Control Remapping";
	}

    /**
     * Fired when a key is typed (except F11 who toggle full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode)
    {
        if (this.buttonId != null)
        {
        	buttonId.FunctionExt = typedChar;
            this.buttonId = null;
        }
        else
        {
            try {
				super.keyTyped(typedChar, keyCode);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui() {
    	this.guiList = new GuiVRControlsList(this, mc);
        this.buttonList.clear();
        this.buttonList.add(new GuiButtonEx(ID_GENERIC_DEFAULTS, this.width / 2 - 155 ,  this.height -25 ,150,20, "Reset To Defaults"));
        this.buttonList.add(new GuiButtonEx(ID_GENERIC_DONE, this.width / 2 - 155  + 160, this.height -25,150,20, "Done"));
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3) {
        if (reinit) {
            initGui();
            reinit = false;
        }
        this.guiList.drawScreen(par1, par2, par3);
        super.drawScreen(par1,par2,par3,false);
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton) {
    	if (par1GuiButton.id == ID_GENERIC_DONE) {
            this.guivrSettings.saveOptions();
            this.mc.displayGuiScreen(this.parentGuiScreen);
        } else if (par1GuiButton.id == ID_GENERIC_DEFAULTS){
        	mc.vrSettings.resetBindings();
        	this.initGui();
        }
    }
    
    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        this.buttonId = null;
      if (mouseButton != 0 || !this.guiList.mouseClicked(mouseX, mouseY, mouseButton))
        {
            try {
				super.mouseClicked(mouseX, mouseY, mouseButton);
			} catch (IOException e) {
				// TODO Auto-generated catch block
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
