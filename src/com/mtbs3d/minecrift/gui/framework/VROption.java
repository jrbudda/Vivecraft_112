package com.mtbs3d.minecrift.gui.framework;

import com.mtbs3d.minecrift.settings.VRSettings;
import net.minecraft.client.Minecraft;

public class VROption
{
    public static enum Position
    {
        POS_LEFT,
        POS_CENTER,
        POS_RIGHT,
    };

    public static final boolean ENABLED = true;
    public static final boolean DISABLED = false;

    public VRSettings.VrOptions _e;
    Position _pos;
    float _row;
    public boolean _enabled;
    String _title = "";
    int _ordinal;

    boolean _defaultb;

    float _defaultf;
    float _maxf;
    float _minf;
    float _incrementf;

    int _defaulti;
    int _maxi;
    int _mini;
    int _incrementi;

    public VROption(VRSettings.VrOptions e, Position pos, float row, boolean enabled, String title)
    {
        _e = e;
        _pos = pos;
        _row = row;
        if (title != null)
            _title = title;
        _enabled = enabled;
    }

    public VROption(int ordinal, Position pos, float row, boolean enabled, String title)
    {
        _ordinal = ordinal;
        _pos = pos;
        _row = row;
        _title = title;
        _enabled = enabled;
    }

    public int getWidth(int screenWidth)
    {
        if (_pos == Position.POS_LEFT)
            return screenWidth / 2 - 155 + 0 * 160;
        else if (_pos == Position.POS_RIGHT)
            return screenWidth / 2 - 155 + 1 * 160;
        else
            return screenWidth / 2 - 155 + 1 * 160 / 2;
    }

    public int getHeight(int screenHeight)
    {
        return (int)Math.ceil(screenHeight / 6 + 21 * _row - 10);
    }

    public String getButtonText()
    {
        if (_title.isEmpty())
        {
            if (_e != null)
                return Minecraft.getMinecraft().vrSettings.getKeyBinding(_e);
        }

        return _title;
    }

    public int getOrdinal()
    {
        if (_e == null)
            return _ordinal;
        else
            return _e.returnEnumOrdinal();
    }
}
