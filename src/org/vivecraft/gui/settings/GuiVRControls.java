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
import org.vivecraft.gui.framework.GuiEventEx;
import org.vivecraft.gui.framework.GuiSliderEx;
import org.vivecraft.gui.framework.GuiSmallButtonEx;
import org.vivecraft.provider.MCOpenVR;
import org.vivecraft.settings.VRSettings;
import org.vivecraft.settings.VRSettings.VrOptions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.optifine.Lang;

public class GuiVRControls extends BaseGuiSettings implements GuiEventEx{

	private static VRSettings.VrOptions[] controlsSettings = new VRSettings.VrOptions[] {
			VRSettings.VrOptions.REVERSE_HANDS,
			VRSettings.VrOptions.ALLOW_ADVANCED_BINDINGS
	};
	
	public GuiVRControls(GuiScreen par1GuiScreen, VRSettings par2vrSettings) {
		super(par1GuiScreen, par2vrSettings);
        screenTitle = "vivecraft.options.screen.controls";
	}

   
    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui() {
		this.buttonList.clear();
		this.buttonList.add(new GuiButtonEx(ID_GENERIC_DEFAULTS, this.width / 2 - 155 ,  this.height -25 ,150,20, Lang.get("vivecraft.gui.loaddefaults")));
		this.buttonList.add(new GuiButtonEx(ID_GENERIC_DONE, this.width / 2 - 155  + 160, this.height -25,150,20, Lang.get("gui.done")));

		addButtons(controlsSettings, this.height / 2 );
    }
	private void addButtons(VRSettings.VrOptions[] buttons, int y) {
		for (int var12 = 2; var12 < buttons.length + 2; ++var12)
		{
			VRSettings.VrOptions var8 = buttons[var12 - 2];
			int width = this.width / 2 - 155 + var12 % 2 * 160;
			int height = this.height / 6 + 21 * (var12 / 2) - 10 + y;

			if (var8 == VRSettings.VrOptions.DUMMY)
				continue;
			
			boolean show = true;
			
			if (var8.getEnumFloat())
			{
			}
			else
			{
				this.buttonList.add(new GuiSmallButtonEx(var8.returnEnumOrdinal(), width, height , var8, this.guivrSettings.getKeyBinding(var8)));
			}
		}
	}
    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3) {
		super.drawScreen(par1, par2, par3);
		this.drawCenteredString(this.fontRenderer, Lang.get("vivecraft.messages.controls.1"), this.width / 2, this.height / 2 - this.fontRenderer.FONT_HEIGHT / 2 - this.fontRenderer.FONT_HEIGHT - 3, 16777215);
		this.drawCenteredString(this.fontRenderer, Lang.get("vivecraft.messages.controls.2"), this.width / 2, this.height / 2 - this.fontRenderer.FONT_HEIGHT / 2, 16777215);
		this.drawCenteredString(this.fontRenderer, Lang.get("vivecraft.messages.controls.3"), this.width / 2, this.height / 2 - this.fontRenderer.FONT_HEIGHT / 2 + this.fontRenderer.FONT_HEIGHT + 3, 16777215);
    }
    
    @Override
	protected void actionPerformed(GuiButton par1GuiButton)
	{
		if (par1GuiButton.enabled)
		{
			if (par1GuiButton.id == ID_GENERIC_DONE)
			{
				Minecraft.getMinecraft().vrSettings.saveOptions();
				this.mc.displayGuiScreen(this.parentGuiScreen);
			}
			else if (par1GuiButton.id == ID_GENERIC_DEFAULTS)
			{
				VRSettings vrSettings = mc.vrSettings;
				vrSettings.vrReverseHands = false;
				vrSettings.allowAdvancedBindings = false;
				Minecraft.getMinecraft().vrSettings.saveOptions();
				this.reinit = true;
			}
			else if (par1GuiButton instanceof GuiSmallButtonEx)
			{
				VRSettings.VrOptions num = VRSettings.VrOptions.getEnumOptions(par1GuiButton.id);
				this.guivrSettings.setOptionValue(((GuiSmallButtonEx)par1GuiButton).returnVrEnumOptions(), 1);
				par1GuiButton.displayString = this.guivrSettings.getKeyBinding(VRSettings.VrOptions.getEnumOptions(par1GuiButton.id));
			}
		}
	}
    protected String[] getTooltipLines(String displayString, int buttonId)
    {
        VRSettings.VrOptions e = VRSettings.VrOptions.getEnumOptions(buttonId);
        if( e != null )
            switch(e)
            {
            case ALLOW_ADVANCED_BINDINGS:
            	return new String[] {
            			"Unhides additional SteamVR bindings for climbey,",
            			"keyboard and mixed reality.",
            			"",
            			"Requires a restart to take effect."                    
            	};
            default:
            	return null;
            }
        else
            switch(buttonId)
            {
//                case 201:
//                    return new String[] {
//                            "Open this configuration screen to adjust the Head",
//                            "  Tracker orientation (direction) settings. ",
//                            "  Ex: Head Tracking Selection (Hydra/Oculus), Prediction"
//                    };
                default:
                    return null;
            }
    }

	@Override
	public boolean event(int id, VrOptions enumm) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean event(int id, String s) {
		// TODO Auto-generated method stub
		return false;
	}
    

    
}
