package com.mtbs3d.minecrift.gameplay.trackers;

import com.mtbs3d.minecrift.gameplay.OpenVRPlayer;
import com.mtbs3d.minecrift.provider.MCOpenVR;
import com.mtbs3d.minecrift.settings.VRSettings;

import com.mtbs3d.minecrift.utils.Debug;
import com.mtbs3d.minecrift.utils.Quaternion;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBoat;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

public class RowTracker extends Tracker{

	public RowTracker(Minecraft mc) {
		super(mc);
	}

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


	Vec3d[] lastUWPs=new Vec3d[2];
	public double[] forces= new double[]{0,0};

	double transmissionEfficiency=0.9;
	
	public boolean isRowing(){
		return true;
	}


	public void doProcess(EntityPlayerSP player){


		EntityBoat boat=(EntityBoat) player.getRidingEntity();
		Quaternion boatRot = new Quaternion(boat.rotationPitch, -(boat.rotationYaw % 360f), 0).normalized();



		for (int paddle = 0; paddle <= 1 ; paddle++) {
			if(isPaddleUnderWater(paddle,boat)){
				Vec3d arm2Pad=getArmToPaddleVector(paddle,boat);
				Vec3d attach=getAttachmentPoint(paddle,boat);

				Vec3d underWaterPoint=attach.add(arm2Pad.normalize()).subtract(boat.getPositionVector());


				if(lastUWPs[paddle]!=null){
					Vec3d forceVector=lastUWPs[paddle].subtract(underWaterPoint); //intentionally reverse
					Vec3d boatMotion=new Vec3d(boat.motionX,boat.motionY,boat.motionZ);
					forceVector=forceVector.subtract(boatMotion);
					Vec3d forward= boatRot.multiply(new Vec3d(0,0,1));


					//scalar projection onto forward vector
					double force=forceVector.dotProduct(forward)*transmissionEfficiency/5;

					if ((force<0 && forces[paddle]>0) || (force>0 && forces[paddle]<0)){
						forces[paddle]=0;
					}else {
						forces[paddle] = Math.min(Math.max(force,-0.1),0.1);
					}
				}
				lastUWPs[paddle]=underWaterPoint;
			}else{
				forces[paddle]=0;
				lastUWPs[paddle]=null;
			}
		}
		
	}

	Vec3d getArmToPaddleVector(int paddle, EntityBoat boat){

		Vec3d attachAbs=getAttachmentPoint(paddle,boat);
		Vec3d armAbs = getAbsArmPos(paddle==0? 1 : 0);

		return attachAbs.subtract(armAbs);
	}




	Vec3d getAttachmentPoint(int paddle, EntityBoat boat){
		Vec3d attachmentPoint = new Vec3d((paddle==0? 9f: -9f) / 16f, (-5 + 15) / 16f, 3 / 16f); //values from ModelBoat
		Quaternion boatRot = new Quaternion(boat.rotationPitch, -(boat.rotationYaw % 360f), 0).normalized();

		return boat.getPositionVector().add(boatRot.multiply(attachmentPoint));
	}

	Vec3d getAbsArmPos(int side){
		Vec3d arm = MCOpenVR.controllerHistory[side].averagePosition(0.1);
		Quaternion worldRot = new Quaternion(0, VRSettings.inst.vrWorldRotation, 0);

		return OpenVRPlayer.get().roomOrigin.add(worldRot.multiply(arm));
	}

	boolean isPaddleUnderWater(int paddle, EntityBoat boat){

		Vec3d attachAbs=getAttachmentPoint(paddle,boat);
		Vec3d armToPaddle = getArmToPaddleVector(paddle,boat).normalize();

		BlockPos blockPos=new BlockPos(attachAbs.add(armToPaddle));

		return boat.world.getBlockState(blockPos).getMaterial().isLiquid();
	}

}
