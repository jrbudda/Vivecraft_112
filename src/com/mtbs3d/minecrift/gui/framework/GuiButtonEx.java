/**
 * Copyright 2013 Mark Browning, StellaArtois
 * Licensed under the LGPL 3.0 or later (See LICENSE.md for details)
 */
package com.mtbs3d.minecrift.gui.framework;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiButtonEx extends GuiButton
{
    public GuiButtonEx(int par1, int par2, int par3, int par4, int par5, String par6Str)
    {
        super(par1, par2, par3, par4, par5, par6Str);
    }

    public int getWidth()
    {
        return super.width;
    }

    public int getHeight()
    {
        return super.height;
    }

    public GuiButtonEx(int par1, int par2, int par3, String par4Str)
    {
        super(par1, par2, par3, par4Str);
    }

    public boolean mousePressed(Minecraft par1Minecraft, int par2, int par3)
    {
        return super.mousePressed(par1Minecraft, par2, par3);
    }
}
