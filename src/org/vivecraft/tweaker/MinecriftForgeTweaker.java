package org.vivecraft.tweaker;

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
        classLoader.addTransformerExclusion("org.vivecraft.asm.");
        classLoader.addClassLoaderExclusion("net.minecraftforge.fml.relauncher.");
        classLoader.addClassLoaderExclusion("net.minecraftforge.classloading.");
        classLoader.addTransformerExclusion("net.minecraftforge.fml.common.asm.transformers.");
        classLoader.addTransformerExclusion("net.minecraftforge.fml.common.patcher.");
        classLoader.addTransformerExclusion("net.minecraftforge.fml.repackage.");
        classLoader.addClassLoaderExclusion("org.apache.commons.");
        classLoader.addClassLoaderExclusion("org.apache.http.");
        classLoader.addClassLoaderExclusion("org.apache.maven.");
        classLoader.addClassLoaderExclusion("com.google.common.");
        classLoader.addClassLoaderExclusion("org.objectweb.asm.");
        classLoader.addClassLoaderExclusion("LZMA.");
        classLoader.addClassLoaderExclusion("org.fusesource.jansi.");
    }

    public String getLaunchTarget()
    {
        dbg("MinecriftForgeTweaker: getLaunchTarget");
        return "org.vivecraft.main.VivecraftMain";
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
