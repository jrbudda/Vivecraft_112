package com.mtbs3d.minecrift.utils;

import java.io.*;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.mtbs3d.minecrift.provider.MCOpenVR;
import com.mtbs3d.minecrift.render.VRShaders;
import com.mtbs3d.minecrift.tweaker.MinecriftClassTransformer;

import jopenvr.HmdMatrix34_t;
import net.minecraft.util.math.Vec3d;

public class Utils
{
  	public static Vector3f convertVector(de.fruitfly.ovr.structs.Vector3f vector) {
		return new Vector3f(vector.x, vector.y, vector.z);
	}

	public static de.fruitfly.ovr.structs.Vector3f convertToOVRVector(Vector3f vector) {
		return new de.fruitfly.ovr.structs.Vector3f(vector.x, vector.y, vector.z);
	}
	
	public static Matrix4f convertOVRMatrix(de.fruitfly.ovr.structs.Matrix4f matrix) {
		Matrix4f mat = new Matrix4f();
		mat.m00 = matrix.M[0][0];
		mat.m01 = matrix.M[0][1];
		mat.m02 = matrix.M[0][2];
		mat.m03 = matrix.M[0][3];
		mat.m10 = matrix.M[1][0];
		mat.m11 = matrix.M[1][1];
		mat.m12 = matrix.M[1][2];
		mat.m13 = matrix.M[1][3];
		mat.m20 = matrix.M[2][0];
		mat.m21 = matrix.M[2][1];
		mat.m22 = matrix.M[2][2];
		mat.m23 = matrix.M[2][3];
		mat.m30 = matrix.M[3][0];
		mat.m31 = matrix.M[3][1];
		mat.m32 = matrix.M[3][2];
		mat.m33 = matrix.M[3][3];
		mat.transpose(mat);
		return mat;
	}
	
	public static de.fruitfly.ovr.structs.Matrix4f convertToOVRMatrix(Matrix4f matrixIn) {
		Matrix4f matrix = new Matrix4f();
		matrixIn.transpose(matrix);
		de.fruitfly.ovr.structs.Matrix4f mat = new de.fruitfly.ovr.structs.Matrix4f();
		mat.M[0][0] = matrix.m00;
		mat.M[0][1] = matrix.m01;
		mat.M[0][2] = matrix.m02;
		mat.M[0][3] = matrix.m03;
		mat.M[1][0] = matrix.m10;
		mat.M[1][1] = matrix.m11;
		mat.M[1][2] = matrix.m12;
		mat.M[1][3] = matrix.m13;
		mat.M[2][0] = matrix.m20;
		mat.M[2][1] = matrix.m21;
		mat.M[2][2] = matrix.m22;
		mat.M[2][3] = matrix.m23;
		mat.M[3][0] = matrix.m30;
		mat.M[3][1] = matrix.m31;
		mat.M[3][2] = matrix.m32;
		mat.M[3][3] = matrix.m33;
		return mat;
	}
	
	public static HmdMatrix34_t convertToMatrix34(Matrix4f matrix) {
		HmdMatrix34_t mat = new HmdMatrix34_t();
		mat.m[0 + 0 * 4] = matrix.m00;
		mat.m[1 + 0 * 4] = matrix.m10;
		mat.m[2 + 0 * 4] = matrix.m20;
		mat.m[3 + 0 * 4] = matrix.m30;
		mat.m[0 + 1 * 4] = matrix.m01;
		mat.m[1 + 1 * 4] = matrix.m11;
		mat.m[2 + 1 * 4] = matrix.m21;
		mat.m[3 + 1 * 4] = matrix.m31;
		mat.m[0 + 2 * 4] = matrix.m02;
		mat.m[1 + 2 * 4] = matrix.m12;
		mat.m[2 + 2 * 4] = matrix.m22;
		mat.m[3 + 2 * 4] = matrix.m32;
		return mat;
	}

	public static double lerp(double from, double to, double percent){
		return from+(to-from)*percent;
	}

