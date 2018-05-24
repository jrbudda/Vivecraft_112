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

public class GuiVRControlsList extends GuiListExtended
{
    private final GuiVRControls parent;
    private final Minecraft mc;
    private GuiListExtended.IGuiListEntry[] listEntries;
    private int maxListLabelWidth = 0;
    private static final String __OBFID = "CL_00000732";
    
    public GuiVRControlsList(GuiVRControls parent, Minecraft mc)
    {
        super(mc, parent.width, parent.height, 63, parent.height - 32, 20);
        this.parent = parent;
        this.mc = mc;
        this.maxListLabelWidth = 90;
        buildList();
    }
    
    public void buildList() {
        ArrayList<VRButtonMapping> bindings = new ArrayList<>(mc.vrSettings.buttonMappings.values());
        Collections.sort(bindings);

        ArrayList<GuiListExtended.IGuiListEntry> entries = new ArrayList<>();

        String cat = null;
        int var7 = bindings.size();
        for (int i = 0; i < var7; i++)
        {
        	VRButtonMapping kb = bindings.get(i);
        	
        	if (parent.guiFilter != kb.isGUIBinding()) continue;
        	String s = kb.keyBinding != null ? kb.keyBinding.getKeyCategory() : (kb.functionDesc.startsWith("keyboard") ? "Keyboard Emulation" : null);
        	if (s == null) continue;
        	if (s != null && !s.equals(cat)) {
                cat = s;
                entries.add(new GuiVRControlsList.CategoryEntry(cat));
            }

            /*int width = mc.fontRenderer.getStringWidth(I18n.format(kb.functionId, new Object[0]));

            if (width > this.maxListLabelWidth)
            {
                this.maxListLabelWidth = width;
            }*/

            entries.add(new GuiVRControlsList.MappingEntry(kb, null));
        }
        
        this.listEntries = entries.toArray(new GuiListExtended.IGuiListEntry[0]);
    }
    
    private boolean checkMappingConflict(VRButtonMapping mapping) {
    	for (VRButtonMapping vb : mc.vrSettings.buttonMappings.values()) {
    		if (vb == mapping) continue;
    		if (vb.isGUIBinding() != mapping.isGUIBinding()) continue;
    		for (ButtonTuple button : vb.buttons) {
    			if (button.controller.getController().isButtonActive(button.button)) {
	    			for (ButtonTuple button2 : mapping.buttons) {
	    				if (button.equals(button2)) return true;
	    			}
    			}
    		}
    	}
    	return false;
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
            this.labelWidth = GuiVRControlsList.this.mc.fontRenderer.getStringWidth(this.labelText);
        }

