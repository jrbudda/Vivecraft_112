/**
 * Copyright 2013 Mark Browning, StellaArtois
 * Licensed under the LGPL 3.0 or later (See LICENSE.md for details)
 */
package com.mtbs3d.minecrift.gui.framework;

import com.mtbs3d.minecrift.gui.framework.GuiButtonEx;
import com.mtbs3d.minecrift.gui.framework.GuiEventEx;
import com.mtbs3d.minecrift.settings.VRSettings;
import net.minecraft.client.Minecraft;

public class GuiSmallButtonEx extends GuiButtonEx
{
    public VRSettings.VrOptions vrOptions;

    GuiEventEx _eventHandler = null;

    public GuiSmallButtonEx(int par1, int par2, int par3, String par4Str)
    {
        this(par1, par2, par3, (VRSettings.VrOptions)null, par4Str);
    }

    public GuiSmallButtonEx(int par1, int par2, int par3, int par4, int par5, String par6Str)
    {
        super(par1, par2, par3, par4, par5, par6Str);
    }

    public GuiSmallButtonEx(int par1, int par2, int par3, VRSettings.VrOptions vrOptions, String par5Str)
    {
        super(par1, par2, par3, 150, 20, par5Str);
        this.vrOptions = vrOptions;
    }

    public VRSettings.VrOptions returnVrEnumOptions()
    {
        return this.vrOptions;
    }

    public void setEventHandler(GuiEventEx eventHandler)
    {
        _eventHandler = eventHandler;
    }

    public boolean mousePressed(Minecraft par1Minecraft, int par2, int par3)
    {
        boolean result = super.mousePressed(par1Minecraft, par2, par3);

        if (_eventHandler != null && result)
        {
            if (vrOptions != null)
                _eventHandler.event(GuiEventEx.ID_VALUE_CHANGED, vrOptions);
        }

        return result;
    }
}
