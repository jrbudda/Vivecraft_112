package com.mtbs3d.minecrift.gameplay.trackers;

import java.nio.ByteBuffer;

import com.mtbs3d.minecrift.api.NetworkHelper;
import com.mtbs3d.minecrift.api.NetworkHelper.PacketDiscriminators;
import com.mtbs3d.minecrift.gameplay.OpenVRPlayer;
import com.mtbs3d.minecrift.gameplay.trackers.Tracker.EntryPoint;
import com.mtbs3d.minecrift.provider.MCOpenVR;

import de.fruitfly.ovr.structs.Matrix4f;
import de.fruitfly.ovr.structs.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.optifine.reflect.Reflector;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;

public class BowTracker extends Tracker {
	private double lastcontrollersDist;
	private double lastcontrollersDot;	
	private double controllersDist;
	private double controllersDot;
	private double currentDraw;
	private double lastDraw;
	public boolean isDrawing; 
	private boolean pressed, lastpressed;	
	
	private boolean canDraw, lastcanDraw;
	public long startDrawTime;
	
	
	private Vec3d leftHandAim;
	
	private final double notchDotThreshold = 20;
	private double maxDraw ;
	private long maxDrawMillis=1100;

	private Vec3d aim;

	public BowTracker(Minecraft mc) {
		super(mc);
	}

	public Vec3d getAimVector(){
		return aim;
//		if(isDrawing)return aim;
//		return leftHandAim;
	}
		
	public double getDrawPercent(){
		return currentDraw/maxDraw;
//		double target= Math.min(currentDraw / maxDraw,1.0);
//		double cap=(Minecraft.getSystemTime()-startDrawTime)/ maxDrawMillis;
//		return target<cap ? target : cap;
	}
	
	public boolean isNotched(){
		return canDraw || isDrawing;	
	}
	
	public static boolean isBow(ItemStack itemStack) {
		if( itemStack == ItemStack.EMPTY) return false;
		if(Minecraft.getMinecraft().vrSettings.bowMode == 0) return false;
		else if(Minecraft.getMinecraft().vrSettings.bowMode == 1) return itemStack.getItem() == Items.BOW; 
		else return itemStack.getItem().getItemUseAction(itemStack) == EnumAction.BOW;			 
	}
	
	public static boolean isHoldingBow(EntityLivingBase e, EnumHand hand) {
		if(Minecraft.getMinecraft().vrSettings.seated) return false;
		return isBow(e.getHeldItem(hand));
	}
	
	public static boolean isHoldingBowEither(EntityLivingBase e) {
		return isHoldingBow(e, EnumHand.MAIN_HAND) || isHoldingBow(e, EnumHand.OFF_HAND)  ;
	}
	
	public boolean isActive(EntityPlayerSP p){
		if(p == null) return false;
		if(p.isDead) return false;
		if(p.isPlayerSleeping()) return false;
		return isHoldingBow(p, EnumHand.MAIN_HAND) || isHoldingBow(p, EnumHand.OFF_HAND)  ;
	}
	
	float tsNotch = 0;
	
	int hapcounter = 0;
	int lasthapStep=0;
	
	public boolean isCharged(){
		return Minecraft.getSystemTime() - startDrawTime >= maxDrawMillis;
	}

	@Override
	public void reset(EntityPlayerSP player) {
		isDrawing = false;
	}

	@Override
	public EntryPoint getEntryPoint() {
		return EntryPoint.SPECIAL_ITEMS;
	}

