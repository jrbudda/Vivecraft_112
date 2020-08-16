package org.vivecraft.gui.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.vivecraft.gui.framework.BaseGuiSettings;
import org.vivecraft.gui.framework.GuiButtonEx;
import org.vivecraft.gui.framework.GuiEventEx;
import org.vivecraft.gui.framework.GuiSliderEx;
import org.vivecraft.gui.framework.GuiSmallButtonEx;
import org.vivecraft.provider.MCOpenVR;
import org.vivecraft.settings.VRSettings;
import org.vivecraft.settings.VRSettings.VrOptions;
import org.vivecraft.utils.HardwareType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.optifine.Lang;

public class GuiMenuWorldSettings extends BaseGuiSettings implements GuiEventEx
{
    static VRSettings.VrOptions[] miscSettings = new VRSettings.VrOptions[]
    {
    	VRSettings.VrOptions.MENU_WORLD_SELECTION,
        VrOptions.DUMMY,
    };
    
    public GuiMenuWorldSettings(GuiScreen guiScreen, VRSettings guivrSettings) {
        super( guiScreen, guivrSettings );
        screenTitle = "vivecraft.options.screen.menuworld";
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.buttonList.clear();
        this.buttonList.add(new GuiButtonEx(ID_GENERIC_DEFAULTS, this.width / 2 - 155 ,  this.height -25 ,150,20, Lang.get("vivecraft.gui.loaddefaults")));
        this.buttonList.add(new GuiButtonEx(ID_GENERIC_DONE, this.width / 2 - 155  + 160, this.height -25,150,20, Lang.get("gui.done")));
        VRSettings.VrOptions[] buttons = miscSettings;
        addButtons(buttons,0);
		this.buttonList.add(new GuiButtonEx(301, this.width / 2 - 155 , 60,150,20, Lang.get("vivecraft.gui.menuworld.refresh")));
		this.buttonList.add(new GuiButtonEx(300, this.width / 2 + 5 , 60 ,150,20, Lang.get("vivecraft.gui.menuworld.loadnew")));
  
    }

	private void addButtons(VRSettings.VrOptions[] buttons, int startY) {
		int extra = startY;
        for (int var12 = 2; var12 < buttons.length + 2; ++var12)
        {
            VRSettings.VrOptions var8 = buttons[var12 - 2];
            int width = this.width / 2 - 155 + var12 % 2 * 160;
            int height = this.height / 6 + 21 * (var12 / 2) - 10 + extra;

            if (var8 == VRSettings.VrOptions.DUMMY)
                continue;

            if (var8 == VRSettings.VrOptions.DUMMY_SMALL) {
                extra += 5;
                continue;
            }
                       
            if (var8.getEnumFloat())
            {
            }
            else
            {
                GuiSmallButtonEx smallButton = new GuiSmallButtonEx(var8.returnEnumOrdinal(), width, height - 20, var8, this.guivrSettings.getKeyBinding(var8));
                smallButton.setEventHandler(this);
                this.buttonList.add(smallButton);
            }
        }
	}

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        if (reinit)
        {
            initGui();
            reinit = false;
        }
        super.drawScreen(par1,par2,par3);
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        VRSettings vr = mc.vrSettings;

        if (par1GuiButton.enabled)
        {
            if (par1GuiButton.id == ID_GENERIC_DONE)
            {
                Minecraft.getMinecraft().vrSettings.saveOptions();
                this.mc.displayGuiScreen(this.parentGuiScreen);
            }
            else if (par1GuiButton.id == ID_GENERIC_DEFAULTS)
            {
        		vr.menuWorldSelection = VRSettings.MENU_WORLD_BOTH;
                Minecraft.getMinecraft().vrSettings.saveOptions();
                this.reinit = true;
            }
			else if (par1GuiButton.id == 300) {
				try {
					if (mc.menuWorldRenderer.isReady())
						mc.menuWorldRenderer.destroy();
					mc.menuWorldRenderer.init();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if (par1GuiButton.id == 301) {
				if (mc.menuWorldRenderer.getWorld() != null) {
					try {
						mc.menuWorldRenderer.destroy();
						mc.menuWorldRenderer.prepare();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
            else if (par1GuiButton instanceof GuiSmallButtonEx)
            {
                VRSettings.VrOptions num = VRSettings.VrOptions.getEnumOptions(par1GuiButton.id);
                    this.guivrSettings.setOptionValue(((GuiSmallButtonEx)par1GuiButton).returnVrEnumOptions(), 1);
                    par1GuiButton.displayString = this.guivrSettings.getKeyBinding(VRSettings.VrOptions.getEnumOptions(par1GuiButton.id));
                    
                    if(num == VRSettings.VrOptions.FREEMOVE_MODE){
                    	this.reinit = true;
                    }
    				if(num == VRSettings.VrOptions.LIMIT_TELEPORT){
                    	this.reinit = true;
                    }  
                    
            }
        }
    }

    @Override
    public boolean event(int id, VRSettings.VrOptions enumm)
    {
		return false;
    }

    @Override
    public boolean event(int id, String s) {
        return true;
    }
    
}
