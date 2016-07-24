package com.fallingdutchman.playerinfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;

/**
 * Created by Douwe Koopmans on 24-7-16.
 */
public class PlayerInfoUtils {
    private PlayerInfoUtils() {}

    public static int getMouseX(ScaledResolution scaledresolution) {
       return Mouse.getX() * scaledresolution.getScaledWidth() / Minecraft.getMinecraft().displayWidth;
    }

    public static int getMouseY(ScaledResolution scaledresolution) {
        final int scaledHeight = scaledresolution.getScaledHeight();
        return  scaledHeight - Mouse.getY() * scaledHeight / Minecraft.getMinecraft().displayHeight - 1;
    }
}
