package com.fallingdutchman.playerinfo.Proxy;

import com.fallingdutchman.playerinfo.Gui.PlayerInfoTabGui;
import com.fallingdutchman.playerinfo.Listeners.HudListener;
import com.fallingdutchman.playerinfo.PlayerInfoCore;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.lang.reflect.Field;

/**
 * Created by Douwe Koopmans on 18-12-15.
 */
public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc != null) {
            if (mc.ingameGUI != null) {
                try {
                    // TODO: 24-7-16 only change the playerlist object when the mouse is ungrabbed
                    final Field playerListField = mc.ingameGUI.getClass().getDeclaredField("overlayPlayerList");
                    playerListField.setAccessible(true);
                    playerListField.set(mc.ingameGUI, new PlayerInfoTabGui(mc, mc.ingameGUI));
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    // TODO: 23-7-16 tell the user there was a critical error and the mod won't be loaded
                    PlayerInfoCore.getInstance().getLogger().error("was unable to modify the \"overlayerplayerList\" field, this is very bad", e);
                    PlayerInfoCore.getInstance().criticalError(e);
                }
            }
        }
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        MinecraftForge.EVENT_BUS.register(new HudListener());
    }
}
