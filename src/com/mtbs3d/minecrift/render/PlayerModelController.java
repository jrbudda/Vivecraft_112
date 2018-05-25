package com.mtbs3d.minecrift.render;

import com.mtbs3d.minecrift.api.VRData;
import com.mtbs3d.minecrift.gameplay.OpenVRPlayer;
import com.mtbs3d.minecrift.provider.MCOpenVR;
import com.mtbs3d.minecrift.settings.VRSettings;
import com.mtbs3d.minecrift.utils.Angle;
import com.mtbs3d.minecrift.utils.Quaternion;
import com.mtbs3d.minecrift.utils.Utils;
import com.mtbs3d.minecrift.utils.Vector3;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Hendrik on 07-Aug-16.
 */
public class PlayerModelController {
	private final Minecraft mc;
	private Map<UUID, RotInfo> vivePlayers = new HashMap<UUID, RotInfo>();
	private Map<UUID, RotInfo> vivePlayersLast = new HashMap<UUID, RotInfo>();
	private Map<UUID, RotInfo> vivePlayersReceived = Collections.synchronizedMap(new HashMap<UUID, RotInfo>());
	

	static PlayerModelController instance;
	public static PlayerModelController getInstance(){
		if(instance==null)
			instance=new PlayerModelController();
		return instance;
	}
	
	private PlayerModelController() {
		this.mc = Minecraft.getMinecraft();
	}

	public static class RotInfo{ 
		public RotInfo(){
			
		}
		public boolean seated, reverse;
		public int hmd = 0;
		public Quaternion leftArmQuat, rightArmQuat, headQuat; 
		public Vec3d leftArmRot, rightArmRot, headRot; 
		public Vec3d leftArmPos, rightArmPos, Headpos;
	}
	
	private Random rand = new Random();

