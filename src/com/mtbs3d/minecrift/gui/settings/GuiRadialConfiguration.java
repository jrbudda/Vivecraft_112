package com.mtbs3d.minecrift.gui.settings;

import java.io.IOException;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.opengl.Display;

import com.mtbs3d.minecrift.gui.framework.BaseGuiSettings;
import com.mtbs3d.minecrift.gui.framework.GuiButtonEx;
import com.mtbs3d.minecrift.gui.framework.GuiSliderEx;
import com.mtbs3d.minecrift.gui.framework.GuiSmallButtonEx;
import com.mtbs3d.minecrift.provider.MCOpenVR;
import com.mtbs3d.minecrift.settings.VRSettings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;

public class GuiRadialConfiguration extends BaseGuiSettings
{
    // VIVE START - hide options not supported by tracked controller UI
    static VRSettings.VrOptions[] options = new VRSettings.VrOptions[] {
            VRSettings.VrOptions.RADIAL_MODE_HOLD,

    };
    // VIVE END - hide options not supported by tracked controller UI

    public GuiRadialConfiguration(GuiScreen guiScreen, VRSettings guivrSettings) {
        super( guiScreen, guivrSettings );
        screenTitle = "Radial Menu Configuration";
    }
    
    private String[] arr;
    private boolean isShift = false;
    private int selectedIndex = -1;
    private GuiRadialItemsList list;
    private boolean isselectmode = false;
    
    public void setKey(KeyBinding key) {
    	
    	if(key != null)
    		arr[selectedIndex] = key.getKeyDescription();
    	else
    		arr[selectedIndex] = "";

    	this.selectedIndex = -1;
    	this.isselectmode = false;
    	this.reinit = true;
		this.list.setEnabled(false);

    	if(!this.isShift)
    		mc.vrSettings.vrRadialItems = ArrayUtils.clone(arr);
    	else
    		mc.vrSettings.vrRadialItemsAlt = ArrayUtils.clone(arr);

    	mc.vrSettings.saveOptions();
    }
    
    
    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
    	list = new GuiRadialItemsList(this, mc);
    	this.buttonList.clear();

