package com.fyxridd.lib.show.chat;

import org.bukkit.plugin.java.JavaPlugin;

public class ShowPlugin extends JavaPlugin {
    public static ShowPlugin instance;

    private ShowManager showManager;

    @Override
    public void onLoad() {
        instance = this;
    }

    //启动插件
    @Override
    public void onEnable() {
        showManager = new ShowManager();

        super.onEnable();
    }

    public ShowManager getShowManager() {
        return showManager;
    }
}