package com.mtbs3d.minecrift.gameplay.trackers;

import java.util.List;

import com.mtbs3d.minecrift.provider.MCOpenVR;
import com.mtbs3d.minecrift.utils.MCReflection;

import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.optifine.reflect.Reflector;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;

public class SwingTracker extends Tracker{
  
    //VIVECRAFT SWINGING SUPPORT
    private Vec3d[] lastWeaponEndAir = new Vec3d[]{new Vec3d(0, 0, 0), new Vec3d(0,0,0)};
    private boolean[] lastWeaponSolid = new boolean[2];
	private Vec3d[] weaponEnd= new Vec3d[2];
	private Vec3d[] weaponEndlast= new Vec3d[]{new Vec3d(0, 0, 0), new Vec3d(0,0,0)};
	
    public boolean[] shouldIlookatMyHand= new boolean[2];
    public boolean[] IAmLookingAtMyHand= new boolean[2];
    
    public int disableSwing = 3;

	public SwingTracker(Minecraft mc) {
		super(mc);
	}

	public boolean isActive(EntityPlayerSP p){
    	if(p == null) return false;
    	if(p.isDead) return false;
    	if(p.isPlayerSleeping()) return false;
    	Minecraft mc = Minecraft.getMinecraft();
    	if (!mc.vrSettings.weaponCollision)
    		return false;
    	if (mc.vrSettings.seated)
    		return false;
    	if(mc.vrSettings.vrFreeMoveMode == mc.vrSettings.FREEMOVE_RUNINPLACE && p.moveForward > 0){
    		return false; //dont hit things while RIPing.
    	}
    	if(p.isActiveItemStackBlocking()){
    		return false; //dont hit things while blocking.
    	}
    	if(mc.jumpTracker.isjumping()) 
    		return false;
    	return true;    
    }
	

