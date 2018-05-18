package com.mtbs3d.minecrift.gameplay.trackers;

import java.util.Random;

import com.mtbs3d.minecrift.gameplay.OpenVRPlayer;
import com.mtbs3d.minecrift.provider.MCOpenVR;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;

/**
 * Created by Hendrik on 02-Aug-16.
 */
public class EatingTracker extends Tracker{
	float mouthtoEyeDistance=0.0f;
	float threshold=0.25f;
	public boolean[] eating= new boolean[2];
	int eattime=2100;
	long eatStart;

	public EatingTracker(Minecraft mc) {
		super(mc);
	}

	public boolean isEating(){
		return eating[0] || eating[1];
	}
	
	public boolean isActive(EntityPlayerSP p){
		if(Minecraft.getMinecraft().vrSettings.seated)
			return false;
		if(p == null) return false;
		if(p.isDead) return false;
		if(p.isPlayerSleeping()) return false;
		if(p.getHeldItemMainhand() != null){
			EnumAction action=p.getHeldItemMainhand().getItemUseAction();
			if(	action == EnumAction.EAT || action == EnumAction.DRINK) return true;
		}
		if(p.getHeldItemOffhand() != null){
			EnumAction action=p.getHeldItemOffhand().getItemUseAction();
			if(	action == EnumAction.EAT || action == EnumAction.DRINK) return true;
		}
		return false;
	}

private Random r = new Random();

	@Override
	public void reset(EntityPlayerSP player) {
		eating[0]=false;
		eating[1]=false;
	}

	public void doProcess(EntityPlayerSP player){

		OpenVRPlayer provider = mc.vrPlayer;
		
		Vec3d hmdPos=provider.vrdata_room_pre.hmd.getPosition();
		Vec3d mouthPos=provider.vrdata_room_pre.getController(0).getCustomVector(new Vec3d(0,-mouthtoEyeDistance,0)).add(hmdPos);

		for(int c=0;c<2;c++){

			Vec3d controllerPos = MCOpenVR.controllerHistory[c].averagePosition(0.333).add(provider.vrdata_room_pre.getController(c).getCustomVector(new Vec3d(0,0,-0.1)));
			controllerPos = controllerPos.add(mc.vrPlayer.vrdata_room_pre.getController(c).getDirection().scale(0.1));
			
			if(mouthPos.distanceTo(controllerPos)<threshold){
				ItemStack is = c==0?player.getHeldItemMainhand():player.getHeldItemOffhand();
				if(is == null) continue;

				if(is.getItemUseAction() == EnumAction.DRINK){ //thats how liquid works.
					if(provider.vrdata_room_pre.getController(c).getCustomVector(new Vec3d(0,1,0)).y > 0) continue;
				}

				if(!eating[c]){
					if(	mc.playerController.processRightClick(player, player.world,c==0?EnumHand.MAIN_HAND:EnumHand.OFF_HAND)==EnumActionResult.SUCCESS){
						mc.entityRenderer.itemRenderer.resetEquippedProgress(c==0?EnumHand.MAIN_HAND:EnumHand.OFF_HAND);
						eating[c]=true;
						eatStart=Minecraft.getSystemTime();
					}
				}
				int crunchiness;
				if(is.getItemUseAction() == EnumAction.DRINK){
					crunchiness=0;
				}else
					crunchiness=2;

				long t = player.getItemInUseCount();
				if(t>0)
					if(t%5 <= crunchiness)
						MCOpenVR.triggerHapticPulse(c, 700 );

				if(Minecraft.getSystemTime()-eatStart > eattime)
					eating[c]=false;

			}else {
				eating[c]=false;
			}
		}
	}
}
