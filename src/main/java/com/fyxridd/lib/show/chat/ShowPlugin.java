package com.fyxridd.lib.show.chat;

import com.fyxridd.lib.core.api.plugin.SimplePlugin;
import com.fyxridd.lib.show.chat.manager.DelayChatManager;

public class ShowPlugin extends SimplePlugin {
    public static ShowPlugin instance;

    private ShowManager showManager;
    private DelayChatManager delayChatManager;

    //启动插件
    @Override
    public void onEnable() {
        instance = this;

        showManager = new ShowManager();
        delayChatManager = new DelayChatManager();

        super.onEnable();
    }

    public ShowManager getShowManager() {
        return showManager;
    }

    public DelayChatManager getDelayChatManager() {
        return delayChatManager;
    }
}