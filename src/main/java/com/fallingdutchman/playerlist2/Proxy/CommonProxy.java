package com.fallingdutchman.playerlist2.Proxy;

import com.fallingdutchman.playerlist2.LogHelper;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Created by Douwe Koopmans on 18-12-15.
 */
public abstract class CommonProxy implements IProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        LogHelper.getInstance().setModLog(event.getModLog());
    }
}
