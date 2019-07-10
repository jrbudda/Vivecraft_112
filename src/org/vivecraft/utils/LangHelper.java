package org.vivecraft.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.src.Config;
import org.apache.commons.io.IOUtils;

public class LangHelper {
	public static void loadLocaleData() {
		String path = "lang/" + Config.getGameSettings().language + ".lang";
		InputStream is = Utils.getAssetAsStream(path, false);
		if (is == null)
			path = "lang/en_us.lang";
		is = Utils.getAssetAsStream(path, false);
		if (is == null)
			return;

		try {
			Map map = I18n.getLocaleProperties();
			for (String s : IOUtils.readLines(is, StandardCharsets.UTF_8)) {
				if (!s.isEmpty() && s.charAt(0) != '#') {
					String[] split = s.split("=", 2);
					map.put(split[0], split[1]);
				}
			}
			is.close();
		} catch (IOException e) {
			System.out.println("Failed reading locale data");
			e.printStackTrace();
		}
	}

	public static void registerResourceListener() {
		IResourceManager resourceManager = Config.getResourceManager();
		if (resourceManager instanceof IReloadableResourceManager)
		{
			((IReloadableResourceManager)resourceManager).registerReloadListener(new IResourceManagerReloadListener() {
				@Override
				public void onResourceManagerReload(IResourceManager resourceManager) {
					LangHelper.loadLocaleData();
				}
			});
		}
	}
}
