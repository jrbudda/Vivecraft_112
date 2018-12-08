package com.mtbs3d.minecrift.gui;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.mtbs3d.minecrift.control.VRButtonMapping;
import com.mtbs3d.minecrift.gui.framework.GuiSmallButtonEx;
import com.mtbs3d.minecrift.gui.framework.TwoHandedGuiScreen;
import com.mtbs3d.minecrift.utils.InputInjector;
import com.mtbs3d.minecrift.utils.KeyboardSimulator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.optifine.reflect.Reflector;

public class GuiRadial extends TwoHandedGuiScreen
{
	private boolean isShift = false;

	String[] arr;
	
	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	public void initGui()
	{
		arr = mc.vrSettings.vrRadialItems;
		String[] alt = mc.vrSettings.vrRadialItemsAlt;

		this.buttonList.clear();

		int numButts = 8;
    	int buttonwidthMin = 120;
    	int degreesPerButt = 360 / numButts;
    	int dist = 48;
    	int centerx = this.width / 2;
    	int centery = this.height / 2;
    	
    	if(this.isShift)
    		arr = alt;

    	for (int i = 0; i < numButts; i++)
    	{
    		KeyBinding b = null;
    		
    		for (KeyBinding kb: mc.gameSettings.keyBindings) {
				if(kb.getKeyDescription().equalsIgnoreCase(arr[i]))
					b = kb;				
			}
    		
    		String str = "?"; 
    		
    		if(b!=null)		
    			str = I18n.format(b.getKeyDescription());
    		
    		int buttonwidth =  Math.max(buttonwidthMin, fontRenderer.getStringWidth(str));
    		
    		int x=0,y=0;

    		if(i==0) {
    			x = 0;
    			y = -dist; 				
    		}
    		else if (i==1) {
    			x = buttonwidth/2 + 8;
    			y = -dist/2;
    		}
    		else if (i==2) {
    			x = buttonwidth/2 + 32;
    			y = 0; 	
    		}
    		else if (i==3) {
    			x = buttonwidth/2 + 8;
    			y = dist/2;      	
    		}
    		else if (i==4) {
    			x = 0;
    			y = dist; 	
    		}
    		else if (i==5) {
    			x = -buttonwidth/2 - 8;
    			y = dist/2;      	
    		}
    		else if (i==6) {
    			x = -buttonwidth/2 - 32;
    			y = 0; 	
    		}
    		else if (i==7) {
    			x = -buttonwidth/2 - 8;
    			y = -dist/2;
    		}

    	if(str != "?") //only draw mapped buttons
    		this.buttonList.add(new GuiButton(i, centerx + x - buttonwidth/2 , centery+y-10, buttonwidth, 20, str ));    
    	
    	}

	}

	public void setShift(boolean shift) {
		if(shift != this.isShift) {
			this.isShift = shift;
			this.initGui();
		}
	}
	
	/**
	 * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
	 */
	protected void actionPerformed(GuiButton par1GuiButton)
	{
		if (par1GuiButton.enabled)
		{
			if (par1GuiButton.id < 200 )
			{
				VRButtonMapping vb = mc.vrSettings.buttonMappings.get(arr[par1GuiButton.id]);
				if(vb!=null) {
					vb.press();
					vb.scheduleUnpress(2);
				}
			} else {
				if(par1GuiButton.id == 201) {
					setShift(!this.isShift);
				}
			}
		}
	}
	
    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
    	this.drawDefaultBackground();
    	
    	super.drawScreen(0, 0, partialTicks);

    }    

}
