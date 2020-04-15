package org.vivecraft.gameplay.trackers;

import org.vivecraft.api.VRData.VRDevicePose;
import org.vivecraft.gameplay.OpenVRPlayer;
import org.vivecraft.provider.MCOpenVR;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;


public class VehicleTracker extends Tracker {

	public VehicleTracker(Minecraft mc) {
		super(mc);
	}
	
    private float PreMount_World_Rotation;
    public Vec3d Premount_Pos_Room = new Vec3d(0, 0, 0);
	public float vehicleInitialRotation = 0;

	@Override
	public boolean isActive(EntityPlayerSP p){
		Minecraft mc = Minecraft.getMinecraft();
		if(p == null) return false;
		if(p.isDead) return false;
		return true;
	}

	@Override
	public void reset(EntityPlayerSP player) {
		minecartStupidityCounter = 2;
		super.reset(player);
	}

	public double getVehicleFloor(Entity vehicle, double original) {
		if(!(vehicle instanceof EntityMinecart))
			return original; //horses are fine.
		
		return vehicle.posY;
	}
	
	public int rotationCooldown = 0; 
	
	@Override
	public void doProcess(EntityPlayerSP player){
		if(!mc.isGamePaused())
		{ //do vehicle rotation, which rotates around a different point.

			if (dismountCooldown > 0) dismountCooldown--;
			if (rotationCooldown > 0) rotationCooldown--;
			
			if(mc.vrSettings.vehicleRotation && mc.player.isRiding() && rotationCooldown == 0){
				Entity e = mc.player.getRidingEntity();		
				rotationTarget = e.rotationYaw;

				if (e instanceof AbstractHorse && !mc.horseTracker.isActive(mc.player)) {
					AbstractHorse el = (AbstractHorse) e;
					rotationTarget = el.renderYawOffset;
					if (el.canBeSteered() && el.isHorseSaddled()){
						return;
					}
				}else if (e instanceof EntityLiving) {
					EntityLiving el = (EntityLiving) e; //this is just pigs in vanilla
					rotationTarget = el.renderYawOffset;
					if (el.canBeSteered()){
						return; 
					}
				}

				boolean smooth = true;
				float smoothIncrement = 5;

				if(e instanceof EntityMinecart){ //what a pain in my ass
				
					if(shouldMinecartTurnView((EntityMinecart) e)) {
						if(minecartStupidityCounter > 0) 
							minecartStupidityCounter--;
					}
					else
						minecartStupidityCounter = 3;

					rotationTarget =  getMinecartRenderYaw((EntityMinecart) e);

					if(minecartStupidityCounter > 0) { //do nothing
						vehicleInitialRotation = (float) rotationTarget;
					}

					double spd = mineCartSpeed((EntityMinecart) e);
					smoothIncrement = 200 * (float) (spd * spd);
					if (smoothIncrement < 5) smoothIncrement = 5;
	//				System.out.println(spd + " " + smoothIncrement);

				}
											
				float difference = mc.vrPlayer.rotDiff_Degrees((float) rotationTarget, vehicleInitialRotation);
				
				if (smooth) {
					if(difference > smoothIncrement) {
						difference = smoothIncrement;
					}

					if(difference < -smoothIncrement) {
						difference = -smoothIncrement;
					}
				}
		//		System.out.println("start " + vehicleInitialRotation + " end " + rotationTarget + " diff " + difference);
				
				//mc.vrPlayer.rotateOriginAround(difference,  e.getPositionVector());

				mc.vrSettings.vrWorldRotation += difference;
				mc.vrSettings.vrWorldRotation %= 360;
				MCOpenVR.seatedRot = mc.vrSettings.vrWorldRotation;

				vehicleInitialRotation -= difference;
				vehicleInitialRotation %= 360;


			} else {
				minecartStupidityCounter = 3;
				if(mc.player.isRiding()){		
					vehicleInitialRotation =  mc.player.getRidingEntity().rotationYaw;				
				}
			}
		}
		
	}
	
	private double rotationTarget = 0;
	
	public void onStartRiding(Entity vehicle, EntityPlayerSP player) {
		Minecraft mc = Minecraft.getMinecraft();
		
		PreMount_World_Rotation = mc.vrPlayer.vrdata_world_pre.rotation_radians;
		Vec3d campos = mc.vrPlayer.vrdata_room_pre.hmd.getPosition();	
		Premount_Pos_Room = new Vec3d(campos.x, 0, campos.z);
		dismountCooldown = 5;
		//mc.vrPlayer.snapRoomOriginToPlayerEntity(this, false);
		if(mc.vrSettings.vehicleRotation){
			float end = mc.vrPlayer.vrdata_world_pre.hmd.getYaw();
			float start = vehicle.rotationYaw % 360;
			
			vehicleInitialRotation = mc.vrSettings.vrWorldRotation;
			rotationCooldown = 2;
			
			if(vehicle instanceof EntityMinecart)
				return; // dont align player with minecart, it doesn't have a 'front'
			
			float difference = mc.vrPlayer.rotDiff_Degrees(start, end);
	    // 	System.out.println("OnStart " + start + " " + end + " " + difference);
        	mc.vrSettings.vrWorldRotation = (float) (Math.toDegrees(mc.vrPlayer.vrdata_world_pre.rotation_radians) + difference);
        	mc.vrSettings.vrWorldRotation %= 360;
        	MCOpenVR.seatedRot = mc.vrSettings.vrWorldRotation;

        }
	}
	
	public void onStopRiding(EntityPlayerSP player) {
        mc.swingTracker.disableSwing = 10;
        mc.sneakTracker.sneakCounter = 0;
        if(mc.vrSettings.vehicleRotation){
       	//I dont wanna do this anymore. 
        //I think its more confusing to get off the thing an not know where you're looking
        //	mc.vrSettings.vrWorldRotation = playerRotation_PreMount;
        //	MCOpenVR.seatedRot = playerRotation_PreMount;
        }
	}
	
	private int minecartStupidityCounter;
	
	private float getMinecartRenderYaw(EntityMinecart entity){	
		Vec3d spd = new Vec3d(entity.posX - entity.lastTickPosX, entity.posY - entity.lastTickPosY, entity.posZ - entity.lastTickPosZ);
		float spdyaw = (float)Math.toDegrees((Math.atan2(-spd.x, spd.z)));
		if(shouldMinecartTurnView(entity))
			return -180+spdyaw;
		else
			return vehicleInitialRotation;
	}
	
	private double mineCartSpeed(EntityMinecart entity) {
		Vec3d spd = new Vec3d(entity.motionX, 0, entity.motionZ);
		return spd.lengthVector();
	}
	
	private boolean shouldMinecartTurnView(EntityMinecart entity){	
		Vec3d spd = new Vec3d(entity.posX - entity.lastTickPosX, entity.posY - entity.lastTickPosY, entity.posZ - entity.lastTickPosZ);
		return spd.lengthVector() > 0.001;
	}
	
	public int dismountCooldown = 0;
	public boolean canRoomscaleDismount(EntityPlayerSP player) {
		 return player.moveForward ==0 && player.moveStrafing ==0 && player.isRiding() && player.getRidingEntity().onGround && dismountCooldown ==0;
	}
	
}
