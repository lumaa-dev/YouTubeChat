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

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import stevekung.mods.stevekungslib.client.gui.IGuiChat;
import stevekung.mods.ytchat.core.EventHandlerYT;

@OnlyIn(Dist.CLIENT)
public class GuiYouTubeChat implements IGuiChat
{
    private int sentHistoryCursor = -1;

    @Override
    public void initGui(List<GuiButton> buttonList, int width, int height)
    {
        this.sentHistoryCursor = EventHandlerYT.rightStreamGui.getSentMessages().size();
    }

    @Override
    public boolean mouseScrolled(double wheel)
    {
        if (wheel > 1.0D)
        {
            wheel = 1.0D;
        }
        if (wheel < -1.0D)
        {
            wheel = -1.0D;
        }
        if (!GuiScreen.isCtrlKeyDown())
        {
            wheel *= 7;
        }
        EventHandlerYT.rightStreamGui.func_194813_a(wheel);
        return true;
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        if (mouseButton == 0)
        {
            ITextComponent itextcomponent = EventHandlerYT.rightStreamGui.getYTChatComponent(mouseX, mouseY);

            if (itextcomponent != null && Minecraft.getInstance().currentScreen.handleComponentClick(itextcomponent))
            {
                return;
            }
        }
    }

    @Override
    public void getSentHistory(int msgPos)
    {
        int i = this.sentHistoryCursor + msgPos;
        int j = EventHandlerYT.rightStreamGui.getSentMessages().size();
        i = MathHelper.clamp(i, 0, j);

        if (i != this.sentHistoryCursor)
        {
            if (i == j)
            {
                this.sentHistoryCursor = j;
            }
            else
            {
                this.sentHistoryCursor = i;
            }
        }
    }

    @Override
    public void render(List<GuiButton> buttonList, int mouseX, int mouseY, float partialTicks)
    {
        ITextComponent itextcomponent = EventHandlerYT.rightStreamGui.getYTChatComponent(mouseX, mouseY);

        if (itextcomponent != null && itextcomponent.getStyle().getHoverEvent() != null)
        {
            //Minecraft.getInstance().currentScreen.handleComponentHover(itextcomponent, mouseX, mouseY);TODO AT handleComponentHover
        }
    }

    @Override
    public void keyTypedScrollDown()
    {
        EventHandlerYT.rightStreamGui.func_194813_a(EventHandlerYT.rightStreamGui.getLineCount() - 1);
    }

    @Override
    public void keyTypedScrollUp()
    {
        EventHandlerYT.rightStreamGui.func_194813_a(-EventHandlerYT.rightStreamGui.getLineCount() + 1);
    }
}