package org.vivecraft.gameplay.trackers;

import java.util.HashSet;

import org.vivecraft.api.NetworkHelper;
import org.vivecraft.control.ControllerType;
import org.vivecraft.provider.MCOpenVR;

import net.minecraft.block.Block;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;

import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class InteractTracker extends Tracker{

	//public boolean[] bukkit= new boolean[2];
	public int hotbar = -1;

	public InteractTracker(Minecraft mc) {
		super(mc);
	}

	public boolean isActive(EntityPlayerSP p){
		if(mc.playerController == null) return false;
		if(p == null) return false;
		if(p.isDead) return false;
		if(p.isPlayerSleeping()) return false;
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.vrSettings.seated)
			return false;
		if(p.isActiveItemStackBlocking() && hotbar < 0){
			return false; 
		}
		if (mc.bowTracker.isNotched())
			return false;
		return true;    
	}

//	public BlockRayTraceResult[] inBlockHit = new BlockRayTraceResult[2];
//	BlockPos[] inBlockPos = new BlockPos[2];
//	Entity[] inEntity = new Entity[2];
//	private EntityRayTraceResult[] inEntityHit = new EntityRayTraceResult[2];
	boolean[] active = new boolean[2];
	boolean[] wasactive = new boolean[2];

	@Override
	public void reset(EntityPlayerSP player) {
		for(int c =0 ;c<2;c++){
//			inBlockPos[c] = null;
//			inBlockHit[c] = null;
//			inEntity[c] = null;
//			inEntityHit[c] = null;
			active[c] = false;
			MCOpenVR.getInputAction(MCOpenVR.keyVRInteract).setEnabled(ControllerType.values()[c], false);
		}
	}

	private HashSet<Class> rightClickable = null;

	@SuppressWarnings("unused")
	public void doProcess(EntityPlayerSP player){ //on tick

//		if(rightClickable == null) {
//			//compile a list of blocks that explicitly declare OnBlockActivated (right click)
//			rightClickable = new HashSet<Class>();
//			for (Object b : Registry.BLOCK) {
//				Class c = b.getClass();
//				try { // constructor throws an exception if method doesn't exist
//					ReflectionMethod r = new MCReflection.ReflectionMethod(c, MCReflection.BlockState_OnBlockActivated, BlockState.class, World.class, BlockPos.class, PlayerEntity.class, Hand.class, BlockRayTraceResult.class);
//					rightClickable.add(c);
//				} catch (Throwable e) {
//				}
//				c = c.getSuperclass();
//				try {
//					ReflectionMethod r = new MCReflection.ReflectionMethod(c, MCReflection.BlockState_OnBlockActivated, BlockState.class, World.class, BlockPos.class, PlayerEntity.class, Hand.class, BlockRayTraceResult.class);
//					rightClickable.add(c);
//				} catch (Throwable e) {
//				}
//			}
//			rightClickable.remove(Block.class);
//			rightClickable.remove(AbstractBlock.class);
//			rightClickable.remove(AbstractBlock.AbstractBlockState.class);
//		}

		Vec3d forward = new Vec3d(0,0,-1);

		reset(player);

		for(int c =0 ;c<2;c++){

			if(c == 0) {
				if(hotbar >= 0) {
					active[c] = true;
				}
			}

//			Vec3d hmdPos = mc.vrPlayer.vrdata_world_pre.getHeadPivot();
//			Vec3d handPos = mc.vrPlayer.vrdata_world_pre.getController(c).getPosition();
//			Vec3d handDirection = mc.vrPlayer.vrdata_world_pre.getHand(c).getCustomVector(forward);
//			ItemStack is = player.getHeldItem(c==0?Hand.MAIN_HAND:Hand.OFF_HAND);
//			Item item = null;
//
//			if(!active[c]) {
//
//				int bx = (int) MathHelper.floor(handPos.x);
//				int by = (int) MathHelper.floor(handPos.y);
//				int bz = (int) MathHelper.floor(handPos.z);
//
//				Vec3d extWeapon = new Vec3d(
//						handPos.x + handDirection.x * (-.1),
//						handPos.y + handDirection.y * (-.1),
//						handPos.z + handDirection.z * (-.1));
//
//				AxisAlignedBB weaponBB = new AxisAlignedBB(handPos, extWeapon);
//
//
//				inEntityHit[c] = ProjectileHelper.rayTraceEntities(mc.getRenderViewEntity(), hmdPos, handPos, weaponBB, (e) ->
//				{
//					return !e.isSpectator() && e.canBeCollidedWith()  && !(e == mc.getRenderViewEntity().getRidingEntity());
//				}, 0);
//
//				if(inEntityHit[c]!=null) {
//					Entity hitEntity = inEntityHit[c].getEntity();
//					inEntity[c] = hitEntity;
//					active[c] = true;
//				}
//			}

//			if(!active[c]) {
//				BlockPos bp =null;
//				bp = new BlockPos(handPos);
//				BlockState block = mc.world.getBlockState(bp);
//				//	Material material = block.getMaterial();
//
//				BlockRayTraceResult hit = block.getRenderShapeTrue(mc.world, bp).rayTrace(hmdPos, handPos, bp);
//				inBlockPos[c] = bp;
//				inBlockHit[c] = hit;		     
//
//				active[c] = hit !=null && (rightClickable.contains(block.getBlock().getClass()) || 
//						rightClickable.contains(block.getBlock().getClass().getSuperclass()));
//
//				bukkit[c] = false;
//				if(!active[c] && is.getItem() == Items.BUCKET) {
//					if(block.getMaterial().isLiquid()) {
//						active[c] = true;
//						bukkit[c] = true;
//					}
//				}			
//			}

			if(!wasactive[c] && active[c]) {
				MCOpenVR.triggerHapticPulse(c, 250);
			}

			MCOpenVR.getInputAction(MCOpenVR.keyVRInteract).setEnabled(ControllerType.values()[c], active[c]);

			wasactive[c] = active[c];
		}
	}

	public boolean isInteractActive(int controller) {
		return active[controller];
	}

	public void processBindings() {
		for(int c =0 ;c<2;c++){
			if(MCOpenVR.keyVRInteract.isPressed(ControllerType.values()[c])) {
				if (!active[c]) 
					continue; //how tho?
				EnumHand hand = EnumHand.values()[c];
				boolean success = false;
				
				if(hotbar >= 0 && hotbar < 9 && mc.player.inventory.currentItem != hotbar && hand == EnumHand.MAIN_HAND) {
					mc.player.inventory.currentItem = hotbar;
					success = true;
				}
				else if(hotbar == 9 && hand == EnumHand.MAIN_HAND) {
					mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.SWAP_HELD_ITEMS, BlockPos.ORIGIN, EnumFacing.DOWN));
					success = true;
				}
//				else if(inEntityHit[c]!=null) {     
//					success = true;
//					if (!mc.playerController.interactWithEntity(mc.player, inEntity[c], inEntityHit[c], hand).isSuccessOrConsume())
//					 if (!mc.playerController.interactWithEntity(mc.player, inEntity[c], hand).isSuccessOrConsume()) {
//							success = false;
//					 }		
//				}
//				else if (inBlockHit[c]!=null) {
//					success = mc.playerController.func_217292_a(mc.player, (ClientWorld) mc.player.world, hand, inBlockHit[c]).isSuccessOrConsume();
//				} else if (bukkit[c]) {
//					success =mc.playerController.processRightClick(mc.player, (ClientWorld) mc.player.world, hand).isSuccessOrConsume();
//				}
				
				if(success){
					//mc.player.swingArm(hand);
					MCOpenVR.triggerHapticPulse(c, 750);	
				}
			}
		}
	}
}

