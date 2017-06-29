/**
 * Copyright 2013 Mark Browning, StellaArtois
 * Licensed under the LGPL 3.0 or later (See LICENSE.md for details)
 */
package com.mtbs3d.minecrift.provider;


import com.mtbs3d.minecrift.api.*;

import com.mtbs3d.minecrift.settings.VRSettings;
import de.fruitfly.ovr.*;
import de.fruitfly.ovr.enums.*;
import de.fruitfly.ovr.structs.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Quaternion;

public class MCOculus extends OculusRift //OculusRift does most of the heavy lifting
{
    public static final int NOT_CALIBRATING = 0;
    public static final int CALIBRATE_AWAITING_FIRST_ORIGIN = 1;
    public static final int CALIBRATE_AT_FIRST_ORIGIN = 2;
    public static final int CALIBRATE_COOLDOWN = 7;
    public static final int CALIBRATE_ABORTED_COOLDOWN = 8;

    public static final long COOLDOWNTIME_MS = 1500L;

    private boolean isCalibrated = false;
    private long coolDownStart = 0L;
    private int calibrationStep = NOT_CALIBRATING;
    private int MagCalSampleCount = 0;
    private boolean forceMagCalibration = false; // Don't force mag cal initially
    private double PredictedDisplayTime = 0d;
    private float yawOffsetRad = 0f;
    private float pitchOffsetRad = 0f;
    private float rollHeadRad = 0f;
    private float pitchHeadRad = 0f;
    private float yawHeadRad = 0f;
    private Posef[] eyePose = new Posef[3];
    private EulerOrient[] eulerOrient = new EulerOrient[3];
    long lastIndex = -1;
    FullPoseState fullPoseState = new FullPoseState();
    boolean isCalibrating = false;

    public MCOculus()
    {
        super();
        eyePose[0] = new Posef();
        eyePose[1] = new Posef();
        eyePose[2] = new Posef();
        eulerOrient[0] = new EulerOrient();
        eulerOrient[1] = new EulerOrient();
        eulerOrient[2] = new EulerOrient();
    }

    
    public EyeType eyeRenderOrder(int index)
    {
        return EyeType.fromInteger(index);
    }

    
    public String getVersion()
    {
        return OculusRift.getVersionString();
    }

    
    public boolean usesDistortion() {
        return true;
    }

    
    public boolean isStereo() {
        return true;
    }

    
    public boolean isGuiOrtho()
    {
        return false;
    }

    public double getFrameTiming() { return PredictedDisplayTime; };

    public static UserProfileData theProfileData = null;

    
    public void beginFrame()
    {
        beginFrame(0);
    }

    
    public void beginFrame(long frameIndex)
    {

    }

    
    public FullPoseState getTrackedPoses(long frameIndex)
    {
        return fullPoseState;
    }

    public Matrix4f getProjectionMatrix(FovPort fov,
                                        EyeType eyeType,      // VIVE added eyeType
                                        float nearClip,
                                        float farClip)
    {
         return super.getProjectionMatrix(fov, nearClip, farClip);
    }

