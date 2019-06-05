package org.vivecraft.gameplay.trackers;

import org.vivecraft.provider.MCOpenVR;
import org.vivecraft.settings.AutoCalibration;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public class SneakTracker extends Tracker {
	public boolean sneakOverride=false;
	public int sneakCounter = 0;

	public SneakTracker(Minecraft mc) {
		super(mc);
	}

	public boolean isActive(EntityPlayerSP p){
		if(Minecraft.getMinecraft().vrSettings.seated)
			return false;
		if(!Minecraft.getMinecraft().vrPlayer.getFreeMove() && !Minecraft.getMinecraft().vrSettings.simulateFalling)
			return false;
		if(!Minecraft.getMinecraft().vrSettings.realisticSneakEnabled)
			return false;
		if(p==null || p.isDead || !p.onGround)
			return false;
		if(p.isRiding())
			return false;
		return true;
	}

	@Override
	public void reset(EntityPlayerSP player) {
		sneakOverride = false;
	}

	public void doProcess(EntityPlayerSP player){

		if(!mc.isGamePaused()) {
			if (mc.sneakTracker.sneakCounter > 0) 
				mc.sneakTracker.sneakCounter--;
		}
		
	    if(( AutoCalibration.getPlayerHeight() - MCOpenVR.hmdPivotHistory.latest().y )> mc.vrSettings.sneakThreshold){
		   sneakOverride=true;
	    }else{
		    sneakOverride=false;
	    }
	}

}
