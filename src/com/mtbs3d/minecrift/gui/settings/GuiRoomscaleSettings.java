package com.mtbs3d.minecrift.gui.settings;

import com.mtbs3d.minecrift.gameplay.trackers.BackpackTracker;
import com.mtbs3d.minecrift.gameplay.trackers.BowTracker;
import com.mtbs3d.minecrift.gui.framework.*;
import com.mtbs3d.minecrift.settings.VRSettings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiRoomscaleSettings extends BaseGuiSettings implements GuiEventEx
{
    static VRSettings.VrOptions[] roomscaleSettings = new VRSettings.VrOptions[]
    {
            VRSettings.VrOptions.WEAPON_COLLISION,
            VRSettings.VrOptions.REALISTIC_JUMP,
            VRSettings.VrOptions.ANIMAL_TOUCHING,
            VRSettings.VrOptions.REALISTIC_SNEAK,
            VRSettings.VrOptions.REALISTIC_CLIMB,
            VRSettings.VrOptions.REALISTIC_ROW,
            VRSettings.VrOptions.REALISTIC_SWIM,
            VRSettings.VrOptions.BOW_MODE,
            VRSettings.VrOptions.BACKPACK_SWITCH
            };
    
    public GuiRoomscaleSettings(GuiScreen guiScreen, VRSettings guivrSettings) {
        super( guiScreen, guivrSettings );
        screenTitle = "Roomscale Interactions Settings";
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.buttonList.clear();
        this.buttonList.add(new GuiButtonEx(ID_GENERIC_DEFAULTS, this.width / 2 - 155 ,  this.height -25 ,150,20, "Reset To Defaults"));
        this.buttonList.add(new GuiButtonEx(ID_GENERIC_DONE, this.width / 2 - 155  + 160, this.height -25,150,20, "Done"));
        VRSettings.VrOptions[] buttons = roomscaleSettings;
        addButtons(buttons,0);
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
                // VIVE START - new options
                GuiSliderEx slider = new GuiSliderEx(var8.returnEnumOrdinal(), width, height - 20, var8, this.guivrSettings.getKeyBinding(var8), minValue, maxValue, increment, this.guivrSettings.getOptionFloatValue(var8));
                slider.setEventHandler(this);
                slider.enabled = getEnabledState(var8);
                this.buttonList.add(slider);
            }
            else
            {
                GuiSmallButtonEx smallButton = new GuiSmallButtonEx(var8.returnEnumOrdinal(), width, height - 20, var8, this.guivrSettings.getKeyBinding(var8));
                smallButton.setEventHandler(this);
                smallButton.enabled = getEnabledState(var8);
                this.buttonList.add(smallButton);
            }
        }
	}

    private boolean getEnabledState(VRSettings.VrOptions var8)
    {
        return true;
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
                //jrbudda//
                vr.weaponCollision = true;
                vr.animaltouching = true;
                vr.realisticClimbEnabled = true;
                vr.realisticJumpEnabled = true;
                vr.realisticSneakEnabled = true;
                vr.realisticSwimEnabled = true;
                vr.realisticRowEnabled = true;
                vr.backpackSwitching = true;
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
                    
                    if(num == VRSettings.VrOptions.MOVE_MODE){
                    	this.reinit = true;
                    }
                    
            }
        }
    }

    @Override
    public boolean event(int id, VRSettings.VrOptions enumm)
    {
        return true;
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
                // VIVE START - new options
                case SIMULATE_FALLING:
                    return new String[] {
                            "If enabled the player will falls to the ground in TP mode",
                            "when standing above empty space. Also allows jumping"
                    } ;
                case WEAPON_COLLISION:
                    return new String[] {
                            "If enabled, you can swing your pickaxe at blocks to",
                            "mine them, or your sword at enemies to hit them."
                    } ;
                case ANIMAL_TOUCHING:
                    return new String[] {
                            "If enabled, touching a passive mob (animal) without a",
                            "weapon will right-click (interact) instead of attacking.",
                            "Turn off for Piggy Slapping, Josh.",
                    } ;
                // VIVE END - new options
                    //JRBUDDA
                case ALLOW_CRAWLING:
                    return new String[] {
                            "If enabled the player will be able to duck under block"
                    } ;
                case REALISTIC_JUMP:
                    return new String[]{
                            "If turned on, once you jump in real life",
                            "Your player will also jump. Also enables",
                            "Jump Boots."
                    };
                case REALISTIC_SNEAK:
                    return new String[]{
                            "If turned on, once you duck in real life",
                            "Your player will also sneak"
                    };
                case REALISTIC_CLIMB:
                    return new String[]{
                            "If turned on, allow climbing ladders and vines",
                            "by touching them. Also enables Climb Claws."
                    };
                case BACKPACK_SWITCH:
                    return new String[]{
                            "If turned on, reaching behind your head with the right",
                            "controller will swap to the 1st hotbar slot, or back to the",
                            "previous slot. Doing the same with the left controller will",
                            "swap the left and right hand items."
                    };
                case REALISTIC_SWIM:
                    return new String[]{
                            "If turned on, allow swimming by doing the breaststoke",
                            "with the controllers."
                    };
                case REALISTIC_ROW:
                    return new String[]{
                            "Row, row, row your boat... by flapping your arms like mad."
                    };
                case BOW_MODE:
                    return new String[]{
                            "Sets when to use Roomscale Archery",
                            "OFF: Never",
                            "Vanilla: Only for the vanilla bow, no mod items",
                            "ON: Always for any item that uses the 'bow' action"                        
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
