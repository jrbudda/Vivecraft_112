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
import org.apache.commons.lang3.StringEscapeUtils;

public class LangHelper {
	public static void loadLocaleData() {
		loadLangFile("en_us");
		loadLangFile(Config.getGameSettings().language);
	}

	private static void loadLangFile(String lang) {
		String path = "lang/" + lang + ".lang";
		InputStream is = Utils.getAssetAsStream(path, false);
		if (is == null)
			return;

		try {
			Map map = I18n.getLocaleProperties();
			StringBuilder buf = new StringBuilder();
			String bufKey = null;
			for (String s : IOUtils.readLines(is, StandardCharsets.UTF_8)) {
				if (!s.isEmpty() && s.charAt(0) != '#') {
					if (s.charAt(s.length() - 1) == '\\') {
						s = s.substring(0, s.length() - 1);
						if (bufKey == null) {
							String[] split = s.split("=", 2);
							if (split.length == 2)
								buf.append(StringEscapeUtils.unescapeJava(split[1]));
							bufKey = split[0];
						} else {
							buf.append(StringEscapeUtils.unescapeJava(s));
						}
					} else if (bufKey != null) {
						buf.append(StringEscapeUtils.unescapeJava(s));
						map.put(bufKey, buf.toString());
						buf.setLength(0);
						bufKey = null;
					} else {
						String[] split = s.split("=", 2);
						map.put(split[0], split.length == 2 ? StringEscapeUtils.unescapeJava(split[1]) : "");
					}
				}
			}
			is.close();
		} catch (IOException e) {
			System.out.println("Failed reading locale data: " + path);
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

	public static String get(String key, Object... params) {
		return I18n.format(key, params);
	}

	public static String getYes() {
		return I18n.format("vivecraft.options.yes");
	}

	public static String getNo() {
		return I18n.format("vivecraft.options.no");
	}
}
