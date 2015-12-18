package com.fallingdutchman.playerlist2;

import com.fallingdutchman.playerlist2.Gui.PlayerListGui;
import net.minecraft.client.Minecraft;

/**
 * Created by Douwe Koopmans on 18-12-15.
 */
public class PlayerListCore {
    private static final PlayerListCore ourInstance = new PlayerListCore();
    private PlayerListGui plgui = new PlayerListGui();
    private Minecraft mc;

    public static PlayerListCore getInstance() {
        return ourInstance;
    }

    private PlayerListCore() {
        mc = Minecraft.getMinecraft();
    }

    public boolean enabled = true;

    public void onPlayerListDraw() {
        plgui.drawScreen(0, 0, 0);
    }

    public void openGui() {
        mc.displayGuiScreen(plgui);
    }
}