    public boolean endFrame()
    {
        // End the frame
        ErrorInfo result = submitFrame();

        Display.processMessages();

        if (result == null)
            return true;

        return result.unqualifiedSuccess;
    }

    
    public HmdParameters getHMDInfo()
    {
        HmdParameters hmdDesc = new HmdParameters();
        if (isInitialized())
            hmdDesc = getHmdParameters();

        return hmdDesc;
    }

	
	public String getName() {
		return "Oculus Rift";
	}

	
	public String getID() {
		return "oculus";
	}

    
    public void update(float ipd,
                       float yawHeadDegrees,
                       float pitchHeadDegrees,
                       float rollHeadDegrees,
                       float worldYawOffsetDegrees,
                       float worldPitchOffsetDegrees,
                       float worldRollOffsetDegrees)
    {
        rollHeadRad = (float)Math.toRadians(rollHeadDegrees);
        pitchHeadRad = (float)Math.toRadians(pitchHeadDegrees);
        yawHeadRad =  (float)Math.toRadians(yawHeadDegrees);
        yawOffsetRad = (float)Math.toRadians(worldYawOffsetDegrees);
        pitchOffsetRad = (float)Math.toRadians(worldPitchOffsetDegrees);
    }

    
//    
//    public Vec3 getEyePosition(EyeType eye)
//   {
//        VRSettings vr = Minecraft.getMinecraft().vrSettings;
//        Vec3 eyePosition = Vec3.createVectorHelper(0, 0, 0);
//        if (vr.usePositionTracking)
//        {
//            float posTrackScale = vr.posTrackDistanceScale;
//            if (vr.debugPos) {
//                posTrackScale = 1f;
//            }
//            Vector3f eyePos = super.getEyePos(eye);
//            eyePosition = Vec3.createVectorHelper(eyePos.x * posTrackScale,
//                                                  eyePos.y * posTrackScale,
//                                                  eyePos.z * posTrackScale);
//        }
//
//        return eyePosition;
//    }

    
	public void resetOrigin() {
        super.resetTracking();
    }

    
    public void resetOriginRotation() {
        // TODO:
    }

    
    public void setPrediction(float delta, boolean enable) {
        // Now ignored
    }


    
    private void processCalibration()
    {
        switch (calibrationStep)
        {
            case NOT_CALIBRATING:
            {
                calibrationStep = CALIBRATE_AWAITING_FIRST_ORIGIN;
                isCalibrated = false;
                break;
            }
            case CALIBRATE_AT_FIRST_ORIGIN:
            {
                //_reset();

                // Calibration of Mag cal is now handled solely by the Oculus config utility.

                MagCalSampleCount = 0;
                coolDownStart = System.currentTimeMillis();
                calibrationStep = CALIBRATE_COOLDOWN;
                resetOrigin();

                break;
            }
            case CALIBRATE_COOLDOWN:
            {
                if ((System.currentTimeMillis() - coolDownStart) > COOLDOWNTIME_MS)
                {
                    coolDownStart = 0;
                    calibrationStep = NOT_CALIBRATING;
                    isCalibrated = true;
                }
                break;
            }
            case CALIBRATE_ABORTED_COOLDOWN:
            {
                if ((System.currentTimeMillis() - coolDownStart) > COOLDOWNTIME_MS)
                {
                    coolDownStart = 0;
                    calibrationStep = NOT_CALIBRATING;
                    isCalibrated = true;
                    isCalibrating = false;
                }
                break;
            }
        }
    }

    
    public void poll(long frameIndex)
    {
        //System.out.println("lastIndex: " + lastIndex);

        EyeType eye;
        if (!isInitialized())
            return;
        if (frameIndex <= this.lastIndex)
            return;

        this.lastIndex = frameIndex;

        // Get our eye pose and tracker state in one hit
        fullPoseState = super.getTrackedPoses(frameIndex);
        PredictedDisplayTime = fullPoseState.PredictedDisplayTime;
        //System.out.println(fullPoseState.toString());

        // Set left eye pose
        eye = EyeType.ovrEye_Left;
        this.eyePose[eye.value()] = fullPoseState.leftEyePose;
        this.eulerOrient[eye.value()] = OculusRift.getEulerAnglesDeg(this.eyePose[eye.value()].Orientation,
                1.0f,
                Axis.Axis_Y,
                Axis.Axis_X,
                Axis.Axis_Z,
                HandedSystem.Handed_L,
                RotateDirection.Rotate_CCW);

        // Set right eye pose
        eye = EyeType.ovrEye_Right;
        this.eyePose[eye.value()] = fullPoseState.rightEyePose;
        this.eulerOrient[eye.value()] = OculusRift.getEulerAnglesDeg(this.eyePose[eye.value()].Orientation,
                1.0f,
                Axis.Axis_Y,
                Axis.Axis_X,
                Axis.Axis_Z,
                HandedSystem.Handed_L,
                RotateDirection.Rotate_CCW);

        // Set center eye pose
        eye = EyeType.ovrEye_Center;
        this.eyePose[eye.value()] = fullPoseState.centerEyePose.ThePose;
        this.eulerOrient[eye.value()] = OculusRift.getEulerAnglesDeg(this.eyePose[eye.value()].Orientation,
                1.0f,
                Axis.Axis_Y,
                Axis.Axis_X,
                Axis.Axis_Z,
                HandedSystem.Handed_L,
                RotateDirection.Rotate_CCW);
    }


	
	public float getHeadYawDegrees(EyeType eye)
    {
        return this.eulerOrient[eye.value()].yaw;
	}

	
	public float getHeadPitchDegrees(EyeType eye)
    {
        return this.eulerOrient[eye.value()].pitch;
	}

	
	public float getHeadRollDegrees(EyeType eye)
    {
        return this.eulerOrient[eye.value()].roll;
	}

    
    public Quaternion getOrientationQuaternion(EyeType eye)
    {
        Quatf orient = this.eyePose[eye.value()].Orientation;
        return new Quaternion(orient.x, orient.y, orient.z, orient.w);
    }

    
    public UserProfileData getProfileData()
    {
        UserProfileData userProfile = null;

        if (isInitialized())
        {
            userProfile = _getUserProfileData();
        }
        else
        {
            userProfile = new UserProfileData();
        }

        return userProfile;
    }

    
    public double getCurrentTimeSecs()
    {
        return getCurrentTimeSeconds();
    }

    
    public boolean providesRenderTextures() { return true; }

    
    public boolean providesMirrorTexture() { return true; }

    // VIVE START
    public void onGuiScreenChanged(GuiScreen previousScreen, GuiScreen newScreen) { }
    // VIVE END

	

	
	public RenderTextureSet createRenderTexture(int width, int height) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public boolean endFrame(EyeType eye) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean isHMDTracking() {
		// TODO Auto-generated method stub
		return false;
	}
}
