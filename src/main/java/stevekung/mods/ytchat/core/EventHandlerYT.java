package stevekung.mods.ytchat.core;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import stevekung.mods.ytchat.gui.GuiChatYT;
import stevekung.mods.ytchat.gui.GuiRightChatYT;
import stevekung.mods.ytchat.gui.GuiSleepMPYT;
import stevekung.mods.ytchat.utils.YouTubeChatReceiver;
import stevekung.mods.ytchat.utils.YouTubeChatService;

public class EventHandlerYT
{
    public static boolean isReceivedChat;
    public static GuiRightChatYT rightStreamGui;
    private boolean onDisconnected;
    private boolean onConnected;
    private int ticks = 40;
    private final Minecraft mc;
    private boolean initVersionCheck;

    public EventHandlerYT()
    {
        this.mc = Minecraft.getMinecraft();
    }

    @SubscribeEvent
    public void onClientConnectedToServer(FMLNetworkEvent.ClientConnectedToServerEvent event)
    {
        EventHandlerYT.rightStreamGui = new GuiRightChatYT(this.mc);
        //this.mc.ingameGUI.persistantChatGUI = new GuiRightChatYT(this.mc);

        if (EventHandlerYT.isReceivedChat)
        {
            this.onConnected = true;
        }
    }

    @SubscribeEvent
    public void onClientDisconnectFromServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        EventHandlerYT.rightStreamGui.clearChatMessages(true);

        if (EventHandlerYT.isReceivedChat)
        {
            this.onDisconnected = true;
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (EventHandlerYT.isReceivedChat)
        {
            if (this.onConnected && this.ticks > 0)
            {
                this.ticks--;

                if (this.ticks == 0)
                {
                    YouTubeChatService.getService().subscribe(YouTubeChatReceiver.getInstance());
                    this.onConnected = false;
                    this.ticks = 40;
                }
            }
            if (this.onDisconnected && this.ticks > 0)
            {
                this.ticks--;

                if (this.ticks == 0)
                {
                    YouTubeChatService.getService().unsubscribe(YouTubeChatReceiver.getInstance());
                    this.onDisconnected = false;
                    this.ticks = 40;
                }
            }
        }

        if (this.mc.player != null)
        {
            if (!this.initVersionCheck)
            {
                YouTubeChatMod.CHECKER.startCheckIfFailed();
            }
            this.initVersionCheck = true;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_F3) && Keyboard.isKeyDown(Keyboard.KEY_D))
        {
            if (EventHandlerYT.rightStreamGui != null)
            {
                EventHandlerYT.rightStreamGui.clearChatMessages(false);
            }
        }
        //EventHandlerYT.replaceGui(this.mc, this.mc.currentScreen);
    }

    @SubscribeEvent
    public void onPreInfoRender(RenderGameOverlayEvent.Pre event)
    {
        if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT)
        {
            ScaledResolution res = new ScaledResolution(this.mc);
            int width = res.getScaledWidth();
            int height = res.getScaledHeight();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.disableAlpha();
            GlStateManager.pushMatrix();
            GlStateManager.translate(width / 2, height - 48, 0.0F);
            EventHandlerYT.rightStreamGui.drawChat(this.mc.ingameGUI.getUpdateCounter());
            EventHandlerYT.rightStreamGui.drawRightChat(this.mc.ingameGUI.getUpdateCounter());
            GlStateManager.popMatrix();
        }
    }

    @SubscribeEvent
    public void onPressKey(InputEvent.KeyInputEvent event)
    {
        /*if (this.mc.currentScreen == null && this.mc.gameSettings.keyBindCommand.isPressed())
        {
            GuiChatYT chatGuiSlash = new GuiChatYT("/");
            this.mc.displayGuiScreen(chatGuiSlash);
        }*/
    }

    @SubscribeEvent
    public void onClientSendChat(ClientChatEvent event)
    {
        String message = event.getMessage();

        if (message.contains("$set_live"))
        {
            YouTubeChatService.liveVideoId = message.replace("$set_live ", "");
        }
    }

    private static void replaceGui(Minecraft mc, GuiScreen currentScreen)
    {
        if (currentScreen != null)
        {
            if (currentScreen instanceof GuiChat && !(currentScreen instanceof GuiChatYT || currentScreen instanceof GuiSleepMP))
            {
                GuiChatYT chatGui = new GuiChatYT();
                mc.displayGuiScreen(chatGui);
            }
            if (currentScreen instanceof GuiSleepMP && !(currentScreen instanceof GuiSleepMPYT))
            {
                GuiSleepMPYT sleepGui = new GuiSleepMPYT();
                mc.displayGuiScreen(sleepGui);
            }
            if (currentScreen instanceof GuiSleepMPYT && !mc.player.isPlayerSleeping())
            {
                mc.displayGuiScreen(null);
            }
        }
    }
}