package com.mtbs3d.minecrift.gui;

import org.lwjgl.input.Mouse;

import com.mtbs3d.minecrift.gui.framework.GuiSmallButtonEx;
import com.mtbs3d.minecrift.utils.InputInjector;
import com.mtbs3d.minecrift.utils.KeyboardSimulator;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.resources.I18n;

public class GuiKeyboard extends GuiScreen
{

	public float cursorX, cursorY;
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
		this.buttonList.add(new GuiButton(201, margin, margin + rows * (20+spacing), 30, 20, "Shift"));
		this.buttonList.add(new GuiButton(199, margin + 4 * (bwidth+spacing), margin + rows * (20+spacing), 5 * (bwidth+spacing), 20, " "));
		this.buttonList.add(new GuiButton(202, cols * (bwidth+spacing) + margin, margin , 35 , 20, "BKSP"));
		this.buttonList.add(new GuiButton(203, cols * (bwidth+spacing) + margin, margin + 2*(20 + spacing) , 35 , 20, "ENTER"));

	}

	public void setShift(boolean shift) {
		if(shift != this.isShift) {
			this.isShift = shift;
			this.initGui();
		}
	}
	
	private void pressKey(String c) {
		if (mc.currentScreen != null && !mc.vrSettings.alwaysSimulateKeyboard) { // experimental, needs testing
			try {
				for (char ch : c.toCharArray()) {
					int[] codes = KeyboardSimulator.getLWJGLCodes(ch);
					int code = codes.length > 0 ? codes[codes.length - 1] : 0;
					if (InputInjector.isSupported()) InputInjector.typeKey(code, ch);
					else mc.currentScreen.keyTypedPublic(ch, code);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
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
			}
		}
	}
	
    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
    	this.drawDefaultBackground();
    	this.drawCenteredString(this.fontRenderer, "Keyboard", this.width / 2, 0, 16777215);
        int mX = (int) (cursorX * this.width / this.mc.displayWidth);
        int mY = (int) (cursorY * this.height / this.mc.displayHeight);
    	super.drawScreen(mX, mY, partialTicks);
    	this.mc.ingameGUI.drawMouseMenuQuad(mX, mY);

    }
    
	//    @Override
	//    protected String[] getTooltipLines(String displayString, int buttonId)
	//    {
	//        VRSettings.VrOptions e = VRSettings.VrOptions.getEnumOptions(buttonId);
	//        if( e != null )
	//            switch(e)
	//            {
	//                case HUD_OPACITY:
	//                    return new String[] {
	//                            "How transparent to draw the in-game HUD and UI",
	//                    };
	//                case HUD_SCALE:
	//                return new String[] {
	//                        "Relative size HUD takes up in field-of-view",
	//                        "  The units are just relative, not in degrees",
	//                        "  or a fraction of FOV or anything"
	//                };
	//                case HUD_PITCH:
	//                    return new String[] {
	//                            "The vertical offset of the HUD, in degrees.",
	//                            "  Negative values are down, positive up."
	//                    };
	//                case HUD_YAW:
	//                    return new String[] {
	//                            "The horizontal offset of the HUD, in degrees.",
	//                            "  Negative values are to the left, positive to",
	//                            "  the right."
	//                    };
	//                case HUD_DISTANCE:
	//                    return new String[] {
	//                            "Distance the floating HUD is drawn in front of your body",
	//                            "  The relative size of the HUD is unchanged by this",
	//                            "  Distance is in meters (though isn't obstructed by blocks)"
	//                    };
	//                case HUD_OCCLUSION:
	//                    return new String[] {
	//                            "Specifies whether the HUD is occluded by closer objects.",
	//                            "  ON:  The HUD will be hidden by closer objects. May",
	//                            "       be hidden completely in confined environments!",
	//                            "  OFF: The HUD is always visible. Stereo depth issues",
	//                            "       may be noticable."
	//                    };
	//                case MENU_ALWAYS_FOLLOW_FACE:
	//                    return new String[] {
	//                            "Specifies when the main menu follows your look direction.",
	//                            "  SEATED: The main menu will only follow in seated mode.",
	//                            "  ALWAYS The main menu will always follow."
	//                    };
	//                case RENDER_MENU_BACKGROUND:
	//                    return new String[] {
	//                            "Specifies whether the in game GUI menus have a ",
	//                            "semi-transparent background or not.",
	//                            "  ON:  Semi-transparent background on in-game menus.",
	//                            "  OFF: No background on in-game menus."
	//                    };
	//                case HUD_LOCK_TO:
	//                    return new String[] {
	//                            "Specifies to which orientation the HUD is locked to.",
	//                            "  HAND:  The HUD will appear just above your off-hand",
	//                            "  HEAD:  The HUD will always appear in your field of view",
	//                            "straight ahead",
	//                            "  WRIST:  The HUD will appear on the inside of your off-hand",
	//                            "arm. It will 'pop out' when looked at."
	//                    };
	//                case OTHER_HUD_SETTINGS:
	//                    return new String[] {
	//                            "Configure Crosshair and overlay settings."
	//                    };
	//                case TOUCH_HOTBAR:
	//                    return new String[] {
	//                            "If enabled allow you to touch the hotbar with",
	//                            "your main hand to select an item."
	//                    };
	//                case AUTO_OPEN_KEYBOARD:
	//                    return new String[] {
	//                    		"If disabled, SteamVR keyboard will only open when you",
	//                    		"click a text field, or if a text field can't lose focus.",
	//                    		"",
	//                            "If enabled, SteamVR keyboard will open automatically",
	//                            "any time a text field comes into focus. Enabling this will",
	//                            "cause it to open in unwanted situations with mods."
	//                    };
	//                default:
	//                    return null;
	//            }
	//        else
	//            switch(buttonId)
	//            {
	//                case 300:
	//                    return new String[] {
	//                            "Configure which controller buttons perform",
	//                            "  which mouse or keyboard function while ",
	//                            "  a GUI is visible."
	//                    };
	//                default:
	//                    return null;
	//            }
	//    }
}
