package com.fallingdutchman.playerinfo.Gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.awt.Color;

/**
 * Created by Douwe Koopmans on 24-7-16.
 */
public class PlayerInfoScreen extends GuiScreen {
    private final Minecraft mc;
    private final FontRenderer fr;
    private final String displayName;
    private final NetworkPlayerInfo playerInfo;

    public PlayerInfoScreen(String displayName, NetworkPlayerInfo playerInfo) {
        this.displayName = displayName;
        this.playerInfo = playerInfo;
        mc = Minecraft.getMinecraft();
        fr = mc.fontRendererObj;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();
        final ScaledResolution res = new ScaledResolution(mc);
        fr.drawStringWithShadow(displayName, res.getScaledWidth() / 2, res.getScaledHeight() / 2, Color.WHITE.getRGB());
    }

    // TODO: 24-7-16
}
