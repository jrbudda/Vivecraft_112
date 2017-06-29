package com.mtbs3d.minecrift.gameplay;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.LayoutStyle;

import com.mtbs3d.minecrift.api.IRoomscaleAdapter;
import com.mtbs3d.minecrift.api.NetworkHelper;
import com.mtbs3d.minecrift.api.NetworkHelper.PacketDiscriminators;
import com.mtbs3d.minecrift.control.VRControllerButtonMapping;
import com.mtbs3d.minecrift.provider.MCOpenVR;
import com.mtbs3d.minecrift.render.PlayerModelController;
import com.mtbs3d.minecrift.utils.BlockWithData;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ClimbTracker {

	private boolean[] latched = new boolean[2];
	private boolean[] wasinblock = new boolean[2];
	private boolean[] wasbutton= new boolean[2];
	private boolean[] waslatched = new boolean[2];

	public List<BlockWithData> blocklist = new ArrayList<BlockWithData>();
	
	public byte serverblockmode = 0;
	
	private boolean gravityOverride=false;
	
	private Minecraft mc;

	public Vec3d[] latchStart = new Vec3d[]{new Vec3d(0,0,0), new Vec3d(0,0,0)};
	public double[] latchStartBodyY = new double[2];
	public int latchStartController = -1;
	boolean wantjump = false;
	AxisAlignedBB box[] = new AxisAlignedBB[2];
	AxisAlignedBB latchbox[] = new AxisAlignedBB[2];
	public ClimbTracker(Minecraft minecraft) {
		this.mc = minecraft;
	}
	
	public boolean isGrabbingLadder(){
		return latched[0] || latched[1];
	}

	public boolean isClaws(ItemStack i){
		if(i.isEmpty())return false;
		if(!i.hasDisplayName()) return false;
		if(i.getItem() != Items.SHEARS) return false;
		if(!(i.getTagCompound().getBoolean("Unbreakable"))) return false;
		return i.getDisplayName().equals("Climb Claws");
	}
	
	public boolean isActive(EntityPlayerSP p){
		if(mc.vrSettings.seated)
			return false;
		if(!mc.vrPlayer.getFreeMove() && !Minecraft.getMinecraft().vrSettings.simulateFalling)
			return false;
		if(!mc.vrSettings.realisticClimbEnabled)
			return false;
		if(p==null || p.isDead)
			return false;
		if(p.isRiding())
			return false;
		if(!isClimbeyClimbEquipped() && p.moveForward > 0 && Minecraft.getMinecraft().vrSettings.vrFreeMove ) 
			return false;
		return true;
	}
	   
    public boolean isClimbeyClimb(){
    	if(!this.isActive(mc.player)) return false;
    	return(isClimbeyClimbEquipped());
    }
    
    public boolean isClimbeyClimbEquipped(){
    	return(NetworkHelper.serverAllowsClimbey && mc.player.isClimbeyClimbEquipped());
    }
    
    private Random rand = new Random(); 
    boolean unsetflag;
    
	private boolean canstand(BlockPos bp, EntityPlayerSP p){
		AxisAlignedBB t = p.world.getBlockState(bp).getCollisionBoundingBox(p.world, bp);
		if(t == null || t.maxY == 0)
			return false;	
		BlockPos bp1 = bp.up();	
		AxisAlignedBB a = p.world.getBlockState(bp1).getCollisionBoundingBox(p.world, bp1);
		if(a != null && a.maxY>0)
			return false;
		BlockPos bp2 = bp1.up();	
		AxisAlignedBB a1 = p.world.getBlockState(bp2).getCollisionBoundingBox(p.world, bp2);
		if(a1 != null && a1.maxY>0)
			return false;		
		return true;
	}


	public void doProcess(EntityPlayerSP player){
		if(!isActive(player)) {
			latchStartController = -1;
			latched[0] = false;
			latched[1] = false;
			player.setNoGravity(false);
			return;
		}

		IRoomscaleAdapter provider = mc.roomScale;

		boolean[] button = new boolean[2];
		boolean[] inblock = new boolean[2];
		boolean[] allowed = new boolean [2];
	
		boolean nope = false;
		
		boolean jump = false;
		boolean ladder = false;
		for(int c=0;c<2;c++){
			Vec3d controllerPos=mc.roomScale.getControllerPos_World(c);
			BlockPos bp = new BlockPos(controllerPos);
			IBlockState bs = mc.world.getBlockState(bp);
			Block b = bs.getBlock();
			box[c] = bs.getCollisionBoundingBox(mc.world, bp);
			
			if(!mc.climbTracker.isClimbeyClimb()){	
				ladder = true;
				if(b instanceof BlockLadder|| b instanceof BlockVine){
					int meta = b.getMetaFromState(bs);
					Vec3d cpos = controllerPos.subtract(bp.getX(), bp.getY(), bp.getZ());
	
					if(meta == 2){
						inblock[c] = cpos.z > .9 && (cpos.x > .1 && cpos.x < .9);
					} else if (meta == 3){
						inblock[c] = cpos.z < .1 && (cpos.x > .1 && cpos.x < .9);
					} else if (meta == 4){
						inblock[c] = cpos.x > .9 && (cpos.z > .1 && cpos.z < .9);
					} else if (meta == 5){
						inblock[c] = cpos.x < .1 && (cpos.z > .1 && cpos.z < .9);
					}	
				//nah, hotboxes too big.	inblock[c] = box[c] != null && box[c].offset(bp).isVecInside(controllerPos);		
				} else {
					if(latchStart[c].subtract(controllerPos).lengthSquared() > 0.25) 
						inblock[c] = false;
					else
						inblock[c] = wasinblock[c];
				}
				
				button[c] = inblock[c];
				allowed[c] = inblock[c];
				
			} else { //Climbey
				//TODO whitelist by block type
				
				if(c == 0)
					button[c] = mc.gameSettings.keyBindAttack.isKeyDown();
				else 
					button[c] = mc.gameSettings.keyBindForward.isKeyDown() && !mc.player.onGround;

				inblock[c] = box[c] != null && box[c].offset(bp).contains(controllerPos);	
				allowed[c] = allowed(b, bs);
			}						
		
			waslatched[c] = latched[c];
			
			if(!button[c] && latched[c]){ //let go 
				latched[c] = false;
				if(c == 0)
					VRControllerButtonMapping.unpressKey(mc.gameSettings.keyBindAttack);
				else 
					VRControllerButtonMapping.unpressKey(mc.gameSettings.keyBindForward);

				jump = true;
			} 

			if(!latched[c] && !nope){ //grab
				if((!wasinblock[c] && inblock[c] && button[c]) ||
						(!wasbutton[c] && button[c] && inblock[c])){ //Grab
					if(allowed[c]){
						wantjump = false;
						latchStart[c] = mc.roomScale.getControllerPos_World(c);
						latchStartBodyY[c] = player.posY;
						latchStartController = c;
						latchbox[c] = box[c];
						latched[c] = true;
						if(c==0){
							latched[1] = false;
							nope = true;
						}
						else 
							latched[0] = false;
						MCOpenVR.triggerHapticPulse(c, 2000);
						mc.vrPlayer.blockDust(latchStart[c].x, latchStart[c].y, latchStart[c].z, 5, bs);

					}
				}
			}		

			wasbutton[c] = button[c];
			wasinblock[c] = inblock[c];

		}
		
		if(!latched[0] && !latched[1]){ 
			//check in case they let go with one hand, and other hand should take over.
			for(int c=0;c<2;c++){
				if(inblock[c] && button[c] && allowed[c]){
					latchStart[c] = mc.roomScale.getControllerPos_World(c);
					latchStartBodyY[c] = player.posY;
					latchStartController = c;
					latched[c] = true;
					latchbox[c] = box[c];
					wantjump = false;
					MCOpenVR.triggerHapticPulse(c, 2000);
					BlockPos bp = new BlockPos(latchStart[c]);
					IBlockState bs = mc.world.getBlockState(bp);
					mc.vrPlayer.blockDust(latchStart[c].x, latchStart[c].y, latchStart[c].z, 5, bs);
				}
			}
		}		
		
		
		if(!wantjump && !ladder) 
			wantjump = mc.gameSettings.keyBindJump.isKeyDown() && mc.jumpTracker.isClimbeyJumpEquipped();
		
		jump &= wantjump;
			
		if(latched[0] || latched[1] && !gravityOverride) {
			unsetflag = true;
			player.setNoGravity(true);
			gravityOverride=true;
		}
		
		if(!latched[0] && !latched[1] && gravityOverride){
			player.setNoGravity(false);
			gravityOverride=false;
		}

		if(!latched[0] && !latched[1] && !jump){
			if(player.onGround && unsetflag){
				unsetflag = false;
				VRControllerButtonMapping.unpressKey(mc.gameSettings.keyBindForward);
			}
			latchStartController = -1;
			return; //fly u fools
		}

		if((latched[0] || latched[1]) && rand.nextInt(20) == 10) {
			mc.player.addExhaustion(.1f);    
			BlockPos bp = new BlockPos(latchStart[latchStartController]);
			IBlockState bs = mc.world.getBlockState(bp);
			mc.vrPlayer.blockDust(latchStart[latchStartController].x, latchStart[latchStartController].y, latchStart[latchStartController].z, 1, bs);
		}

		
		Vec3d now = mc.roomScale.getControllerPos_World(latchStartController);
		Vec3d start = latchStart[latchStartController];
		
		Vec3d delta= now.subtract(start);
		
		if(wantjump) //bzzzzzz
			MCOpenVR.triggerHapticPulse(latchStartController, 200);
		
		if(!jump){
			player.motionY = -delta.y;
			if(!ladder){
				player.motionX = -delta.x;
				player.motionZ = -delta.z;
			}
			BlockPos b = new BlockPos(latchStart[latchStartController]);
			double yheight = latchStart[latchStartController].subtract(b.getX(), b.getY(), b.getZ()).y;
			if(!wantjump && latchbox[latchStartController] != null && yheight > latchbox[latchStartController].maxY*.8 && canstand(b, player)){		
				double hmd = mc.roomScale.getHMDPos_Room().y;
				Vec3d dir = mc.roomScale.getHMDDir_World().scale(0.5f);
				double con = mc.roomScale.getControllerPos_Room(latchStartController).y;
				if(con <= hmd/2){
					boolean ok=	mc.world.getCollisionBoxes(player, player.getEntityBoundingBox().offset(0,(latchbox[latchStartController].maxY + b.getY()) - player.posY ,0)).isEmpty();
					if(ok){
						if(ladder)
							player.setPosition(player.posX+dir.x, latchbox[latchStartController].maxY + b.getY(), player.posZ + dir.z);
						else
							player.setPosition(player.posX, latchbox[latchStartController].maxY + b.getY(), player.posZ);
						mc.vrPlayer.snapRoomOriginToPlayerEntity(player, false, false, 0);
					}
				}
			}

			player.fallDistance = 0;
			if(mc.isIntegratedServerRunning()) //handle server falling.
				for (EntityPlayerMP p : mc.getIntegratedServer().getPlayerList().getPlayers()) {
					if(p.getEntityId() == mc.player.getEntityId())
						p.fallDistance = 0;
				} else {
					CPacketCustomPayload pack =	NetworkHelper.getVivecraftClientPacket(PacketDiscriminators.CLIMBING, new byte[]{});
					if(mc.getConnection() !=null)
						mc.getConnection().sendPacket(pack);
				}

		} else { //jump!
			wantjump = false;
			Vec3d pl = player.getPositionVector().subtract(delta);

			Vec3d m = MCOpenVR.controllerHistory[latchStartController].netMovement(0.3);
			
			float limit = 1f;
			if(m.lengthVector() > limit) m = m.scale(limit/m.lengthVector());
			
			if (player.isPotionActive(MobEffects.JUMP_BOOST))
				m=m.scale((player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1.5));
			
			m=m.rotateYaw(mc.vrPlayer.worldRotationRadians);

			player.motionX=-m.x;
			player.motionY=-m.y;
			player.motionZ=-m.z;

			player.lastTickPosX = pl.x;
			player.lastTickPosY = pl.y;
			player.lastTickPosZ = pl.z;			
			pl = pl.addVector(player.motionX, player.motionY, player.motionZ);					
			player.setPosition(pl.x, pl.y, pl.z);
			mc.vrPlayer.snapRoomOriginToPlayerEntity(player, false, false, 0);	
			mc.player.addExhaustion(.3f);    

		}
	}

	private boolean allowed(Block b, IBlockState bs) {
		if(serverblockmode == 0) return true;
		if(serverblockmode == 1){
			for (BlockWithData blockWithData : blocklist) {
				if(blockWithData.matches(b,bs)) return true;
			}
			return false;
		}
		if(serverblockmode == 2){
			for (BlockWithData blockWithData : blocklist) {
				if(blockWithData.matches(b,bs)) return false;
			}
			return true;
		}
		return false; //how did u get here?
	}
}
