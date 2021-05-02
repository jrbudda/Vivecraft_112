package org.vivecraft.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.vivecraft.asm.ObfNames;

import com.google.common.base.Throwables;

import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldProvider;

public class MCReflection {
	public static final ReflectionField EntityPlayer_spawnChunk = new ReflectionField(EntityPlayer.class, "field_71077_c");
	public static final ReflectionField EntityPlayer_spawnForced = new ReflectionField(EntityPlayer.class, "field_82248_d");

	public static class ReflectionField {
		private final Class<?> clazz;
		private final String srgName;
		private Field field;

		public ReflectionField(Class<?> clazz, String srgName) {
			this.clazz = clazz;
			this.srgName = srgName;
			reflect();
		}

		public Object get(Object obj) {
			try {
				return field.get(obj);
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException(e);
			}
		}

		public void set(Object obj, Object value) {
			try {
				field.set(obj, value);
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException(e);
			}
		}

		private void reflect() {
			try
			{
				field = clazz.getDeclaredField(srgName);
			}
			catch (NoSuchFieldException e)
			{
				try
				{
					field = clazz.getDeclaredField(ObfNames.resolveField(srgName, true));
				}
				catch (NoSuchFieldException e1)
				{
					try
					{
						field = clazz.getDeclaredField(ObfNames.getDevMapping(srgName));
					}
					catch (NoSuchFieldException e2)
					{
						StringBuilder sb = new StringBuilder(srgName);
						if (!srgName.equals(ObfNames.resolveField(srgName, true)))
							sb.append(',').append(ObfNames.resolveField(srgName, true));
						if (!srgName.equals(ObfNames.getDevMapping(srgName)))
							sb.append(',').append(ObfNames.getDevMapping(srgName));
						throw new RuntimeException("reflecting field " + sb.toString() + " in " + clazz.toString(), e);
					}
				}
			}

			field.setAccessible(true); //lets be honest this is why we have this method.
		}
	}

	public static class ReflectionMethod {
		private final Class<?> clazz;
		private final String srgName;
		private final Class<?>[] params;
		private Method method;

		public ReflectionMethod(Class<?> clazz, String srgName, Class<?>... params) {
			this.clazz = clazz;
			this.srgName = srgName;
			this.params = params;
			reflect();
		}

		public Object invoke(Object obj, Object... args) {
			try {
				return method.invoke(obj, args);
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException(e);
			}
		}

		private void reflect() {
			try
			{
				method = clazz.getDeclaredMethod(srgName, params);
			}
			catch (NoSuchMethodException e)
			{
				try
				{
					method = clazz.getDeclaredMethod(ObfNames.resolveMethod(srgName, true), params);
				}
				catch (NoSuchMethodException e1)
				{
					try
					{
						method = clazz.getDeclaredMethod(ObfNames.getDevMapping(srgName), params);
					}
					catch (NoSuchMethodException e2)
					{
						StringBuilder sb = new StringBuilder(srgName);
						if (!srgName.equals(ObfNames.resolveMethod(srgName, true)))
							sb.append(',').append(ObfNames.resolveMethod(srgName, true));
						if (!srgName.equals(ObfNames.getDevMapping(srgName)))
							sb.append(',').append(ObfNames.getDevMapping(srgName));
						if (params.length > 0) {
							sb.append(" with params ");
							sb.append(Arrays.stream(params).map(Class::getName).collect(Collectors.joining(",")));
						}
						throw new RuntimeException("reflecting method " + sb.toString() + " in " + clazz.toString(), e);
					}
				}
			}

			method.setAccessible(true);
		}
	}

	public static class ReflectionConstructor {
		private final Class<?> clazz;
		private final Class<?>[] params;
		private Constructor constructor;

		public ReflectionConstructor(Class<?> clazz, Class<?>... params) {
			this.clazz = clazz;
			this.params = params;
			reflect();
		}

		public Object newInstance(Object... args) {
			try {
				return constructor.newInstance(args);
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException(e);
			}
		}

		private void reflect() {
			try
			{
				constructor = clazz.getDeclaredConstructor(params);
			}
			catch (NoSuchMethodException e)
			{
				StringBuilder sb = new StringBuilder();
				if (params.length > 0) {
					sb.append(" with params ");
					sb.append(Arrays.stream(params).map(Class::getName).collect(Collectors.joining(",")));
				}
				throw new RuntimeException("reflecting constructor " + sb.toString() + " in " + clazz.toString(), e);
			}

			constructor.setAccessible(true);
		}
	}
}