	public void Update(UUID uuid, byte[] hmddata, byte[] c0data, byte[] c1data, boolean localPlayer) {
		if (!localPlayer && mc.player.getUniqueID().equals(uuid))
			return; // Don't update local player from server packet
	
		Vec3d hmdpos = null, c0pos = null, c1pos = null;
		Quaternion hmdq = null, c0q = null, c1q = null;
		boolean seated=false, reverse = false;
		for (int i = 0; i <= 2; i++) {
			try {
				byte[]arr = null;
				switch(i){
				case 0:	arr = hmddata;
				break;
				case 1: arr = c0data;
				break;
				case 2: arr = c1data;
				break;
				}

				ByteArrayInputStream by = new ByteArrayInputStream(arr);
				DataInputStream da = new DataInputStream(by);

				boolean bool = false;
				if(arr.length >=29)
					bool = da.readBoolean();		

				float posx = da.readFloat();
				float posy = da.readFloat();
				float posz = da.readFloat();
				float rotw = da.readFloat();
				float rotx = da.readFloat();
				float roty = da.readFloat();
				float rotz = da.readFloat();

				da.close();
				
				switch(i){
				case 0:	
					if(bool){ //seated
						seated = true;
					}
					hmdpos = new Vec3d(posx, posy, posz);
					hmdq = new Quaternion(rotw, rotx, roty, rotz);
					break;
				case 1: 
					if(bool){ //reverse
						reverse = true;
					}
					c0pos = new Vec3d(posx, posy, posz);
					c0q = new Quaternion(rotw, rotx, roty, rotz);
					break;
				case 2: 
					if(bool){ //reverse
						reverse = true;
					}
					c1pos = new Vec3d(posx, posy, posz);
					c1q = new Quaternion(rotw, rotx, roty, rotz);
					break;
				}
				
			} catch (IOException e) {

			}
		}

		
		Vector3 shoulderR=new Vector3(0,-0.0f,0);

		//Vector3f sLV3f=MCOpenVR.hmdRotation.transform(new Vector3f((float) shoulderL.x,(float) shoulderL.y,(float) shoulderL.z));
		//Vector3f sRV3f=MCOpenVR.hmdRotation.transform(new Vector3f((float) shoulderR.x,(float) shoulderR.y,(float) shoulderR.z));

		Vector3 forward = new Vector3(0,0,-1);
		Vector3 dir = hmdq.multiply(forward);
		Vector3 dir2 = c0q.multiply(forward);
		Vector3 dir3 = c1q.multiply(forward);


				 
		//Quaternion qua=new Quaternion(Vector3.up(),yaw1);

		//shoulderL=shoulderL.multiply(qua.getMatrix());
		//shoulderR=shoulderR.multiply(qua.getMatrix());

//		Vec2f[] vecs=new Vec2f[2];
//
//		for (int i = 0; i <= 1; i++) {
//			Vec3d ctr= i == 0 ? c0pos : c1pos;
//
//			Vec3d offset= i==0 ? shoulderR.toVec3d() : shoulderR.toVec3d();
//			Vec3d vecCtr = ctr.subtract(hmdpos.add(offset)).normalize();
//			Vec3d def = new Vec3d(0,0,-1);
//			
//			Angle euler=Quaternion.createFromToVector(Utils.convertVector(def),Utils.convertVector(vecCtr)).toEuler();
//
//			double pitch = -euler.getPitch();
//			double yaw = euler.getYaw();
//			pitch-=90;
//			yaw=-yaw;
//
//			vecs[i] = new Vec2f((float)Math.toRadians(pitch), (float)Math.toRadians(yaw));
//		}
		
		RotInfo out = new RotInfo();
		out.reverse =reverse;
		out.seated = seated;
		if(donors.containsKey(uuid))out.hmd = donors.get(uuid);
		out.leftArmRot=new Vec3d(dir3.getX(), dir3.getY(), dir3.getZ());
		out.rightArmRot=new Vec3d(dir2.getX(), dir2.getY(), dir2.getZ());
		out.headRot = new Vec3d(dir.getX(), dir.getY(), dir.getZ());
		out.Headpos = hmdpos;
		out.leftArmPos = c1pos;
		out.rightArmPos = c0pos;
		out.leftArmQuat = c1q;
		out.rightArmQuat = c0q;
		out.headQuat = hmdq;
		
		if(out.hmd > 3 && rand.nextInt(10) < 4){
			Vector3 derp = dir.multiply(0.1f);
			Minecraft.getMinecraft().world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK,
					hmdpos.x+ ((double)this.rand.nextFloat() - 0.5D)*.02f,
					hmdpos.y - 0.8f + ((double)this.rand.nextFloat() - 0.5D)*.02f,
					hmdpos.z + ((double)this.rand.nextFloat()- 0.5D)*.02f,
					-derp.getX() + ((double)this.rand.nextFloat()- 0.5D)*.01f,((double)this.rand.nextFloat()- .05f)*.05f, -derp.getZ() + ((double)this.rand.nextFloat()- 0.5D)*.01f,
					new int[]{rand.nextInt(128)+128,rand.nextInt(128)+128,rand.nextInt(128)+128}
					);     
		}
		
