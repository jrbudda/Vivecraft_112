package com.mtbs3d.minecrift.asm;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ObfNames {
	private static final Map<String, String> mappings = new HashMap<>();

	static {
		// TODO: Load mappings from a file packaged in jar rather than hard-coding just the ones we need.
		mappings.put("net.minecraft.client.gui.inventory.GuiContainer", "bmg");
		mappings.put("net.minecraft.client.gui.inventory.GuiContainerCreative", "bmp");
		mappings.put("net.minecraft.client.renderer.GlStateManager", "bus");
		mappings.put("net.minecraft.inventory.Slot", "agr");
		mappings.put("net.minecraft.entity.Entity", "vg");
		mappings.put("net.minecraft.entity.player.EntityPlayerMP", "oq");
		mappings.put("net.minecraft.creativetab.CreativeTabs", "ahp");
		mappings.put("net.minecraft.client.gui.inventory.GuiContainerCreative$ContainerCreative", "bmp$b");
		mappings.put("net.minecraft.util.NonNullList", "fi");
	}

	public static Map<String, String> getMappings() {
		return Collections.unmodifiableMap(mappings);
	}

	public static String resolve(String name, boolean obfuscated) {
		if (obfuscated) {
			String canon = name.replace('/', '.');
			if (mappings.containsKey(canon))
				return mappings.get(canon);
			else
				return null;
		}
		return name;
	}

	public static String resolve(String name) {
		return resolve(name, true);
	}

	public static String resolveDescriptor(String desc, boolean obfuscated) {
		if (obfuscated) {
			for (Map.Entry<String, String> entry : mappings.entrySet())
				desc = desc.replace("L" + entry.getKey().replace('.', '/') + ";", "L" + entry.getValue() + ";");
		}
		return desc;
	}

	public static String resolveDescriptor(String desc) {
		return resolveDescriptor(desc, true);
	}
}