package com.mtbs3d.minecrift.tweaker;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.util.List;

public class MinecriftForgeTweaker implements ITweaker
{
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile)
    {
        dbg("MinecriftForgeTweaker: acceptOptions");
    }

    public void injectIntoClassLoader(LaunchClassLoader classLoader)
    {
        dbg("MinecriftForgeTweaker: injectIntoClassLoader");
        classLoader.addTransformerExclusion("com.mtbs3d.minecrift.asm.");
    }

    public String getLaunchTarget()
    {
        dbg("MinecriftForgeTweaker: getLaunchTarget");
        return "com.mtbs3d.minecrift.main.VivecraftMain";
    }

    public String[] getLaunchArguments()
    {
        dbg("MinecriftForgeTweaker: getLaunchArguments");
        return new String[0];
    }

    private static void dbg(String str)
    {
        System.out.println(str);
    }
}
