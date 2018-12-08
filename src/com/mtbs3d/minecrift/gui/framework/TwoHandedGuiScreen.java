package com.mtbs3d.minecrift.gui.framework;

import java.io.IOException;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.mtbs3d.minecrift.control.ControllerType;
import com.mtbs3d.minecrift.provider.MCOpenVR;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.optifine.reflect.Reflector;

public abstract class TwoHandedGuiScreen extends GuiScreen
{
	public float cursorX1, cursorY1;
	public float cursorX2, cursorY2;
	private int lastHoveredButtonId1 = -1, lastHoveredButtonId2 = -1;
	protected boolean reinit;
	
	@Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		if (mouseButton == 0)
		{
			for (int i = 0; i < this.buttonList.size(); ++i)
			{
				GuiButton guibutton = this.buttonList.get(i);

				if (guibutton.mousePressed(this.mc, mouseX, mouseY))
				{
					this.selectedButton = guibutton;
					guibutton.playPressSound(this.mc.getSoundHandler());
					
					if((int)((int)cursorX2 * this.width / this.mc.displayWidth) == mouseX)
						MCOpenVR.triggerHapticPulse(ControllerType.RIGHT, 2000);
					else 
						MCOpenVR.triggerHapticPulse(ControllerType.LEFT, 2000);

					this.actionPerformed(guibutton);
				}
			}
		}
	}
	
	@Override
    public void handleInput() throws IOException
    {
        if (Mouse.isCreated())
        {
            while (Mouse.next())
            {
            	this.mouseHandled = false;
                this.handleMouseInput();
                
            }
        }

        if (Keyboard.isCreated())
        {
            while (Keyboard.next())
            {
            	this.keyHandled = false;
                this.handleKeyboardInput();
            }
        }
    }
	
	@Override
    protected void drawHoveringText(List<String> textLines, int x, int y, FontRenderer font)
    {
    	if (!textLines.isEmpty())
        {
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int i = 0;

            for (String s : textLines)
            {
                int j = this.fontRenderer.getStringWidth(s);

                if (j > i)
                {
                    i = j;
                }
            }

            int l1 = x + 12;
            int i2 = y - 12;
            int k = 8;

            if (textLines.size() > 1)
            {
                k += 2 + (textLines.size() - 1) * 10;
            }

            if (l1 + i > this.width)
            {
                l1 -= 28 + i;
            }

            if (i2 + k + 6 > this.height)
            {
                i2 = this.height - k - 6;
            }

            this.zLevel = 300.0F;
            this.itemRender.zLevel = 300.0F;
            int l = -267386864;
            this.drawGradientRect(l1 - 3, i2 - 4, l1 + i + 3, i2 - 3, -267386864, -267386864);
            this.drawGradientRect(l1 - 3, i2 + k + 3, l1 + i + 3, i2 + k + 4, -267386864, -267386864);
            this.drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 + k + 3, -267386864, -267386864);
            this.drawGradientRect(l1 - 4, i2 - 3, l1 - 3, i2 + k + 3, -267386864, -267386864);
            this.drawGradientRect(l1 + i + 3, i2 - 3, l1 + i + 4, i2 + k + 3, -267386864, -267386864);
            int i1 = 1347420415;
            int j1 = 1344798847;
            this.drawGradientRect(l1 - 3, i2 - 3 + 1, l1 - 3 + 1, i2 + k + 3 - 1, 1347420415, 1344798847);
            this.drawGradientRect(l1 + i + 2, i2 - 3 + 1, l1 + i + 3, i2 + k + 3 - 1, 1347420415, 1344798847);
            this.drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 - 3 + 1, 1347420415, 1347420415);
            this.drawGradientRect(l1 - 3, i2 + k + 2, l1 + i + 3, i2 + k + 3, 1344798847, 1344798847);

            for (int k1 = 0; k1 < textLines.size(); ++k1)
            {
                String s1 = textLines.get(k1);
                this.fontRenderer.drawStringWithShadow(s1, (float)l1, (float)i2, -1);

                if (k1 == 0)
                {
                    i2 += 2;
                }

                i2 += 10;
            }

            this.zLevel = 0.0F;
            this.itemRender.zLevel = 0.0F;
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }
	
	@Override
    public void setWorldAndResolution(Minecraft mc, int width, int height)
    {
    	this.mc = mc;
    	this.itemRender = mc.getRenderItem();
    	this.fontRenderer = mc.fontRenderer;
    	this.width = width;
    	this.height = height;
    	this.buttonList.clear();
    	this.initGui();
    }

	@Override
    public void drawDefaultBackground()
    {
        // We're not using for mod GUIs so no background needed
    }

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		
        if (reinit)
        {
            initGui();
            reinit = false;
        }
		
        int mX1 = (int) (cursorX1 * this.width / this.mc.displayWidth);
        int mY1 = (int) (cursorY1 * this.height / this.mc.displayHeight);
        int mX2 = (int) (cursorX2 * this.width / this.mc.displayWidth);
        int mY2 = (int) (cursorY2 * this.height / this.mc.displayHeight);

        int hoveredButtonId1 = -1, hoveredButtonId2 = -1;
        for (int i = 0; i < this.buttonList.size(); ++i)
        {
        	GuiButton butt = (GuiButton)this.buttonList.get(i);
        	boolean buttonhovered1 = mX1 >= butt.x && mY1 >= butt.y && mX1 < butt.x + butt.getButtonWidth() && mY1 < butt.y + 20;
        	boolean buttonhovered2 = mX2 >= butt.x && mY2 >= butt.y && mX2 < butt.x + butt.getButtonWidth() && mY2 < butt.y + 20;
        	if(buttonhovered1)
        		butt.drawButton(this.mc, mX1, mY1, partialTicks);
        	else
        		butt.drawButton(this.mc, mX2, mY2, partialTicks);
        	
        	if (buttonhovered1)
        		hoveredButtonId1 = butt.id;
        	if (buttonhovered2)
        		hoveredButtonId2 = butt.id;
        }

        if (hoveredButtonId1 == -1) {
        	lastHoveredButtonId1 = -1;
        } else if (lastHoveredButtonId1 != hoveredButtonId1) {
			MCOpenVR.triggerHapticPulse(ControllerType.LEFT, 300);
    		lastHoveredButtonId1 = hoveredButtonId1;
    	}
        if (hoveredButtonId2 == -1) {
        	lastHoveredButtonId2 = -1;
        } else if (lastHoveredButtonId2 != hoveredButtonId2) {
			MCOpenVR.triggerHapticPulse(ControllerType.RIGHT, 300);
    		lastHoveredButtonId2 = hoveredButtonId2;
    	}

        for (int j = 0; j < this.labelList.size(); ++j)
        {
            ((GuiLabel)this.labelList.get(j)).drawLabel(this.mc, mouseX, mouseY);
        }
        
    	this.mc.ingameGUI.drawMouseMenuQuad(mX1, mY1);
    	this.mc.ingameGUI.drawMouseMenuQuad(mX2, mY2);

	}
}
