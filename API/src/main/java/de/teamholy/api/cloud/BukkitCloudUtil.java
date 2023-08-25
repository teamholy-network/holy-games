package de.teamholy.api.cloud;

import de.teamholy.api.BukkitHolyAPI;
import de.teamholy.api.events.bukkit.CloudChannelListenEvent;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.channel.ChannelMessage;
import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.channel.ChannelMessageReceiveEvent;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import de.teamholy.core.bukkit.BukkitCore;
import eu.koboo.markup.MarkupAPI;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.UUID;

/* copyright by Yassino */
public class BukkitCloudUtil implements Listener {

    private BukkitHolyAPI instance;


    @EventListener
    public void onListen(ChannelMessageReceiveEvent event) {
        if (event.getMessage() == null) return;

        instance.getServer().getPluginManager().callEvent(new CloudChannelListenEvent(event.getData(),event.getMessage(),event.getChannel()));
    }

    public BukkitCloudUtil(BukkitHolyAPI instance) {
        this.instance = instance;
        CloudNetDriver.getInstance().getEventManager().registerListener(this);
    }

    public int getMaxCount() {
        return 100;
    }

    @Getter
    private final IPlayerManager playerManager = CloudNetDriver.getInstance().getServicesRegistry()
            .getFirstService(IPlayerManager.class);


    public String getRankColor(UUID uuid) {
        if (MarkupAPI.isNicked(Bukkit.getPlayer(uuid)))
            return "§7";
        return getRankColorWithoutNick(uuid);
    }

    public String getRankColorWithoutNick(UUID uuid) {
        return BukkitCore.getAPI().getCloudManager().getColor(uuid);
    }

    public String getName(UUID uuid) {
        return BukkitCore.getAPI().getUuidManager().getName(uuid);
    }

    public UUID getUuid(String name) {
        return BukkitCore.getAPI().getUuidManager().getUUID(name);
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
