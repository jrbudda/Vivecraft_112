package com.mtbs3d.minecrift.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.google.common.base.Throwables;

import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.world.WorldProvider;

public class MCReflection {
	public static Field SoundManager_sndSystem = getDeclaredField(SoundManager.class, "sndSystem", "f", "field_148620_e");
	public static Method WorldProvider_generateLightBrightnessTable = getDeclaredMethod(WorldProvider.class, "generateLightBrightnessTable", "a", "func_76556_a");
	public static Field PlayerControllerMP_blockHitDelay = getDeclaredField(PlayerControllerMP.class, "blockHitDelay", "g", "field_78781_i");
	public static Field PlayerControllerMP_isHittingBlock = getDeclaredField(PlayerControllerMP.class, "isHittingBlock", "h", "field_78778_j");
	public static Field GuiChat_inputField = getDeclaredField(GuiChat.class, "inputField", "a", "field_146415_a");
	public static Field KeyBinding_pressed = getDeclaredField(KeyBinding.class, "pressed", "i", "field_74513_e");
	public static Field KeyBinding_pressTime = getDeclaredField(KeyBinding.class, "pressTime", "j", "field_151474_i");
	public static Method KeyBinding_unpressKey = getDeclaredMethod(KeyBinding.class, "unpressKey", "k", "func_74505_d");
	public static Field WorldProvider_terrainType = getDeclaredField(WorldProvider.class, "terrainType", "h", "field_76577_b");
	public static Field TileEntityRendererDispatcher_fontRenderer = getDeclaredField(TileEntityRendererDispatcher.class, "fontRenderer", "o", "field_147557_n");
	
	public static Object getField(Field field, Object obj) {
		try {
			return field.get(obj);
		} catch (ReflectiveOperationException e) {
			Throwables.propagate(e);
		}
		return null; // shut up compiler
	}
	
	public static void setField(Field field, Object obj, Object value) {
		try {
			field.set(obj, value);
		} catch (ReflectiveOperationException e) {
			Throwables.propagate(e);
		}
	}
	
	public static Object invokeMethod(Method method, Object obj, Object... args) {
		try {
			return method.invoke(obj, args);
		} catch (ReflectiveOperationException e) {
			Throwables.propagate(e);
		}
		return null; // shut up compiler
	}
	
	public static Object invokeConstructor(Constructor constructor, Object... args) {
		try {
			return constructor.newInstance(args);
		} catch (ReflectiveOperationException e) {
			Throwables.propagate(e);
		}
		return null; // shut up compiler
	}
	
	public static Class<?> getClassForName(String name) {
		Class clazz = null;
		
		try
		{
			clazz = Class.forName(name);
		}
		catch (ClassNotFoundException e)
		{
			System.out.println("[Vivecraft] WARNING: could not reflect class " + name);
		}
		
		return clazz;
	}

	public static Field getDeclaredField(Class<?> clazz, String unObfuscatedName, String obfuscatedName, String srgName)
	{
		Field field = null;
	
		try
		{
			field = clazz.getDeclaredField(unObfuscatedName);
		}
		catch (NoSuchFieldException e)
		{
			try
			{
				field = clazz.getDeclaredField(obfuscatedName);
			}
			catch (NoSuchFieldException e1)
			{
				try
				{
					field = clazz.getDeclaredField(srgName);
				}
				catch (NoSuchFieldException e2)
				{
					System.out.println("[Vivecraft] WARNING: could not reflect field " + unObfuscatedName + "," + srgName + "," + obfuscatedName + " in " + clazz.toString());
					Throwables.propagate(e);
				};
			};
		}
	
		field.setAccessible(true); //lets be honest this is why we have this method.
		
		return field;
	}

	public static Method getDeclaredMethod(Class<?> clazz, String unObfuscatedName, String obfuscatedName, String srgName, Class<?>... params)
	{
		Method method = null;
	
		try
		{
			method = clazz.getDeclaredMethod(unObfuscatedName, params);
		}
		catch (NoSuchMethodException e)
		{
			try
			{
				method = clazz.getDeclaredMethod(obfuscatedName, params);
			}
			catch (NoSuchMethodException e1)
			{
				try
				{
					method = clazz.getDeclaredMethod(srgName, params);
				}
				catch (NoSuchMethodException e2)
				{
					System.out.println("[Vivecraft] WARNING: could not reflect method " + unObfuscatedName + "," + srgName + "," + obfuscatedName + " in " + clazz.toString());
					Throwables.propagate(e);
				};
			};
		}
	
		method.setAccessible(true);
		
		return method;
	}

	public static Constructor getDeclaredConstructor(Class<?> clazz, Class<?>... params)
	{
		Constructor constructor = null;
	
		try
		{
			constructor = clazz.getDeclaredConstructor(params);
		}
		catch (NoSuchMethodException e)
		{
			try
			{
				constructor = clazz.getDeclaredConstructor(params);
			}
			catch (NoSuchMethodException e1)
			{
				try
				{
					constructor = clazz.getDeclaredConstructor(params);
				}
				catch (NoSuchMethodException e2)
				{
					System.out.println("[Vivecraft] WARNING: could not reflect constructor in " + clazz.toString());
				};
			};
		}
	
		constructor.setAccessible(true);
		
		return constructor;
	}
}
