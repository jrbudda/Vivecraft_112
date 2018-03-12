package com.mtbs3d.minecrift.render;

public enum renderPass{
	Left, Right, Center, Third;
	
	public int value(){
		return ordinal();
	}
	
}