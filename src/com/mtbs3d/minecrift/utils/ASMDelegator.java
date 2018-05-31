package com.mtbs3d.minecrift.utils;

import net.minecraft.client.gui.GuiScreen;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;
import org.lwjgl.input.Mouse;

public class ASMDelegator {
	public static boolean containerCreativeMouseDown(int eatTheStack) {
		return Mouse.isButtonDown(0) || GuiScreen.mouseDown;
	}

	public static void addCreativeItems(CreativeTabs tab, NonNullList<ItemStack> list) {
		if (tab == CreativeTabs.FOOD || tab == null) {
			ItemStack eatMe = new ItemStack(Items.PUMPKIN_PIE).setStackDisplayName("EAT ME");
			ItemStack drinkMe = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER).setStackDisplayName("DRINK ME");
			list.add(eatMe);
			list.add(drinkMe);
		}
		if (tab == CreativeTabs.TOOLS || tab == null) {
			ItemStack jumpBoots = new ItemStack(Items.LEATHER_BOOTS).setStackDisplayName("Jump Boots");
			jumpBoots.getTagCompound().setBoolean("Unbreakable", true);
			jumpBoots.getTagCompound().setInteger("HideFlags", 4);
			ItemStack climbClaws = new ItemStack(Items.SHEARS).setStackDisplayName("Climb Claws");
			climbClaws.getTagCompound().setBoolean("Unbreakable", true);
			climbClaws.getTagCompound().setInteger("HideFlags", 4);
			list.add(jumpBoots);
			list.add(climbClaws);
		}
	}

	public static void addCreativeSearch(String query, NonNullList<ItemStack> list) {
		NonNullList<ItemStack> myList = NonNullList.create();
		addCreativeItems(null, myList);
		for (ItemStack stack : myList) {
			if (query.isEmpty() || stack.getDisplayName().toLowerCase().contains(query.toLowerCase()))
				list.add(stack);
		}
	}
	
	public static void dummy(float f) {
		// does nothing
	}
}
