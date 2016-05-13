package com.fyxridd.lib.show.chat;

import org.bukkit.plugin.java.JavaPlugin;

public class ShowPlugin extends JavaPlugin {
    public static ShowPlugin instance;

    private ShowManager showManager;

    //启动插件
    @Override
    public void onEnable() {
        instance = this;

        showManager = new ShowManager();

        super.onEnable();
    }

    public ShowManager getShowManager() {
        return showManager;
    }
}