    	if(this.isselectmode) {
        	this.buttonList.add(new GuiButtonEx(251, this.width / 2 - 155 ,  this.height -25 ,150,20, "Clear"));
        	this.buttonList.add(new GuiButtonEx(250, this.width / 2 - 180  + 160, this.height -25,150,20, "Cancel"));
    	}else {
    		
        	this.buttonList.add(new GuiButtonEx(ID_GENERIC_DEFAULTS, this.width / 2 - 155 ,  this.height -25 ,150,20, "Reset To Defaults"));
        	this.buttonList.add(new GuiButtonEx(ID_GENERIC_DONE, this.width / 2 - 180  + 160, this.height -25,150,20, "Done"));
        	if(this.isShift)
        		this.buttonList.add(new GuiButton(201, this.width / 2 - 180+160, 32, 150, 20, "Main Set"));         
        	else
        		this.buttonList.add(new GuiButton(201, this.width / 2 - 180+160 ,32, 150, 20, "Alternate Set"));         

        	VRSettings.VrOptions[] buttons = options;

        	for (int var12 = 2; var12 < buttons.length + 2; ++var12)
        	{
        		VRSettings.VrOptions var8 = buttons[var12 - 2];
        		int width = this.width / 2 - 180 + var12 % 2 * 160;
        		int height =  32 * (var12 / 2);
        		if (var8 == VRSettings.VrOptions.DUMMY)
        			continue;
        		this.buttonList.add(new GuiSmallButtonEx(var8.returnEnumOrdinal(), width, height, var8, this.guivrSettings.getKeyBinding(var8)));         
        	}    

        	int numButts = 8;
        	int buttonwidthMin = 120;
        	int degreesPerButt = 360 / numButts;
        	int dist = 48;
        	int centerx = this.width / 2;
        	int centery = this.height / 2;
        	arr = ArrayUtils.clone(mc.vrSettings.vrRadialItems);
        	String[] alt = ArrayUtils.clone(mc.vrSettings.vrRadialItemsAlt);

        	if(this.isShift)
        		arr = alt;
    		
    	 	for (int i = 0; i < numButts; i++)
        	{
        		KeyBinding b = null;
        		for (KeyBinding kb: mc.gameSettings.keyBindings) {
    				if(kb.getKeyDescription().equalsIgnoreCase(arr[i]))
    					b = kb;				
    			}
        		
        		String str = ""; 
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

        		this.buttonList.add(new GuiButton(i, centerx + x - buttonwidth/2 , centery+y, buttonwidth, 20, str ));    
        	}
    	}
    	
   
    }    

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
    	if (par1GuiButton.enabled)
    	{
    		if (par1GuiButton.id == ID_GENERIC_DONE)
    		{
    			Minecraft.getMinecraft().vrSettings.saveOptions();
    			this.mc.displayGuiScreen(this.parentGuiScreen);
    		}
    		else if (par1GuiButton.id == ID_GENERIC_DEFAULTS)
    		{
    			this.guivrSettings.radialModeHold = true;
    			this.guivrSettings.vrRadialItems = this.guivrSettings.getRadialItemsDefault();
    			this.guivrSettings.vrRadialItemsAlt = new String[8];
    			
    			Minecraft.getMinecraft().vrSettings.saveOptions();
    			this.reinit = true;
    		}
    		else if(par1GuiButton.id == 201) {
    			this.isShift = !this.isShift;
    			this.reinit = true;
    		}
    		else if(par1GuiButton.id == 250) {
    			this.isselectmode = false;
    			this.reinit = true;
    		}
    		else if(par1GuiButton.id == 251) {
    			this.setKey(null);
    		}
    		else if (par1GuiButton instanceof GuiSmallButtonEx)
    		{
    			VRSettings.VrOptions num = VRSettings.VrOptions.getEnumOptions(par1GuiButton.id);
    			this.guivrSettings.setOptionValue(((GuiSmallButtonEx)par1GuiButton).returnVrEnumOptions(), 1);
    			par1GuiButton.displayString = this.guivrSettings.getKeyBinding(VRSettings.VrOptions.getEnumOptions(par1GuiButton.id));
    		}
    		else if(par1GuiButton.id < 200) {
    			this.selectedIndex = par1GuiButton.id;
    			this.isselectmode = true;
    			this.list.setEnabled(true);
    			this.reinit = true;
    		}
    	}
    }


    @Override
    public void drawScreen(int par1, int par2, float par3) {
        this.drawDefaultBackground();
    	
    	if(!MCOpenVR.isBound(MCOpenVR.radialMenu))
    		this.drawCenteredString(this.fontRenderer, "The radial menu is not currently bound to a controller button.", this.width / 2, this.height - 50, 13777215);
    	
    	if(this.isShift)
    		this.drawCenteredString(this.fontRenderer, "Hold (Gui Shift) with the radial menu open to switch to this set", this.width / 2, this.height - 36, 13777015);

    	if(this.isselectmode)
    		list.drawScreen(par1, par2, par3);    
    	
    	super.drawScreen(par1, par2, par3, false);

    }

    @Override
    protected String[] getTooltipLines(String displayString, int buttonId)
    {
        VRSettings.VrOptions e = VRSettings.VrOptions.getEnumOptions(buttonId);
        if( e != null )
            switch(e)
            {
            case RADIAL_MODE_HOLD:
                return new String[] {
                        "HOLD: Hold radial menu button, hover over",
                        "selection and release.",
                        "PRESS: Press radial menu button, click buttons,",
                        "press menu button again to dismiss."
                };
            default:
                return null;
            }
        else
            switch(buttonId)
            {
                case 201:
                    return new String[] {
                            "Switch between alternate and main set of items"
                    };
            default:
                return null;
            }
    }
    
    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {

    	boolean flag = false;

    	if (this.isselectmode) {
    		flag = this.list.mouseClicked(mouseX, mouseY, mouseButton);
    	} 

    	if (!flag)
    	{
    		try {
    			super.mouseClicked(mouseX, mouseY, mouseButton);
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    }
}
