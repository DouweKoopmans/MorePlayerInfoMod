package com.fallingdutchman.playerlist2.Listeners;

import com.fallingdutchman.playerlist2.PlayerListCore;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 * Created by Douwe Koopmans on 18-12-15.
 */
public class HudListener {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void hudListener(RenderGameOverlayEvent event)
    {
        if (event.type == RenderGameOverlayEvent.ElementType.PLAYER_LIST) {
            event.setCanceled(true);
        }

        PlayerListCore.getInstance().onPlayerListDraw();
    }


    @SubscribeEvent
    public void rightClickPlayerList(GuiScreenEvent.MouseInputEvent.Post event) {
        if (event.gui == null && Mouse.isButtonDown(1)
                && Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindPlayerList.getKeyCode())) {
            PlayerListCore.getInstance().openGui();
        }
    }
}
