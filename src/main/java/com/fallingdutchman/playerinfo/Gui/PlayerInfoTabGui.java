package com.fallingdutchman.playerinfo.Gui;

import com.fallingdutchman.playerinfo.PlayerInfoUtils;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.Comparator;
import java.util.List;

/**
 * "custom" player tab gui
 * Created by Douwe Koopmans on 18-12-15.
 */
public class PlayerInfoTabGui extends GuiPlayerTabOverlay {

    private static final Ordering<NetworkPlayerInfo> field_175252_a = Ordering.from(new PlayerInfoTabGui.PlayerComparator());
    private final Minecraft mc;
    private final GuiIngame guiIngame;
    private IChatComponent footer;
    private IChatComponent header;
    /**
     * The last time the playerlist was opened (went from not being renderd, to being rendered)
     */
    private long lastTimeOpened;
    /**
     * Whether or not the playerlist is currently being rendered
     */
    private boolean isBeingRendered;
    private boolean inFocus = false;

    public PlayerInfoTabGui(Minecraft mcIn, GuiIngame guiIngameIn) {
        super(mcIn, guiIngameIn);
        this.mc = mcIn;
        this.guiIngame = guiIngameIn;
    }

    /**
     * Returns the name that should be rendered for the player supplied
     */
    public String getPlayerName(NetworkPlayerInfo networkPlayerInfoIn) {
        return networkPlayerInfoIn.getDisplayName() != null ? networkPlayerInfoIn.getDisplayName().getFormattedText() : ScorePlayerTeam.formatPlayerName(networkPlayerInfoIn.getPlayerTeam(), networkPlayerInfoIn.getGameProfile().getName());
    }

    /**
     * Called by GuiIngame to update the information stored in the playerlist, does not actually render the list,
     * however.
     */
    public void updatePlayerList(boolean willBeRendered) {
        if (willBeRendered && !this.isBeingRendered) {
            this.lastTimeOpened = Minecraft.getSystemTime();
        }

        this.isBeingRendered = willBeRendered;
    }

    private void updateMouseState() {
        if (Mouse.isButtonDown(1)) {
            if (!inFocus) {
                this.mc.setIngameNotInFocus();
                this.inFocus = true;
            } else {
                this.mc.setIngameFocus();
                this.inFocus = false;
            }
        }
    }

