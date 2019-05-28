/*
 * Copyright 2017-2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package stevekung.mods.ytchat.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import stevekung.mods.stevekungslib.client.gui.GuiChatBase;

/**
 *
 * The separate Stream Chat GUI that move into right side of the screen.
 * @author SteveKunG
 *
 */
@OnlyIn(Dist.CLIENT)
public class GuiRightChatYT extends GuiNewChat
{
    private final Minecraft mc;
    private final List<ChatLine> chatLines = new ArrayList<>();
    private final List<ChatLine> drawnChatLines = new ArrayList<>();
    private int scrollPos;
    private boolean isScrolled;

    public GuiRightChatYT(Minecraft mc)
    {
        super(mc);
        this.mc = mc;
    }

    @Override
    public void clearChatMessages(boolean clearSent)
    {
        this.drawnChatLines.clear();
        this.chatLines.clear();
        super.clearChatMessages(clearSent);
    }

    @Override
    public void resetScroll()
    {
        super.resetScroll();
        this.scrollPos = 0;
        this.isScrolled = false;
    }

    @Override
    public void func_194813_a(double amount)//TODO scroll
    {
        super.func_194813_a(amount);
        this.scrollPos += amount;
        int i = this.drawnChatLines.size();

        if (this.scrollPos > i - this.getLineCount())
        {
            this.scrollPos = i - this.getLineCount();
        }
        if (this.scrollPos <= 0)
        {
            this.scrollPos = 0;
            this.isScrolled = false;
        }
    }

    @Override
    public boolean getChatOpen()
    {
        return this.mc.currentScreen instanceof GuiChatBase;
    }

    @Override
    public void deleteChatLine(int id)
    {
        super.deleteChatLine(id);
        Iterator<ChatLine> iterator = this.drawnChatLines.iterator();

        while (iterator.hasNext())
        {
            ChatLine chatline = iterator.next();

            if (chatline.getChatLineID() == id)
            {
                iterator.remove();
            }
        }

        iterator = this.chatLines.iterator();

        while (iterator.hasNext())
        {
            ChatLine chatline1 = iterator.next();

            if (chatline1.getChatLineID() == id)
            {
                iterator.remove();
                break;
            }
        }
    }

    public void drawRightChat(int updateCounter)
    {
        if (this.mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN)
        {
            int i = this.getLineCount();
            int j = this.drawnChatLines.size();
            double f = this.mc.gameSettings.chatOpacity * 0.9D + 0.1D;

            if (j > 0)
            {
                boolean flag = false;

                if (this.getChatOpen())
                {
                    flag = true;
                }

                double f1 = this.getScale();
                int k = MathHelper.ceil(this.getChatWidth() / f1);
                GlStateManager.pushMatrix();
                GlStateManager.translatef(2.0F, 8.0F, 0.0F);
                GlStateManager.scaled(f1, f1, 1.0F);
                int l = 0;

                for (int i1 = 0; i1 + this.scrollPos < this.drawnChatLines.size() && i1 < i; ++i1)
                {
                    ChatLine chatline = this.drawnChatLines.get(i1 + this.scrollPos);

                    if (chatline != null)
                    {
                        int j1 = updateCounter - chatline.getUpdatedCounter();

                        if (j1 < 200 || flag)
                        {
                            double d0 = j1 / 200.0D;
                            d0 = 1.0D - d0;
                            d0 = d0 * 10.0D;
                            d0 = MathHelper.clamp(d0, 0.0D, 1.0D);
                            d0 = d0 * d0;
                            int l1 = (int)(255.0D * d0);

                            if (flag)
                            {
                                l1 = 255;
                            }

                            l1 = (int)(l1 * f);
                            ++l;

                            if (l1 > 3)
                            {
                                int j2 = -i1 * 9;
                                String s = chatline.getChatComponent().getFormattedText();
                                int stringWidth = this.mc.fontRenderer.getStringWidth(s) + 4;
                                GlStateManager.pushMatrix();
                                GlStateManager.translatef(this.mc.mainWindow.getScaledWidth() / 2 - 310, 0.0F, 0.0F);
                                drawRect(-2, j2 - 9, 0 + k + 4, j2, l1 / 2 << 24);
                                GlStateManager.popMatrix();
                                GlStateManager.enableBlend();
                                this.mc.fontRenderer.drawStringWithShadow(s, this.mc.mainWindow.getScaledWidth() / 2 - stringWidth, j2 - 8, 16777215 + (l1 << 24));
                                GlStateManager.disableAlphaTest();
                                GlStateManager.disableBlend();
                            }
                        }
                    }
                }
                if (flag)
                {
                    int k2 = this.mc.fontRenderer.FONT_HEIGHT;
                    GlStateManager.translated(-3.0F, 0.0F, 0.0F);
                    int l2 = j * k2 + j;
                    int i3 = l * k2 + l;
                    int j3 = this.scrollPos * i3 / j;
                    int k1 = i3 * i3 / l2;

                    if (l2 != i3)
                    {
                        int k3 = j3 > 0 ? 170 : 96;
                        int l3 = this.isScrolled ? 13382451 : 3355562;
                        drawRect(this.mc.mainWindow.getScaledWidth() / 2, -j3, this.mc.mainWindow.getScaledWidth() / 2 + 1, -j3 - k1, l3 + (k3 << 24));
                        drawRect(this.mc.mainWindow.getScaledWidth() / 2, -j3, this.mc.mainWindow.getScaledWidth() / 2 + 1, -j3 - k1, 13421772 + (k3 << 24));
                    }
                }
                GlStateManager.popMatrix();
            }
        }
    }

