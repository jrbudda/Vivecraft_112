package org.vivecraft.tweaker;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

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
        classLoader.registerTransformer("org.vivecraft.tweaker.MinecriftClassTransformer");

        // Do this here before anything else is loaded
        try {
            File foamFixCfg = new File("config/foamfix.cfg");
            if (foamFixCfg.exists()) {
                String str = new String(Files.readAllBytes(foamFixCfg.toPath()), StandardCharsets.UTF_8);
                String str2 = str.replace("B:forceDisable=false", "B:forceDisable=true");
                if (!str2.equals(str)) {
                    Files.write(foamFixCfg.toPath(), str2.getBytes(StandardCharsets.UTF_8));
                    dbg("Disabled FoamFix coremod");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
