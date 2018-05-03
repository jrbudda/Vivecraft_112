package com.mtbs3d.minecrift.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import com.google.common.io.Files;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class MenuWorldExporter {
	public static byte[] saveArea(World world, int xMin, int zMin, int xSize, int zSize, int ground) throws IOException {
		int ySize = 256;
		short[] blocks = new short[xSize * ySize * zSize];
		int[] lightmap = new int[xSize * ySize * zSize];
		byte[] biomemap = new byte[xSize * zSize];
		for (int x = xMin; x < xMin + xSize; x++) {
			int xl = x - xMin;
			for (int z = zMin; z < zMin + zSize; z++) {
				int zl = z - zMin;
				int index2 = zl * xSize + xl;
				BlockPos pos2 = new BlockPos(x, 0, z);
				biomemap[index2] = (byte)Biome.getIdForBiome(world.getBiome(pos2));
				for (int y = 0; y < ySize; y++) {
					int index3 = (y * zSize + zl) * xSize + xl;
					BlockPos pos3 = new BlockPos(x, y, z);
					IBlockState state = world.getBlockState(pos3);
					blocks[index3] = (short)Block.getStateId(state);
					lightmap[index3] = world.getCombinedLight(pos3, state.getLightValue());
				}
			}
		}
		
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(data);
		dos.writeInt(xSize);
		dos.writeInt(ySize);
		dos.writeInt(zSize);
		dos.writeInt(ground);
		dos.writeInt(world.provider.getDimensionType().getId());
		for (int i = 0; i < blocks.length; i++) {
			dos.writeShort(blocks[i] & 0xFFFF);
		}
		for (int i = 0; i < lightmap.length; i++) {
			dos.writeInt(lightmap[i]);
		}
		for (int i = 0; i < biomemap.length; i++) {
			dos.writeByte(biomemap[i] & 0xFF);
		}
		
		Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
		deflater.setInput(data.toByteArray());
		deflater.finish();
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] buffer = new byte[1048576];
		while (!deflater.finished()) {
			int len = deflater.deflate(buffer);
			output.write(buffer, 0, len);
		}
		
		return output.toByteArray();
	}
	
	public static void saveAreaToFile(World world, int xMin, int zMin, int xSize, int zSize, int ground, File file) throws IOException {
		byte[] bytes = saveArea(world, xMin, zMin, xSize, zSize, ground);
		Files.write(bytes, file);
	}
	
	public static FakeBlockAccess loadWorld(byte[] data) throws IOException, DataFormatException {
		Inflater inflater = new Inflater();
		inflater.setInput(data);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] buffer = new byte[1048576];
		while (!inflater.finished()) {
			int len = inflater.inflate(buffer);
			output.write(buffer, 0, len);
		}
		
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(output.toByteArray()));
		int xSize = dis.readInt();
		int ySize = dis.readInt();
		int zSize = dis.readInt();
		int ground = dis.readInt();
		DimensionType dimensionType = DimensionType.getById(dis.readInt());
		IBlockState[] blocks = new IBlockState[xSize * ySize * zSize];
		for (int i = 0; i < blocks.length; i++) {
			blocks[i] = Block.getStateById(dis.readShort() & 0xFFFF);
		}
		int[] lightmap = new int[xSize * ySize * zSize];
		for (int i = 0; i < lightmap.length; i++) {
			lightmap[i] = dis.readInt();
		}
		Biome[] biomemap = new Biome[xSize * zSize];
		for (int i = 0; i < biomemap.length; i++) {
			biomemap[i] = Biome.getBiome(dis.readByte() & 0xFF, Biomes.PLAINS);
		}
		
		return new FakeBlockAccess(blocks, lightmap, biomemap, xSize, ySize, zSize, ground, dimensionType);
	}
	
	public static FakeBlockAccess loadWorld(InputStream is) throws IOException, DataFormatException {
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		byte[] buffer = new byte[1048576];
		int count;
		while ((count = is.read(buffer)) != -1) {
			data.write(buffer, 0, count);
		}
		return loadWorld(data.toByteArray());
	}
}
