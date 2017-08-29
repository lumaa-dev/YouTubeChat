/**
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.youtube.gaming.chat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;

/**
 *
 * The separate Stream Chat GUI that move into right side of the screen.
 * @author SteveKunG
 *
 */
@SideOnly(Side.CLIENT)
public class GuiRightStreamChat extends Gui
{
    private final Minecraft mc;
    private final List<ChatLine> chatLines = new ArrayList<>();
    private final List<ChatLine> drawnChatLines = new ArrayList<>();

    public GuiRightStreamChat(Minecraft mc)
    {
        this.mc = mc;
    }

    void drawChat(int updateCounter)
    {
        ScaledResolution res = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);

        if (this.mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN)
        {
            int i = this.getLineCount();
            int j = this.drawnChatLines.size();
            float f = this.mc.gameSettings.chatOpacity * 0.9F + 0.1F;

            if (j > 0)
            {
                float f1 = this.getChatScale();
                int k = MathHelper.ceiling_float_int(this.getChatWidth() / f1);
                GL11.glPushMatrix();
                GL11.glTranslatef(2.0F, 8.0F, 0.0F);
                GL11.glScalef(f1, f1, 1.0F);

                for (int i1 = 0; i1 < this.drawnChatLines.size() && i1 < i; ++i1)
                {
                    ChatLine chatline = this.drawnChatLines.get(i1);

                    if (chatline != null)
                    {
                        int j1 = updateCounter - chatline.getUpdatedCounter();

                        if (j1 < 200)
                        {
                            double d0 = j1 / 200.0D;
                            d0 = 1.0D - d0;
                            d0 = d0 * 10.0D;
                            d0 = MathHelper.clamp_double(d0, 0.0D, 1.0D);
                            d0 = d0 * d0;
                            int l1 = (int)(255.0D * d0);

                            l1 = (int)(l1 * f);
                            if (l1 > 3)
                            {
                                int j2 = -i1 * 9;
                                String s = chatline.func_151461_a().getFormattedText();
                                int stringWidth = this.mc.fontRenderer.getStringWidth(s) + 4;
                                GL11.glPushMatrix();
                                GL11.glTranslatef(res.getScaledWidth() / 2 - 310, 0.0F, 0.0F);
                                drawRect(-2, j2 - 9, 0 + k + 4, j2, l1 / 2 << 24);
                                GL11.glPopMatrix();
                                GL11.glEnable(GL11.GL_BLEND);
                                this.mc.fontRenderer.drawStringWithShadow(s, res.getScaledWidth() / 2 - stringWidth, j2 - 8, 16777215 + (l1 << 24));
                                GL11.glDisable(GL11.GL_ALPHA_TEST);
                                GL11.glDisable(GL11.GL_BLEND);
                            }
                        }
                    }
                }
                GL11.glPopMatrix();
            }
        }
    }

    void clearChatMessages()
    {
        this.drawnChatLines.clear();
        this.chatLines.clear();
    }

    void printChatMessage(IChatComponent chatComponent)
    {
        this.printChatMessageWithOptionalDeletion(chatComponent, 0);
    }

    private void printChatMessageWithOptionalDeletion(IChatComponent chatComponent, int chatLineId)
    {
        this.setChatLine(chatComponent, chatLineId, this.mc.ingameGUI.getUpdateCounter(), false);
    }

    private void setChatLine(IChatComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly)
    {
        if (chatLineId != 0)
        {
            this.deleteChatLine(chatLineId);
        }

        int k = MathHelper.floor_float(this.getChatWidth() / this.getChatScale());
        int l = 0;
        List<IChatComponent> arraylist1 = Lists.newArrayList(chatComponent);

        for (int i1 = 0; i1 < arraylist1.size(); ++i1)
        {
            IChatComponent ichatcomponent1 = arraylist1.get(i1);
            String s = this.func_146235_b(ichatcomponent1.getChatStyle().getFormattingCode() + ichatcomponent1.getUnformattedTextForChat());
            int j1 = this.mc.fontRenderer.getStringWidth(s);
            ChatComponentText chatcomponenttext1 = new ChatComponentText(s);
            chatcomponenttext1.setChatStyle(ichatcomponent1.getChatStyle().createShallowCopy());
            boolean flag1 = false;

            if (l + j1 > k)
            {
                String s1 = this.mc.fontRenderer.trimStringToWidth(s, k - l, false);
                String s2 = s1.length() < s.length() ? s.substring(s1.length()) : null;

                if (s2 != null && s2.length() > 0)
                {
                    int k1 = s1.lastIndexOf(" ");

                    if (k1 >= 0 && this.mc.fontRenderer.getStringWidth(s.substring(0, k1)) > 0)
                    {
                        s1 = s.substring(0, k1);
                        s2 = s.substring(k1);
                    }
                    ChatComponentText chatcomponenttext2 = new ChatComponentText(s2);
                    chatcomponenttext2.setChatStyle(ichatcomponent1.getChatStyle().createShallowCopy());
                    arraylist1.add(i1 + 1, chatcomponenttext2);
                }
                j1 = this.mc.fontRenderer.getStringWidth(s1);
                chatcomponenttext1 = new ChatComponentText(s1);
                chatcomponenttext1.setChatStyle(ichatcomponent1.getChatStyle().createShallowCopy());
                flag1 = true;
            }

            if (l + j1 <= k)
            {
                l += j1;
            }
            else
            {
                flag1 = true;
            }

            if (flag1)
            {
                l = 0;
            }
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

    @Nullable
    private IChatComponent getChatComponent(int mouseX, int mouseY)
    {
        if (!this.getChatOpen())
        {
            return null;
        }
        else
        {
            ScaledResolution scaledresolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
            int i = scaledresolution.getScaleFactor();
            float f = this.getChatScale();
            int j = mouseX / i - 2;
            int k = mouseY / i - 40;
            j = MathHelper.floor_float(j / f);
            k = MathHelper.floor_float(k / f);

            if (j >= 0 && k >= 0)
            {
                int l = Math.min(this.getLineCount(), this.drawnChatLines.size());

                if (j <= MathHelper.floor_float(this.getChatWidth() / this.getChatScale()) && k < this.mc.fontRenderer.FONT_HEIGHT * l + l)
                {
                    int i1 = k / this.mc.fontRenderer.FONT_HEIGHT;

                    if (i1 >= 0 && i1 < this.drawnChatLines.size())
                    {
                        ChatLine chatline = this.drawnChatLines.get(i1);
                        int l1 = 0;
                        Iterator iterator = chatline.func_151461_a().iterator();

                        while (iterator.hasNext())
                        {
                            IChatComponent ichatcomponent = (IChatComponent)iterator.next();

                            if (ichatcomponent instanceof ChatComponentText)
                            {
                                l1 += this.mc.fontRenderer.getStringWidth(this.func_146235_b(((ChatComponentText)ichatcomponent).getChatComponentText_TextValue()));

                                if (l1 > l)
                                {
                                    return ichatcomponent;
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

    private boolean getChatOpen()
    {
        return this.mc.currentScreen instanceof GuiChat;
    }

    private void deleteChatLine(int id)
    {
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

    private int getChatWidth()
    {
        return GuiNewChat.func_146233_a(this.mc.gameSettings.chatWidth);
    }

    private int getChatHeight()
    {
        return GuiNewChat.func_146243_b(this.getChatOpen() ? this.mc.gameSettings.chatHeightFocused : this.mc.gameSettings.chatHeightUnfocused);
    }

    private float getChatScale()
    {
        return this.mc.gameSettings.chatScale;
    }

    private int getLineCount()
    {
        return this.getChatHeight() / 9;
    }

    private String func_146235_b(String text)
    {
        return Minecraft.getMinecraft().gameSettings.chatColours ? text : EnumChatFormatting.getTextWithoutFormattingCodes(text);
    }
}