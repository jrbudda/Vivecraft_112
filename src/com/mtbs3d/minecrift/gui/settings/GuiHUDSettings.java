package com.mtbs3d.minecrift.gui.settings;

import com.mtbs3d.minecrift.gameplay.screenhandlers.GuiHandler;
import com.mtbs3d.minecrift.gui.framework.BaseGuiSettings;
import com.mtbs3d.minecrift.gui.framework.GuiButtonEx;
import com.mtbs3d.minecrift.gui.framework.GuiSliderEx;
import com.mtbs3d.minecrift.gui.framework.GuiSmallButtonEx;
import com.mtbs3d.minecrift.settings.VRSettings;
import com.mtbs3d.minecrift.settings.VRSettings.VrOptions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiHUDSettings extends BaseGuiSettings
{
    static VRSettings.VrOptions[] hudOptions = new VRSettings.VrOptions[] {
//            VRSettings.VrOptions.HUD_HIDE,
            VRSettings.VrOptions.HUD_LOCK_TO,
            VRSettings.VrOptions.HUD_OCCLUSION,
            //VRSettings.VrOptions.HUD_SCALE,
            //VRSettings.VrOptions.HUD_DISTANCE,
            //VRSettings.VrOptions.HUD_PITCH,
            //VRSettings.VrOptions.HUD_YAW,
            VRSettings.VrOptions.HUD_OPACITY,
            VRSettings.VrOptions.RENDER_MENU_BACKGROUND,
            VRSettings.VrOptions.TOUCH_HOTBAR,
            VRSettings.VrOptions.MENU_ALWAYS_FOLLOW_FACE,
            VRSettings.VrOptions.AUTO_OPEN_KEYBOARD,
    };

    public GuiHUDSettings(GuiScreen guiScreen, VRSettings guivrSettings) {
        super( guiScreen, guivrSettings );
        screenTitle = "HUD and GUI Settings";
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
    	this.buttonList.clear();
    	this.buttonList.add(new GuiSmallButtonEx(301, this.width / 2 - 78, this.height / 6 - 14, "Hide Hud (F1): " + mc.gameSettings.hideGUI));
    	this.buttonList.add(new GuiButtonEx(ID_GENERIC_DEFAULTS, this.width / 2 - 155 ,  this.height -25 ,150,20, "Reset To Defaults"));
    	this.buttonList.add(new GuiButtonEx(ID_GENERIC_DONE, this.width / 2 - 155  + 160, this.height -25,150,20, "Done"));
    	if(!mc.vrSettings.seated)
    		this.buttonList.add(new GuiButtonEx(300, this.width / 2 + 5 , this.height / 6 + 74,150,20, "GUI Buttons..."));
    	VRSettings.VrOptions[] buttons = hudOptions;

    	for (int var12 = 2; var12 < buttons.length + 2; ++var12)
    	{
    		VRSettings.VrOptions var8 = buttons[var12 - 2];
    		int width = this.width / 2 - 155 + var12 % 2 * 160;
    		int height = this.height / 6 + 21 * (var12 / 2) - 10;

    		if (var8 == VRSettings.VrOptions.DUMMY)
    			continue;

    		if (var8.getEnumFloat())
    		{
    			float minValue = 0.0f;
    			float maxValue = 1.0f;
    			float increment = 0.01f;

    			if (var8 == VRSettings.VrOptions.HUD_SCALE)
    			{
    				minValue = 0.35f;
    				maxValue = 2.5f;
    				increment = 0.01f;
    			}
    			else if (var8 == VRSettings.VrOptions.HUD_DISTANCE)
    			{
    				minValue = 0.25f;
    				maxValue = 5.0f;
    				increment = 0.01f;
    			}
    			else if (var8 == VRSettings.VrOptions.HUD_PITCH)
    			{
    				minValue = -45f;
    				maxValue = 45f;
    				increment = 1f;
    			}
    			else if (var8 == VRSettings.VrOptions.HUD_YAW)
    			{
    				minValue = -100f;
    				maxValue = 100f;
    				increment = 1f;
    			}
    			else if (var8 == VRSettings.VrOptions.HUD_OPACITY)
    			{
    				minValue = 0.15f;
    				maxValue = 1.0f;
    				increment = 0.05f;
    			}

    			this.buttonList.add(new GuiSliderEx(var8.returnEnumOrdinal(), width, height, var8, this.guivrSettings.getKeyBinding(var8), minValue, maxValue, increment, this.guivrSettings.getOptionFloatValue(var8)));
    		}
    		else
    		{
    			this.buttonList.add(new GuiSmallButtonEx(var8.returnEnumOrdinal(), width, height, var8, this.guivrSettings.getKeyBinding(var8)));
    		}
    	}
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.enabled)
        {
           if (par1GuiButton.id < 200 && par1GuiButton instanceof GuiSmallButtonEx)
            {
                VRSettings.VrOptions num = VRSettings.VrOptions.getEnumOptions(par1GuiButton.id);
                this.guivrSettings.setOptionValue(((GuiSmallButtonEx)par1GuiButton).returnVrEnumOptions(), 1);
                par1GuiButton.displayString = this.guivrSettings.getKeyBinding(VRSettings.VrOptions.getEnumOptions(par1GuiButton.id));
                if(par1GuiButton.id == VrOptions.MENU_ALWAYS_FOLLOW_FACE.ordinal())
                	GuiHandler.onGuiScreenChanged(mc.currentScreen, mc.currentScreen, false);
            }
            else if (par1GuiButton.id == ID_GENERIC_DONE)
            {
                Minecraft.getMinecraft().vrSettings.saveOptions();
                this.mc.displayGuiScreen(this.parentGuiScreen);
            }
            else if (par1GuiButton.id == ID_GENERIC_DEFAULTS)
            {
                this.guivrSettings.hudDistance = 1.25f;
                this.guivrSettings.hudScale = 1.5f;
                this.guivrSettings.hudPitchOffset = -2f;
                this.guivrSettings.hudYawOffset = 0f;
                this.guivrSettings.hudOpacity = 1f;
                this.guivrSettings.menuBackground = false;
                this.guivrSettings.vrHudLockMode = guivrSettings.HUD_LOCK_HAND;
                this.guivrSettings.hudOcclusion = true;
                this.guivrSettings.menuAlwaysFollowFace = false;
                this.guivrSettings.autoOpenKeyboard = true;
                this.mc.gameSettings.hideGUI = false;

                Minecraft.getMinecraft().vrSettings.saveOptions();
                this.reinit = true;
            }
            else if (par1GuiButton.id == 300)
            {
            	this.guivrSettings.saveOptions();
            	GuiVRControls guiVRControls = new GuiVRControls(this, this.guivrSettings);
            	guiVRControls.guiFilter = true;
                this.mc.displayGuiScreen(guiVRControls);
            }
            else if (par1GuiButton.id == 301)
            {
            	mc.gameSettings.hideGUI = !mc.gameSettings.hideGUI;
                this.reinit = true;

            }
        }
    }

    @Override
    protected String[] getTooltipLines(String displayString, int buttonId)
    {
        VRSettings.VrOptions e = VRSettings.VrOptions.getEnumOptions(buttonId);
        if( e != null )
            switch(e)
            {
                case HUD_OPACITY:
                    return new String[] {
                            "How transparent to draw the in-game HUD and UI",
                    };
                case HUD_SCALE:
                return new String[] {
                        "Relative size HUD takes up in field-of-view",
                        "  The units are just relative, not in degrees",
                        "  or a fraction of FOV or anything"
                };
                case HUD_PITCH:
                    return new String[] {
                            "The vertical offset of the HUD, in degrees.",
                            "  Negative values are down, positive up."
                    };
                case HUD_YAW:
                    return new String[] {
                            "The horizontal offset of the HUD, in degrees.",
                            "  Negative values are to the left, positive to",
                            "  the right."
                    };
                case HUD_DISTANCE:
                    return new String[] {
                            "Distance the floating HUD is drawn in front of your body",
                            "  The relative size of the HUD is unchanged by this",
                            "  Distance is in meters (though isn't obstructed by blocks)"
                    };
                case HUD_OCCLUSION:
                    return new String[] {
                            "Specifies whether the HUD is occluded by closer objects.",
                            "  ON:  The HUD will be hidden by closer objects. May",
                            "       be hidden completely in confined environments!",
                            "  OFF: The HUD is always visible. Stereo depth issues",
                            "       may be noticable."
                    };
                case MENU_ALWAYS_FOLLOW_FACE:
                    return new String[] {
                            "Specifies when the main menu follows your look direction.",
                            "  SEATED: The main menu will only follow in seated mode.",
                            "  ALWAYS The main menu will always follow."
                    };
                case RENDER_MENU_BACKGROUND:
                    return new String[] {
                            "Specifies whether the in game GUI menus have a ",
                            "semi-transparent background or not.",
                            "  ON:  Semi-transparent background on in-game menus.",
                            "  OFF: No background on in-game menus."
                    };
                case HUD_LOCK_TO:
                    return new String[] {
                            "Specifies to which orientation the HUD is locked to.",
                            "  HAND:  The HUD will appear just above your off-hand",
                            "  HEAD:  The HUD will always appear in your field of view",
                            "straight ahead",
                            "  WRIST:  The HUD will appear on the inside of your off-hand",
                            "arm. It will 'pop out' when looked at."
                    };
                case OTHER_HUD_SETTINGS:
                    return new String[] {
                            "Configure Crosshair and overlay settings."
                    };
                case TOUCH_HOTBAR:
                    return new String[] {
                            "If enabled allow you to touch the hotbar with",
                            "your main hand to select an item."
                    };
                case AUTO_OPEN_KEYBOARD:
                    return new String[] {
                    		"If disabled, the keyboard will only open when you",
                    		"click a text field, or if a text field can't lose focus.",
                    		"",
                            "If enabled, the keyboard will open automatically",
                            "any time a text field comes into focus. Enabling this will",
                            "cause it to open in unwanted situations with mods."
                    };
                default:
                    return null;
            }
        else
            switch(buttonId)
            {
                case 300:
                    return new String[] {
                            "Configure which controller buttons perform",
                            "  which mouse or keyboard function while ",
                            "  a GUI is visible."
                    };
                default:
                    return null;
            }
    }
}
