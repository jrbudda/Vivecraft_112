package org.vivecraft.gui.settings;

import org.vivecraft.gameplay.screenhandlers.GuiHandler;
import org.vivecraft.gui.framework.BaseGuiSettings;
import org.vivecraft.gui.framework.GuiButtonEx;
import org.vivecraft.gui.framework.GuiSliderEx;
import org.vivecraft.gui.framework.GuiSmallButtonEx;
import org.vivecraft.settings.VRSettings;
import org.vivecraft.settings.VRSettings.VrOptions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.optifine.Lang;

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
			VRSettings.VrOptions.AUTO_OPEN_KEYBOARD,
			VRSettings.VrOptions.TOUCH_HOTBAR,
			VRSettings.VrOptions.PHYSICAL_KEYBOARD,
			VRSettings.VrOptions.MENU_ALWAYS_FOLLOW_FACE,
			VRSettings.VrOptions.PHYSICAL_KEYBOARD_SCALE,
    };

    public GuiHUDSettings(GuiScreen guiScreen, VRSettings guivrSettings) {
        super( guiScreen, guivrSettings );
        screenTitle = "vivecraft.options.screen.gui";
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
    	this.buttonList.clear();
    	this.buttonList.add(new GuiSmallButtonEx(301, this.width / 2 - 78, this.height / 6 - 14, Lang.get("vivecraft.options." + VrOptions.HUD_HIDE.name()) + ": " + mc.gameSettings.hideGUI));
    	this.buttonList.add(new GuiButtonEx(ID_GENERIC_DEFAULTS, this.width / 2 - 155 ,  this.height -25 ,150,20, Lang.get("vivecraft.gui.loaddefaults")));
    	this.buttonList.add(new GuiButtonEx(ID_GENERIC_DONE, this.width / 2 - 155  + 160, this.height -25,150,20, Lang.get("gui.done")));
    	this.buttonList.add(new GuiButtonEx(302, this.width / 2 - 155  + 160, this.height / 6 +150 ,150,20, Lang.get("vivecraft.options.screen.menuworld.button")));

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
    			else if (var8 == VRSettings.VrOptions.HUD_OPACITY)
    			{
    				minValue = 0.15f;
    				maxValue = 1.0f;
    				increment = 0.05f;
    			}
				else if (var8 == VRSettings.VrOptions.PHYSICAL_KEYBOARD_SCALE)
				{
					minValue = 0.75f;
					maxValue = 1.5f;
					increment = 0.01f;
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
                this.guivrSettings.physicalKeyboard = true;
                this.guivrSettings.physicalKeyboardScale = 1.0f;
                this.mc.gameSettings.hideGUI = false;

                Minecraft.getMinecraft().vrSettings.saveOptions();
                this.reinit = true;
            }
            else if (par1GuiButton.id == 301)
            {
            	mc.gameSettings.hideGUI = !mc.gameSettings.hideGUI;
                this.reinit = true;

            }
            else if (par1GuiButton.id == 302)
            {
				mc.displayGuiScreen(new GuiMenuWorldSettings(this, guivrSettings));
            }
        }
    }
}
