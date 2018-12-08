package com.mtbs3d.minecrift.render;

import java.lang.reflect.Method;
import java.util.*;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.lwjgl.opengl.GL11;

import com.google.common.base.Throwables;
import com.mtbs3d.minecrift.gameplay.OpenVRPlayer;
import com.mtbs3d.minecrift.utils.FakeBlockAccess;
import com.mtbs3d.minecrift.utils.MCReflection;
import com.mtbs3d.minecrift.utils.Utils;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBufferUploader;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.src.Config;
import net.optifine.reflect.Reflector;
import net.optifine.shaders.Shaders;
import net.optifine.util.TextureUtils;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Timer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;

public class MenuWorldRenderer {
    private static final ResourceLocation MOON_PHASES_TEXTURES = new ResourceLocation("textures/environment/moon_phases.png");
    private static final ResourceLocation SUN_TEXTURES = new ResourceLocation("textures/environment/sun.png");
    private static final ResourceLocation CLOUDS_TEXTURES = new ResourceLocation("textures/environment/clouds.png");
    private static final ResourceLocation END_SKY_TEXTURES = new ResourceLocation("textures/environment/end_sky.png");
	
    private Minecraft mc;
	private WorldProvider worldProvider;
	private FakeBlockAccess blockAccess;
	private final DynamicTexture lightmapTexture;
	private final int[] lightmapColors;
	private final ResourceLocation locationLightMap;
	private boolean lightmapUpdateNeeded;
	private float torchFlickerX;
	private float torchFlickerDX;
	public long time = 1000;
	private VertexBuffer[] vertexBuffers;
	private VertexFormat vertexBufferFormat;
    private VertexBuffer starVBO;
    private VertexBuffer skyVBO;
    private VertexBuffer sky2VBO;
    public MenuCloudRenderer cloudRenderer;
    public Set<TextureAtlasSprite> visibleTextures = new HashSet<>();
    private Random rand;
    private boolean init;
    private boolean ready;
	
