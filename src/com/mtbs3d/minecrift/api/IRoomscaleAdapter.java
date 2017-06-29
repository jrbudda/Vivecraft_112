package com.mtbs3d.minecrift.api;

import java.nio.FloatBuffer;

import org.lwjgl.util.vector.Quaternion;

import com.mtbs3d.minecrift.render.QuaternionHelper;

import de.fruitfly.ovr.util.BufferUtil;
import net.minecraft.client.Minecraft.renderPass;
import net.minecraft.util.math.Vec3d;

/**
 * This interface defines convenience methods for getting 'world coordinate' vectors from room-scale VR systems.
 *
 * @author jrbudda
 *
 */
public interface IRoomscaleAdapter  {

    public boolean isHMDTracking();
	public Vec3d getHMDPos_World();
	public Vec3d getHMDPos_Room(); 
	public Vec3d getHMDDir_World(); 
	public float getHMDYaw_World();  //degrees
	public float getHMDPitch_World(); //degrees
	
	public FloatBuffer getHMDMatrix_World();
	public FloatBuffer getHMDMatrix_Room();
	public FloatBuffer getControllerMatrix_World(int controller);
	
	public Vec3d getEyePos_World(renderPass currentPass);
	public Vec3d getEyePos_Room(renderPass currentPass);

	public float getControllerYaw_Room(int controller); //degrees
	public float getControllerPitch_Room(int controller); //degrees
	public Vec3d getControllerPos_Room(int i);
	public Vec3d getControllerDir_Room(int c);
	
	public float getControllerYaw_World(int controller); //degrees
	public float getControllerPitch_World(int controller); //degrees
	public float getControllerRoll_World(int controller); //degrees
	public Vec3d getControllerPos_World(int c);
	public Vec3d getControllerDir_World(int c);
	
	public boolean isControllerTracking(int c);
	
	public Vec3d getCustomControllerVector(int controller, Vec3d axis);
	public Vec3d getCustomHMDVector(Vec3d axis);

	public Vec3d getRoomOriginPos_World(); //degrees
	public Vec3d getRoomOriginUpDir_World(); //what do you do
	


	
}