	public static double lerpMod(double from, double to, double percent, double mod){
		if(Math.abs(to-from) < mod/2){
			return from+(to-from)*percent;
		}else{
			return from+(to-from -Math.signum(to-from)*mod)*percent;
		}
	}

	public static double absLerp(double value, double target, double stepSize){
		double step=Math.abs(stepSize);
		if (target-value>step){
			return value+step;
		}
		else if (target-value<-step){
			return value-step;
		}else {
			return target;
		}
	}
	
	public static Vector3f directionFromMatrix(Matrix4f matrix, float x, float y, float z) {
		Vector4f vec = new Vector4f(x, y, z, 0);
		Matrix4f.transform(matrix, vec, vec);
		vec.normalise(vec);
		return new Vector3f(vec.x, vec.y, vec.z);
	}
	
	/* With thanks to http://ramblingsrobert.wordpress.com/2011/04/13/java-word-wrap-algorithm/ */
    public static void wordWrap(String in, int length, ArrayList<String> wrapped)
    {
        String newLine = "\n";
        String wrappedLine;
        boolean quickExit = false;

        // Remove carriage return
        in = in.replace("\r", "");

        if(in.length() < length)
        {
            quickExit = true;
            length = in.length();
        }

        // Split on a newline if present
        if(in.substring(0, length).contains(newLine))
        {
            wrappedLine = in.substring(0, in.indexOf(newLine)).trim();
            wrapped.add(wrappedLine);
            wordWrap(in.substring(in.indexOf(newLine) + 1), length, wrapped);
            return;
        }
        else if (quickExit)
        {
            wrapped.add(in);
            return;
        }

        // Otherwise, split along the nearest previous space / tab / dash
        int spaceIndex = Math.max(Math.max( in.lastIndexOf(" ", length),
                in.lastIndexOf("\t", length)),
                in.lastIndexOf("-", length));

        // If no nearest space, split at length
        if(spaceIndex == -1)
            spaceIndex = length;

        // Split!
        wrappedLine = in.substring(0, spaceIndex).trim();
        wrapped.add(wrappedLine);
        wordWrap(in.substring(spaceIndex), length, wrapped);
    }
    
	public static Vector2f convertVector(Vector2 vector) {
		return new Vector2f(vector.getX(), vector.getY());
	}

	public static Vector2 convertVector(Vector2f vector) {
		return new Vector2(vector.getX(), vector.getY());
	}

	public static Vector3f convertVector(Vector3 vector) {
		return new Vector3f(vector.getX(), vector.getY(), vector.getZ());
	}

	public static Vector3 convertVector(Vector3f vector) {
		return new Vector3(vector.getX(), vector.getY(), vector.getZ());
	}

	public static Vector3 convertVector(Vec3d vector) {
		return new Vector3((float)vector.x, (float)vector.y, (float)vector.z);
	}

	public static Vector3f convertToVector3f(Vec3d vector) {
		return new Vector3f((float)vector.x, (float)vector.y, (float)vector.z);
	}

	public static Quaternion quatLerp(Quaternion start, Quaternion end, float fraction) {
		Quaternion quat = new Quaternion();
		quat.w = start.w + (end.w - start.w) * fraction;
		quat.x = start.x + (end.x - start.x) * fraction;
		quat.y = start.y + (end.y - start.y) * fraction;
		quat.z = start.z + (end.z - start.z) * fraction;
		return quat;
	}

	public static Matrix4f matrix3to4(Matrix3f matrix) {
		Matrix4f mat = new Matrix4f();
		mat.m00 = matrix.m00;
		mat.m01 = matrix.m01;
		mat.m02 = matrix.m02;
		mat.m10 = matrix.m10;
		mat.m11 = matrix.m11;
		mat.m12 = matrix.m12;
		mat.m20 = matrix.m20;
		mat.m21 = matrix.m21;
		mat.m22 = matrix.m22;
		return mat;
	}

