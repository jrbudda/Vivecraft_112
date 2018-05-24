package com.mtbs3d.minecrift.gui.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mtbs3d.minecrift.gui.framework.*;
import com.mtbs3d.minecrift.provider.MCOpenVR;
import com.mtbs3d.minecrift.settings.VRSettings;
import com.mtbs3d.minecrift.settings.VRSettings.VrOptions;
import com.mtbs3d.minecrift.utils.HardwareType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiStandingSettings extends BaseGuiSettings implements GuiEventEx
{
    static VRSettings.VrOptions[] locomotionSettings = new VRSettings.VrOptions[]
    {
    		VRSettings.VrOptions.REVERSE_HANDS,
            VRSettings.VrOptions.WALK_UP_BLOCKS,
            VRSettings.VrOptions.VEHICLE_ROTATION,
            VRSettings.VrOptions.BCB_ON,
            VRSettings.VrOptions.WALK_MULTIPLIER,
            VRSettings.VrOptions.ALLOW_MODE_SWITCH,
            VRSettings.VrOptions.WORLD_ROTATION_INCREMENT

    };

    static VRSettings.VrOptions[] teleportSettings = new VRSettings.VrOptions[]
    {
            VRSettings.VrOptions.LIMIT_TELEPORT,
            VRSettings.VrOptions.SIMULATE_FALLING,
            VRSettings.VrOptions.TELEPORT_UP_LIMIT,
            VRSettings.VrOptions.TELEPORT_DOWN_LIMIT,
            VRSettings.VrOptions.TELEPORT_HORIZ_LIMIT
    };
    static VRSettings.VrOptions[] freeMoveSettings = new VRSettings.VrOptions[]
    {
    		VRSettings.VrOptions.FREEMOVE_MODE,
            VRSettings.VrOptions.INERTIA_FACTOR,
            VRSettings.VrOptions.MOVEMENT_MULTIPLIER,
            VRSettings.VrOptions.FOV_REDUCTION,
    };
    static VRSettings.VrOptions[] freeMoveSettingsJP = new VRSettings.VrOptions[]
    {
            VRSettings.VrOptions.ANALOG_DEADZONE,
    		VRSettings.VrOptions.FREEMOVE_WMR_STICK
    };
    static VRSettings.VrOptions[] freeMoveSettingsNJP = new VRSettings.VrOptions[]
    {
    		VRSettings.VrOptions.ANALOG_DEADZONE,
            VRSettings.VrOptions.ANALOG_MOVEMENT
    };
    
    public GuiStandingSettings(GuiScreen guiScreen, VRSettings guivrSettings) {
        super( guiScreen, guivrSettings );
        screenTitle = "Standing Locomotion Settings";
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.buttonList.clear();
        this.buttonList.add(new GuiButtonEx(ID_GENERIC_DEFAULTS, this.width / 2 - 155 ,  this.height -25 ,150,20, "Reset To Defaults"));
        this.buttonList.add(new GuiButtonEx(ID_GENERIC_DONE, this.width / 2 - 155  + 160, this.height -25,150,20, "Done"));
        VRSettings.VrOptions[] buttons = locomotionSettings;
        addButtons(buttons,0);
        mc.vrSettings.vrFreeMove = mc.vrPlayer.getFreeMove();
        GuiSmallButtonEx mode = new GuiSmallButtonEx(VRSettings.VrOptions.MOVE_MODE.returnEnumOrdinal(), this.width / 2 - 75, this.height / 6 + 80,VRSettings.VrOptions.MOVE_MODE, this.guivrSettings.getKeyBinding(VRSettings.VrOptions.MOVE_MODE));
        mode.setEventHandler(this);
        this.buttonList.add(mode);
  
		if(mc.vrPlayer.getFreeMove()){
			List<VrOptions> fm = new ArrayList<VRSettings.VrOptions>();
			Collections.addAll(fm, freeMoveSettings);
	    	if(guivrSettings.vrFreeMoveMode == guivrSettings.FREEMOVE_JOYPAD) 
				Collections.addAll(fm, freeMoveSettingsJP);
	    	else if (guivrSettings.vrFreeMoveMode != guivrSettings.FREEMOVE_RUNINPLACE) 
				Collections.addAll(fm, freeMoveSettingsNJP);
        	addButtons((VrOptions[]) fm.toArray(new VrOptions[fm.size()]),115);
		}
        else
        	addButtons(teleportSettings,115);        
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
            
            HardwareType hw = MCOpenVR.getHardwareType();
            if (var8 == VRSettings.VrOptions.FREEMOVE_WMR_STICK) 
            	if(!(hw.hasTouchpad && hw.hasStick)) continue;
           
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
                if (var8 == VRSettings.VrOptions.STRAFE_MULTIPLIER)
                {
                    minValue = 0f;
                    maxValue = 1.0f;
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
                else if (var8 == VRSettings.VrOptions.ANALOG_DEADZONE) {
                    minValue = 0f;
                    maxValue = 0.5f;
                    increment = 0.01f;
                }
                else if(var8 == VrOptions.TELEPORT_DOWN_LIMIT){
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
                Minecraft.getMinecraft().gameSettings.saveOptions();
                Minecraft.getMinecraft().vrSettings.saveOptions();
                this.mc.displayGuiScreen(this.parentGuiScreen);
            }
            else if (par1GuiButton.id == ID_GENERIC_DEFAULTS)
            {
                vr.inertiaFactor = VRSettings.INERTIA_NORMAL;
                vr.movementSpeedMultiplier = 1f;
                vr.simulateFalling = true;
                //jrbudda//
                vr.vrAllowCrawling = false;
                vr.vrAllowLocoModeSwotch = true;
                vr.vrFreeMove = false;
                vr.vrLimitedSurvivalTeleport = true;
                vr.vrShowBlueCircleBuddy = true;
                vr.walkMultiplier=1;
                vr.vrFreeMoveMode = vr.FREEMOVE_CONTROLLER;
                vr.vehicleRotation = true;
                vr.useFOVReduction = false;
                vr.walkUpBlocks = true;
                vr.analogMovement = true;
                vr.vrTeleportDownLimit = 4;
                vr.vrTeleportUpLimit = 1;
                vr.vrTeleportHorizLimit = 16;

                //end jrbudda
                
                Minecraft.getMinecraft().gameSettings.viewBobbing = true;

                Minecraft.getMinecraft().gameSettings.saveOptions();
                Minecraft.getMinecraft().vrSettings.saveOptions();
                this.reinit = true;
            }
            else if (par1GuiButton instanceof GuiSmallButtonEx)
            {
                VRSettings.VrOptions num = VRSettings.VrOptions.getEnumOptions(par1GuiButton.id);
                    this.guivrSettings.setOptionValue(((GuiSmallButtonEx)par1GuiButton).returnVrEnumOptions(), 1);
                    par1GuiButton.displayString = this.guivrSettings.getKeyBinding(VRSettings.VrOptions.getEnumOptions(par1GuiButton.id));
                    
                    if(num == VRSettings.VrOptions.MOVE_MODE || num == VRSettings.VrOptions.FREEMOVE_MODE){
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

    @Override
    protected String[] getTooltipLines(String displayString, int buttonId)
    {
        VRSettings.VrOptions e = VRSettings.VrOptions.getEnumOptions(buttonId);
        if( e != null )
            switch(e)
            {
                case MOVEMENT_MULTIPLIER:
                    return new String[] {
                            "Sets a movement multiplier, allowing slower movement",
                            "than default. This may help reduce locomotion induced",
                            "simulator sickness.",
                            "WARNING: May trigger anti-cheat warnings if on a",
                            "Multiplayer server!!",
                            "Defaults to standard Minecraft movement (1.0)",
                            "speed)."
                    } ;
                case STRAFE_MULTIPLIER:
                    return new String[] {
                            "Sets an additional strafe (side-to-side) movement",
                            "multiplier. This is applied on top of the movement",
                            "multiplier. A value of zero will disable strafe.",
                            "This may help reduce locomotion induced simulator",
                            "sickness. WARNING: May trigger anti-cheat warnings",
                            "if on a Multiplayer server!!",
                            "Defaults to 0.33 (1.0 is standard Minecraft movement",
                            "speed)."
                    } ;
                case WALK_UP_BLOCKS:
                    return new String[] {
                            "Allows you to set the ability to walk up blocks without",
                            "having to jump. HOTKEY - RCtrl-B",
                            "WARNING: May trigger anti-cheat warnings if on a",
                            "Multiplayer server!!",
                            "  OFF: (Default) You will have to jump up blocks.",
                            "  ON:  You can walk up single blocks. May reduce",
                            "       locomotion induced simulator sickness for some."
                    } ;
                case INERTIA_FACTOR:
                    return new String[]{
                            "Sets the player's movement inertia in single player",
                            "mode. Lower inertia means faster acceleration, higher",
                            "inertia slower accelaration. High inertia may reduce",
                            "motion sickness for some, but beware of cliff edges!!",
                            "  Normal: (Default) Standard Minecraft player",
                            "           movement.",
                            "  Automan < Normal < A lot < Even More. Does not",
                            "  affect lava, water or jumping movement currently."
                    };
                // VIVE START - new options
                case SIMULATE_FALLING:
                    return new String[] {
                            "If enabled the player will falls to the ground in TP mode",
                            "when standing above empty space. Also allows jumping"
                    } ;
                case ALLOW_MODE_SWITCH:
                    return new String[] {
                            "Allows the use of the Pick Block button to switch between",
                            "Teleport and Free Move mode."
                    } ;
                case ALLOW_CRAWLING:
                    return new String[] {
                            "If enabled the player will be able to duck under block"
                    } ;
                case MOVE_MODE:
                    return new String[] {
                            "Current move mode. Teleport or Free Move."
                    } ;
                case LIMIT_TELEPORT:
                    return new String[] {
                            "If enabled the arc teleporter will be have restrictions",
                            "in survival mode. It will not be able to jump up the side", 
                            "of blocks, it will consume food, and it will have an energy",
                            "bar that refills over time."
                    } ;
                case BCB_ON:
                    return new String[] {
                            "Shows your body position as a square shadow on the ground.",
                            "This is your Square Shadow Buddy (tm).",
                            "Do not lose your Square Shadow Buddy."
                    };
                case WALK_MULTIPLIER:
                    return new String[]{
                            "Multiplies your position in the room by a factor",
                            "Allows you to walk around more,",
                            "but may cause motion sickness"
                    };
                case FREEMOVE_MODE:
                    return new String[] {
                            "The source for freemove direction.","",
                            "Controller: Offhand controller pointing direction",
                            "HMD: Headset look direction",
                            "Run In Place:",
                            "    Direction is based on how controllers are swinging.",
                            "Joy/Pad: ",
                            "    Uses the offhand touchpad or joystick for all motion.",
                    } ;
                case FREEMOVE_WMR_STICK:
                	return new String[] {
                			"Whether to use touchpad or stick for Joy/Pad movement."	
                	};
                case ANALOG_DEADZONE:
                	return new String[] {
                			"How far an axis must be pushed before movement is",
                			"registered for Joy/Pad or Analog movement. Adjust this",
                			"if you are drifting while not touching the axis."
                	};
                case VEHICLE_ROTATION:
                    return new String[] {
                            "Riding in a vehicle will rotate the world",
                            "as the vehicle rotates. May be disorienting."
                            
                    } ;
                case FOV_REDUCTION:
                    return new String[] {
                            "Shrinks the field of view while moving. Can help with",
                            "motion sickness."
                    } ;
                case WORLD_ROTATION_INCREMENT:
                    return new String[] {
                            "How many degrees to rotate when",
                            "rotating the world."
                            
                    };
                case ANALOG_MOVEMENT:
                    return new String[] {
                            "Walking speed will be determined by the controller button",
                            "axis, if the bound button has a variable axis."    ,"",
                            "For full analog control it is better to use 'Joy/Pad mode"                      
                    };
                case TELEPORT_DOWN_LIMIT:
                    return new String[] {
                            "Limit the number of blocks you can teleport below you"
                    };
                case TELEPORT_UP_LIMIT:
                    return new String[] {
                            "Limit the number of blocks you can teleport above you"
                    };
                case TELEPORT_HORIZ_LIMIT:
                    return new String[] {
                            "Limit the number of blocks you can teleport sideways you"
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
    
}
