package org.vivecraft.gui.settings;

import org.vivecraft.gui.framework.BaseGuiSettings;
import org.vivecraft.gui.framework.GuiEventEx;
import org.vivecraft.gui.framework.GuiSmallButtonEx;
import org.vivecraft.settings.VRSettings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiOtherRenderOpticsSettings extends BaseGuiSettings implements GuiEventEx
{
    static VRSettings.VrOptions[] oculusOptionsUseSingleIpd = new VRSettings.VrOptions[]
    {

    };

    static VRSettings.VrOptions[] oculusOptionsUseTwinIpd = new VRSettings.VrOptions[]
    {

    };

    public GuiOtherRenderOpticsSettings(GuiScreen guiScreen, VRSettings guivrSettings) {
        super( guiScreen, guivrSettings );
        screenTitle = "IPD / FOV Settings";
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
//        this.buttonList.clear();
//        this.buttonList.add(new GuiButtonEx(ID_GENERIC_DEFAULTS, this.width / 2 - 155 ,  this.height -25 ,150,20, Lang.get("vivecraft.gui.loaddefaults")));
//        this.buttonList.add(new GuiButtonEx(ID_GENERIC_DONE, this.width / 2 - 155  + 160, this.height -25,150,20, Lang.get("gui.done"));
//        String eyeRelief = "";//String.format("Base Eye Relief: %.3fmm ", new Object[] { this.mc.entityRenderer.getBaseEyeRelief() * 1000f });
//        VRSettings.VrOptions[] buttons = null;
//        if (Minecraft.getMinecraft().vrSettings.useHalfIpds == false)
//            buttons = oculusOptionsUseSingleIpd;
//        else
//            buttons = oculusOptionsUseTwinIpd;
//
//        for (int var12 = 2; var12 < buttons.length + 2; ++var12)
//        {
//            VRSettings.VrOptions var8 = buttons[var12 - 2];
//            int width = this.width / 2 - 155 + var12 % 2 * 160;
//            int height = this.height / 6 + 21 * (var12 / 2) - 10;
//
//            if (var8 == VRSettings.VrOptions.DUMMY)
//                continue;
//
//            if (var8.getEnumFloat())
//            {
//                float minValue = 0.0f;
//                float maxValue = 1.0f;
//                float increment = 0.01f;
//
//                if (var8 == VRSettings.VrOptions.EYE_RELIEF_ADJUST)
//                {
//                    minValue = -0.035f;
//                    maxValue =  0.035f;
//                    increment = 0.00001f;
//                }
//                else if (var8 == VRSettings.VrOptions.FOV_CHANGE)
//                {
//                    minValue  = -10f;
//                    maxValue  = 10f;
//                    increment = 0.1f;
//                }
//                else if (var8 == VRSettings.VrOptions.TOTAL_IPD)
//                {
//                    minValue = 0.055f;
//                    maxValue = 0.075f;
//                    increment = 0.0001f;
//                }
//                else if (var8 == VRSettings.VrOptions.LEFT_HALF_IPD)
//                {
//                    minValue = -0.0375f;
//                    maxValue = -0.0225f;
//                    increment = 0.0001f;
//                }
//                else if (var8 == VRSettings.VrOptions.RIGHT_HALF_IPD)
//                {
//                    minValue = 0.0225f;
//                    maxValue = 0.0375f;
//                    increment = 0.0001f;
//                }
//
//                GuiSliderEx slider = new GuiSliderEx(var8.returnEnumOrdinal(), width, height, var8, this.guivrSettings.getKeyBinding(var8), minValue, maxValue, increment, this.guivrSettings.getOptionFloatValue(var8));
//                slider.setEventHandler(this);
//                slider.enabled = getEnabledState(var8);
//                this.buttonList.add(slider);
//            }
//            else
//            {
//                if (var8 == VRSettings.VrOptions.EYE_RELIEF_PLACEHOLDER)
//                {
//                    GuiSmallButtonEx button = new GuiSmallButtonEx(9999, width, height, var8, eyeRelief);
//                    button.enabled = false;
//                    this.buttonList.add(button);
//                }
//                else
//                {
//                    GuiSmallButtonEx smallButton = new GuiSmallButtonEx(var8.returnEnumOrdinal(), width, height, var8, this.guivrSettings.getKeyBinding(var8));
//                    smallButton.setEventHandler(this);
//                    smallButton.enabled = getEnabledState(var8);
//                    this.buttonList.add(smallButton);
//                }
//            }
//        }
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
        if (par1GuiButton.enabled)
        {
            if (par1GuiButton.id == ID_GENERIC_DONE)
            {
                Minecraft.getMinecraft().vrSettings.saveOptions();
                this.mc.displayGuiScreen(this.parentGuiScreen);
            }
            else if (par1GuiButton.id == ID_GENERIC_DEFAULTS)
            {

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

    @Override
    public boolean event(int id, VRSettings.VrOptions enumm)
    {

        return true;
    }

    @Override
    public boolean event(int id, String s) {
        return true;
    }
}
