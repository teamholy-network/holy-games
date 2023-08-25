package de.teamholy.api;


import de.teamholy.api.cloud.CloudUtil;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;

/* copyright by Yassino */
@Getter
public class BungeeHolyAPI extends Plugin {

    @Getter
    private static BungeeHolyAPI instance;
    private CloudUtil cloudUtil;
    private String prefix = "§6Teamholy §8× §7";



    @Override
    public void onEnable() {
        instance = this;
        cloudUtil = new CloudUtil(this);
    }

    @Override
    public void onDisable() {

    }
}
