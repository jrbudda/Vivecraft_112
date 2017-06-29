package com.mtbs3d.minecrift.gameplay;

import com.mtbs3d.minecrift.api.IRoomscaleAdapter;
import com.mtbs3d.minecrift.provider.MCOpenVR;
import com.mtbs3d.minecrift.settings.VRSettings;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class RowTracker {

	public boolean isActive(EntityPlayerSP p){
		if(Minecraft.getMinecraft().vrSettings.seated)
			return false;
		if(!Minecraft.getMinecraft().vrSettings.realisticRowEnabled)
			return false;
		if(p==null || p.isDead)
			return false;
		if(Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown()) //important
			return false;
		if(!(p.getRidingEntity() instanceof EntityBoat))
			return false;
		return true;
	}

	public boolean LOar, ROar, Foar;
	
	public void doProcess(Minecraft minecraft, EntityPlayerSP player){
		if(!isActive(player)) {
			LOar = false;
			ROar = false;
			return;
		}

		double c0move = MCOpenVR.controllerHistory[0].averageSpeed(0.5);
		double c1move = MCOpenVR.controllerHistory[1].averageSpeed(0.5);

		ROar = c0move > 0.9f;
		LOar = c1move > 0.9f;
		Foar = c0move > 0.4f && c1move > 0.4f;
		
		//TODO: Backwards paddlin'
		
	}

}
