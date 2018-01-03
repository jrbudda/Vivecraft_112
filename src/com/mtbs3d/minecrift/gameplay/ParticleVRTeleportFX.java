package com.mtbs3d.minecrift.gameplay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

//TODO re-implement
public class ParticleVRTeleportFX extends Particle
{
    float initialScale;

    protected ParticleVRTeleportFX(World worldIn, double p_i46284_2_, double p_i46284_4_, double p_i46284_6_, double p_i46284_8_, double p_i46284_10_, double p_i46284_12_)
    {
        this(worldIn, p_i46284_2_, p_i46284_4_, p_i46284_6_, p_i46284_8_, p_i46284_10_, p_i46284_12_, 1.0F);
    }

    public ParticleVRTeleportFX(World worldIn, double p_i46285_2_, double p_i46285_4_, double p_i46285_6_, double p_i46285_8_, double p_i46285_10_, double p_i46285_12_, float sizeAgeScale)
    {
        super(worldIn, p_i46285_2_, p_i46285_4_, p_i46285_6_);
        this.motionX = p_i46285_8_;
        this.motionY = p_i46285_10_;
        this.motionZ = p_i46285_12_;
        this.particleScale *= 0.70F;
        this.particleScale *= sizeAgeScale;
        this.initialScale = this.particleScale;
        this.particleMaxAge = (int)(6.0D / (Math.random() * 0.8D + 0.6D));
        this.particleMaxAge = (int)((float)this.particleMaxAge * sizeAgeScale);
     //   this.noClip = false;
        this.setParticleTextureIndex(144);
        this.onUpdate();
    }

    public void renderParticle(Tessellator p_70539_1_, float p_70539_2_, float p_70539_3_, float p_70539_4_, float p_70539_5_, float p_70539_6_, float p_70539_7_)
    {
//        float progress = ((float)this.particleAge + p_70539_2_) / (float)this.particleMaxAge * 32.0F;
//        progress = MathHelper.clamp_float(progress, 0.0F, 1.0F);
//        this.particleScale = this.initialScale * progress;
//        super.renderParticle(p_70539_1_, p_70539_2_, p_70539_3_, p_70539_4_, p_70539_5_, p_70539_6_, p_70539_7_);
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
//        this.prevPosX = this.posX;
//        this.prevPosY = this.posY;
//        this.prevPosZ = this.posZ;
//
//        if (this.particleAge++ >= this.particleMaxAge)
//        {
//            this.setDead();
//        }
//
//        OpenVRPlayer vrPlayer = OpenVRPlayer.get();
//        this.moveEntity(this.motionX*(0.05+0.95*vrPlayer.movementTeleportProgress),
//                        this.motionY*(0.05+0.95*vrPlayer.movementTeleportProgress),
//                        this.motionZ*(0.05+0.95*vrPlayer.movementTeleportProgress));
//
//        if (this.onGround)
//        {
//            this.motionX *= 0.699999988079071D;
//            this.motionZ *= 0.699999988079071D;
//        }
    }
}

// VIVE END - teleport effect