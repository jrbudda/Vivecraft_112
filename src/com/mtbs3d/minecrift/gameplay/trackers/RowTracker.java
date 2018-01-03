package com.mtbs3d.minecrift.gameplay.trackers;

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
		if(Minecraft.getMinecraft().bowTracker.isNotched())
			return false;
		return true;
	}

	public float LOar, ROar, Foar;
	
	public boolean isRowing(){
		return ROar + LOar + Foar > 0;
	}
	
	public void doProcess(Minecraft minecraft, EntityPlayerSP player){
		if(!isActive(player)) {
			LOar = 0;
			ROar = 0;
			Foar = 0;
			return;
		}

		double c0move = MCOpenVR.controllerHistory[0].averageSpeed(0.5);
		double c1move = MCOpenVR.controllerHistory[1].averageSpeed(0.5);

		float minspeed = 0.5f;
		float maxspeed = 2;
		
		ROar = (float) Math.max(c0move - minspeed,0);
		LOar = (float) Math.max(c1move - minspeed,0);
		Foar = ROar > 0 && LOar > 0 ? (ROar + LOar) / 2 : 0;
		if(Foar > maxspeed) Foar = maxspeed;
		if(ROar > maxspeed) ROar = maxspeed;
		if(LOar > maxspeed) LOar = maxspeed;

		//TODO: Backwards paddlin'
		
	}

}
