package com.mtbs3d.minecrift.gameplay.trackers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import com.mtbs3d.minecrift.gameplay.OpenVRPlayer;
import net.minecraft.entity.player.EntityPlayer;

/**
 * register in {@link OpenVRPlayer}
 * */
public abstract class Tracker {
	Minecraft mc;
	public Tracker(Minecraft mc){
		this.mc=mc;
	}

	public abstract boolean isActive(EntityPlayerSP player);
	public abstract void doProcess(EntityPlayerSP player);
	public void reset(EntityPlayerSP player){}

	public EntryPoint getEntryPoint(){return EntryPoint.LIVING_UPDATE;}

	public enum EntryPoint{
		LIVING_UPDATE, SPECIAL_ITEMS
	}
}
