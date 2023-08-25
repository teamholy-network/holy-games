package de.teamholy.api.cloud;

import de.teamholy.api.BungeeHolyAPI;
import de.teamholy.api.events.bungee.CloudChannelListenEvent;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.channel.ChannelMessage;
import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.channel.ChannelMessageReceiveEvent;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import de.teamholy.core.bungee.BungeeCore;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Listener;

import java.util.UUID;
/* copyright by Yassino */
public class CloudUtil implements Listener {

    @Getter
    private IPlayerManager playerManager;

    private BungeeHolyAPI instance;

    @EventListener
    public void onListen(ChannelMessageReceiveEvent event) {
        if (event.getMessage() == null) return;

        instance.getProxy().getPluginManager().callEvent(new CloudChannelListenEvent(event.getData(),event.getMessage(),event.getChannel()));
    }

    public CloudUtil(BungeeHolyAPI instance) {
        this.instance = instance;
        this.playerManager = CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class);
        CloudNetDriver.getInstance().getEventManager().registerListener(this);
    }

    public int getMaxCount() {
        return 125;
    }

    public String getRankColor(UUID uuid) {
        return BungeeCore.getAPI().getCloudManager().getColor(uuid);
    }

    public String getName(UUID uuid) {
        return BungeeCore.getAPI().getUuidManager().getName(uuid);
    }

    public UUID getUuid(String name) {
        return BungeeCore.getAPI().getUuidManager().getUUID(name);
    }

    public void sendCloudMessage(String channel, String message, JsonDocument data) {
        ChannelMessage.builder()
                .channel(channel)
                .message(message)
                .json(data)
                .targetAll()
                .build()
                .send();
    }

}
