package org.vivecraft.gui.settings;

import org.vivecraft.gui.framework.BaseGuiSettings;
import org.vivecraft.gui.framework.GuiButtonEx;
import org.vivecraft.gui.framework.GuiEventEx;
import org.vivecraft.gui.framework.GuiSliderEx;
import org.vivecraft.gui.framework.GuiSmallButtonEx;
import org.vivecraft.settings.VRSettings;
import org.vivecraft.settings.VRSettings.VrOptions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.optifine.Lang;

public class GuiFreeMoveSettings extends BaseGuiSettings implements GuiEventEx {
	private static VRSettings.VrOptions[] standingSettings = new VRSettings.VrOptions[] {
			VRSettings.VrOptions.FREEMOVE_MODE,
			VRSettings.VrOptions.FOV_REDUCTION,
			VRSettings.VrOptions.INERTIA_FACTOR,
			VRSettings.VrOptions.MOVEMENT_MULTIPLIER,
			VRSettings.VrOptions.AUTO_SPRINT,
			VRSettings.VrOptions.AUTO_SPRINT_THRESHOLD,
			VRSettings.VrOptions.ANALOG_MOVEMENT
	};

	private static VRSettings.VrOptions[] seatedSettings = new VRSettings.VrOptions[] {
			VRSettings.VrOptions.SEATED_HMD,
			VRSettings.VrOptions.FOV_REDUCTION
	};

	public GuiFreeMoveSettings(GuiScreen guiScreen, VRSettings settings) {
		super(guiScreen, settings);
		screenTitle = "vivecraft.options.screen.freemove";
	}

	@Override
	public void initGui()
	{
		this.buttonList.clear();
		this.buttonList.add(new GuiButtonEx(ID_GENERIC_DEFAULTS, this.width / 2 - 155 ,  this.height -25 ,150,20, Lang.get("vivecraft.gui.loaddefaults")));
		this.buttonList.add(new GuiButtonEx(ID_GENERIC_DONE, this.width / 2 - 155  + 160, this.height -25,150,20, Lang.get("gui.done")));

		if (mc.vrSettings.seated)
			addButtons(seatedSettings, 0);
		else
			addButtons(standingSettings, 0);
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
           
            boolean show = true;
            
            if (var8.getEnumFloat())
            {
                float minValue = 0.0f;
                float maxValue = 1.0f;
                float increment = 0.01f;

                if (var8 == VRSettings.VrOptions.MOVEMENT_MULTIPLIER)
                {
                    minValue = 0.15f;
                    maxValue = 1.3f;
                    increment = 0.01f;
                }
                else if (var8 == VRSettings.VrOptions.WALK_MULTIPLIER){
                    minValue=1f;
                    maxValue=10f;
                    increment=0.1f;
                }  		
                else if (var8 == VRSettings.VrOptions.WORLD_ROTATION_INCREMENT){
                    minValue = -1f;
                    maxValue = 4f;
                    increment = 1f;
                }
                else if (var8 == VRSettings.VrOptions.AUTO_SPRINT_THRESHOLD) {
                    minValue = 0.5f;
                    maxValue = 1.0f;
                    increment = 0.01f;
                }   
                if(show) {
                	GuiSliderEx slider = new GuiSliderEx(var8.returnEnumOrdinal(), width, height - 20, var8, this.guivrSettings.getKeyBinding(var8), minValue, maxValue, increment, this.guivrSettings.getOptionFloatValue(var8));
                	slider.setEventHandler(this);
                	this.buttonList.add(slider);
                }
            }
            else
            {
                GuiSmallButtonEx smallButton = new GuiSmallButtonEx(var8.returnEnumOrdinal(), width, height - 20, var8, this.guivrSettings.getKeyBinding(var8));
                smallButton.setEventHandler(this);
                this.buttonList.add(smallButton);
            }
        }
	}
	
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
				vrSettings.inertiaFactor = VRSettings.INERTIA_NORMAL;
				vrSettings.movementSpeedMultiplier = 1f;
				vrSettings.vrFreeMoveMode = VRSettings.FREEMOVE_CONTROLLER;
				vrSettings.useFOVReduction = false;
				vrSettings.seatedUseHMD = false;
				vrSettings.analogMovement = true;
				vrSettings.autoSprint = true;
				vrSettings.autoSprintThreshold = 0.9f;
				Minecraft.getMinecraft().vrSettings.saveOptions();
				this.reinit = true;
			}
			else if (par1GuiButton.id == 300) {
				this.mc.displayGuiScreen(new GuiFreeMoveSettings(this, guivrSettings));
			}
			else if (par1GuiButton.id == 301) {
				this.mc.displayGuiScreen(this.parentGuiScreen);
				this.mc.displayGuiScreen(new GuiTeleportSettings(this, guivrSettings));
			}

			else if (par1GuiButton instanceof GuiSmallButtonEx)
			{
				VRSettings.VrOptions num = VRSettings.VrOptions.getEnumOptions(par1GuiButton.id);
				this.guivrSettings.setOptionValue(((GuiSmallButtonEx)par1GuiButton).returnVrEnumOptions(), 1);
				par1GuiButton.displayString = this.guivrSettings.getKeyBinding(VRSettings.VrOptions.getEnumOptions(par1GuiButton.id));
			}
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
