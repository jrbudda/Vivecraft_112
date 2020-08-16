/**
  * Copyright 2013 Mark Browning, StellaArtois
 * Licensed under the LGPL 3.0 or later (See LICENSE.md for details)
 */
package org.vivecraft.gui.settings;

import org.vivecraft.gameplay.screenhandlers.GuiHandler;
import org.vivecraft.gui.framework.BaseGuiSettings;
import org.vivecraft.gui.framework.GuiButtonEx;
import org.vivecraft.gui.framework.GuiEventEx;
import org.vivecraft.gui.framework.GuiSliderEx;
import org.vivecraft.gui.framework.GuiSmallButtonEx;
import org.vivecraft.gui.framework.VROption;
import org.vivecraft.provider.MCOpenVR;
import org.vivecraft.provider.OpenVRStereoRenderer;
import org.vivecraft.settings.VRSettings;
import org.vivecraft.settings.VRSettings.VrOptions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import net.optifine.Lang;


public class GuiMinecriftSettings extends BaseGuiSettings implements GuiEventEx
{    
    static VROption[] vrAlwaysOptions = new VROption[]
        {
            new VROption(202,VROption.Position.POS_LEFT,  1,  VROption.ENABLED, "vivecraft.options.screen.gui.button"),
            new VROption(206,VROption.Position.POS_LEFT,   0, VROption.ENABLED, "vivecraft.options.screen.stereorendering.button"),
            new VROption(207,VROption.Position.POS_RIGHT,  0, VROption.ENABLED, "vivecraft.options.screen.quickcommands.button"),
            new VROption(210,VROption.Position.POS_RIGHT,  1, VROption.ENABLED, "vivecraft.options.screen.guiother.button"),
            new VROption(VRSettings.VrOptions.WORLD_SCALE,       	VROption.Position.POS_LEFT,   6f, VROption.ENABLED, null),
            new VROption(VRSettings.VrOptions.WORLD_ROTATION,       VROption.Position.POS_RIGHT,   6f, VROption.ENABLED, null),
 
        };
    
    static VROption[] vrStandingOptions = new VROption[]
            {
                new VROption(209,VROption.Position.POS_LEFT,   4f, VROption.ENABLED, "vivecraft.options.screen.standing.button"),
                new VROption(221,VROption.Position.POS_RIGHT,   4f, VROption.ENABLED, "vivecraft.options.screen.roomscale.button"),
                new VROption(220,VROption.Position.POS_LEFT,   5f, VROption.ENABLED, "vivecraft.options.screen.controls.button"),
                new VROption(224,VROption.Position.POS_RIGHT,   5f, VROption.ENABLED, "vivecraft.options.screen.radialmenu.button"),
               // new VROption(VRSettings.VrOptions.REVERSE_HANDS,   VROption.Position.POS_RIGHT,   5f, VROption.ENABLED, null),
            };
    
    static VROption[] vrSeatedOptions = new VROption[]
            {
                    new VROption(211, VROption.Position.POS_LEFT, 4f, VROption.ENABLED, "vivecraft.options.screen.seated.button"),
                    new VROption(VRSettings.VrOptions.RESET_ORIGIN, VROption.Position.POS_RIGHT,   4f, VROption.ENABLED, null),
            };
    
    static VROption[] vrConfirm = new VROption[]
            {
                    new VROption(222, VROption.Position.POS_RIGHT,  2,  VROption.ENABLED, "gui.cancel"),
                    new VROption(223, VROption.Position.POS_LEFT,   2, VROption.ENABLED, "vivecraft.gui.ok"),
            };
    
    boolean isConfirm = false;
    /** An array of all of EnumOption's video options. */

    GameSettings settings;

    public GuiMinecriftSettings( GuiScreen par1GuiScreen,
                                VRSettings par2vrSettings,
                                GameSettings gameSettings)
    {
    	super( par1GuiScreen, par2vrSettings );
    	screenTitle = "vivecraft.options.screen.main";
        settings = gameSettings;
    }
    
    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
    	this.buttonList.clear();
    	int profileButtonWidth = 120;

