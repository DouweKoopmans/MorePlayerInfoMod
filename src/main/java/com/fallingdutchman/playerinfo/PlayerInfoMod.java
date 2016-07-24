package com.fallingdutchman.playerinfo;

import com.fallingdutchman.playerinfo.Proxy.IProxy;
import com.fallingdutchman.playerinfo.References.References;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLModDisabledEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by Douwe Koopmans on 18-12-15.
 */
@Mod(modid = References.MOD_ID, name = References.MOD_NAME, version = References.MOD_VERSION,
        acceptedMinecraftVersions = References.MC_VERSIONS, clientSideOnly = true, canBeDeactivated = true)
public class PlayerInfoMod {
    @SidedProxy(clientSide = References.CLIENT_PROXY_CLASS, serverSide = References.SERVER_PROXY_CLASS)
    private static IProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @SubscribeEvent
    public void deactivated(FMLModDisabledEvent event) {
        PlayerInfoCore.getInstance().getLogger().warn("the %s");
        PlayerInfoCore.getInstance().enabled = false;
    }
}
