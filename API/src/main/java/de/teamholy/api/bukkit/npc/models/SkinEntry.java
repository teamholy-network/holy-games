package de.teamholy.api.bukkit.npc.models;

import de.teamholy.api.BukkitHolyAPI;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import de.teamholy.core.bukkit.BukkitCore;
import eu.koboo.en2do.repository.entity.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;
import java.util.function.Consumer;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class SkinEntry {

    @Id
    private UUID uuid;

    private String value, signature;

    public void fetch(Consumer<SkinEntry> consumer) {
        if (BukkitHolyAPI.getInstance().getBukkitCacheHandler().getSkinEntryHashMap().containsKey(uuid)) {
            consumer.accept(BukkitHolyAPI.getInstance().getBukkitCacheHandler().getSkinEntryHashMap().get(uuid));
        } else {
            BukkitCore.getAPI().getExecutor().submit(() -> {
                try {
                    URLConnection uRLConnection = (new URL("https://api.minetools.eu/profile/" + uuid)).openConnection();
                    uRLConnection.setReadTimeout(3000);
                    JsonElement jsonElement1 = (new JsonParser()).parse(new BufferedReader(new InputStreamReader(uRLConnection.getInputStream())));
                    JsonElement jsonElement2 = jsonElement1.getAsJsonObject().get("raw");
                    JsonElement jsonElement3 = jsonElement2.getAsJsonObject().get("properties");
                    JsonElement jsonElement4 = jsonElement3.getAsJsonArray().get(0);
                    value = jsonElement4.getAsJsonObject().get("value").toString().replace("\"", "");
                    signature = jsonElement4.getAsJsonObject().get("signature").toString().replace("\"", "");
                    BukkitHolyAPI.getInstance().getNpcSkinRepository().save(this);
                } catch (IOException| NullPointerException exception) {
                    value = "BSz7C28jJUtPulPTfXS7SuAdJAjubBxvCd73xPF64kmyaWtm3Fbs/s9+2fLSgSog+FFmju0HWtNzH340pi2qXCJMcY3cLlVaouSPbbXiUS7pQCTRw2fbmioRMA+tjjv+edFEr1noMB5un5Wv0SxoDcovSvYpWt/VnqtvjmXnZuDxCtMdbD+3pfHLxdRpg/aqglIxuAGMBq6YZO3fC2zem8JtGq+96XAFi447OFgM/mjSuFIv7lm/I7i3Nz43w2JX8X8h1GaoAAr2nDpruywRxjdkrzeH2mfEURKoOc92gmNOpE1NFrPx9isvHU22aDDdHb19i+R28XVpPoljmBF2qmIgXT2uI5VkTFWqh1gm/cYloJURdLqMLYsUbWlFAJgXHSGdydtky6J9bdZiw/Nh8pMXdCDOzusCz+w2ZtNKlzepuOgqBS4Y3Ppl+PtuJqf9TPkaTLfK8XS9NnWAML+Zoh9A4dPmKo7zQl4V1dwDWczSA43toya2gE76tNUE2ygosQhRG+oEdUi5uO4WexCwab+nhCi1PgtXjb7jspTlWXOTWNRT3mzhh7LuOs/19FnTOttjABF1wJyq+yzJfFBZ/YLFgFr2UxcfkdTfIiR3bJvMaxX0rf7L/spiWVUTpm+HlqHtb/suvIZpSCMtbf+rzbnHrvz6CHETEw5lWbWJfCU=";
                    signature = "ewogICJ0aW1lc3RhbXAiIDogMTYyMzA5NDMzMDAyNCwKICAicHJvZmlsZUlkIiA6ICI3ZjgzNWZiYzBiNDU0ZGVmYjViMmQzYmRmZDNiMjNmOCIsCiAgInByb2ZpbGVOYW1lIiA6ICJpYW1TbG93bHkiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzFjNmViMGFkN2M1YjNhZDk0ZjcyY2QzYjk2NTI4MGU2N2I5NTZmMDQ0MWVlNTg5YTVlNjM3ZmQ1NGNjN2I0MyIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9";
                }
                BukkitHolyAPI.getInstance().getBukkitCacheHandler().getSkinEntryHashMap().put(uuid,this);
                consumer.accept(this);
            });
        }
    }

}