		vivePlayersReceived.put(uuid, out);

	}
	
	public void Update(UUID uuid, byte[] hmddata, byte[] c0data, byte[] c1data) {
		Update(uuid, hmddata, c0data, c1data, false);
	}
	
	public void tick() {
		for (Map.Entry<UUID, RotInfo> entry : vivePlayers.entrySet()) {
			vivePlayersLast.put(entry.getKey(), entry.getValue());
		}
		for (Map.Entry<UUID, RotInfo> entry : vivePlayersReceived.entrySet()) {
			vivePlayers.put(entry.getKey(), entry.getValue());
		}
		for (Iterator<UUID> it = vivePlayers.keySet().iterator(); it.hasNext();) {
			UUID uuid = it.next();
			World world = Minecraft.getMinecraft().world;
			if (world != null && world.getPlayerEntityByUUID(uuid) == null) {
				it.remove();
				vivePlayersLast.remove(uuid);
				vivePlayersReceived.remove(uuid);
			}
		}
	}
	
	private Map<UUID, Integer> donors = new HashMap<UUID, Integer>();

	public void setHMD(UUID uuid, int level){
		donors.put(uuid, level);
	}
	
	public boolean HMDCHecked(UUID uuid){
		return donors.containsKey(uuid);
	}
	
	public RotInfo getRotationsForPlayer(UUID uuid){
		if (debug) uuid = mc.player.getUniqueID();
		RotInfo rot = vivePlayers.get(uuid);
		if (rot != null && vivePlayersLast.containsKey(uuid)) {
			RotInfo rotLast = vivePlayersLast.get(uuid);
			RotInfo rotLerp = new RotInfo();
			float pt = Minecraft.getMinecraft().timer.renderPartialTicks;
			rotLerp.reverse = rot.reverse;
			rotLerp.seated = rot.seated;
			rotLerp.hmd = rot.hmd;
			rotLerp.leftArmPos = Utils.vecLerp(rotLast.leftArmPos, rot.leftArmPos, pt);
			rotLerp.rightArmPos = Utils.vecLerp(rotLast.rightArmPos, rot.rightArmPos, pt);
			rotLerp.Headpos = Utils.vecLerp(rotLast.Headpos, rot.Headpos, pt);
			rotLerp.leftArmQuat = rot.leftArmQuat;//Utils.slerp(rotLast.leftArmQuat, rot.leftArmQuat, pt);
			rotLerp.rightArmQuat =rot.rightArmQuat;//Utils.slerp(rotLast.rightArmQuat, rot.rightArmQuat, pt);
			rotLerp.headQuat = rot.headQuat;//Utils.slerp(rotLast.headQuat, rot.headQuat, pt);
			Vector3 forward = new Vector3(0,0,-1);
			rotLerp.leftArmRot = Utils.vecLerp(rotLast.leftArmRot,Utils.convertToVec3d(rotLerp.leftArmQuat.multiply(forward)), pt);
			rotLerp.rightArmRot = Utils.vecLerp(rotLast.rightArmRot, Utils.convertToVec3d(rotLerp.rightArmQuat.multiply(forward)), pt);
			rotLerp.headRot = Utils.vecLerp(rotLast.headRot,Utils.convertToVec3d(rotLerp.headQuat.multiply(forward)), pt);
			return rotLerp;
		}
		return rot;
	}

	/**
	 * gets the {@link RotInfo} for both SinglePlayer and Multiplayer {@link EntityPlayer}s
	 * */
	public RotInfo getRotationFromEntity(EntityPlayer player){
		UUID playerId = player.getGameProfile().getId();
		if (mc.player.getUniqueID().equals(playerId)) {
			VRData data=Minecraft.getMinecraft().vrPlayer.vrdata_world_render;
			RotInfo rotInfo=new RotInfo();

			Quaternion quatLeft=new Quaternion(data.getController(1).getMatrix());
			Quaternion quatRight=new Quaternion(data.getController(0).getMatrix());
			Quaternion quatHmd=new Quaternion(data.hmd.getMatrix());

			rotInfo.headQuat=quatHmd;
			rotInfo.leftArmQuat=quatLeft;
			rotInfo.rightArmQuat=quatRight;
			rotInfo.seated=mc.vrSettings.seated;

			rotInfo.leftArmPos = data.getController(1).getPosition();
			rotInfo.rightArmPos = data.getController(0).getPosition();
			rotInfo.Headpos = data.hmd.getPosition(); 

			return rotInfo;

		} else {
			return PlayerModelController.getInstance().getRotationsForPlayer(playerId);
		}
	}

	public boolean debug = false;

	public boolean isTracked(UUID uuid){
		debug = false;
		if(debug) return true;
		return vivePlayers.containsKey(uuid);
	}
}
