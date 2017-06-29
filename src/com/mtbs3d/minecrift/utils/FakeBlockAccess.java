package com.mtbs3d.minecrift.utils;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;

public class FakeBlockAccess implements IBlockAccess {
	private DimensionType dimensionType;
	private IBlockState[] blocks;
	private int[] lightmap;
	private Biome[] biomemap;
	private int xSize;
	private int ySize;
	private int zSize;
	private int ground;
	
	public FakeBlockAccess(IBlockState[] blocks, int[] lightmap, Biome[] biomemap, int xSize, int ySize, int zSize, int ground, DimensionType dimensionType) {
		this.blocks = blocks;
		this.lightmap = lightmap;
		this.biomemap = biomemap;
		this.xSize = xSize;
		this.ySize = ySize;
		this.zSize = zSize;
		this.ground = ground;
		this.dimensionType = dimensionType;
	}
	
	private int encodeCoords(int x, int z) {
		return z * xSize + x;
	}
	
	private int encodeCoords(int x, int y, int z) {
		return (y * zSize + z) * xSize + x;
	}
	
	private boolean checkCoords(BlockPos pos) {
		if (pos.getX() < 0 || pos.getY() < 0 || pos.getZ() < 0 || pos.getX() >= xSize || pos.getY() >= ySize || pos.getZ() >= xSize) return false;
		return true;
	}
	
	public int getGround() {
		return ground;
	}
	
	public int getXSize() {
		return xSize;
	}

	public int getYSize() {
		return ySize;
	}

	public int getZSize() {
		return zSize;
	}
	
	public DimensionType getDimensionType() {
		return dimensionType;
	}

	@Override
	public IBlockState getBlockState(BlockPos pos) {
		if (!checkCoords(pos)) return Blocks.BEDROCK.getDefaultState();
		IBlockState state = blocks[encodeCoords(pos.getX(), pos.getY(), pos.getZ())];
		return state != null ? state : Blocks.AIR.getDefaultState();
	}

	@Override
	public TileEntity getTileEntity(BlockPos pos) {
		return null;
	}

	@Override
	public int getCombinedLight(BlockPos pos, int lightValue) {
		if (!checkCoords(pos)) return 0;
		return lightmap[encodeCoords(pos.getX(), pos.getY(), pos.getZ())];
	}

	@Override
	public boolean isAirBlock(BlockPos pos) {
		return this.getBlockState(pos).getMaterial() == Material.AIR;
	}

	@Override
	public Biome getBiome(BlockPos pos) {
		if (!checkCoords(pos)) return Biomes.PLAINS;
		return biomemap[encodeCoords(pos.getX(), pos.getZ())];
	}

	@Override
	public int getStrongPower(BlockPos pos, EnumFacing direction) {
		return 0;
	}

	@Override
	public WorldType getWorldType() {
		return WorldType.DEFAULT;
	}
}
