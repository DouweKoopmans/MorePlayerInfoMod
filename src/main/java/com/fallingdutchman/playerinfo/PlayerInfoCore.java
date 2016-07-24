package com.fallingdutchman.playerinfo;

import com.fallingdutchman.playerinfo.Gui.PlayerInfoTabGui;
import com.fallingdutchman.playerinfo.References.References;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.apache.logging.log4j.Logger;

/**
 * Created by Douwe Koopmans on 18-12-15.
 */
public class PlayerInfoCore {
    private static final PlayerInfoCore ourInstance = new PlayerInfoCore();

    private Minecraft mc;
    private PlayerInfoTabGui gui;

    private Logger logger;

    boolean enabled = true;
    private ScaledResolution res;

    public static PlayerInfoCore getInstance() {
        return ourInstance;
    }

    private PlayerInfoCore() {}

    public void init() {
        mc = Minecraft.getMinecraft();
        gui = new PlayerInfoTabGui(mc, mc.ingameGUI);
    }

    public void renderPlayerList() {
        if (res == null) {
            res = new ScaledResolution(mc);
        }

        gui.renderPlayerlist(res.getScaledWidth(), this.mc.theWorld.getScoreboard(),
                this.mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(0));
    }

    public void criticalError(Throwable throwable) {
        getLogger().fatal("*********************************************************");
        getLogger().fatal("* a fatal error occurred causing the %s mod (mod id = %s)", References.MOD_NAME, References.MOD_ID);
        getLogger().fatal("* will disable the mod");
        getLogger().fatal("* stacktrace: ", throwable);
        getLogger().fatal("**********************************************************");

        enabled = false;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
