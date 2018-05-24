package com.mtbs3d.minecrift.gui.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.base.Joiner;
import com.mtbs3d.minecrift.control.ButtonTuple;
import com.mtbs3d.minecrift.control.ButtonType;
import com.mtbs3d.minecrift.control.ControllerType;
import com.mtbs3d.minecrift.control.VRButtonMapping;
import com.mtbs3d.minecrift.gui.framework.GuiEnterText;
import com.mtbs3d.minecrift.provider.MCOpenVR;

public class GuiRadialItemsList extends GuiListExtended
{
    private final GuiRadialConfiguration parent;
    private final Minecraft mc;
    private GuiListExtended.IGuiListEntry[] listEntries;
    private int maxListLabelWidth = 0;
    private static final String __OBFID = "CL_00000732";
    
    public GuiRadialItemsList(GuiRadialConfiguration parent, Minecraft mc)
    {
        super(mc, parent.width, parent.height, 63, parent.height - 32, 20);
        this.parent = parent;
        this.mc = mc;
        this.maxListLabelWidth = 90;
        buildList();
    }
    
    public void buildList() {
        KeyBinding[] bindings = ArrayUtils.clone(mc.gameSettings.keyBindings);
        Arrays.sort(bindings);
        
        ArrayList<GuiListExtended.IGuiListEntry> entries = new ArrayList<>();

        String cat = null;
        int var7 = bindings.length;
        for (int i = 0; i < var7; i++)
        {
        	KeyBinding kb = bindings[i];       	
        	String s = kb != null ? kb.getKeyCategory() : null;
        	if (s == null) continue;
        	if (s != null && !s.equals(cat)) {
                cat = s;
                entries.add(new GuiRadialItemsList.CategoryEntry(cat));
            }
            entries.add(new GuiRadialItemsList.MappingEntry(kb, this.parent));
        }
        
        this.listEntries = entries.toArray(new GuiListExtended.IGuiListEntry[0]);
    }
    
    protected int getSize()
    {
        return this.listEntries.length;
    }

    /**
     * Gets the IGuiListEntry object for the given index
     */
    public GuiListExtended.IGuiListEntry getListEntry(int i)
    {
        return this.listEntries[i];
    }

    protected int getScrollBarX()
    {
        return super.getScrollBarX() + 15;
    }

    /**
     * Gets the width of the list
     */
    public int getListWidth()
    {
        return super.getListWidth() + 32;
    }

    public class CategoryEntry implements GuiListExtended.IGuiListEntry
    {
        private final String labelText;
        private final int labelWidth;
        private static final String __OBFID = "CL_00000734";

        public CategoryEntry(String p_i45028_2_)
        {
            this.labelText = I18n.format(p_i45028_2_, new Object[0]);
            this.labelWidth = GuiRadialItemsList.this.mc.fontRenderer.getStringWidth(this.labelText);
        }

		@Override
        public void drawEntry(int p_148279_1_, int p_148279_2_, int p_148279_3_, int p_148279_4_, int p_148279_5_,  int p_148279_7_, int p_148279_8_, boolean p_148279_9_, float partial)
        {
            mc.fontRenderer.drawString(this.labelText, GuiRadialItemsList.this.mc.currentScreen.width / 2 - this.labelWidth / 2, p_148279_3_ + p_148279_5_ - GuiRadialItemsList.this.mc.fontRenderer.FONT_HEIGHT - 1, 6777215);
        }

        public boolean mousePressed(int p_148278_1_, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_)
        {
            return false;
        }

        public void mouseReleased(int p_148277_1_, int p_148277_2_, int p_148277_3_, int p_148277_4_, int p_148277_5_, int p_148277_6_) 
        {
        }



		@Override
		public void updatePosition(int p_178011_1_, int p_178011_2_, int p_178011_3_, float partial) 
		{		
		}

    }

    public class MappingEntry implements GuiListExtended.IGuiListEntry
    {
        private final KeyBinding myKey;
        private static final String __OBFID = "CL_00000735";
        private GuiRadialConfiguration parentScreen;
        
        private MappingEntry(KeyBinding key, GuiRadialConfiguration parent)
        {
            this.myKey = key;
            this.parentScreen = parent;
            updateButtonText();
        }
        
        private void updateButtonText() {

        }
        
		@Override
        public void drawEntry(int p_148279_1_, int x, int y, int p_148279_4_, int p_148279_5_, int p_148279_7_, int p_148279_8_, boolean p_148279_9_, float par)
        {
			mc.fontRenderer.drawString(I18n.format(this.myKey.getKeyDescription()), mc.currentScreen.width / 2 - maxListLabelWidth / 2, y + p_148279_5_ / 2 - GuiRadialItemsList.this.mc.fontRenderer.FONT_HEIGHT / 2, 16777215);
        }
        
        public boolean mousePressed(int p_148278_1_, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_)
        {
        	parentScreen.setKey(myKey);
        	return true;
        }

        public void mouseReleased(int p_148277_1_, int p_148277_2_, int p_148277_3_, int p_148277_4_, int p_148277_5_, int p_148277_6_)
        {

        }


		@Override
		public void updatePosition(int p_192633_1_, int p_192633_2_, int p_192633_3_, float p_192633_4_) {	
		}


    }
}
