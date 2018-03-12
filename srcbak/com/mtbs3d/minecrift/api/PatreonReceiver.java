package com.mtbs3d.minecrift.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mtbs3d.minecrift.render.PlayerModelController;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.src.Config;
import net.minecraft.src.IFileDownloadListener;

public class PatreonReceiver implements IFileDownloadListener
{
    private String player = null;
    private EntityPlayer p;
    
    public PatreonReceiver(String p_i65_1_, EntityPlayer p)
    {
        this.player = p_i65_1_;
        this.p = p;
    }

    public void fileDownloadFinished(String p_fileDownloadFinished_1_, byte[] p_fileDownloadFinished_2_, Throwable p_fileDownloadFinished_3_)
    {
        if (p_fileDownloadFinished_2_ != null)
        {
            try
            {         
            	String s = new String(p_fileDownloadFinished_2_, "ASCII");
            	String lines[] = s.split("\\r?\\n");
            	for (String string : lines) {
            		try{
    					String[] bits = string.split(":");
    					if(bits[0].equalsIgnoreCase(player))
    		            	PlayerModelController.getInstance().setHMD(p.getUniqueID(), Integer.parseInt(bits[1]));      
            		} catch(Exception e){continue;}
				}
            }
            catch (Exception exception)
            {
                Config.dbg("Error parsing data: " + p_fileDownloadFinished_1_ + ", " + exception.getClass().getName() + ": " + exception.getMessage());
            }
        }
    }
}
