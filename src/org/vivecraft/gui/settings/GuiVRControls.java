/**
 * Copyright 2013 Mark Browning, StellaArtois
 * Licensed under the LGPL 3.0 or later (See LICENSE.md for details)
 */
package org.vivecraft.gui.settings;

import java.io.IOException;
import java.util.Set;


import org.vivecraft.control.ControllerType;

import org.vivecraft.gui.framework.BaseGuiSettings;
import org.vivecraft.gui.framework.GuiButtonEx;
import org.vivecraft.provider.MCOpenVR;
import org.vivecraft.settings.VRSettings;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class GuiVRControls extends BaseGuiSettings {


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

    }


    
    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui() {

    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3) {

    }
    
    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton) {

    }
    
    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {

    }

    /**
     * Called when a mouse button is released.  Args : mouseX, mouseY, releaseButton\n \n@param state Will be negative
     * to indicate mouse move and will be either 0 or 1 to indicate mouse up.
     */
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {

    }
    

    
}
