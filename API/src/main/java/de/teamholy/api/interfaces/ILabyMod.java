package de.teamholy.api.interfaces;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.EncoderException;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutCustomPayload;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.nio.charset.Charset;
import java.util.UUID;

/* copyright by Yassino */
public interface ILabyMod {
    default void setSubtitle(Player receiver, UUID subtitlePlayer, String value) {
        JsonArray array = new JsonArray();
        JsonObject subtitle = new JsonObject();
        subtitle.addProperty("uuid", subtitlePlayer.toString());
        subtitle.addProperty("size", Double.valueOf(0.8D));
        if (value != null)
            subtitle.addProperty("value", value);
        array.add((JsonElement)subtitle);
        sendLMCMessage(receiver, "account_subtitle", array);
    }

    default void updateGameInfo(Player player, boolean hasGame, String gamemode, long startTime) {
        JsonObject obj = new JsonObject();
        obj.addProperty("hasGame", Boolean.valueOf(hasGame));
        if (hasGame) {
            obj.addProperty("game_mode", gamemode);
            obj.addProperty("game_startTime", Long.valueOf(startTime));
        }
        sendLMCMessage(player, "discord_rpc", obj);
    }

    default void sendCurrentPlayingGameMode(Player receiver, boolean visible, String gamemodeName) {
        JsonObject object = new JsonObject();
        object.addProperty("show_gamemode", Boolean.valueOf(visible));
        object.addProperty("gamemode_name", gamemodeName);
        sendLMCMessage(receiver, "server_gamemode", object);
    }

    default JsonObject addSecret(JsonObject jsonObject, String hasKey, String key, UUID secret, String domain) {
        jsonObject.addProperty(hasKey, Boolean.valueOf(true));
        jsonObject.addProperty(key, secret.toString() + ":" + domain);
        return jsonObject;
    }

    static void sendLMCMessage(Player player, String key, JsonObject messageContent) {
        byte[] bytes = getBytesToSend( key, messageContent.toString() );

        PacketDataSerializer pds = new PacketDataSerializer( Unpooled.wrappedBuffer( bytes ) );
        PacketPlayOutCustomPayload payloadPacket = new PacketPlayOutCustomPayload( "labymod3:main", pds );
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket( payloadPacket );
    }

    static void sendLMCMessage(Player player, String key, JsonArray messageContent) {
        byte[] bytes = getBytesToSend( key, messageContent.toString() );

        PacketDataSerializer pds = new PacketDataSerializer( Unpooled.wrappedBuffer( bytes ) );
        PacketPlayOutCustomPayload payloadPacket = new PacketPlayOutCustomPayload( "labymod3:main", pds );
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket( payloadPacket );
    }

    static byte[] getBytesToSend(String messageKey, String messageContents) {
        ByteBuf byteBuf = Unpooled.buffer();
        writeString(byteBuf, messageKey);
        writeString(byteBuf, messageContents);
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        return bytes;
    }

    static void writeVarIntToBuffer(ByteBuf buf, int input) {
        while ((input & 0xFFFFFF80) != 0) {
            buf.writeByte(input & 0x7F | 0x80);
            input >>>= 7;
        }
        buf.writeByte(input);
    }

    static void writeString(ByteBuf buf, String string) {
        byte[] abyte = string.getBytes(Charset.forName("UTF-8"));
        if (abyte.length > 32767)
            throw new EncoderException("String too big (was " + string.length() + " bytes encoded, max " + 32767 + ")");
        writeVarIntToBuffer(buf, abyte.length);
        buf.writeBytes(abyte);
    }

    default int readVarIntFromBuffer(ByteBuf buf) {
        int i = 0;
        int j = 0;
        while (true) {
            byte b0 = buf.readByte();
            i |= (b0 & Byte.MAX_VALUE) << j++ * 7;
            if (j > 5)
                throw new RuntimeException("VarInt too big");
            if ((b0 & 0x80) != 128)
                return i;
        }
    }

}