    public void printYTChatMessage(ITextComponent chatComponent)
    {
        this.printYTChatMessageWithOptionalDeletion(chatComponent, 0);
    }

    @Nullable
    public ITextComponent getYTChatComponent(double mouseX, double mouseY)
    {
        if (!this.getChatOpen())
        {
            return null;
        }
        else
        {
            double i = this.mc.mainWindow.getGuiScaleFactor();
            double f = this.getScale();
            double j = this.mc.mainWindow.getScaledWidth() - 4 - mouseX / i;
            double k = mouseY / i - 40;
            j = MathHelper.floor(j / f);
            k = MathHelper.floor(k / f);

            if (j >= 0 && k >= 0)
            {
                int l = Math.min(this.getLineCount(), this.drawnChatLines.size());

                if (j <= MathHelper.floor(this.getChatWidth() / this.getScale()) && k < this.mc.fontRenderer.FONT_HEIGHT * l + l)
                {
                    int i1 = (int)(k / this.mc.fontRenderer.FONT_HEIGHT + this.scrollPos);

                    if (i1 >= 0 && i1 < this.drawnChatLines.size())
                    {
                        ChatLine chatline = this.drawnChatLines.get(i1);
                        int j1 = 0;

                        for (ITextComponent itextcomponent : chatline.getChatComponent())
                        {
                            if (itextcomponent instanceof TextComponentString)
                            {
                                j1 += this.mc.fontRenderer.getStringWidth(GuiUtilRenderComponents.removeTextColorsIfConfigured(((TextComponentString)itextcomponent).getText(), false));

                                if (j1 > j)
                                {
                                    return itextcomponent;
                                }
                            }
                        }
                    }
                    return null;
                }
                else
                {
                    return null;
                }
            }
            else
            {
                return null;
            }
        }
    }

    private void printYTChatMessageWithOptionalDeletion(ITextComponent chatComponent, int chatLineId)
    {
        this.setChatLine(chatComponent, chatLineId, this.mc.ingameGUI.getTicks(), false);
    }

    private void setChatLine(ITextComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly)
    {
        if (chatLineId != 0)
        {
            this.deleteChatLine(chatLineId);
        }

        int i = MathHelper.floor(this.getChatWidth() / this.getScale());
        List<ITextComponent> list = GuiUtilRenderComponents.splitText(chatComponent, i, this.mc.fontRenderer, false, false);
        boolean flag = this.getChatOpen();

        for (ITextComponent itextcomponent : list)
        {
            if (flag && this.scrollPos > 0)
            {
                this.isScrolled = true;
                this.func_194813_a(1);//TODO scroll
            }
            this.drawnChatLines.add(0, new ChatLine(updateCounter, itextcomponent, chatLineId));
        }

        while (this.drawnChatLines.size() > 100)
        {
            this.drawnChatLines.remove(this.drawnChatLines.size() - 1);
        }

        if (!displayOnly)
        {
            this.chatLines.add(0, new ChatLine(updateCounter, chatComponent, chatLineId));

            while (this.chatLines.size() > 100)
            {
                this.chatLines.remove(this.chatLines.size() - 1);
            }
        }
    }
}