    /**
     * Renders the playerlist, its background, headers and footers.
     */
    public void renderPlayerlist(int screenWidth, Scoreboard scoreboardIn, ScoreObjective scoreObjectiveIn) {
        updateMouseState();

        NetHandlerPlayClient nethandlerplayclient = this.mc.thePlayer.sendQueue;
        List<NetworkPlayerInfo> playerList = field_175252_a.sortedCopy(nethandlerplayclient.getPlayerInfoMap());
        int maxPlayerNameLength = 0;
        int maxObjectiveWidth = 0;

        for (NetworkPlayerInfo networkplayerinfo : playerList) {
            int playerEntryWidth = this.mc.fontRendererObj.getStringWidth(this.getPlayerName(networkplayerinfo));
            maxPlayerNameLength = Math.max(maxPlayerNameLength, playerEntryWidth);

            if (scoreObjectiveIn != null && scoreObjectiveIn.getRenderType() != IScoreObjectiveCriteria.EnumRenderType.HEARTS) {
                playerEntryWidth = this.mc.fontRendererObj.getStringWidth(
                        " " + scoreboardIn.getValueFromObjective(networkplayerinfo.getGameProfile().getName(),
                                scoreObjectiveIn).getScorePoints());
                maxObjectiveWidth = Math.max(maxObjectiveWidth, playerEntryWidth);
            }
        }

        // only draw the first 80 entries in the player list
        playerList = playerList.subList(0, Math.min(playerList.size(), 80));
        int playerListSize = playerList.size();
        // the number of lists (horizontally) needed, this is linear 80 = 4, 40 = 2 etc.
        int numOfLists;
        // the number of entries per list
        int entryListsSize = playerListSize;

        // entryListsSize = (playerListSize + y) / y, for every y >= 0 and entryListsSize < 20 where playerListSize: [1-80]
        for (numOfLists = 1; entryListsSize > 20; entryListsSize = (playerListSize + numOfLists - 1) / numOfLists) {
            ++numOfLists;
        }

        boolean drawAdditionalPlayerInfo = this.mc.isIntegratedServerRunning() || this.mc.getNetHandler().getNetworkManager().getIsencrypted();
        int objectiveStringWidth;

        if (scoreObjectiveIn != null) {
            if (scoreObjectiveIn.getRenderType() == IScoreObjectiveCriteria.EnumRenderType.HEARTS) {
                objectiveStringWidth = 90;
            } else {
                objectiveStringWidth = maxObjectiveWidth;
            }
        } else {
            objectiveStringWidth = 0;
        }

        int requiredWidth = Math.min(numOfLists * ((drawAdditionalPlayerInfo ? 9 : 0) + maxPlayerNameLength + objectiveStringWidth + 13), screenWidth - 50) / numOfLists;
        int xPos = screenWidth / 2 - (requiredWidth * numOfLists + (numOfLists - 1) * 5) / 2;
        int yPos = 10;
        int width = requiredWidth * numOfLists + (numOfLists - 1) * 5;
        List<String> headerStrings = null;
        List<String> footerStrings = null;

        if (this.header != null) {
            headerStrings = this.mc.fontRendererObj.listFormattedStringToWidth(this.header.getFormattedText(), screenWidth - 50);

            for (String s : headerStrings) {
                width = Math.max(width, this.mc.fontRendererObj.getStringWidth(s));
            }
        }

        if (this.footer != null) {
            footerStrings = this.mc.fontRendererObj.listFormattedStringToWidth(this.footer.getFormattedText(), screenWidth - 50);

            for (String footerEntry : footerStrings) {
                width = Math.max(width, this.mc.fontRendererObj.getStringWidth(footerEntry));
            }
        }

        if (headerStrings != null) {
            // draw the header background
            drawRect(screenWidth / 2 - width / 2 - 1, yPos - 1, screenWidth / 2 + width / 2 + 1, yPos + headerStrings.size() * this.mc.fontRendererObj.FONT_HEIGHT, Integer.MIN_VALUE);

            for (String headerEntry : headerStrings) {
                int stringWidth = this.mc.fontRendererObj.getStringWidth(headerEntry);
                // draw the header
                this.mc.fontRendererObj.drawStringWithShadow(headerEntry, (float) (screenWidth / 2 - stringWidth / 2), (float) yPos, -1);
                // adjust the top player list to not overlap with the header
                yPos += this.mc.fontRendererObj.FONT_HEIGHT;
            }

            ++yPos;
        }

        // draw background player list (doesn't include header)
        drawRect(screenWidth / 2 - width / 2 - 1, yPos - 1, screenWidth / 2 + width / 2 + 1, yPos + entryListsSize * 9, Integer.MIN_VALUE);

        for (int playerIndex = 0; playerIndex < playerListSize; ++playerIndex) {
            // which of the horizontal lists is needed
            int listIndex = playerIndex / entryListsSize;
            int yOffset = playerIndex % entryListsSize;
            int left = xPos + listIndex * requiredWidth + listIndex * 5;
            int top = yPos + yOffset * this.mc.fontRendererObj.FONT_HEIGHT;
            String backgroundColor = "#20FFFF";
            if (inFocus) {
                final ScaledResolution res = new ScaledResolution(mc);
                final int mouseX = PlayerInfoUtils.getMouseX(res);
                final int mouseY = PlayerInfoUtils.getMouseY(res);
                if ((mouseX >= left && mouseX < left + requiredWidth) && (mouseY >= top && mouseY < top + 8)) {
                    // TODO: 24-7-16 look into something a bit bluish
                    backgroundColor = "#FFF";
                }
            }
            // draw player entry background
            drawRect(left, top, left + requiredWidth, top + 8, Color.decode(backgroundColor).getRGB());
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

            if (playerIndex < playerList.size()) {
                NetworkPlayerInfo networkPlayerInfo = playerList.get(playerIndex);
                String playerName = this.getPlayerName(networkPlayerInfo);
                GameProfile gameprofile = networkPlayerInfo.getGameProfile();

                if (inFocus && Mouse.isButtonDown(0)) {
                    this.clicked(networkPlayerInfo, playerName);
                }

                if (drawAdditionalPlayerInfo) {
                    EntityPlayer entityplayer = this.mc.theWorld.getPlayerEntityByUUID(gameprofile.getId());
                    boolean mojangster = entityplayer != null && entityplayer.isWearing(EnumPlayerModelParts.CAPE) &&
                            (gameprofile.getName().equals("Dinnerbone") || gameprofile.getName().equals("Grumm"));
                    this.mc.getTextureManager().bindTexture(networkPlayerInfo.getLocationSkin());
                    int textureV = 8 + (mojangster ? 8 : 0);
                    int vHeight = 8 * (mojangster ? -1 : 1);
                    Gui.drawScaledCustomSizeModalRect(left, top, 8.0F, (float) textureV, 8, vHeight, 8, 8, 64.0F, 64.0F);

                    if (entityplayer != null && entityplayer.isWearing(EnumPlayerModelParts.HAT)) {
                        int hatTextureV = 8 + (mojangster ? 8 : 0);
                        int hatVHeight = 8 * (mojangster ? -1 : 1);
                        Gui.drawScaledCustomSizeModalRect(left, top, 40.0F, (float) hatTextureV, 8, hatVHeight, 8, 8, 64.0F, 64.0F);
                    }

                    left += 9;
                }

                if (networkPlayerInfo.getGameType() == WorldSettings.GameType.SPECTATOR) {
                    playerName = EnumChatFormatting.ITALIC + playerName;
                    this.mc.fontRendererObj.drawStringWithShadow(playerName, (float) left, (float) top, -1862270977);
                } else {
                    this.mc.fontRendererObj.drawStringWithShadow(playerName, (float) left, (float) top, -1);
                }

                if (scoreObjectiveIn != null && networkPlayerInfo.getGameType() != WorldSettings.GameType.SPECTATOR) {
                    int scoreboardLeft = left + maxPlayerNameLength + 1;
                    int scoreboardRight = scoreboardLeft + objectiveStringWidth;

                    if (scoreboardRight - scoreboardLeft > 5) {
                        this.drawScoreboardValues(scoreObjectiveIn, top, gameprofile.getName(), scoreboardLeft, scoreboardRight, networkPlayerInfo);
                    }
                }

                this.drawPing(requiredWidth, left - (drawAdditionalPlayerInfo ? 9 : 0), top, networkPlayerInfo);
            }
        }

        if (footerStrings != null) {
            yPos = yPos + entryListsSize * 9 + 1;
            //draw footer background
            drawRect(screenWidth / 2 - width / 2 - 1, yPos - 1, screenWidth / 2 + width / 2 + 1, yPos + footerStrings.size() * this.mc.fontRendererObj.FONT_HEIGHT, Integer.MIN_VALUE);

            for (String footerEntry : footerStrings) {
                int entryWidth = this.mc.fontRendererObj.getStringWidth(footerEntry);
                // draw footer
                this.mc.fontRendererObj.drawStringWithShadow(footerEntry, (float) (screenWidth / 2 - entryWidth / 2), (float) yPos, -1);
                yPos += this.mc.fontRendererObj.FONT_HEIGHT;
            }
        }
    }

