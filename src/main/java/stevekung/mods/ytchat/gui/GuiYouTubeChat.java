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

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevekung.mods.stevekunglib.client.gui.IGuiChat;
import stevekung.mods.ytchat.core.EventHandlerYT;

@SideOnly(Side.CLIENT)
public class GuiYouTubeChat implements IGuiChat
{
    private int sentHistoryCursor = -1;

    @Override
    public void initGui(List<GuiButton> buttonList, int width, int height)
    {
        this.sentHistoryCursor = EventHandlerYT.rightStreamGui.getSentMessages().size();
    }

    @Override
    public void handleMouseInput(int width, int height)
    {
        int i = Mouse.getEventDWheel();

        if (i != 0)
        {
            if (i > 1)
            {
                i = 1;
            }
            if (i < -1)
            {
                i = -1;
            }

            if (!GuiScreen.isShiftKeyDown())
            {
                i *= 7;
            }
            EventHandlerYT.rightStreamGui.scroll(i);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (mouseButton == 0)
        {
            ITextComponent itextcomponent = EventHandlerYT.rightStreamGui.getYTChatComponent(Mouse.getX(), Mouse.getY());

            if (itextcomponent != null && Minecraft.getMinecraft().currentScreen.handleComponentClick(itextcomponent))
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
    public void drawScreen(List<GuiButton> buttonList, int mouseX, int mouseY, float partialTicks)
    {
        ITextComponent itextcomponent = EventHandlerYT.rightStreamGui.getYTChatComponent(Mouse.getX(), Mouse.getY());

        if (itextcomponent != null && itextcomponent.getStyle().getHoverEvent() != null)
        {
            Minecraft.getMinecraft().currentScreen.handleComponentHover(itextcomponent, mouseX, mouseY);
        }
    }

    @Override
    public void pageUp()
    {
        EventHandlerYT.rightStreamGui.scroll(EventHandlerYT.rightStreamGui.getLineCount() - 1);
    }

    @Override
    public void pageDown()
    {
        EventHandlerYT.rightStreamGui.scroll(-EventHandlerYT.rightStreamGui.getLineCount() + 1);
    }

    @Override
    public void actionPerformed(GuiButton button) {}

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {}

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {}

    @Override
    public void onGuiClosed() {}

    @Override
    public void updateScreen(List<GuiButton> buttonList, int width, int height) {}
}