	public void doProcess(EntityPlayerSP player){ //on tick

        
        mc.mcProfiler.startSection("updateSwingAttack");
        
        Vec3d forward = new Vec3d(0,0,-1);
        
        for(int c =0 ;c<2;c++){

        	Vec3d handPos = mc.vrPlayer.vrdata_world_pre.getController(c).getPosition();
        	Vec3d handDirection = mc.vrPlayer.vrdata_world_pre.getHand(c).getCustomVector(forward);

        	ItemStack is = player.getHeldItem(c==0?EnumHand.MAIN_HAND:EnumHand.OFF_HAND);
        	Item item = null;

        	double speedthresh = 1.8f;
        	float weaponLength;
        	float entityReachAdd;

        	if(is!=null )item = is.getItem();

            boolean tool = false;
            boolean sword = false;

            if(item instanceof ItemSword){
            	sword = true;
            	tool = true;    	
            }
            else if (item instanceof ItemTool ||
            		item instanceof ItemHoe
            		){
            	tool = true;
            }
            else if(item !=null && Reflector.forgeExists()){ //tinkers hack
            	String t = item.getClass().getSuperclass().getName().toLowerCase();
            	//System.out.println(c);
            	if (t.contains("weapon") || t.contains("sword")) {
            		sword = true;
            		tool = true;
            	} else 	if 	(t.contains("tool")){
            		tool = true;
            	}
            }    

            if (sword){
                 	entityReachAdd = 2.5f;
            		weaponLength = 0.3f;
            		tool = true;
            } else if (tool){
            	entityReachAdd = 1.8f;
            	weaponLength = 0.3f;
        		tool = true;
            } else if (item !=null){
            	weaponLength = 0.1f;
            	entityReachAdd = 0.3f;
            } else {
            	weaponLength = 0.0f;
            	entityReachAdd = 0.3f;
            }

        	weaponLength *= mc.vrPlayer.vrdata_world_pre.worldScale;

        	weaponEnd[c] = new Vec3d(
        			handPos.x + handDirection.x * weaponLength,
        			handPos.y + handDirection.y * weaponLength,
        			handPos.z + handDirection.z * weaponLength);     

        	if (disableSwing > 0 ) {
        		disableSwing--;
        		if(disableSwing<0)disableSwing = 0;
        		weaponEndlast[c] = new Vec3d(weaponEnd[c].x,	 weaponEnd[c].y, 	 weaponEnd[c].z);
        		return;
        	}


        	float speed = (float) MCOpenVR.controllerHistory[c].averageSpeed(0.1);

        	weaponEndlast[c] = new Vec3d(weaponEnd[c].x, weaponEnd[c].y, weaponEnd[c].z);

//        	int passes = (int) (tickDist / .1f); //TODO someday....

        	int bx = (int) MathHelper.floor(weaponEnd[c].x);
        	int by = (int) MathHelper.floor(weaponEnd[c].y);
        	int bz = (int) MathHelper.floor(weaponEnd[c].z);

        	boolean inAnEntity = false;
        	boolean insolidBlock = false;
        	boolean canact = speed > speedthresh && !lastWeaponSolid[c];

        	Vec3d extWeapon = new Vec3d(
        			handPos.x + handDirection.x * (weaponLength + entityReachAdd),
        			handPos.y + handDirection.y * (weaponLength + entityReachAdd),
        			handPos.z + handDirection.z * (weaponLength + entityReachAdd));

        	//Check EntityCollisions first
        	//experiment.
        	AxisAlignedBB weaponBB = new AxisAlignedBB(
        			handPos.x < extWeapon.x ? handPos.x : extWeapon.x  ,
        					handPos.y < extWeapon.y ? handPos.y : extWeapon.y  ,
        							handPos.z < extWeapon.z ? handPos.z : extWeapon.z  ,
        									handPos.x > extWeapon.x ? handPos.x : extWeapon.x  ,
        											handPos.y > extWeapon.y ? handPos.y : extWeapon.y  ,
        													handPos.z > extWeapon.z ? handPos.z : extWeapon.z  
        			);

        	List entities = mc.world.getEntitiesWithinAABBExcludingEntity(
        			mc.getRenderViewEntity(), weaponBB);
        	for (int e = 0; e < entities.size(); ++e)
        	{
        		Entity hitEntity = (Entity) entities.get(e);
        		if (hitEntity.canBeCollidedWith() && !(hitEntity == mc.getRenderViewEntity().getRidingEntity()) )			{
        			if(mc.vrSettings.animaltouching && hitEntity instanceof EntityAnimal && !tool && !lastWeaponSolid[c] && !player.isInWater()){
        				mc.playerController.interactWithEntity(player, hitEntity, c==0?EnumHand.MAIN_HAND:EnumHand.OFF_HAND);
        				disableSwing = 3;
        				MCOpenVR.triggerHapticPulse(c, 250);
        				lastWeaponSolid[c] = true;
        				inAnEntity = true;
        			} 
        			else 
        			{
        				if(canact){
        					mc.playerController.attackEntity(player, hitEntity);
        					MCOpenVR.triggerHapticPulse(c, 1000);
        					lastWeaponSolid[c] = true;
        				}
        				inAnEntity = true;
        			}
        		}
        	}

        	if(!inAnEntity && !sword){
        		if(mc.climbTracker.isClimbeyClimb()){
        			if(c == 0 && mc.gameSettings.keyBindAttack.isKeyDown() || !tool ) continue;
        			if(c == 1 && mc.gameSettings.keyBindForward.isKeyDown() || !tool ) continue;
        		}
        		
        		BlockPos bp =null;
        		bp = new BlockPos(weaponEnd[c]);
        		IBlockState block = mc.world.getBlockState(bp);
        		Material material = block.getMaterial();

        		// every time end of weapon enters a solid for the first time, trace from our previous air position
        		// and damage the block it collides with... 

        		RayTraceResult col = mc.world.rayTraceBlocks(lastWeaponEndAir[c], weaponEnd[c], true, false, true);
        		
        		mc.playerController.hitVecOverride = col.hitVec;
        		
        		boolean flag = col!=null && col.getBlockPos().equals(bp); //fix ladder but prolly break everything else.
        		if (flag && (shouldIlookatMyHand[c] || (col != null && col.typeOfHit == Type.BLOCK)))
        		{
        			this.shouldIlookatMyHand[c] = false;
        			if (!(material == material.AIR))
        			{
        				if (block.getMaterial().isLiquid()) {
        					if(item == Items.BUCKET) {       						
        						//mc.playerController.onPlayerRightClick(player, player.world,is, col.blockX, col.blockY, col.blockZ, col.sideHit,col.hitVec);
        						this.shouldIlookatMyHand[c] = true;
        						if (IAmLookingAtMyHand[c]){

        							if(	Minecraft.getMinecraft().playerController.processRightClick(player, player.world,c==0?EnumHand.MAIN_HAND:EnumHand.OFF_HAND)==EnumActionResult.SUCCESS){
        								mc.entityRenderer.itemRenderer.resetEquippedProgress(c==0?EnumHand.MAIN_HAND:EnumHand.OFF_HAND);					
        							}
        						}
        					}
        				} else {
        					if(canact && (!mc.vrSettings.realisticClimbEnabled || (!(block.getBlock() instanceof BlockLadder) && !(block.getBlock() instanceof BlockVine)))) { 
        						int p = 3;
        						if(item instanceof ItemHoe){
        							Minecraft.getMinecraft().playerController.
        							processRightClickBlock(player, (WorldClient) player.world,bp,col.sideHit, col.hitVec, c==0?EnumHand.MAIN_HAND:EnumHand.OFF_HAND);
        						}else{
        							p += (speed - speedthresh) / 2;

        							for (int i = 0; i < p; i++)
        							{
        								//set delay to 0
        								clearBlockHitDelay();			
        								boolean test = mc.climbTracker.isGrabbingLadder();
        								//all this comes from plaeyrControllerMP clickMouse and friends.

        								//all this does is sets the block you're currently hitting, has no effect in survival mode after that.
        								//but if in creaive mode will clickCreative on the block
        								mc.playerController.clickBlock(col.getBlockPos(), col.sideHit);

        								if(!getIsHittingBlock()) //seems to be the only way to tell it broke.
        									break;

        								//apply destruction for survival only
        								mc.playerController.onPlayerDamageBlock(col.getBlockPos(), col.sideHit);

        								if(!getIsHittingBlock()) //seems to be the only way to tell it broke.
        									break;

        								//something effects
        								mc.effectRenderer.addBlockHitEffects(col.getBlockPos(), col.sideHit);

        							}
        							
        						}
        						mc.vrPlayer.blockDust(col.hitVec.x, col.hitVec.y, col.hitVec.z, 3*p, block);

        						MCOpenVR.triggerHapticPulse(c, 250*p);
        						//   System.out.println("Hit block speed =" + speed + " mot " + mot + " thresh " + speedthresh) ;            				
        						lastWeaponSolid[c] = true;
        					}
        					insolidBlock = true;
        				}
        			}
        		}
        		mc.playerController.hitVecOverride = null;
        	}

            if ((!inAnEntity && !insolidBlock ) || lastWeaponEndAir[c].lengthVector() ==0)
        	{
        		this.lastWeaponEndAir[c] = new Vec3d(
        				weaponEnd[c].x,
        				weaponEnd[c].y,
        				weaponEnd[c].z
        				);
        		lastWeaponSolid[c] = false;
        	}


        }
        
        mc.mcProfiler.endSection();
    
    }

	private boolean getIsHittingBlock(){
		return (Boolean)MCReflection.PlayerControllerMP_isHittingBlock.get(Minecraft.getMinecraft().playerController);
	}
	
    // VIVE START - function to allow damaging blocks immediately
	private void clearBlockHitDelay() {
		MCReflection.PlayerControllerMP_blockHitDelay.set(Minecraft.getMinecraft().playerController, 0);
	}
	
}