    private void clicked(NetworkPlayerInfo playerInfo, String displayName) {
        // TODO: 24-7-16
        this.mc.displayGuiScreen(new PlayerInfoScreen(displayName, playerInfo));
    }

    protected void drawPing(int parentEntryWidth, int xPos, int yPos, NetworkPlayerInfo networkPlayerInfoIn) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(icons);
        int pingLevel;

        if (networkPlayerInfoIn.getResponseTime() < 0) {
            pingLevel = 5;
        } else if (networkPlayerInfoIn.getResponseTime() < 150) {
            pingLevel = 0;
        } else if (networkPlayerInfoIn.getResponseTime() < 300) {
            pingLevel = 1;
        } else if (networkPlayerInfoIn.getResponseTime() < 600) {
            pingLevel = 2;
        } else if (networkPlayerInfoIn.getResponseTime() < 1000) {
            pingLevel = 3;
        } else {
            pingLevel = 4;
        }

        this.zLevel += 100.0F;
        this.drawTexturedModalRect(xPos + parentEntryWidth - 11, yPos, 0, 176 + pingLevel * 8, 10, 8);
        this.zLevel -= 100.0F;
    }

    // TODO: 23-7-16 decrypt this
    private void drawScoreboardValues(ScoreObjective scoreObjective, int top, String playerName, int left, int right, NetworkPlayerInfo playerInfo) {
        int objectiveValue = scoreObjective.getScoreboard().getValueFromObjective(playerName, scoreObjective).getScorePoints();

        if (scoreObjective.getRenderType() == IScoreObjectiveCriteria.EnumRenderType.HEARTS) {
            this.mc.getTextureManager().bindTexture(icons);

            if (this.lastTimeOpened == playerInfo.func_178855_p()) {
                if (objectiveValue < playerInfo.func_178835_l()) {
                    playerInfo.func_178846_a(Minecraft.getSystemTime());
                    playerInfo.func_178844_b((long) (this.guiIngame.getUpdateCounter() + 20));
                } else if (objectiveValue > playerInfo.func_178835_l()) {
                    playerInfo.func_178846_a(Minecraft.getSystemTime());
                    playerInfo.func_178844_b((long) (this.guiIngame.getUpdateCounter() + 10));
                }
            }

            if (Minecraft.getSystemTime() - playerInfo.func_178847_n() > 1000L || this.lastTimeOpened != playerInfo.func_178855_p()) {
                playerInfo.func_178836_b(objectiveValue);
                playerInfo.func_178857_c(objectiveValue);
                playerInfo.func_178846_a(Minecraft.getSystemTime());
            }

            playerInfo.func_178843_c(this.lastTimeOpened);
            playerInfo.func_178836_b(objectiveValue);
            // ??????
            int j = MathHelper.ceiling_float_int((float) Math.max(objectiveValue, playerInfo.func_178860_m()) / 2.0F);
            int k = Math.max(MathHelper.ceiling_float_int((float) (objectiveValue / 2)), Math.max(MathHelper.ceiling_float_int((float) (playerInfo.func_178860_m() / 2)), 10));
            boolean flag = playerInfo.func_178858_o() > (long) this.guiIngame.getUpdateCounter() && (playerInfo.func_178858_o() - (long) this.guiIngame.getUpdateCounter()) / 3L % 2L == 1L;

            if (j > 0) {
                float f = Math.min((float) (right - left - 4) / (float) k, 9.0F);

                if (f > 3.0F) {
                    // l is the index of the heart we are currently drawing ??
                    for (int l = j; l < k; ++l) {
                        // draw individual hearts ??
                        this.drawTexturedModalRect((float) left + (float) l * f, (float) top, flag ? 25 : 16, 0, 9, 9);
                    }

                    for (int j1 = 0; j1 < j; ++j1) {
                        // draw individual hearts ??
                        this.drawTexturedModalRect((float) left + (float) j1 * f, (float) top, flag ? 25 : 16, 0, 9, 9);

                        if (flag) {
                            if (j1 * 2 + 1 < playerInfo.func_178860_m()) {
                                this.drawTexturedModalRect((float) left + (float) j1 * f, (float) top, 70, 0, 9, 9);
                            }

                            if (j1 * 2 + 1 == playerInfo.func_178860_m()) {
                                this.drawTexturedModalRect((float) left + (float) j1 * f, (float) top, 79, 0, 9, 9);
                            }
                        }

                        if (j1 * 2 + 1 < objectiveValue) {
                            this.drawTexturedModalRect((float) left + (float) j1 * f, (float) top, j1 >= 10 ? 160 : 52, 0, 9, 9);
                        }

                        if (j1 * 2 + 1 == objectiveValue) {
                            this.drawTexturedModalRect((float) left + (float) j1 * f, (float) top, j1 >= 10 ? 169 : 61, 0, 9, 9);
                        }
                    }
                } else {
                    //  we are drawing the hp as a value, result is "x hp" being rendered (where x is the readable health value

                    // the amount of health you have left (not actually a percentage, instead this is a decimal)
                    float healthPercentage = MathHelper.clamp_float((float) objectiveValue / 20.0F, 0.0F, 1.0F);

                    int healthTextColor = (int) ((1.0F - healthPercentage) * 255.0F) << 16 | (int) (healthPercentage * 255.0F) << 8;
                    String healthText = "" + (float) objectiveValue / 2.0F;

                    if (right - this.mc.fontRendererObj.getStringWidth(healthText + "hp") >= left) {
                        healthText = healthText + "hp";
                    }

                    this.mc.fontRendererObj.drawStringWithShadow(healthText,
                            (float) ((right + left) / 2 - this.mc.fontRendererObj.getStringWidth(healthText) / 2),
                            (float) top, healthTextColor);
                }
            }
        } else {
            String objectiveString = EnumChatFormatting.YELLOW + "" + objectiveValue;
            this.mc.fontRendererObj.drawStringWithShadow(objectiveString, (float) (right - this.mc.fontRendererObj.getStringWidth(objectiveString)), (float) top, 16777215);
        }
    }

    public void setFooter(IChatComponent footerIn) {
        this.footer = footerIn;
    }

    public void setHeader(IChatComponent headerIn) {
        this.header = headerIn;
    }

    public void resetFooterHeader() {
        this.header = null;
        this.footer = null;
    }

    @SideOnly(Side.CLIENT)
    private static class PlayerComparator implements Comparator<NetworkPlayerInfo> {
        private PlayerComparator() {
        }

        public int compare(NetworkPlayerInfo p_compare_1_, NetworkPlayerInfo p_compare_2_) {
            ScorePlayerTeam scoreplayerteam = p_compare_1_.getPlayerTeam();
            ScorePlayerTeam scoreplayerteam1 = p_compare_2_.getPlayerTeam();
            return ComparisonChain.start().compareTrueFirst(p_compare_1_.getGameType() != WorldSettings.GameType.SPECTATOR, p_compare_2_.getGameType() != WorldSettings.GameType.SPECTATOR).compare(scoreplayerteam != null ? scoreplayerteam.getRegisteredName() : "", scoreplayerteam1 != null ? scoreplayerteam1.getRegisteredName() : "").compare(p_compare_1_.getGameProfile().getName(), p_compare_2_.getGameProfile().getName()).result();
        }
    }
}
