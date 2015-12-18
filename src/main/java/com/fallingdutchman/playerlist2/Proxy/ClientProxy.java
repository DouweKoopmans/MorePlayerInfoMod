package com.fallingdutchman.playerlist2.Proxy;

import com.fallingdutchman.playerlist2.Listeners.HudListener;
import com.fallingdutchman.playerlist2.LogHelper;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Created by Douwe Koopmans on 18-12-15.
 */
public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
    }

    @Override
    public void init(FMLInitializationEvent event) {
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(new HudListener());
    }
}
