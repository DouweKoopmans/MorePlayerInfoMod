package com.fallingdutchman.playerinfo.Proxy;

import com.fallingdutchman.playerinfo.PlayerInfoCore;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Created by Douwe Koopmans on 18-12-15.
 */
public abstract class CommonProxy implements IProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        PlayerInfoCore.getInstance().setLogger(event.getModLog());
    }


    @Override
    public void postInit(FMLPostInitializationEvent event) {
        PlayerInfoCore.getInstance().init();
    }
}
