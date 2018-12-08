package com.mtbs3d.minecrift.render;

import com.mojang.authlib.GameProfile;
import com.mtbs3d.minecrift.utils.MCReflection;

import java.lang.reflect.Method;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.optifine.reflect.Reflector;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import org.lwjgl.opengl.GL11;

public class RenderVRPlayer extends RenderPlayer
{

	public RenderVRPlayer(RenderManager renderManager) {
		super(renderManager);
	}

	public RenderVRPlayer(RenderManager renderManager, boolean useSmallArms) {
		super(renderManager, useSmallArms);
	}
	
	@Override
    public void renderRightArm(AbstractClientPlayer clientPlayer)
    {
        float f = 1.0F;
        if(Minecraft.getMinecraft().player.isSneaking()) f= 0.75f;
        GlStateManager.color(1.0F, 1.0F, 1.0F, f);
        float f1 = 0.0625F;
        ModelPlayer modelplayer = this.getMainModel();
		MCReflection.RenderPlayer_setModelVisibilities.invoke(this, clientPlayer);
        GlStateManager.enableBlend();
        GlStateManager.enableCull();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        modelplayer.swingProgress = 0.0F;
        modelplayer.isSneak = false;
        modelplayer.bipedRightArm.rotateAngleX = 0;
        modelplayer.bipedRightArm.rotateAngleY = 0;
        modelplayer.bipedRightArm.rotateAngleZ = 0;
//        modelplayer.bipedRightArm.offsetX = 0;
//        modelplayer.bipedRightArm.offsetY = 0;
//        modelplayer.bipedRightArm.offsetZ = 0;
        modelplayer.bipedRightArm.render(0.0625F);
//        modelplayer.bipedRightArmwear.offsetX = 0;
//        modelplayer.bipedRightArmwear.offsetY = 0;
//        modelplayer.bipedRightArmwear.offsetZ = 0;
        modelplayer.bipedRightArmwear.rotateAngleY = 0.0F;
        modelplayer.bipedRightArmwear.rotateAngleX = 0.0F;
        modelplayer.bipedRightArmwear.rotateAngleZ = 0.0F;
//        modelplayer.bipedRightArmwear.rotationPointX = 0.0F;
//        modelplayer.bipedRightArmwear.rotationPointY = 0.0F;
        modelplayer.bipedRightArmwear.rotationPointZ = 0.0F;
        modelplayer.bipedRightArmwear.render(0.0625F);
        
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0f);
    }
	
	@Override
    public void renderLeftArm(AbstractClientPlayer clientPlayer)
    {
        float f = 1.0F;
        if(Minecraft.getMinecraft().player.isSneaking()) f= 0.75f;
        GlStateManager.color(1.0F, 1.0F, 1.0f, f);
        float f1 = 0.0625F;
        ModelPlayer modelplayer = this.getMainModel();
		MCReflection.RenderPlayer_setModelVisibilities.invoke(this, clientPlayer);
        GlStateManager.enableBlend();
        GlStateManager.enableCull();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        modelplayer.isSneak = false;
        modelplayer.swingProgress = 0.0F;
        modelplayer.bipedLeftArm.rotateAngleX = 0;
        modelplayer.bipedLeftArm.rotateAngleY = 0;
        modelplayer.bipedLeftArm.rotateAngleZ = 0;
//        modelplayer.bipedLeftArm.offsetX = 0;
//        modelplayer.bipedLeftArm.offsetY = 0;
//        modelplayer.bipedLeftArm.offsetZ = 0;
        modelplayer.bipedLeftArm.render(0.0625F);
//        modelplayer.bipedLeftArmwear.offsetX = 0;
//        modelplayer.bipedLeftArmwear.offsetY = 0;
//        modelplayer.bipedLeftArmwear.offsetZ = 0;
        modelplayer.bipedLeftArmwear.rotateAngleX = 0.0F;
        modelplayer.bipedLeftArmwear.rotateAngleY = 0.0F;
        modelplayer.bipedLeftArmwear.rotateAngleZ = 0.0F;
//        modelplayer.bipedLeftArmwear.rotationPointX = 0.0F;
//        modelplayer.bipedLeftArmwear.rotationPointY = 0.0F;
        modelplayer.bipedLeftArmwear.rotationPointZ = 0.0F;

        modelplayer.bipedLeftArmwear.render(0.0625F);
        GlStateManager.disableBlend();
    }
	
}