	@Override
	public void doProcess(EntityPlayerSP player){
		OpenVRPlayer provider = mc.vrPlayer;

		if(mc.vrSettings.seated){
			aim = mc.vrPlayer.vrdata_world_render.getController(0).getCustomVector(new Vec3d(0,0,1));
			return;
		}
		

		lastcontrollersDist = controllersDist;
		lastcontrollersDot = controllersDot;
		lastpressed = pressed;
		lastDraw = currentDraw;
		lastcanDraw = canDraw;
		maxDraw = mc.player.height * 0.22;

		//these are wrong since this is called every frame but should be fine so long as they're only compared to each other.
		Vec3d rightPos = provider.vrdata_world_render.getController(0).getPosition();
		Vec3d leftPos = provider.vrdata_world_render.getController(1).getPosition();
		//
			
		controllersDist = leftPos.distanceTo(rightPos);

		Vec3d forward = new Vec3d(0,1,0);

		Vec3d stringPos=provider.vrdata_world_render.getHand(1).getCustomVector(forward).scale(maxDraw*0.5).add(leftPos);
		double notchDist=rightPos.distanceTo(stringPos);

		aim = rightPos.subtract(leftPos).normalize();

		Vec3d rightaim3 = provider.vrdata_world_render.getHand(0).getCustomVector(new Vec3d(0,0,-1));
		
		Vector3f rightAim = new Vector3f((float)rightaim3.x, (float) rightaim3.y, (float) rightaim3.z);
		leftHandAim = provider.vrdata_world_render.getHand(1).getCustomVector(new Vec3d(0, 0, -1));
	 	Vec3d l4v3 = provider.vrdata_world_render.getHand(1).getCustomVector(new Vec3d(0, -1, 0));
		 
		Vector3f leftforeward = new Vector3f((float)l4v3.x, (float) l4v3.y, (float) l4v3.z);

		controllersDot = 180 / Math.PI * Math.acos(leftforeward.dot(rightAim));

		pressed = mc.gameSettings.keyBindAttack.isKeyDown();

		float notchDistThreshold = (float) (0.3 * provider.vrdata_world_render.worldScale);
		
		boolean main = this.isHoldingBow(player, EnumHand.MAIN_HAND);
		
		EnumHand hand = main ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
		
		ItemStack ammo;
		ItemStack bow;

		if(main){ //autofind ammo.
			ammo = findAmmoItemStack(player);
			bow = player.getHeldItemMainhand();
		}
		else { //BYOA
			ammo = this.isArrow(player.getHeldItemMainhand()) ?  player.getHeldItemMainhand() : null;
			bow = player.getHeldItemOffhand();
		}
		
		if(ammo !=null && notchDist <= notchDistThreshold && controllersDot <= notchDotThreshold)
		{
			//can draw
			if(!canDraw) {
				startDrawTime = Minecraft.getSystemTime();
			}

			canDraw = true;
			tsNotch = Minecraft.getSystemTime();
			
			if(!isDrawing){
				player.setItemInUseClient(bow);
				player.setItemInUseCountClient(bow.getMaxItemUseDuration() - 1 );
				mc.playerController.processRightClick(player, player.world, hand);//server

			}

		} else if((Minecraft.getSystemTime() - tsNotch) > 500) {
			canDraw = false;
			player.setItemInUseClient(ItemStack.EMPTY);//client draw only
		}
			
		if (!isDrawing && canDraw  && pressed && !lastpressed) {
			//draw     	    	
			isDrawing = true;
			mc.playerController.processRightClick(player, player.world, hand);//server
		}

		if(isDrawing && !pressed && lastpressed && getDrawPercent() > 0.0) {
			//fire!
			MCOpenVR.triggerHapticPulse(0, 500); 	
			MCOpenVR.triggerHapticPulse(1, 3000); 	
			CPacketCustomPayload pack =	NetworkHelper.getVivecraftClientPacket(PacketDiscriminators.DRAW, ByteBuffer.allocate(4).putFloat((float) getDrawPercent()).array());
			Minecraft.getMinecraft().getConnection().sendPacket(pack);
			mc.playerController.onStoppedUsingItem(player); //server
			pack =	NetworkHelper.getVivecraftClientPacket(PacketDiscriminators.DRAW, ByteBuffer.allocate(4).putFloat(0).array()); //reset to 0, in case user switches modes.
			Minecraft.getMinecraft().getConnection().sendPacket(pack);
			isDrawing = false;     	
		}
		
		if(!pressed){
			isDrawing = false;
		}
		
		if (!isDrawing && canDraw && !lastcanDraw) {
			MCOpenVR.triggerHapticPulse(1, 800);
			MCOpenVR.triggerHapticPulse(0, 800); 	
			//notch     	    	
		}
		
		if(isDrawing){
			currentDraw = controllersDist - notchDistThreshold ;
			if (currentDraw > maxDraw) currentDraw = maxDraw;		
			
			int hap = 0;
			if (getDrawPercent() > 0 ) hap = (int) (getDrawPercent() * 500)+ 700;
		
			int use = (int) (bow.getMaxItemUseDuration() - getDrawPercent() * maxDrawMillis);

			int stage0=bow.getMaxItemUseDuration();
			int stage1=bow.getMaxItemUseDuration()-15;
			int stage2=0;

			player.setItemInUseClient(bow);//client draw only
			double drawperc=getDrawPercent();
			if(drawperc>=1) {
				player.setItemInUseCountClient(stage2);

			}else if(drawperc>0.4) {
				player.setItemInUseCountClient(stage1);
			}else {
				player.setItemInUseCountClient(stage0);
			}

			int hapstep=(int)(drawperc*4*4*3);
			if ( hapstep % 2 == 0 && lasthapStep!= hapstep) {
				MCOpenVR.triggerHapticPulse(0, hap);
				if(drawperc==1)
					MCOpenVR.triggerHapticPulse(1,hap);
			}

			if(isCharged() && hapcounter %4==0){
				MCOpenVR.triggerHapticPulse(1,200);
			}
			
			//else if(drawperc==1 && hapcounter % 8 == 0){
			//	provider.triggerHapticPulse(0,400);     //Not sure if i like this part or not
			//}

			lasthapStep = hapstep;
			hapcounter++;

		} else {
			hapcounter = 0;
			lasthapStep=0;
		}


	}
	
	
    public ItemStack findAmmoItemStack(EntityPlayer player){
        boolean flag = player.capabilities.isCreativeMode || 
        		EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, player.getHeldItemMainhand()) > 0;
        		
        ItemStack itemstack = this.findAmmo(player);

        if (itemstack != null || flag)
        {
            if (itemstack == null)
            {
                return new ItemStack(Items.ARROW);
            }
        }
        return itemstack;
    }
    
    //The 2 methods below are from ItemBow.
    private ItemStack findAmmo(EntityPlayer player)
    {
        if (this.isArrow(player.getHeldItem(EnumHand.OFF_HAND)))
        {
            return player.getHeldItem(EnumHand.OFF_HAND);
        }
        else if (this.isArrow(player.getHeldItem(EnumHand.MAIN_HAND)))
        {
            return player.getHeldItem(EnumHand.MAIN_HAND);
        }
        else
        {
            for (int i = 0; i < player.inventory.getSizeInventory(); ++i)
            {
                ItemStack itemstack = player.inventory.getStackInSlot(i);

                if (this.isArrow(itemstack))
                {
                    return itemstack;
                }
            }

            return ItemStack.EMPTY;
        }
    }

    protected boolean isArrow(ItemStack stack)
    {
        if(Reflector.forgeExists()){
        	return stack.getItem() instanceof ItemArrow || stack.getItem().getClass().getName().toLowerCase().contains("arrow");
        }else {
        	return stack.getItem() instanceof ItemArrow;
        }
    }

}

