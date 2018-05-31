package com.mtbs3d.minecrift.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class MenuWorldDownloader {
	private static final String baseUrl = "https://cache.techjargaming.com/vivecraft/";
	private static boolean init;
	private static int worldCount;
	private static Random rand;
	
	public static void init() {
		if (init) return;
		try {
			worldCount = Integer.parseInt(Utils.httpReadLine(baseUrl + "menuworldcount.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		rand = new Random();
		rand.nextInt();
		init = true;
	}
	
	public static InputStream getRandomWorld() throws IOException, NoSuchAlgorithmException {
		init();
		InputStream customWorld = getCustomWorld();
		if (customWorld != null) return customWorld;
		if (worldCount == 0) {
			return getRandomWorldFallback();
		}
		try {
			String path = "menuworlds/world" + rand.nextInt(worldCount) + ".mmw";
			File file = new File(path);
			file.getParentFile().mkdirs();
			if (file.exists()) {
				String localSha1 = Utils.getFileChecksum(file, "SHA-1");
				String remoteSha1 = Utils.httpReadLine(baseUrl + "checksum.php?file=" + path);
				if (localSha1.equals(remoteSha1)) {
					System.out.println("SHA-1 matches for " + path);
					return new FileInputStream(file);
				}
			}
			System.out.println("Downloading world " + path);
			Utils.httpReadToFile(baseUrl + path, file, true);
			return new FileInputStream(file);
		} catch (IOException e) {
			e.printStackTrace();
			return getRandomWorldFallback();
		}
	}

	private static InputStream getCustomWorld() throws IOException {
		File dir = new File("menuworlds/custom");
		if (dir.exists()) {
			File file = getRandomFileInDirectory(dir);
			if (file != null) {
				System.out.println("Using custom world menuworlds/custom/" + file.getName());
				return new FileInputStream(file);
			}
		}
		File customFile = new File("menuworlds/worldcustom.mmw");
		if (customFile.exists()) {
			System.out.println("Using custom world menuworlds/worldcustom.mmw");
			return new FileInputStream(customFile);
		}
		return null;
	}
	
	private static InputStream getRandomWorldFallback() throws IOException {
		System.out.println("Couldn't download a world, trying random file from directory");
		File dir = new File("menuworlds");
		if (dir.exists()) {
			File file = getRandomFileInDirectory(dir);
			if (file != null) {
				System.out.println("Using world menuworlds/" + file.getName());
				return new FileInputStream(file);
			}
		}
		return null;
	}

	private static File getRandomFileInDirectory(File dir) {
		File[] files = dir.listFiles(file -> file.isFile() && file.getName().toLowerCase().endsWith(".mmw"));
		if (files.length > 0)
			return files[rand.nextInt(files.length)];
		return null;
	}
}