    	if(!isConfirm){
    		screenTitle = "vivecraft.options.screen.main";
    		VROption mode = new VROption(VRSettings.VrOptions.PLAY_MODE_SEATED,VROption.Position.POS_RIGHT,  2,  VROption.ENABLED, null);
    		GuiSmallButtonEx profilesButton = new GuiSmallButtonEx(mode.getOrdinal(), (this.width / 2 - profileButtonWidth/2 ) , this.height / 4 + 24 , mode._e, Lang.get(mode.getButtonText()));
    		profilesButton.setWidth(profileButtonWidth);
    		this.buttonList.add(profilesButton);
    		this.buttonList.add(new GuiButtonEx(ID_GENERIC_DEFAULTS, this.width / 2 - 155 ,  this.height -25 ,150,20, Lang.get("vivecraft.gui.loaddefaults")));
    		this.buttonList.add(new GuiButtonEx(ID_GENERIC_DONE, this.width / 2 - 155  + 160, this.height -25,150,20, Lang.get("gui.done")));
    		VROption[] buttons = null;

    		buttons = vrAlwaysOptions;

    		processButtons(buttons);

    		if(mc.vrSettings.seated) {
    			processButtons(vrSeatedOptions);
    		}else {
    			processButtons(vrStandingOptions);
    			if(mc.vrSettings.allowStandingOriginOffset)
    				processButtons(new VROption[] {
    						new VROption(VRSettings.VrOptions.RESET_ORIGIN, VROption.Position.POS_RIGHT,   4f, VROption.ENABLED, null),
    				}
    						);
    		}

    	}
    	else {
            this.screenTitle = "vivecraft.messages.seatedmode";
			processButtons(vrConfirm);
    	}
    }

	private void processButtons(VROption[] buttons) {
		for (VROption var8 : buttons)
    	{
    		int width = var8.getWidth(this.width);
    		int height = var8.getHeight(this.height);
    		VrOptions o = VrOptions.getEnumOptions(var8.getOrdinal());
    		    		
    		if(o==null || o.getEnumBoolean() ){
      			GuiSmallButtonEx button = new GuiSmallButtonEx(var8.getOrdinal(), width, height, var8._e, var8.getButtonText());
    			button.enabled = var8._enabled;
    			this.buttonList.add(button);
    		}
    		else if (o.getEnumFloat()){
                float minValue = 0.0f;
                float maxValue = 1.0f;
                float increment = 0.001f;
                
    			if(o == VrOptions.WORLD_SCALE){
                     minValue = 0f;
                     maxValue = 29f;
                     increment = 1f;
    			}
    			else if (o == VrOptions.WORLD_ROTATION){
                     minValue = 0f;
                     maxValue = 360f;
                     increment = Minecraft.getMinecraft().vrSettings.vrWorldRotationIncrement;
    			}

    	        GuiSliderEx slider = new GuiSliderEx(o.returnEnumOrdinal(), width, height, o, this.guivrSettings.getKeyBinding(o), minValue, maxValue, increment, this.guivrSettings.getOptionFloatValue(o));
    	        slider.setEventHandler(this);
    	        slider.enabled = true;
    	        this.buttonList.add(slider);
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
            VRSettings vr = Minecraft.getMinecraft().vrSettings;
//            IHMDInfo hmdInfo = Minecraft.getMinecraft().hmdInfo;
            OpenVRStereoRenderer stereoProvider = Minecraft.getMinecraft().stereoProvider;
//            IOrientationProvider headTracker = Minecraft.getMinecraft().headTracker;
//            IEyePositionProvider positionTracker = Minecraft.getMinecraft().positionTracker;

            if (par1GuiButton.id < 200 && par1GuiButton instanceof GuiSmallButtonEx)
            {
            	VRSettings.VrOptions num = VRSettings.VrOptions.getEnumOptions(par1GuiButton.id);
          
            	if (num == VRSettings.VrOptions.PLAY_MODE_SEATED)
                {
                	this.reinit = true;
            		if(mc.vrSettings.seated == false){
                    	this.isConfirm = true;
                    	return;
                    }
            		
                }
    			else if(num== VRSettings.VrOptions.RESET_ORIGIN){
    				MCOpenVR.resetPosition();
    				Minecraft.getMinecraft().vrSettings.saveOptions();
    				this.mc.displayGuiScreen(null);
    				this.mc.setIngameFocus();
    			}

                this.guivrSettings.setOptionValue(((GuiSmallButtonEx)par1GuiButton).returnVrEnumOptions(), 1);
                par1GuiButton.displayString = this.guivrSettings.getKeyBinding(VRSettings.VrOptions.getEnumOptions(par1GuiButton.id));

            	if (num == VRSettings.VrOptions.PLAY_MODE_SEATED)
            		GuiHandler.onGuiScreenChanged(mc.currentScreen, mc.currentScreen, false);

            }
            else if (par1GuiButton.id == 201)
            {
                Minecraft.getMinecraft().vrSettings.saveOptions();
              //  this.mc.displayGuiScreen(new GuiPlayerPreferenceSettings(this, this.guivrSettings));
            }
            else if (par1GuiButton.id == 202)
            {

                    Minecraft.getMinecraft().vrSettings.saveOptions();
                    this.mc.displayGuiScreen(new GuiHUDSettings(this, this.guivrSettings));

            }
            else if (par1GuiButton.id == 206)
            {

                    Minecraft.getMinecraft().vrSettings.saveOptions();
	                this.mc.displayGuiScreen(new GuiRenderOpticsSettings(this, this.guivrSettings, this.settings));

            } 
            else if (par1GuiButton.id == 207)
            {

                    Minecraft.getMinecraft().vrSettings.saveOptions();
	                this.mc.displayGuiScreen(new GuiQuickCommandEditor(this, this.guivrSettings));

            } 
            else if (par1GuiButton.id == ID_GENERIC_DONE)
            {
                Minecraft.getMinecraft().vrSettings.saveOptions();
                this.mc.displayGuiScreen(this.parentGuiScreen);
            }
            else if (par1GuiButton.id == 209)
            {
                this.guivrSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiStandingSettings(this, this.guivrSettings));
            }
            else if (par1GuiButton.id == 210)
            {
                this.guivrSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiOtherHUDSettings(this, this.guivrSettings));
            }
            else if (par1GuiButton.id == 211)
            {
                this.guivrSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiSeatedOptions(this, this.guivrSettings));
            }
            else if (par1GuiButton.id == 220)
            {
                this.guivrSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiVRControls(this, this.guivrSettings));
            }
            else if (par1GuiButton.id == 221)
            {
                this.guivrSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiRoomscaleSettings(this, this.guivrSettings));
            }
            else if (par1GuiButton.id == 222)
            {
            	mc.vrSettings.seated = false;
                this.guivrSettings.saveOptions();
            	this.isConfirm = false;
            	this.reinit = true;
            }
            else if (par1GuiButton.id == 224)
            {
                this.guivrSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiRadialConfiguration(this,this.guivrSettings));
            }
            else if (par1GuiButton.id == 223)
            {
            	mc.vrSettings.seated = true;
                this.guivrSettings.saveOptions();
            	this.isConfirm = false;
            	this.reinit = true;
            }
            else if (par1GuiButton.id == ID_GENERIC_DEFAULTS)
            {
                mc.vrSettings.vrWorldRotation = 0;
                MCOpenVR.seatedRot = 0;
                mc.vrSettings.vrWorldScale = 1;
                mc.vrSettings.vrWorldRotationIncrement = 45f;
                mc.vrSettings.seated = false;
				MCOpenVR.clearOffset();
                this.guivrSettings.saveOptions();
            	this.reinit = true;
            }   	
        }
    }

	@Override
	public boolean event(int id, VrOptions enumm) {
		return false;
	}

	@Override
	public boolean event(int id, String s) {
		// TODO Auto-generated method stub
		return false;
	}

}