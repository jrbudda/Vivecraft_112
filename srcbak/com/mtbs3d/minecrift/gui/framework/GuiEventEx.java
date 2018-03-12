/**
 * Copyright 2013 Mark Browning, StellaArtois
 * Licensed under the LGPL 3.0 or later (See LICENSE.md for details)
 */
package com.mtbs3d.minecrift.gui.framework;

import com.mtbs3d.minecrift.settings.VRSettings;

public interface GuiEventEx
{
    public static int ID_VALUE_CHANGED = 0;

    public boolean event(int id, VRSettings.VrOptions enumm);
    public boolean event(int id, String s);
}
