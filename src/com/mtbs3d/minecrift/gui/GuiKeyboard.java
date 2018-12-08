package com.mtbs3d.minecrift.gui;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

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
import net.optifine.reflect.Reflector;

public class GuiKeyboard extends TwoHandedGuiScreen
{

	private boolean isShift = false;

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	public void initGui()
	{
		String arr = mc.vrSettings.keyboardKeys;
		String alt = mc.vrSettings.keyboardKeysShift;

		this.buttonList.clear();
		//this.buttonList.add(new GuiSmallButtonEx(301, this.width / 2 - 78, this.height / 6 - 14, "Hide Hud (F1): " + mc.gameSettings.hideGUI));

		if(this.isShift)
			arr = alt;

		int cols = 13;
		int rows = 4;
		int margin = 32;
		int spacing = 2;
		int bwidth = 25;
		double tmp = (double)arr.length() / (double)cols;
		
		if (Math.floor(tmp) == tmp)
			rows = (int) tmp;
		else
			rows = (int) (tmp+1);	
		
		for (int r=0; r<rows;r++) {
			for (int i=0; i<cols;i++) {
				int c = r*cols+i;
				char x = 32;
				if (c<arr.length()) {
					x = arr.charAt(c);
				}
				GuiButton butt = new GuiButton(c, margin + i*(bwidth+spacing), margin + r*(20+spacing), bwidth, 20, String.valueOf(x));
				this.buttonList.add(butt);
			}
		}
		this.buttonList.add(new GuiButton(201, 0, margin + 3* (20 + spacing), 30, 20, "Shift"));
		this.buttonList.add(new GuiButton(199, margin + 4 * (bwidth+spacing), margin + rows * (20+spacing), 5 * (bwidth+spacing), 20, " "));
		this.buttonList.add(new GuiButton(202, cols * (bwidth+spacing) + margin, margin , 35 , 20, "BKSP"));
		this.buttonList.add(new GuiButton(203, cols * (bwidth+spacing) + margin, margin + 2*(20 + spacing) , 35 , 20, "ENTER"));
		this.buttonList.add(new GuiButton(204, 0, margin + (20 + spacing), 30, 20, "TAB"));

	}

	public void setShift(boolean shift) {
		if(shift != this.isShift) {
			this.isShift = shift;
			this.reinit = true;
		}
	}
	
	private void pressKey(String c) {
		if (mc.currentScreen != null && !mc.vrSettings.alwaysSimulateKeyboard) { // experimental, needs testing
			try {
				for (char ch : c.toCharArray()) {
					int[] codes = KeyboardSimulator.getLWJGLCodes(ch);
					int code = codes.length > 0 ? codes[codes.length - 1] : 0;
					if (InputInjector.isSupported()) 
						InputInjector.typeKey(code, ch);
					else 
						mc.currentScreen.keyTypedPublic(ch, code);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			if(Display.isActive())
				KeyboardSimulator.type(c); //holy shit it works.
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
				pressKey(par1GuiButton.displayString);
			} else {
				if(par1GuiButton.id == 201) {
					setShift(!this.isShift);
				}
				else if(par1GuiButton.id == 202) {
					pressKey(Character.toString((char) 8));
				}
				else if(par1GuiButton.id == 203) {
					pressKey(Character.toString((char) 13));
				}
				else if(par1GuiButton.id == 204) {
					pressKey(Character.toString((char) 9));
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
    	this.drawCenteredString(this.fontRenderer, "Keyboard", this.width / 2, 2, 16777215);
    	
    	if(!Display.isActive() && (mc.currentScreen == null || mc.vrSettings.alwaysSimulateKeyboard))
    		this.drawCenteredString(this.fontRenderer, "Warning: Desktop window needs focus!", this.width / 2, this.height - 25, 13777215);

    	super.drawScreen(0, 0, partialTicks);

    }    

}
