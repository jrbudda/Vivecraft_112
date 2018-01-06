package com.mtbs3d.minecrift.tweaker;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.util.List;

public class MinecriftVanillaTweaker implements ITweaker
{
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile)
    {
        dbg("MinecriftVanillaTweaker: acceptOptions");
    }

    public void injectIntoClassLoader(LaunchClassLoader classLoader)
    {
        dbg("MinecriftVanillaTweaker: injectIntoClassLoader");
        classLoader.addTransformerExclusion("com.mtbs3d.minecrift.asm.");
        classLoader.registerTransformer("com.mtbs3d.minecrift.tweaker.MinecriftClassTransformer");
        classLoader.registerTransformer("com.mtbs3d.minecrift.asm.VivecraftASMTransformer");
    }

    public String getLaunchTarget()
    {
        dbg("MinecriftVanillaTweaker: getLaunchTarget");
        return "net.minecraft.client.main.Main";
    }

    public String[] getLaunchArguments()
    {
        dbg("MinecriftVanillaTweaker: getLaunchArguments");
        return new String[0];
    }

    private static void dbg(String str)
    {
        System.out.println(str);
    }
}