		@Override
        public void drawEntry(int p_148279_1_, int p_148279_2_, int p_148279_3_, int p_148279_4_, int p_148279_5_,  int p_148279_7_, int p_148279_8_, boolean p_148279_9_, float partial)
        {
            mc.fontRenderer.drawString(this.labelText, GuiVRControlsList.this.mc.currentScreen.width / 2 - this.labelWidth / 2, p_148279_3_ + p_148279_5_ - GuiVRControlsList.this.mc.fontRenderer.FONT_HEIGHT - 1, 16777215);
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
        private final VRButtonMapping myKey;
        private final GuiButton btnChangeKeyBinding;
        private final GuiButton btnChangeKeyBindingList;
        private final GuiButton btnDeleteKeyBinding;
        private static final String __OBFID = "CL_00000735";
        private GuiEnterText guiEnterText;
        private GuiVRControls parentScreen;
        
        private MappingEntry(VRButtonMapping key, GuiVRControls parent)
        {
            this.myKey = key;
            this.btnChangeKeyBinding = new GuiButton(0, 0, 0, 120, 18, "");
            this.btnChangeKeyBindingList = new GuiButton(0, 0, 0, 18, 18, "M");
            this.btnDeleteKeyBinding = new GuiButton(0, 0, 0, 18, 18, TextFormatting.RED + "X");
            this.parentScreen = parent;
            updateButtonText();
        }
        
        private void updateButtonText() {
            String str = "";
            for (ButtonTuple tuple : myKey.buttons) {
            	if (tuple.controller.getController().isButtonActive(tuple.button)) {
            		if (!str.isEmpty()) {
            			str = "Multiple";
            			break;
            		}
            		str = tuple.toReadableString();
            	}
            }
            if (str.isEmpty()) str = "None";
            else str = str.substring(0, Math.min(18, str.length()));

            if (parent.pressMode && parent.mapping == myKey) {
            	this.btnChangeKeyBinding.displayString = "> " + TextFormatting.YELLOW + str + TextFormatting.RESET + " <";
            } else if (!str.equals("None") && checkMappingConflict(myKey)) {
            	this.btnChangeKeyBinding.displayString = TextFormatting.RED + str;
            } else {
            	this.btnChangeKeyBinding.displayString = str;
            }
        }
        
		@Override
        public void drawEntry(int p_148279_1_, int x, int y, int p_148279_4_, int p_148279_5_, int p_148279_7_, int p_148279_8_, boolean p_148279_9_, float par)
        {

        	GuiVRControlsList.this.mc.fontRenderer.drawString(I18n.format(this.myKey.functionId), x + 40  - GuiVRControlsList.this.maxListLabelWidth, y + p_148279_5_ / 2 - GuiVRControlsList.this.mc.fontRenderer.FONT_HEIGHT / 2, 16777215);
        	this.btnChangeKeyBinding.x = x + 90;
        	this.btnChangeKeyBinding.y = y;
        	updateButtonText();
        
        	boolean var10 = GuiVRControlsList.this.parent.mapping == myKey;
        	this.btnChangeKeyBinding.drawButton(GuiVRControlsList.this.mc, p_148279_7_, p_148279_8_, par);
        	
        	this.btnChangeKeyBindingList.x = x + 211;
        	this.btnChangeKeyBindingList.y = y;
        	this.btnChangeKeyBindingList.drawButton(GuiVRControlsList.this.mc, p_148279_7_, p_148279_8_, par);

        	if (myKey.functionDesc.startsWith("keyboard ")) {
	        	this.btnDeleteKeyBinding.x = x + 230;
	        	this.btnDeleteKeyBinding.y = y;
	        	this.btnDeleteKeyBinding.drawButton(GuiVRControlsList.this.mc, p_148279_7_, p_148279_8_, par);
        	}
        }
        
        public boolean mousePressed(int p_148278_1_, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_)
        {
        	if (this.btnChangeKeyBinding.mousePressed(GuiVRControlsList.this.mc, p_148278_2_, p_148278_3_))
        	{
        		if (!parent.pressMode) {
        			parent.pressMode = true;
                	parent.mapping = myKey;   
                	parent.mappingButtons = new HashSet<>(myKey.buttons);
                	return true;
        		} else if (parent.mapping == myKey) {
    				parent.pressMode = false;
        			parent.mapping = null;
        			parent.mappingButtons = null;
        			return true;
        		}
        		return false;
        	}
        	else if (this.btnChangeKeyBindingList.mousePressed(GuiVRControlsList.this.mc, p_148278_2_, p_148278_3_))
            {           	
        		if (parent.pressMode) return false;
            	parent.selectionMode = true;
            	parent.mapping = myKey;   
            	parent.mappingButtons = new HashSet<>(myKey.buttons);
            	return true;          
            }
            else if (this.btnDeleteKeyBinding.mousePressed(GuiVRControlsList.this.mc, p_148278_2_, p_148278_3_))
            {           	
            	if (parent.pressMode) return false;
            	GuiVRControlsList.this.mc.vrSettings.buttonMappings.remove(myKey.functionId);
            	GuiVRControlsList.this.buildList();
            	return true;          
            }
            else
            {
                return false;
            }
        }

        public void mouseReleased(int p_148277_1_, int p_148277_2_, int p_148277_3_, int p_148277_4_, int p_148277_5_, int p_148277_6_)
        {
            this.btnChangeKeyBinding.mouseReleased(p_148277_2_, p_148277_3_);
            this.btnDeleteKeyBinding.mouseReleased(p_148277_2_, p_148277_3_);
        }


		@Override
		public void updatePosition(int p_192633_1_, int p_192633_2_, int p_192633_3_, float p_192633_4_) {	
		}


    }
}