	public static byte[] loadAsset(String name, boolean required) {
		InputStream is = VRShaders.class.getResourceAsStream("/assets/vivecraft/" + name);
		byte[] out = new byte[0];
		try {
			if (is == null) {
				//uhh debugging?
				Path dir = Paths.get(System.getProperty("user.dir")); // ../mcpxxx/jars/
				Path p5 = dir.getParent().resolve("src/resources/assets/vivecraft/" + name);
				if (!p5.toFile().exists()) {
					p5 = dir.getParent().getParent().resolve("resources/assets/vivecraft/" + name);
				}
				if (p5.toFile().exists()) {
					is = new FileInputStream(p5.toFile());
				}
			}
			out = IOUtils.toByteArray(is);
			is.close();
		} catch (Exception e) {
			if (required) {
				throw new RuntimeException("Failed to load asset: " + name, e);
			} else {
				System.out.println("Failed to load asset: " + name);
				e.printStackTrace();
			}
		}
		return out;
	}
	
	public static String loadAssetAsString(String name, boolean required) {
		return new String(loadAsset(name, required), Charsets.UTF_8);
	}
	
	public static void unpackNatives(String directory) {
		try {
			new File("openvr/" + directory).mkdirs();
			if (new File("openvr/" + directory + "/opencomposite.ini").exists())
				return;
				
			// dev environment
			try {
				Path dir = Paths.get(System.getProperty("user.dir")); // ..\mcpxxx\jars\
				Path path = dir.getParent().resolve("src/resources/natives/" + directory);
				if (!path.toFile().exists()) {
					path = dir.getParent().getParent().resolve("resources/natives/" + directory);
				}
				if (path.toFile().exists()) { 
					System.out.println("Copying " + directory + " natives...");
					for (File file : path.toFile().listFiles()) {
						System.out.println(file.getName());
						Files.copy(file, new File("openvr/" + directory + "/" + file.getName()));
					}
					return;
				}
	
			} catch (Exception e) {
			}
			//
			
			//Live
			System.out.println("Unpacking " + directory + " natives...");
			ZipFile zip = MinecriftClassTransformer.findMinecriftZipFile();
			Enumeration<? extends ZipEntry> entries = zip.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (entry.getName().startsWith("natives/" + directory)) {
					String name = Paths.get(entry.getName()).getFileName().toString();
					System.out.println(name);
					writeStreamToFile(zip.getInputStream(entry), new File("openvr/" + directory + "/" + name));
				}
			}
			zip.close();
			//
		} catch (Exception e) {
			System.out.println("Failed to unpack natives");
			e.printStackTrace();
		}
	}
	
	public static void writeStreamToFile(InputStream is, File file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		byte[] buffer = new byte[4096];
		int count;
		while ((count = is.read(buffer, 0, buffer.length)) != -1) {
			fos.write(buffer, 0, count);
		}
		fos.flush();
		fos.close();
		is.close();
	}

	public static String httpReadLine(String url) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setReadTimeout(3000);
		conn.setUseCaches(false);
		conn.setDoInput(true);
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line = br.readLine();
		br.close();
		conn.disconnect();
		return line;
	}

	public static byte[] httpReadAll(String url) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setReadTimeout(3000);
		conn.setUseCaches(false);
		conn.setDoInput(true);
		InputStream is = conn.getInputStream();
		ByteArrayOutputStream bout = new ByteArrayOutputStream(conn.getContentLength());
		byte[] bytes = new byte[4096];
		int count;
		while ((count = is.read(bytes, 0, bytes.length)) != -1) {
			bout.write(bytes, 0, count);
		}
		is.close();
		conn.disconnect();
		return bout.toByteArray();
	}

	public static String httpReadAllString(String url) throws IOException {
		return new String(httpReadAll(url), StandardCharsets.UTF_8);
	}

	public static void httpReadToFile(String url, File file, boolean writeWhenComplete) throws MalformedURLException, IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setReadTimeout(3000);
		conn.setUseCaches(false);
		conn.setDoInput(true);
		InputStream is = conn.getInputStream();
		if (writeWhenComplete) {
			ByteArrayOutputStream bout = new ByteArrayOutputStream(conn.getContentLength());
			byte[] bytes = new byte[4096];
			int count;
			while ((count = is.read(bytes, 0, bytes.length)) != -1) {
				bout.write(bytes, 0, count);
			}
			OutputStream out = new FileOutputStream(file);
			out.write(bout.toByteArray());
			out.flush();
			out.close();
		} else {
			OutputStream out = new FileOutputStream(file);
			byte[] bytes = new byte[4096];
			int count;
			while ((count = is.read(bytes, 0, bytes.length)) != -1) {
				out.write(bytes, 0, count);
			}
			out.flush();
			out.close();
		}
		is.close();
		conn.disconnect();
	}
	
    public static void httpReadToFile(String url, File file) throws IOException {
        httpReadToFile(url, file, false);
    }

	public static List<String> httpReadList(String url) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setReadTimeout(3000);
		conn.setUseCaches(false);
		conn.setDoInput(true);
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		List<String> list = new ArrayList<String>();
		String line;
		while ((line = br.readLine()) != null) {
			list.add(line);
		}
		br.close();
		conn.disconnect();
		return list;
	}

	public static String getFileChecksum(File file, String algorithm) throws FileNotFoundException, IOException, NoSuchAlgorithmException {
		InputStream is = new FileInputStream(file);
		byte[] bytes = new byte[(int)file.length()];
		is.read(bytes);
		is.close();
		MessageDigest md = MessageDigest.getInstance(algorithm);
		md.update(bytes);
		Formatter fmt = new Formatter();
		for (byte b : md.digest()) {
			fmt.format("%02x", b);
		}
		String str = fmt.toString();
		fmt.close();
		return str;
	}

	public static byte[] readFile(File file) throws FileNotFoundException, IOException {
		FileInputStream is = new FileInputStream(file);
		return readFully(is);
	}

	public static String readFileString(File file) throws FileNotFoundException, IOException {
		return new String(readFile(file), "UTF-8");
	}
	
	public static byte[] readFully(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] bytes = new byte[4096];
		int count;
		while ((count = in.read(bytes, 0, bytes.length)) != -1) {
			out.write(bytes, 0, count);
		}
		in.close();
		return out.toByteArray();
	}

	public static Vec3d convertToVec3d(Vector3 vector) {
		return new Vec3d(vector.getX(), vector.getY(), vector.getZ());
	}
	public static Quaternion slerp(Quaternion start, Quaternion end, float alpha) {
		final float d = start.x * end.x + start.y * end.y + start.z * end.z + start.w * end.w;
		float absDot = d < 0.f ? -d : d;

		// Set the first and second scale for the interpolation
		float scale0 = 1f - alpha;
		float scale1 = alpha;

		// Check if the angle between the 2 quaternions was big enough to
		// warrant such calculations
		if ((1 - absDot) > 0.1) {// Get the angle between the 2 quaternions,
			// and then store the sin() of that angle
			final float angle = (float)Math.acos(absDot);		
			final float invSinTheta = 1f / (float)Math.sin(angle);

			// Calculate the scale for q1 and q2, according to the angle and
			// it's sine value
			scale0 = ((float)Math.sin((1f - alpha) * angle) * invSinTheta);
			scale1 = ((float)Math.sin((alpha * angle)) * invSinTheta);
		}

		if (d < 0.f) scale1 = -scale1;
	
		// Calculate the x, y, z and w values for the quaternion by using a
		// special form of linear interpolation for quaternions.
		float x = (scale0 * start.x) + (scale1 * end.x);
		float y = (scale0 * start.y) + (scale1 * end.y);
		float z = (scale0 * start.z) + (scale1 * end.z);
		float w = (scale0 * start.w) + (scale1 * end.w);

		// Return the interpolated quaternion
		return new Quaternion(w, x, y, z);
	}
	public static Vec3d vecLerp(Vec3d start, Vec3d end, double fraction) {
		double x = start.x + (end.x - start.x) * fraction;
		double y = start.y + (end.y - start.y) * fraction;
		double z = start.z + (end.z - start.z) * fraction;
		return new Vec3d(x, y, z);
	}

}
