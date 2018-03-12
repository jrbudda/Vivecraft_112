package com.mtbs3d.minecrift.render;

/**
 * Created by StellaArtois on 2/7/2016.
 */
public class RenderConfigException extends Exception {
    public String title;
    public String error;

    public RenderConfigException(String title, String error)
    {
        this.title = title;
        this.error = error;
    }

    public String toString(){
		return error;   	
    }
    
}