	public MenuWorldRenderer() {
		this.mc = Minecraft.getMinecraft();
        this.lightmapTexture = new DynamicTexture(16, 16);
        this.locationLightMap = mc.getTextureManager().getDynamicTextureLocation("lightMap", this.lightmapTexture);
        this.lightmapColors = this.lightmapTexture.getTextureData();
        this.vertexBufferFormat = new VertexFormat();
        this.vertexBufferFormat.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.POSITION, 3));
        this.cloudRenderer = new MenuCloudRenderer(mc);
        this.rand = new Random();
        this.rand.nextInt(); // toss some bits in the bin
	}
	
	public void render() {
		prepare();
		GL11.glPushClientAttrib(GL11.GL_CLIENT_VERTEX_ARRAY_BIT);
		OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, 0);
        enableLightmap();
        GlStateManager.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.glEnableClientState(GL11.GL_COLOR_ARRAY);
        GlStateManager.glDisableClientState(GL11.GL_NORMAL_ARRAY);
        
        mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		RenderHelper.disableStandardItemLighting();
        for (VertexBuffer vertexBuffer : vertexBuffers) {
	        vertexBuffer.bindBuffer();
            GlStateManager.glVertexPointer(3, GL11.GL_FLOAT, 28, 0);
            GlStateManager.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, 28, 12);
            GlStateManager.glTexCoordPointer(2, GL11.GL_FLOAT, 28, 16);
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.glTexCoordPointer(2, GL11.GL_SHORT, 28, 24);
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
            vertexBuffer.drawArrays(GL11.GL_QUADS);
        }

        GlStateManager.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        GlStateManager.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.glDisableClientState(GL11.GL_COLOR_ARRAY);
        disableLightmap();
		GL11.glPopClientAttrib();
	}
	
	public void prepare() {
		if (vertexBuffers == null) {
			// TODO: Figure out why AO doesn't work correctly here.
			int ao = mc.gameSettings.ambientOcclusion;
			mc.gameSettings.ambientOcclusion = 0;
			boolean shaders = Shaders.shaderPackLoaded;
			Shaders.shaderPackLoaded = false;
			DefaultVertexFormats.updateVertexFormats();
			Blocks.LEAVES.setGraphicsLevel(true);
			Blocks.LEAVES2.setGraphicsLevel(true);
			TextureUtils.resourcesReloaded(Config.getResourceManager());
			visibleTextures.clear();
			vertexBuffers = new VertexBuffer[3];
			BlockRendererDispatcher blockRenderer = mc.getBlockRendererDispatcher();
			int ground = rand.nextInt(1000) == 0 ? blockAccess.getGround() + 100 : blockAccess.getGround(); // lol
			for (int i = 0; i < 3; i++) {
				BufferBuilder vertBuffer = new BufferBuilder(2097152);
				vertBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
				vertBuffer.setTranslation(-blockAccess.getXSize() / 2, -ground, -blockAccess.getZSize() / 2);
				vertBuffer.setBlockLayer(i == 0 ? BlockRenderLayer.SOLID : (i == 1 ? BlockRenderLayer.CUTOUT : BlockRenderLayer.TRANSLUCENT));
				for (int x = 0; x < blockAccess.getXSize(); x++) {
					for (int y = 0; y < blockAccess.getYSize(); y++) {
						for (int z = 0; z < blockAccess.getZSize(); z++) {
							BlockPos pos = new BlockPos(x, y, z);
							IBlockState state = blockAccess.getBlockState(pos);
							if (state != null && state.getRenderType() != EnumBlockRenderType.INVISIBLE) {
								switch (state.getBlock().getBlockLayer()) {
									case SOLID:
										if (i != 0) continue;
										break;
									case CUTOUT:
									case CUTOUT_MIPPED:
										if (i != 1) continue;
										break;
									case TRANSLUCENT:
										if (i != 2) continue;
										break;
								}
								blockRenderer.renderBlock(state, pos, blockAccess, vertBuffer);
							}
						}
					}
				}
				vertBuffer.setTranslation(0, 0, 0);
				if (i == 2) vertBuffer.sortVertexData(0, blockAccess.getGround(), 0);
				vertBuffer.finishDrawing();
				vertexBuffers[i] = new VertexBuffer(vertBuffer.getVertexFormat());
				vertexBuffers[i].bufferData(vertBuffer.getByteBuffer());
			}
			mc.gameSettings.ambientOcclusion = ao;
			Shaders.shaderPackLoaded = shaders;
			DefaultVertexFormats.updateVertexFormats();
			copyVisibleTextures();
			ready = true;
		}
	}
	
	public void destroy() {
		if (vertexBuffers != null) {
			for (VertexBuffer vertexBuffer : vertexBuffers) {
				if (vertexBuffer != null) vertexBuffer.deleteGlBuffers();
			}
			vertexBuffers = null;
		}
		ready = false;
	}
	
	public FakeBlockAccess getWorld() {
		return blockAccess;
	}
	
	public void setWorld(FakeBlockAccess blockAccess) {
		this.blockAccess = blockAccess;
		if (blockAccess != null) {
			this.worldProvider = blockAccess.getDimensionType().createDimension();
			MCReflection.WorldProvider_terrainType.set(this.worldProvider, WorldType.DEFAULT);
			MCReflection.WorldProvider_generateLightBrightnessTable.invoke(this.worldProvider);
	        this.lightmapUpdateNeeded = true;
		}
	}
	
	public void init() throws Exception {
		if (init) return;
        this.generateSky();
        this.generateSky2();
        this.generateStars();
        init = true;
	}
	
	public boolean isReady() {
		return ready;
	}

	// VanillaFix support
	@SuppressWarnings("unchecked")
	private void copyVisibleTextures() {
		if (Reflector.VFTemporaryStorage.exists()) {
			if (Reflector.VFTemporaryStorage_texturesUsed.exists()) {
				visibleTextures.addAll((Collection<TextureAtlasSprite>)Reflector.getFieldValue(Reflector.VFTemporaryStorage_texturesUsed));
			} else if (Reflector.VFTextureAtlasSprite_needsAnimationUpdate.exists()) {
				for (TextureAtlasSprite texture : (Collection<TextureAtlasSprite>)MCReflection.TextureMap_listAnimatedSprites.get(mc.getTextureMapBlocks())) {
					if (Reflector.callBoolean(texture, Reflector.VFTextureAtlasSprite_needsAnimationUpdate))
						visibleTextures.add(texture);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void pushVisibleTextures() {
		if (Reflector.VFTemporaryStorage.exists()) {
			if (Reflector.VFTemporaryStorage_texturesUsed.exists()) {
				Collection<TextureAtlasSprite> coll = (Collection<TextureAtlasSprite>)Reflector.getFieldValue(Reflector.VFTemporaryStorage_texturesUsed);
				coll.addAll(visibleTextures);
			} else if (Reflector.VFTextureAtlasSprite_markNeedsAnimationUpdate.exists()) {
				for (TextureAtlasSprite texture : visibleTextures)
					Reflector.call(texture, Reflector.VFTextureAtlasSprite_markNeedsAnimationUpdate);
			}
		}
	}
	// End VanillaFix support
	
    public void renderSky(float x, float y, float z, int pass)
    {
        if (worldProvider.getDimensionType() == DimensionType.THE_END)
        {
            this.renderSkyEnd();
        }
        else if (worldProvider.isSurfaceWorld())
        {
            GlStateManager.disableTexture2D();

            Vec3d vec3d = this.getSkyColor(x, y, z);
            //vec3d = CustomColors.getSkyColor(vec3d, this.mc.world, this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().posY + 1.0D, this.mc.getRenderViewEntity().posZ);

            float f = (float)vec3d.x;
            float f1 = (float)vec3d.y;
            float f2 = (float)vec3d.z;

            if (pass != 2)
            {
                float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
                float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
                float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
                f = f3;
                f1 = f4;
                f2 = f5;
            }

            GlStateManager.color(f, f1, f2);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder vertexbuffer = tessellator.getBuffer();
            GlStateManager.depthMask(false);
            GlStateManager.enableFog();

            GlStateManager.color(f, f1, f2);

            this.skyVBO.bindBuffer();
            GlStateManager.glEnableClientState(32884);
            GlStateManager.glVertexPointer(3, 5126, 12, 0);
            this.skyVBO.drawArrays(7);
            this.skyVBO.unbindBuffer();
            GlStateManager.glDisableClientState(32884);

            GlStateManager.disableFog();

            GlStateManager.disableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderHelper.disableStandardItemLighting();
            float[] afloat = this.worldProvider.calcSunriseSunsetColors(this.getCelestialAngle(), 0);

            if (afloat != null)
            {
                GlStateManager.disableTexture2D();

                GlStateManager.shadeModel(7425);
                GlStateManager.pushMatrix();
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(MathHelper.sin(this.getCelestialAngleRadians()) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
                float f6 = afloat[0];
                float f7 = afloat[1];
                float f8 = afloat[2];

                if (pass != 2)
                {
                    float f9 = (f6 * 30.0F + f7 * 59.0F + f8 * 11.0F) / 100.0F;
                    float f10 = (f6 * 30.0F + f7 * 70.0F) / 100.0F;
                    float f11 = (f6 * 30.0F + f8 * 70.0F) / 100.0F;
                    f6 = f9;
                    f7 = f10;
                    f8 = f11;
                }

                vertexbuffer.begin(6, DefaultVertexFormats.POSITION_COLOR);
                vertexbuffer.pos(0.0D, 100.0D, 0.0D).color(f6, f7, f8, afloat[3]).endVertex();
                int j = 16;

                for (int l = 0; l <= 16; ++l)
                {
                    float f18 = (float)l * ((float)Math.PI * 2F) / 16.0F;
                    float f12 = MathHelper.sin(f18);
                    float f13 = MathHelper.cos(f18);
                    vertexbuffer.pos((double)(f12 * 120.0F), (double)(f13 * 120.0F), (double)(-f13 * 40.0F * afloat[3])).color(afloat[0], afloat[1], afloat[2], 0.0F).endVertex();
                }

                tessellator.draw();
                GlStateManager.popMatrix();
                GlStateManager.shadeModel(7424);
            }

            GlStateManager.enableTexture2D();

            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.pushMatrix();
            float f15 = 1.0F; //- this.world.getRainStrength(partialTicks);
            GlStateManager.color(1.0F, 1.0F, 1.0F, f15);
            GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
            //CustomSky.renderSky(this.world, this.renderEngine, this.world.getCelestialAngle(partialTicks), f15);

            GlStateManager.rotate(this.getCelestialAngle() * 360.0F, 1.0F, 0.0F, 0.0F);

            float f16 = 30.0F;

        	mc.renderEngine.bindTexture(SUN_TEXTURES);
            vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
            vertexbuffer.pos((double)(-f16), 100.0D, (double)(-f16)).tex(0.0D, 0.0D).endVertex();
            vertexbuffer.pos((double)f16, 100.0D, (double)(-f16)).tex(1.0D, 0.0D).endVertex();
            vertexbuffer.pos((double)f16, 100.0D, (double)f16).tex(1.0D, 1.0D).endVertex();
            vertexbuffer.pos((double)(-f16), 100.0D, (double)f16).tex(0.0D, 1.0D).endVertex();
            tessellator.draw();

            f16 = 20.0F;

        	mc.renderEngine.bindTexture(MOON_PHASES_TEXTURES);
            int i = this.getMoonPhase();
            int k = i % 4;
            int i1 = i / 4 % 2;
            float f19 = (float)(k + 0) / 4.0F;
            float f21 = (float)(i1 + 0) / 2.0F;
            float f23 = (float)(k + 1) / 4.0F;
            float f14 = (float)(i1 + 1) / 2.0F;
            vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
            vertexbuffer.pos((double)(-f16), -100.0D, (double)f16).tex((double)f23, (double)f14).endVertex();
            vertexbuffer.pos((double)f16, -100.0D, (double)f16).tex((double)f19, (double)f14).endVertex();
            vertexbuffer.pos((double)f16, -100.0D, (double)(-f16)).tex((double)f19, (double)f21).endVertex();
            vertexbuffer.pos((double)(-f16), -100.0D, (double)(-f16)).tex((double)f23, (double)f21).endVertex();
            tessellator.draw();

            GlStateManager.disableTexture2D();

            float f17 = this.getStarBrightness() * f15;

            if (f17 > 0.0F)
            {
                GlStateManager.color(f17, f17, f17, f17);

                this.starVBO.bindBuffer();
                GlStateManager.glEnableClientState(32884);
                GlStateManager.glVertexPointer(3, 5126, 12, 0);
                this.starVBO.drawArrays(7);
                this.starVBO.unbindBuffer();
                GlStateManager.glDisableClientState(32884);
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.enableFog();

            GlStateManager.popMatrix();
            GlStateManager.disableTexture2D();

            GlStateManager.color(0.0F, 0.0F, 0.0F);
            double d0 = y - 63;

            if (d0 < 0.0D)
            {
                GlStateManager.pushMatrix();
                GlStateManager.translate(0.0F, 12.0F, 0.0F);

                this.sky2VBO.bindBuffer();
                GlStateManager.glEnableClientState(32884);
                GlStateManager.glVertexPointer(3, 5126, 12, 0);
                this.sky2VBO.drawArrays(7);
                this.sky2VBO.unbindBuffer();
                GlStateManager.glDisableClientState(32884);

                GlStateManager.popMatrix();
                float f20 = 1.0F;
                float f22 = -((float)(d0 + 65.0D));
                float f24 = -1.0F;
                vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
                vertexbuffer.pos(-1.0D, (double)f22, 1.0D).color(0, 0, 0, 255).endVertex();
                vertexbuffer.pos(1.0D, (double)f22, 1.0D).color(0, 0, 0, 255).endVertex();
                vertexbuffer.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                vertexbuffer.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                vertexbuffer.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
                vertexbuffer.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
                vertexbuffer.pos(1.0D, (double)f22, -1.0D).color(0, 0, 0, 255).endVertex();
                vertexbuffer.pos(-1.0D, (double)f22, -1.0D).color(0, 0, 0, 255).endVertex();
                vertexbuffer.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
                vertexbuffer.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                vertexbuffer.pos(1.0D, (double)f22, 1.0D).color(0, 0, 0, 255).endVertex();
                vertexbuffer.pos(1.0D, (double)f22, -1.0D).color(0, 0, 0, 255).endVertex();
                vertexbuffer.pos(-1.0D, (double)f22, -1.0D).color(0, 0, 0, 255).endVertex();
                vertexbuffer.pos(-1.0D, (double)f22, 1.0D).color(0, 0, 0, 255).endVertex();
                vertexbuffer.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                vertexbuffer.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
                vertexbuffer.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
                vertexbuffer.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                vertexbuffer.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                vertexbuffer.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
                tessellator.draw();
            }

            if (this.worldProvider.isSkyColored())
            {
                GlStateManager.color(f * 0.2F + 0.04F, f1 * 0.2F + 0.04F, f2 * 0.6F + 0.1F);
            }
            else
            {
                GlStateManager.color(f, f1, f2);
            }

            /*if (mc.gameSettings.renderDistanceChunks <= 4)
            {
                GlStateManager.color(this.mc.entityRenderer.fogColorRed, this.mc.entityRenderer.fogColorGreen, this.mc.entityRenderer.fogColorBlue);
            }*/

            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, -((float)(d0 - 16.0D)), 0.0F);

            if (Config.isSkyEnabled())
            {
                //GlStateManager.callList(this.glSkyList2);
            }

            GlStateManager.popMatrix();
            GlStateManager.enableTexture2D();

            GlStateManager.depthMask(true);
        }
    }

    private void renderSkyEnd()
    {
        if (Config.isSkyEnabled())
        {
            GlStateManager.disableFog();
            GlStateManager.disableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.depthMask(false);
            mc.renderEngine.bindTexture(END_SKY_TEXTURES);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder vertexbuffer = tessellator.getBuffer();

            for (int i = 0; i < 6; ++i)
            {
                GlStateManager.pushMatrix();

                if (i == 1)
                {
                    GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                }

                if (i == 2)
                {
                    GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                }

                if (i == 3)
                {
                    GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
                }

                if (i == 4)
                {
                    GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
                }

                if (i == 5)
                {
                    GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
                }

                vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                int j = 40;
                int k = 40;
                int l = 40;

                if (Config.isCustomColors())
                {
                    Vec3d vec3d = new Vec3d((double)j / 255.0D, (double)k / 255.0D, (double)l / 255.0D);
                    //vec3d = CustomColors.getWorldSkyColor(vec3d, this.world, this.mc.getRenderViewEntity(), 0.0F);
                    j = (int)(vec3d.x * 255.0D);
                    k = (int)(vec3d.y * 255.0D);
                    l = (int)(vec3d.z * 255.0D);
                }

                vertexbuffer.pos(-100.0D, -100.0D, -100.0D).tex(0.0D, 0.0D).color(j, k, l, 255).endVertex();
                vertexbuffer.pos(-100.0D, -100.0D, 100.0D).tex(0.0D, 16.0D).color(j, k, l, 255).endVertex();
                vertexbuffer.pos(100.0D, -100.0D, 100.0D).tex(16.0D, 16.0D).color(j, k, l, 255).endVertex();
                vertexbuffer.pos(100.0D, -100.0D, -100.0D).tex(16.0D, 0.0D).color(j, k, l, 255).endVertex();
                tessellator.draw();
                GlStateManager.popMatrix();
            }

            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.enableAlpha();
        }
    }
    
    public void renderClouds(int pass, double x, double y, double z)
    {
        if (this.worldProvider.isSurfaceWorld()) {
        	Vec3d vec3d = this.getCloudColour();
        	this.cloudRenderer.prepareToRender(mc.getRenderPartialTicks(), vec3d);
        	GlStateManager.pushMatrix();
	        GlStateManager.disableCull();
	        Tessellator tessellator = Tessellator.getInstance();
	        BufferBuilder vertexbuffer = tessellator.getBuffer();
	        float f = 12.0F;
	        float f1 = 4.0F;
	        double d0 = this.mc.tickCounter + mc.getRenderPartialTicks();
	        double d1 = (x + d0 * 0.029999999329447746D) / 12.0D;
	        double d2 = z / 12.0D + 0.33000001311302185D;
	        float f2 = this.worldProvider.getCloudHeight() - (float)y + 0.33F;
	        f2 = f2 + mc.gameSettings.ofCloudsHeight * 128.0F;
	        int i = MathHelper.floor(d1 / 2048.0D);
	        int j = MathHelper.floor(d2 / 2048.0D);
	        d1 = d1 - (double)(i * 2048);
	        d2 = d2 - (double)(j * 2048);
	        mc.renderEngine.bindTexture(CLOUDS_TEXTURES);
	        GlStateManager.enableBlend();
	        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
	        float f3 = (float)vec3d.x;
	        float f4 = (float)vec3d.y;
	        float f5 = (float)vec3d.z;
	
	        if (pass != 2)
	        {
	            float f6 = (f3 * 30.0F + f4 * 59.0F + f5 * 11.0F) / 100.0F;
	            float f7 = (f3 * 30.0F + f4 * 70.0F) / 100.0F;
	            float f8 = (f3 * 30.0F + f5 * 70.0F) / 100.0F;
	            f3 = f6;
	            f4 = f7;
	            f5 = f8;
	        }
	
	        float f25 = f3 * 0.9F;
	        float f26 = f4 * 0.9F;
	        float f27 = f5 * 0.9F;
	        float f9 = f3 * 0.7F;
	        float f10 = f4 * 0.7F;
	        float f11 = f5 * 0.7F;
	        float f12 = f3 * 0.8F;
	        float f13 = f4 * 0.8F;
	        float f14 = f5 * 0.8F;
	        float f15 = 0.00390625F;
	        float f16 = (float)MathHelper.floor(d1) * 0.00390625F;
	        float f17 = (float)MathHelper.floor(d2) * 0.00390625F;
	        float f18 = (float)(d1 - (double)MathHelper.floor(d1));
	        float f19 = (float)(d2 - (double)MathHelper.floor(d2));
	        int k = 8;
	        int l = 4;
	        float f20 = 9.765625E-4F;
	        GlStateManager.scale(12.0F, 1.0F, 12.0F);
	
	        for (int i1 = 0; i1 < 2; ++i1)
	        {
	            if (i1 == 0)
	            {
	                GlStateManager.colorMask(false, false, false, false);
	            }
	            else
	            {
	                switch (pass)
	                {
	                    case 0:
	                        GlStateManager.colorMask(false, true, true, true);
	                        break;
	
	                    case 1:
	                        GlStateManager.colorMask(true, false, false, true);
	                        break;
	
	                    case 2:
	                        GlStateManager.colorMask(true, true, true, true);
	                }
	            }
	
	            this.cloudRenderer.renderGlList((float)x, (float)y, (float)z);
	        }
	
	        if (this.cloudRenderer.shouldUpdateGlList((float)y))
	        {
	            this.cloudRenderer.startUpdateGlList();
	
	            for (int l1 = -3; l1 <= 4; ++l1)
	            {
	                for (int j1 = -3; j1 <= 4; ++j1)
	                {
	                    vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
	                    float f21 = (float)(l1 * 8);
	                    float f22 = (float)(j1 * 8);
	                    float f23 = f21 - f18;
	                    float f24 = f22 - f19;
	
	                    if (f2 > -5.0F)
	                    {
	                        vertexbuffer.pos((double)(f23 + 0.0F), (double)(f2 + 0.0F), (double)(f24 + 8.0F)).tex((double)((f21 + 0.0F) * 0.00390625F + f16), (double)((f22 + 8.0F) * 0.00390625F + f17)).color(f9, f10, f11, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
	                        vertexbuffer.pos((double)(f23 + 8.0F), (double)(f2 + 0.0F), (double)(f24 + 8.0F)).tex((double)((f21 + 8.0F) * 0.00390625F + f16), (double)((f22 + 8.0F) * 0.00390625F + f17)).color(f9, f10, f11, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
	                        vertexbuffer.pos((double)(f23 + 8.0F), (double)(f2 + 0.0F), (double)(f24 + 0.0F)).tex((double)((f21 + 8.0F) * 0.00390625F + f16), (double)((f22 + 0.0F) * 0.00390625F + f17)).color(f9, f10, f11, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
	                        vertexbuffer.pos((double)(f23 + 0.0F), (double)(f2 + 0.0F), (double)(f24 + 0.0F)).tex((double)((f21 + 0.0F) * 0.00390625F + f16), (double)((f22 + 0.0F) * 0.00390625F + f17)).color(f9, f10, f11, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
	                    }
	
	                    if (f2 <= 5.0F)
	                    {
	                        vertexbuffer.pos((double)(f23 + 0.0F), (double)(f2 + 4.0F - 9.765625E-4F), (double)(f24 + 8.0F)).tex((double)((f21 + 0.0F) * 0.00390625F + f16), (double)((f22 + 8.0F) * 0.00390625F + f17)).color(f3, f4, f5, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
	                        vertexbuffer.pos((double)(f23 + 8.0F), (double)(f2 + 4.0F - 9.765625E-4F), (double)(f24 + 8.0F)).tex((double)((f21 + 8.0F) * 0.00390625F + f16), (double)((f22 + 8.0F) * 0.00390625F + f17)).color(f3, f4, f5, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
	                        vertexbuffer.pos((double)(f23 + 8.0F), (double)(f2 + 4.0F - 9.765625E-4F), (double)(f24 + 0.0F)).tex((double)((f21 + 8.0F) * 0.00390625F + f16), (double)((f22 + 0.0F) * 0.00390625F + f17)).color(f3, f4, f5, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
	                        vertexbuffer.pos((double)(f23 + 0.0F), (double)(f2 + 4.0F - 9.765625E-4F), (double)(f24 + 0.0F)).tex((double)((f21 + 0.0F) * 0.00390625F + f16), (double)((f22 + 0.0F) * 0.00390625F + f17)).color(f3, f4, f5, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
	                    }
	
	                    if (l1 > -1)
	                    {
	                        for (int k1 = 0; k1 < 8; ++k1)
	                        {
	                            vertexbuffer.pos((double)(f23 + (float)k1 + 0.0F), (double)(f2 + 0.0F), (double)(f24 + 8.0F)).tex((double)((f21 + (float)k1 + 0.5F) * 0.00390625F + f16), (double)((f22 + 8.0F) * 0.00390625F + f17)).color(f25, f26, f27, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
	                            vertexbuffer.pos((double)(f23 + (float)k1 + 0.0F), (double)(f2 + 4.0F), (double)(f24 + 8.0F)).tex((double)((f21 + (float)k1 + 0.5F) * 0.00390625F + f16), (double)((f22 + 8.0F) * 0.00390625F + f17)).color(f25, f26, f27, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
	                            vertexbuffer.pos((double)(f23 + (float)k1 + 0.0F), (double)(f2 + 4.0F), (double)(f24 + 0.0F)).tex((double)((f21 + (float)k1 + 0.5F) * 0.00390625F + f16), (double)((f22 + 0.0F) * 0.00390625F + f17)).color(f25, f26, f27, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
	                            vertexbuffer.pos((double)(f23 + (float)k1 + 0.0F), (double)(f2 + 0.0F), (double)(f24 + 0.0F)).tex((double)((f21 + (float)k1 + 0.5F) * 0.00390625F + f16), (double)((f22 + 0.0F) * 0.00390625F + f17)).color(f25, f26, f27, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
	                        }
	                    }
	
	                    if (l1 <= 1)
	                    {
	                        for (int i2 = 0; i2 < 8; ++i2)
	                        {
	                            vertexbuffer.pos((double)(f23 + (float)i2 + 1.0F - 9.765625E-4F), (double)(f2 + 0.0F), (double)(f24 + 8.0F)).tex((double)((f21 + (float)i2 + 0.5F) * 0.00390625F + f16), (double)((f22 + 8.0F) * 0.00390625F + f17)).color(f25, f26, f27, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
	                            vertexbuffer.pos((double)(f23 + (float)i2 + 1.0F - 9.765625E-4F), (double)(f2 + 4.0F), (double)(f24 + 8.0F)).tex((double)((f21 + (float)i2 + 0.5F) * 0.00390625F + f16), (double)((f22 + 8.0F) * 0.00390625F + f17)).color(f25, f26, f27, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
	                            vertexbuffer.pos((double)(f23 + (float)i2 + 1.0F - 9.765625E-4F), (double)(f2 + 4.0F), (double)(f24 + 0.0F)).tex((double)((f21 + (float)i2 + 0.5F) * 0.00390625F + f16), (double)((f22 + 0.0F) * 0.00390625F + f17)).color(f25, f26, f27, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
	                            vertexbuffer.pos((double)(f23 + (float)i2 + 1.0F - 9.765625E-4F), (double)(f2 + 0.0F), (double)(f24 + 0.0F)).tex((double)((f21 + (float)i2 + 0.5F) * 0.00390625F + f16), (double)((f22 + 0.0F) * 0.00390625F + f17)).color(f25, f26, f27, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
	                        }
	                    }
	
	                    if (j1 > -1)
	                    {
	                        for (int j2 = 0; j2 < 8; ++j2)
	                        {
	                            vertexbuffer.pos((double)(f23 + 0.0F), (double)(f2 + 4.0F), (double)(f24 + (float)j2 + 0.0F)).tex((double)((f21 + 0.0F) * 0.00390625F + f16), (double)((f22 + (float)j2 + 0.5F) * 0.00390625F + f17)).color(f12, f13, f14, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
	                            vertexbuffer.pos((double)(f23 + 8.0F), (double)(f2 + 4.0F), (double)(f24 + (float)j2 + 0.0F)).tex((double)((f21 + 8.0F) * 0.00390625F + f16), (double)((f22 + (float)j2 + 0.5F) * 0.00390625F + f17)).color(f12, f13, f14, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
	                            vertexbuffer.pos((double)(f23 + 8.0F), (double)(f2 + 0.0F), (double)(f24 + (float)j2 + 0.0F)).tex((double)((f21 + 8.0F) * 0.00390625F + f16), (double)((f22 + (float)j2 + 0.5F) * 0.00390625F + f17)).color(f12, f13, f14, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
	                            vertexbuffer.pos((double)(f23 + 0.0F), (double)(f2 + 0.0F), (double)(f24 + (float)j2 + 0.0F)).tex((double)((f21 + 0.0F) * 0.00390625F + f16), (double)((f22 + (float)j2 + 0.5F) * 0.00390625F + f17)).color(f12, f13, f14, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
	                        }
	                    }
	
	                    if (j1 <= 1)
	                    {
	                        for (int k2 = 0; k2 < 8; ++k2)
	                        {
	                            vertexbuffer.pos((double)(f23 + 0.0F), (double)(f2 + 4.0F), (double)(f24 + (float)k2 + 1.0F - 9.765625E-4F)).tex((double)((f21 + 0.0F) * 0.00390625F + f16), (double)((f22 + (float)k2 + 0.5F) * 0.00390625F + f17)).color(f12, f13, f14, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
	                            vertexbuffer.pos((double)(f23 + 8.0F), (double)(f2 + 4.0F), (double)(f24 + (float)k2 + 1.0F - 9.765625E-4F)).tex((double)((f21 + 8.0F) * 0.00390625F + f16), (double)((f22 + (float)k2 + 0.5F) * 0.00390625F + f17)).color(f12, f13, f14, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
	                            vertexbuffer.pos((double)(f23 + 8.0F), (double)(f2 + 0.0F), (double)(f24 + (float)k2 + 1.0F - 9.765625E-4F)).tex((double)((f21 + 8.0F) * 0.00390625F + f16), (double)((f22 + (float)k2 + 0.5F) * 0.00390625F + f17)).color(f12, f13, f14, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
	                            vertexbuffer.pos((double)(f23 + 0.0F), (double)(f2 + 0.0F), (double)(f24 + (float)k2 + 1.0F - 9.765625E-4F)).tex((double)((f21 + 0.0F) * 0.00390625F + f16), (double)((f22 + (float)k2 + 0.5F) * 0.00390625F + f17)).color(f12, f13, f14, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
	                        }
	                    }
	
	                    tessellator.draw();
	                }
	            }
	
	            this.cloudRenderer.endUpdateGlList((float)x, (float)y, (float)z);
	        }
	
	        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	        GlStateManager.disableBlend();
	        GlStateManager.enableCull();
        	GlStateManager.popMatrix();
        }
    }
	
    public float getCelestialAngle()
    {
        return this.worldProvider.calculateCelestialAngle(time, 0);
    }
    
    public float getCelestialAngleRadians()
    {
        float f = this.getCelestialAngle();
        return f * ((float)Math.PI * 2F);
    }

    public int getMoonPhase()
    {
        return this.worldProvider.getMoonPhase(time);
    }

	public float getSunBrightness() {
        float f = this.getCelestialAngle();
        float f1 = 1.0F - (MathHelper.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.2F);
        f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
        f1 = 1.0F - f1;
        f1 = (float)((double)f1 * (1.0D - (double)(/*this.getRainStrength(partialTicks)*/ 0 * 5.0F) / 16.0D));
        f1 = (float)((double)f1 * (1.0D - (double)(/*this.getThunderStrength(partialTicks)*/ 0 * 5.0F) / 16.0D));
        return f1 * 0.8F + 0.2F;
	}

    public float getStarBrightness()
    {
        float f = this.getCelestialAngle();
        float f1 = 1.0F - (MathHelper.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.25F);
        f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
        return f1 * f1 * 0.5F;
    }
	
    public Vec3d getSkyColor(float x, float y, float z)
    {
        float f = this.getCelestialAngle();
        float f1 = MathHelper.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.5F;
        f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
        int i = MathHelper.floor(x);
        int j = MathHelper.floor(y);
        int k = MathHelper.floor(z);
        BlockPos blockpos = new BlockPos(i, j, k);
        Biome biome = this.blockAccess.getBiome(blockpos);
        float f2 = biome.getFloatTemperature(blockpos);
        int l = biome.getSkyColorByTemp(f2);
        float f3 = (float)(l >> 16 & 255) / 255.0F;
        float f4 = (float)(l >> 8 & 255) / 255.0F;
        float f5 = (float)(l & 255) / 255.0F;
        f3 = f3 * f1;
        f4 = f4 * f1;
        f5 = f5 * f1;
        /*float f6 = this.getRainStrength(partialTicks);

        if (f6 > 0.0F)
        {
            float f7 = (f3 * 0.3F + f4 * 0.59F + f5 * 0.11F) * 0.6F;
            float f8 = 1.0F - f6 * 0.75F;
            f3 = f3 * f8 + f7 * (1.0F - f8);
            f4 = f4 * f8 + f7 * (1.0F - f8);
            f5 = f5 * f8 + f7 * (1.0F - f8);
        }

        float f10 = this.getThunderStrength(partialTicks);

        if (f10 > 0.0F)
        {
            float f11 = (f3 * 0.3F + f4 * 0.59F + f5 * 0.11F) * 0.2F;
            float f9 = 1.0F - f10 * 0.75F;
            f3 = f3 * f9 + f11 * (1.0F - f9);
            f4 = f4 * f9 + f11 * (1.0F - f9);
            f5 = f5 * f9 + f11 * (1.0F - f9);
        }

        if (this.lastLightningBolt > 0)
        {
            float f12 = (float)this.lastLightningBolt - partialTicks;

            if (f12 > 1.0F)
            {
                f12 = 1.0F;
            }

            f12 = f12 * 0.45F;
            f3 = f3 * (1.0F - f12) + 0.8F * f12;
            f4 = f4 * (1.0F - f12) + 0.8F * f12;
            f5 = f5 * (1.0F - f12) + 1.0F * f12;
        }*/

        return new Vec3d((double)f3, (double)f4, (double)f5);
    }
    
    public Vec3d getFogColor()
    {
        float f = this.getCelestialAngle();
        return this.worldProvider.getFogColor(f, 0);
    }

    public Vec3d getCloudColour()
    {
        float f = this.getCelestialAngle();
        float f1 = MathHelper.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.5F;
        f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
        float f2 = 1.0F;
        float f3 = 1.0F;
        float f4 = 1.0F;
        /*float f5 = this.getRainStrength(partialTicks);

        if (f5 > 0.0F)
        {
            float f6 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.6F;
            float f7 = 1.0F - f5 * 0.95F;
            f2 = f2 * f7 + f6 * (1.0F - f7);
            f3 = f3 * f7 + f6 * (1.0F - f7);
            f4 = f4 * f7 + f6 * (1.0F - f7);
        }*/

        f2 = f2 * (f1 * 0.9F + 0.1F);
        f3 = f3 * (f1 * 0.9F + 0.1F);
        f4 = f4 * (f1 * 0.85F + 0.15F);
        /*float f9 = this.getThunderStrength(partialTicks);

        if (f9 > 0.0F)
        {
            float f10 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.2F;
            float f8 = 1.0F - f9 * 0.95F;
            f2 = f2 * f8 + f10 * (1.0F - f8);
            f3 = f3 * f8 + f10 * (1.0F - f8);
            f4 = f4 * f8 + f10 * (1.0F - f8);
        }*/

        return new Vec3d((double)f2, (double)f3, (double)f4);
    }

    private void generateSky() throws Exception
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();

        if (this.skyVBO != null) {
            this.skyVBO.deleteGlBuffers();
        }

        this.skyVBO = new VertexBuffer(this.vertexBufferFormat);
        mc.renderGlobal.renderSky(vertexbuffer, 16.0F, false);
        vertexbuffer.finishDrawing();
        vertexbuffer.reset();
        this.skyVBO.bufferData(vertexbuffer.getByteBuffer());
    }

    private void generateSky2() throws Exception
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();

        if (this.sky2VBO != null) {
            this.sky2VBO.deleteGlBuffers();
        }
        this.sky2VBO = new VertexBuffer(this.vertexBufferFormat);
        mc.renderGlobal.renderSky(vertexbuffer, -16.0F, true);
        vertexbuffer.finishDrawing();
        vertexbuffer.reset();
        this.sky2VBO.bufferData(vertexbuffer.getByteBuffer());
    }

    private void generateStars() throws Exception
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();

        if (this.starVBO != null)
        {
            this.starVBO.deleteGlBuffers();
        }

        this.starVBO = new VertexBuffer(this.vertexBufferFormat);
        mc.renderGlobal.renderStars(vertexbuffer);
        vertexbuffer.finishDrawing();
        vertexbuffer.reset();
        this.starVBO.bufferData(vertexbuffer.getByteBuffer());
    }
    
    public void disableLightmap()
    {
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }
	
    public void enableLightmap()
    {
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.matrixMode(GL11.GL_TEXTURE);
        GlStateManager.loadIdentity();
        float f = 0.00390625F;
        GlStateManager.scale(f, f, f);
        GlStateManager.translate(8.0F, 8.0F, 8.0F);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        mc.getTextureManager().bindTexture(this.locationLightMap);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }
	
	public void updateTorchFlicker()
    {
        this.torchFlickerDX = (float)((double)this.torchFlickerDX + (Math.random() - Math.random()) * Math.random() * Math.random());
        this.torchFlickerDX = (float)((double)this.torchFlickerDX * 0.9D);
        this.torchFlickerX += this.torchFlickerDX - this.torchFlickerX;
        this.lightmapUpdateNeeded = true;
    }

    public void updateLightmap()
    {
        if (this.lightmapUpdateNeeded)
        {
            /*if (Config.isCustomColors() && CustomColors.updateLightmap(world, this.torchFlickerX, this.lightmapColors, this.mc.player.isPotionActive(MobEffects.NIGHT_VISION)))
            {
                this.lightmapTexture.updateDynamicTexture();
                this.lightmapUpdateNeeded = false;
                return;
            }*/

            float f = this.getSunBrightness();
            float f1 = f * 0.95F + 0.05F;

            for (int i = 0; i < 256; ++i)
            {
                float f2 = this.worldProvider.getLightBrightnessTable()[i / 16] * f1;
                float f3 = this.worldProvider.getLightBrightnessTable()[i % 16] * (this.torchFlickerX * 0.1F + 1.5F);

                /*if (world.getLastLightningBolt() > 0)
                {
                    f2 = this.worldProvider.getLightBrightnessTable()[i / 16];
                }*/

                float f4 = f2 * (f * 0.65F + 0.35F);
                float f5 = f2 * (f * 0.65F + 0.35F);
                float f6 = f3 * ((f3 * 0.6F + 0.4F) * 0.6F + 0.4F);
                float f7 = f3 * (f3 * f3 * 0.6F + 0.4F);
                float f8 = f4 + f3;
                float f9 = f5 + f6;
                float f10 = f2 + f7;
                f8 = f8 * 0.96F + 0.03F;
                f9 = f9 * 0.96F + 0.03F;
                f10 = f10 * 0.96F + 0.03F;

                if (blockAccess.getDimensionType().getId() == 1)
                {
                    f8 = 0.22F + f3 * 0.75F;
                    f9 = 0.28F + f6 * 0.75F;
                    f10 = 0.25F + f7 * 0.75F;
                }

                if (f8 > 1.0F)
                {
                    f8 = 1.0F;
                }

                if (f9 > 1.0F)
                {
                    f9 = 1.0F;
                }

                if (f10 > 1.0F)
                {
                    f10 = 1.0F;
                }

                float f16 = mc.gameSettings.gammaSetting;
                float f17 = 1.0F - f8;
                float f13 = 1.0F - f9;
                float f14 = 1.0F - f10;
                f17 = 1.0F - f17 * f17 * f17 * f17;
                f13 = 1.0F - f13 * f13 * f13 * f13;
                f14 = 1.0F - f14 * f14 * f14 * f14;
                f8 = f8 * (1.0F - f16) + f17 * f16;
                f9 = f9 * (1.0F - f16) + f13 * f16;
                f10 = f10 * (1.0F - f16) + f14 * f16;
                f8 = f8 * 0.96F + 0.03F;
                f9 = f9 * 0.96F + 0.03F;
                f10 = f10 * 0.96F + 0.03F;

                if (f8 > 1.0F)
                {
                    f8 = 1.0F;
                }

                if (f9 > 1.0F)
                {
                    f9 = 1.0F;
                }

                if (f10 > 1.0F)
                {
                    f10 = 1.0F;
                }

                if (f8 < 0.0F)
                {
                    f8 = 0.0F;
                }

                if (f9 < 0.0F)
                {
                    f9 = 0.0F;
                }

                if (f10 < 0.0F)
                {
                    f10 = 0.0F;
                }

                int j = 255;
                int k = (int)(f8 * 255.0F);
                int l = (int)(f9 * 255.0F);
                int i1 = (int)(f10 * 255.0F);
                this.lightmapColors[i] = -16777216 | k << 16 | l << 8 | i1;
            }

            this.lightmapTexture.updateDynamicTexture();
            this.lightmapUpdateNeeded = false;
        }
    }
    
    public static class MenuCloudRenderer
    {
        private Minecraft mc;
        private boolean updated = false;
        float partialTicks;
        private int glListClouds = -1;
        private int cloudTickCounterUpdate = 0;
        private double cloudPlayerX = 0.0D;
        private double cloudPlayerY = 0.0D;
        private double cloudPlayerZ = 0.0D;
        private Vec3d color;
        private Vec3d lastColor;

        public MenuCloudRenderer(Minecraft p_i23_1_)
        {
            this.mc = p_i23_1_;
            this.glListClouds = GLAllocation.generateDisplayLists(1);
        }

        public void prepareToRender(float partialTicks, Vec3d color)
        {
            this.partialTicks = partialTicks;
            this.lastColor = this.color;
            this.color = color;
        }

        public boolean shouldUpdateGlList(float posY)
        {
            if (!this.updated)
            {
                return true;
            }
            else if (this.mc.tickCounter >= this.cloudTickCounterUpdate + 100)
            {
                return true;
            }
            else if (!this.color.equals(this.lastColor) && this.mc.tickCounter >= this.cloudTickCounterUpdate + 1)
            {
                return true;
            }
            else
            {
                boolean flag = this.cloudPlayerY < 128.0D + (double)(this.mc.gameSettings.ofCloudsHeight * 128.0F);
                boolean flag1 = posY < 128.0D + (double)(this.mc.gameSettings.ofCloudsHeight * 128.0F);
                return flag1 != flag;
            }
        }

        public void startUpdateGlList()
        {
            GL11.glNewList(this.glListClouds, GL11.GL_COMPILE);
        }

        public void endUpdateGlList(float x, float y, float z)
        {
            GL11.glEndList();
            this.cloudTickCounterUpdate = this.mc.tickCounter;
            this.cloudPlayerX = x;
            this.cloudPlayerY = y;
            this.cloudPlayerZ = z;
            this.updated = true;
            GlStateManager.resetColor();
        }

        public void renderGlList(float x, float y, float z)
        {
            double d3 = (this.mc.tickCounter - this.cloudTickCounterUpdate) + this.partialTicks;
            float f = (float)(x - this.cloudPlayerX + d3 * 0.03D);
            float f1 = (float)(y - this.cloudPlayerY);
            float f2 = (float)(z - this.cloudPlayerZ);
            GlStateManager.pushMatrix();

            GlStateManager.translate(-f / 12.0F, -f1, -f2 / 12.0F);

            GlStateManager.callList(this.glListClouds);
            GlStateManager.popMatrix();
            GlStateManager.resetColor();
        }

        public void reset()
        {
            this.updated = false;
        }
    }
}
