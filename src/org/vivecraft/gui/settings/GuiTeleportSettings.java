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

public class GuiTeleportSettings extends BaseGuiSettings implements GuiEventEx {
	private static VRSettings.VrOptions[] teleportSettings = new VRSettings.VrOptions[] {
			VRSettings.VrOptions.SIMULATE_FALLING,
			VRSettings.VrOptions.LIMIT_TELEPORT
	};

	private static VRSettings.VrOptions[] limitedTeleportSettings = new VRSettings.VrOptions[] {
			VRSettings.VrOptions.TELEPORT_UP_LIMIT,
			VRSettings.VrOptions.TELEPORT_DOWN_LIMIT,
			VRSettings.VrOptions.TELEPORT_HORIZ_LIMIT
	};

	public GuiTeleportSettings(GuiScreen guiScreen, VRSettings settings) {
		super(guiScreen, settings);
		screenTitle = "vivecraft.options.screen.teleport";
	}

	@Override
	public void initGui()
	{
		this.buttonList.clear();
		this.buttonList.add(new GuiButtonEx(ID_GENERIC_DEFAULTS, this.width / 2 - 155 ,  this.height -25 ,150,20, Lang.get("vivecraft.gui.loaddefaults")));
		this.buttonList.add(new GuiButtonEx(ID_GENERIC_DONE, this.width / 2 - 155  + 160, this.height -25,150,20, Lang.get("gui.done")));

		addButtons(teleportSettings, 0);
		if (guivrSettings.vrLimitedSurvivalTeleport)
			addButtons(limitedTeleportSettings, 30);
		
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
				float minValue = 0.0f;
				float maxValue = 1.0f;
				float increment = 0.01f;

                if(var8 == VrOptions.TELEPORT_DOWN_LIMIT){
                    minValue = 0f;
                    maxValue = 16f;
                    increment = 1f;
                    show = this.guivrSettings.vrLimitedSurvivalTeleport;
                }
                else if(var8 == VrOptions.TELEPORT_UP_LIMIT){
                    minValue = 0f;
                    maxValue = 4f;
                    increment = 1f;
                    show = this.guivrSettings.vrLimitedSurvivalTeleport;
                }
                else if(var8 == VrOptions.TELEPORT_HORIZ_LIMIT){
                    minValue = 0f;
                    maxValue = 32f;
                    increment = 1f;
                    show = this.guivrSettings.vrLimitedSurvivalTeleport;
                }
				if(show) {
					GuiSliderEx slider = new GuiSliderEx(var8.returnEnumOrdinal(), width, height , var8, this.guivrSettings.getKeyBinding(var8), minValue, maxValue, increment, this.guivrSettings.getOptionFloatValue(var8));
					slider.setEventHandler(this);
					this.buttonList.add(slider);
				}
			}
			else
			{
				this.buttonList.add(new GuiSmallButtonEx(var8.returnEnumOrdinal(), width, height , var8, this.guivrSettings.getKeyBinding(var8)));
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
				vrSettings.vrLimitedSurvivalTeleport = true;
				vrSettings.simulateFalling = true;
				vrSettings.vrTeleportDownLimit = 4;
				vrSettings.vrTeleportUpLimit = 1;
				vrSettings.vrTeleportHorizLimit = 16;
				Minecraft.getMinecraft().vrSettings.saveOptions();
				this.reinit = true;
			}
			else if (par1GuiButton instanceof GuiSmallButtonEx)
			{
				if (par1GuiButton.id == VRSettings.VrOptions.LIMIT_TELEPORT.ordinal())
				{
					this.reinit = true;
				}
				
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
