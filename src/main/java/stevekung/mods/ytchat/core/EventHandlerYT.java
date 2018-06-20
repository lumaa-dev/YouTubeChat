package stevekung.mods.ytchat.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import stevekung.mods.ytchat.gui.GuiRightStreamChat;
import stevekung.mods.ytchat.utils.YouTubeChatReceiver;
import stevekung.mods.ytchat.utils.YouTubeChatService;

public class EventHandlerYT
{
    public static boolean isReceivedChat;
    public static GuiRightStreamChat rightStreamGui;
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
        EventHandlerYT.rightStreamGui = new GuiRightStreamChat(this.mc);

        if (EventHandlerYT.isReceivedChat)
        {
            this.onConnected = true;
        }
    }

    @SubscribeEvent
    public void onClientDisconnectFromServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        EventHandlerYT.rightStreamGui.clearChatMessages();

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
            GlStateManager.popMatrix();
        }
    }
}