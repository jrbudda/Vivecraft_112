package com.mtbs3d.minecrift.api;

import java.nio.FloatBuffer;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.util.vector.Matrix4f;

import com.google.common.base.Charsets;
import com.mtbs3d.minecrift.render.PlayerModelController;
import com.mtbs3d.minecrift.utils.Quaternion;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.util.math.Vec3d;

public class NetworkHelper {

	public enum PacketDiscriminators {
		VERSION,
		REQUESTDATA,
		HEADDATA,
		CONTROLLER0DATA,
		CONTROLLER1DATA,
		WORLDSCALE,
		DRAW,
		MOVEMODE,
		UBERPACKET,
		TELEPORT,
		CLIMBING
	}
	private final static String channel = "Vivecraft";
	
	public static CPacketCustomPayload getVivecraftClientPacket(PacketDiscriminators command, byte[] payload)
	{
		PacketBuffer pb = new PacketBuffer(Unpooled.buffer());
		pb.writeByte(command.ordinal());
		pb.writeBytes(payload);
        return  (new CPacketCustomPayload(channel, pb));
	}
	
	public static SPacketCustomPayload getVivecraftServerPacket(PacketDiscriminators command, byte[] payload)
	{
		PacketBuffer pb = new PacketBuffer(Unpooled.buffer());
		pb.writeByte(command.ordinal());
		pb.writeBytes(payload);
        return (new SPacketCustomPayload(channel, pb));
	}
	
	public static SPacketCustomPayload getVivecraftServerPacket(PacketDiscriminators command, String payload)
	{
		PacketBuffer pb = new PacketBuffer(Unpooled.buffer());
		pb.writeByte(command.ordinal());
		pb.writeString(payload);
        return (new SPacketCustomPayload(channel, pb));
	}
	
	
	public static boolean serverWantsData = false;
	public static boolean serverAllowsClimbey = false;
	public static boolean serverSupportsDirectTeleport = false;
	
	private static float worldScallast = 0;

	public static void sendVRPlayerPositions(IRoomscaleAdapter player) {
		if(!serverWantsData) return;
		float worldScale = Minecraft.getMinecraft().vrPlayer.worldScale;
	
		if (worldScale != worldScallast) {
			ByteBuf payload = Unpooled.buffer();
			payload.writeFloat(worldScale);
			byte[] out = new byte[payload.readableBytes()];
			payload.readBytes(out);
			CPacketCustomPayload pack = getVivecraftClientPacket(PacketDiscriminators.WORLDSCALE,out);
			Minecraft.getMinecraft().getConnection().sendPacket(pack);
			
			worldScallast = worldScale;
		}
		byte[] a=null, b = null, c=null;
		{
			FloatBuffer buffer = player.getHMDMatrix_World();
			buffer.rewind();
			Matrix4f matrix = new Matrix4f();
			matrix.load(buffer);

			Vec3d headPosition = player.getHMDPos_World();
			Quaternion headRotation = new Quaternion(matrix);
			
			ByteBuf payload = Unpooled.buffer();
			payload.writeBoolean(Minecraft.getMinecraft().vrSettings.seated);
			payload.writeFloat((float)headPosition.x);
			payload.writeFloat((float)headPosition.y);
			payload.writeFloat((float)headPosition.z);
			payload.writeFloat((float)headRotation.w);
			payload.writeFloat((float)headRotation.x);
			payload.writeFloat((float)headRotation.y);
			payload.writeFloat((float)headRotation.z);
			byte[] out = new byte[payload.readableBytes()];
			payload.readBytes(out);
			a = out;
			CPacketCustomPayload pack = getVivecraftClientPacket(PacketDiscriminators.HEADDATA,out);
			Minecraft.getMinecraft().getConnection().sendPacket(pack);
			
		}	
		
		for (int i = 0; i < 2; i++) {
			Vec3d controllerPosition = player.getControllerPos_World(i);
			FloatBuffer buffer = player.getControllerMatrix_World(i);
			buffer.rewind();
			Matrix4f matrix = new Matrix4f();
			matrix.load(buffer);
			Quaternion controllerRotation = new Quaternion(matrix);
		
			ByteBuf payload = Unpooled.buffer();
			payload.writeBoolean(Minecraft.getMinecraft().vrSettings.vrReverseHands);
			payload.writeFloat((float)controllerPosition.x);
			payload.writeFloat((float)controllerPosition.y);
			payload.writeFloat((float)controllerPosition.z);
			payload.writeFloat((float)controllerRotation.w);
			payload.writeFloat((float)controllerRotation.x);
			payload.writeFloat((float)controllerRotation.y);
			payload.writeFloat((float)controllerRotation.z);
			byte[] out = new byte[payload.readableBytes()];
			if(i == 0) b = out;
			else c = out;
			payload.readBytes(out);
			CPacketCustomPayload pack  = getVivecraftClientPacket(i == 0? PacketDiscriminators.CONTROLLER0DATA : PacketDiscriminators.CONTROLLER1DATA,out);
			Minecraft.getMinecraft().getConnection().sendPacket(pack);
		}
		
		PlayerModelController.getInstance().Update(Minecraft.getMinecraft().player.getUniqueID(), a, b, c);
		
	}
}
