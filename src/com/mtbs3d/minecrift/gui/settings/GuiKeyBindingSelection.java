package com.mtbs3d.minecrift.gui.settings;

import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.ArrayUtils;

import com.mtbs3d.minecrift.control.ButtonTuple;
import com.mtbs3d.minecrift.control.ButtonType;
import com.mtbs3d.minecrift.control.ControllerType;
import com.mtbs3d.minecrift.control.VRButtonMapping;
import com.mtbs3d.minecrift.provider.MCOpenVR;

public class GuiKeyBindingSelection extends GuiListExtended
{
    private final GuiVRControls controlsScreen;
    private final Minecraft mc;
    private final GuiListExtended.IGuiListEntry[] listEntries;
    private int maxListLabelWidth;

    public GuiKeyBindingSelection(GuiVRControls controls, Minecraft mcIn)
    {
        super(mcIn, controls.width + 45, controls.height, 63, controls.height - 32, 20);
        this.controlsScreen = controls;
        this.mc = mcIn;

        ArrayList<GuiListExtended.IGuiListEntry> entries = new ArrayList<>();
        
        for (ControllerType controller : ControllerType.values())
        {
	        for (ButtonType button : controller.getController().getActiveButtons())
	        {
	        	String buttonName = new ButtonTuple(button, controller).toReadableString();
	            int j = mcIn.fontRenderer.getStringWidth(buttonName);
	            if (j > this.maxListLabelWidth) {
	                this.maxListLabelWidth = j;
	            }
	            entries.add(new GuiKeyBindingSelection.ButtonEntry(new ButtonTuple(button, controller), buttonName));
	            if (controller.getController().canButtonBeTouched(button)) {
	            	String buttonName2 = new ButtonTuple(button, controller, true).toReadableString();
		            int k = mcIn.fontRenderer.getStringWidth(buttonName2);
		            if (k > this.maxListLabelWidth) {
		                this.maxListLabelWidth = k;
		            }
		            entries.add(new GuiKeyBindingSelection.ButtonEntry(new ButtonTuple(button, controller, true), buttonName2));
	            }
	        }
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
    public GuiListExtended.IGuiListEntry getListEntry(int index)
    {
        return this.listEntries[index];
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

        public CategoryEntry(String name)
        {
            this.labelText = I18n.format(name);
            this.labelWidth = GuiKeyBindingSelection.this.mc.fontRenderer.getStringWidth(this.labelText);
        }

        public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks)
        {
            GuiKeyBindingSelection.this.mc.fontRenderer.drawString(TextFormatting.AQUA + this.labelText, GuiKeyBindingSelection.this.mc.currentScreen.width / 2 - this.labelWidth / 2, y + slotHeight - GuiKeyBindingSelection.this.mc.fontRenderer.FONT_HEIGHT - 1, 16777215);
        }

        public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY)
        {
            return false;
        }

        public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY)
        {
        }

        public void updatePosition(int p_192633_1_, int p_192633_2_, int p_192633_3_, float p_192633_4_)
        {
        }
    }

    public class ButtonEntry implements GuiListExtended.IGuiListEntry
    {
        private final ButtonTuple button;
        private final String buttonName;

        private ButtonEntry(ButtonTuple button, String buttonName)
        {
            this.button = button;
            this.buttonName = buttonName;
        }


		public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks)
        {
            TextFormatting formatting = TextFormatting.WHITE;
            boolean flag = (mouseX <= GuiKeyBindingSelection.this.width * .6) && mouseY < y + slotHeight && mouseY > y;
            boolean flag2 = GuiKeyBindingSelection.this.controlsScreen.mappingButtons.contains(this.button);
            boolean flag3 = checkMappingConflict();
            if (flag) formatting = TextFormatting.GREEN;
            else if (flag3) formatting = TextFormatting.RED;
            GuiKeyBindingSelection.this.mc.fontRenderer.drawString(formatting + this.buttonName, x + 190 - GuiKeyBindingSelection.this.maxListLabelWidth, y + slotHeight / 2 - GuiKeyBindingSelection.this.mc.fontRenderer.FONT_HEIGHT / 2, 16777215);
            if (flag2) GuiKeyBindingSelection.this.mc.fontRenderer.drawString("->", x + 175 - GuiKeyBindingSelection.this.maxListLabelWidth, y + slotHeight / 2 - GuiKeyBindingSelection.this.mc.fontRenderer.FONT_HEIGHT / 2, 16777215);
           
        }

        public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY)
        {
        	if(mouseX > GuiKeyBindingSelection.this.width * .6) return false;
        	if(GuiKeyBindingSelection.this.controlsScreen.mapping == null) return false; //how did u get here?
        	if (GuiKeyBindingSelection.this.controlsScreen.mappingButtons.contains(this.button))
        		GuiKeyBindingSelection.this.controlsScreen.mappingButtons.remove(this.button);
        	else
        		GuiKeyBindingSelection.this.controlsScreen.mappingButtons.add(this.button);
        	return true;
        }

        public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY)
        {
        }

        public void updatePosition(int p_192633_1_, int p_192633_2_, int p_192633_3_, float p_192633_4_)
        {
        }
        
        private boolean checkMappingConflict() {
        	if (GuiKeyBindingSelection.this.controlsScreen.mapping == null) return false;
        	for (VRButtonMapping mapping : mc.vrSettings.buttonMappings.values()) {
        		if (mapping == GuiKeyBindingSelection.this.controlsScreen.mapping) continue;
        		if (mapping.isGUIBinding() != GuiKeyBindingSelection.this.controlsScreen.mapping.isGUIBinding()) continue;
        		if (mapping.buttons.contains(this.button)) return true;
        	}
        	return false;
        }
    }
}
