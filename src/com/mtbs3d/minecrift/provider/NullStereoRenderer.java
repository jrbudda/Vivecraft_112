package com.mtbs3d.minecrift.provider;

import com.mtbs3d.minecrift.api.IStereoProvider;
import de.fruitfly.ovr.enums.EyeType;
import de.fruitfly.ovr.structs.*;
import net.minecraft.client.Minecraft.renderPass;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

/**
 * Created by StellaArtois on 26/6/2014.
 */
public class NullStereoRenderer implements IStereoProvider
{

    public String getID() {
        return "mono";
    }


    public String getName() {
        return "Mono";
    }

    @Override
    public EyeType eyeRenderOrder(int index)
    {
        //return EyeType.ovrEye_Center;
        return EyeType.ovrEye_Left; // Hack for now
    }

    @Override
    public boolean usesDistortion() {
        return false;
    }

    @Override
    public boolean isStereo() {
        return false;
    }

    @Override
    public double getFrameTiming() { return (double)System.currentTimeMillis() / 1000d; }
    
    @Override
    public Matrix4f getProjectionMatrix(FovPort fov, int eyeType, float nearClip, float farClip) {
        return null;
    } // VIVE included eyeType

    @Override
    public double getCurrentTimeSecs()
    {
        return System.nanoTime() / 1000000000d;
    }

    @Override
    public boolean providesMirrorTexture() { return false; }

    @Override
    public int createMirrorTexture(int width, int height)
    {
        return -1;
    }

    @Override
    public void deleteMirrorTexture() {}

    @Override
    public boolean providesRenderTextures() { return false; }

    @Override
    public RenderTextureSet createRenderTexture(int lwidth, int lheight)
    { 
        return null;
    }

	@Override
	public boolean setCurrentRenderTextureInfo(int index, int textureIdx, int depthId, int depthWidth, int depthHeight) {
		return true;
	}

    @Override
    public void deleteRenderTextures() {}

    @Override
    public String getLastError() { return "Success"; }

    @Override
    public void configureRenderer(GLConfig cfg) {}

    // VIVE START
    public void onGuiScreenChanged(GuiScreen previousScreen, GuiScreen newScreen) { }
    // VIVE END

	@Override
	public boolean endFrame(renderPass eye) {
		this.endFrame();
		return true;
	}
	

	@Override
	public boolean providesStencilMask() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public float[] getStencilMask(renderPass eye) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public RenderTextureInfo getRenderTextureSizes(float renderScaleFactor) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void endFrame() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean isInitialized() {
		return true;
	}


	@Override
	public String getinitError() {
		return "u dun goof'd";
	}

}
