/**
 * Copyright 2013 Mark Browning, StellaArtois
 * Licensed under the LGPL 3.0 or later (See LICENSE.md for details)
 */
package org.vivecraft.gui.framework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.vivecraft.settings.VRSettings;
import org.vivecraft.utils.Utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.optifine.Lang;

public class BaseGuiSettings extends GuiScreen
{
	protected GuiScreen parentGuiScreen;

    /** The title string that is displayed in the top-center of the screen. */
    protected String screenTitle = "";

    /** GUI game settings */
    protected VRSettings guivrSettings;

    public static final int ID_GENERIC_DONE = 9000;
    public static final int ID_GENERIC_MODE_CHANGE = 9001;
    public static final int ID_GENERIC_RESETORIGIN = 9002;
    public static final int ID_GENERIC_RECALIBRATE = 9003;
    public static final int ID_GENERIC_DEFAULTS    = 9004;
    public static final int ID_GENERIC_REMAP       = 9005;
    public static final int ID_GENERIC_REINIT      = 9006;

    private int lastMouseX = 0;
    private int lastMouseY = 0;
    private long mouseStillTimeMs = 0L;
    public static final long TOOLTIP_DELAY_MS = 750;

    protected boolean reinit = false;

    /**
     * True if the system is 64-bit (using a simple indexOf test on a system property)
     */
    private boolean is64bit = false;

    /** An array of all of EnumOption's video options. */

    public BaseGuiSettings( GuiScreen par1GuiScreen,
                                VRSettings par2vrSettings)
    {
		this.parentGuiScreen = par1GuiScreen;
        this.guivrSettings = par2vrSettings;
    }

    public void drawScreen(int par1, int par2, float par3) {
    	this.drawScreen( par1, par2, par3, true );
    }
    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3, boolean drawBackground)
    {
        if (this.reinit)
        {
            this.buttonList.clear();
            initGui();
            this.reinit = false;
        }

        if( drawBackground)
        	this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, Lang.get(this.screenTitle), this.width / 2, 15, 16777215);
        super.drawScreen(par1, par2, par3);

        if (Math.abs(par1 - this.lastMouseX) <= 5 && Math.abs(par2 - this.lastMouseY) <= 5)
        {
            long delayMs = TOOLTIP_DELAY_MS;

            if (System.currentTimeMillis() >= this.mouseStillTimeMs + delayMs)
            {
                int var5 = this.width / 2 - 150;
                int var6 = this.height / 6 - 5;

                if (par2 <= var6 + 98)
                {
                    var6 += 105;
                }

                int var7 = var5 + 150 + 150;
                int var8 = var6 + 84 + 10;
                GuiButton var9 = this.getSelectedButton(par1, par2);

                if (var9 != null)
                {
                    String var10 = this.getButtonName(var9.displayString);
                    String[] var11 = this.getTooltipLines(var9.id, var7 - var5 - 10);

                    if (var11 == null)
                    {
                        return;
                    }

                    this.drawGradientRect(var5, var6, var7, var8, -536870912, -536870912);

                    for (int var12 = 0; var12 < var11.length; ++var12)
                    {
                        String var13 = var11[var12];
                        this.fontRenderer.drawString(var13, var5 + 5, var6 + 5 + var12 * 11, 14540253);
                    }
                }
            }
        }
        else
        {
            this.lastMouseX = par1;
            this.lastMouseY = par2;
            this.mouseStillTimeMs = System.currentTimeMillis();
        }
    }

    protected final String[] getTooltipLines(int buttonId, int boxWidth)
    {
        VRSettings.VrOptions option = VRSettings.VrOptions.getEnumOptions(buttonId);
        if (option == null)
            return null;

        String key = "vivecraft.options." + option.name() + ".tooltip";
        String str = Lang.get(key, null);

        if (str == null)
            return null;

        String[] lines = str.split("\\r?\\n", -1);

        List<String> newLines = new ArrayList<>();
        for (String line : lines) {
            if (line.isEmpty()) {
                newLines.add(line);
                continue;
            }

            int spaceCount = line.indexOf(line.trim().charAt(0));
            String spaces = String.join("", Collections.nCopies(spaceCount, " "));
            List<ITextComponent> list = Utils.wrapText(new TextComponentString(line), boxWidth, Minecraft.getMinecraft().fontRenderer, true, spaces);

            StringBuilder style = new StringBuilder();
            for (ITextComponent text : list) {
                newLines.add(style.toString() + text.getUnformattedText());

                String s = text.getUnformattedText();
                for (int i = 0; i < s.length(); i++) {
                    if (s.charAt(i) == '\u00a7') {
                        if (i + 1 >= s.length())
                            break;

                        char c = s.charAt(i + 1);
                        style.append('\u00a7').append(c);

                        i++;
                    }
                }
            }
        }

        return newLines.toArray(new String[0]);
    }

    protected String getButtonName(String var1)
    {
        int var2 = var1.indexOf(58);
        return var2 < 0 ? var1 : var1.substring(0, var2);
    }

    protected GuiButton getSelectedButton(int var1, int var2)
    {
        for (int var3 = 0; var3 < this.buttonList.size(); ++var3)
        {
            if (this.buttonList.get(var3) instanceof GuiButtonEx)
            {
                GuiButtonEx var4 = (GuiButtonEx) this.buttonList.get(var3);
                boolean var5 = var1 >= var4.x && var2 >= var4.y && var1 < var4.x + var4.getWidth() && var2 < var4.y + var4.getHeight();

                if (var5) {
                    return var4;
                }
            }
        }

        return null;
    }
}
