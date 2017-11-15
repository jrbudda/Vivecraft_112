package com.mtbs3d.minecrift.gameplay;

import com.mtbs3d.minecrift.provider.MCOpenVR;
import com.mtbs3d.minecrift.provider.OpenVRPlayer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

import java.util.Random;


public class BackpackTracker {
	public boolean[] wasIn = new boolean[2];
	public int previousSlot = 0;
	
	public boolean isActive(EntityPlayerSP p){
		if(Minecraft.getMinecraft().vrSettings.seated)
			return false;
		if(p == null) return false;
		if(p.isDead) return false;
		if(p.isPlayerSleeping()) return false;
		if(Minecraft.getMinecraft().bowTracker.isDrawing) return false;
		return true;
	}

	
	private Vec3d down = new Vec3d(0, -1, 0);
	
	public void doProcess(Minecraft minecraft, EntityPlayerSP player){
		if(!isActive(player)) {
			return;
		}
		OpenVRPlayer provider = minecraft.vrPlayer;

		Vec3d hmdPos=provider.vrdata_room_pre.hmd.getPosition();

		for(int c=0; c<2; c++) { //just main for 1710, no dual wielding
			Vec3d controllerPos = provider.vrdata_room_pre.getController(c).getPosition();//.add(provider.getCustomControllerVector(c, new Vec3(0, 0, -0.1)));
			Vec3d controllerDir = provider.vrdata_room_pre.getController(c).getDirection();
			Vec3d hmddir = provider.vrdata_room_pre.hmd.getDirection();
			Vec3d delta = hmdPos.subtract(controllerPos);
			double dot = controllerDir.dotProduct(down);
			double dotDelta = delta.dotProduct(hmddir);
			boolean zone = ((Math.abs(hmdPos.y - controllerPos.y)) < 0.25) && //controller below hmd
					(dotDelta > 0); // behind head
			
			if (zone){
				if(!wasIn[c] && (dot > .6)){
					if(c==0){ //mainhand
						if(player.inventory.currentItem != 0){
							previousSlot = player.inventory.currentItem;
							player.inventory.currentItem = 0;	
						} else {
							player.inventory.currentItem = previousSlot;
							previousSlot = 0;
						}}
					else { //offhand
						ItemStack of = player.getHeldItemOffhand();
						ItemStack two = player.inventory.getStackInSlot(1);
						player.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, two);
						player.inventory.setInventorySlotContents(1, of);
					}
					MCOpenVR.triggerHapticPulse(c, 1500);
					wasIn[c] = true;
				}
			} else {
				wasIn[c] = false;
			}
		}
}

}
