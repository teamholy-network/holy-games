package de.teamholy.api.events.bungee;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Event;

/* copyright by Yassino */
@Getter
public class CloudChannelListenEvent extends Event {

    private JsonDocument data;
    private String message, channel;

    public CloudChannelListenEvent(JsonDocument data, String message, String channel) {
        this.data = data;
        this.message = message;
        this.channel = channel;
    }



}
