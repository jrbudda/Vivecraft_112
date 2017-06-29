package com.mtbs3d.minecrift.utils;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockWithData {
	public Block id;
	public int data = -1;
	public boolean hasdata = false;
	
	public BlockWithData(Block id, int data) {
		this.id = id;
		this.data = data;
		this.hasdata = true;
	}
	
	public BlockWithData(Block id) {
		this.id = id;
	}
	
	public boolean matches(Block b, IBlockState bs){
		if(b == id){
			if(this.hasdata){
				return this.data == b.getMetaFromState(bs);
			}
			return true;
		}
		return false;
	}
	
}
