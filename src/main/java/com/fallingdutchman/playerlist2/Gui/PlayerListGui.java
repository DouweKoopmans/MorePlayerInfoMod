package com.fallingdutchman.playerlist2.Gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

/**
 * Created by Douwe Koopmans on 18-12-15.
 */
public class PlayerListGui extends GuiScreen {
    private Minecraft mc = Minecraft.getMinecraft();

    @Override
    public void handleMouseInput() throws IOException {
        if (mc.currentScreen != null) {
            // TODO: 18-12-15 do actual mouse logic
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawListBackground();
        // TODO: 18-12-15
    }

    private void drawListBackground() {
        // TODO: 18-12-15
    }

    @Override
    public void drawBackground(int tint) {}
}
