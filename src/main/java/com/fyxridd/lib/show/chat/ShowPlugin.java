package com.fyxridd.lib.show.chat;

import com.fyxridd.lib.core.api.config.ConfigApi;
import com.fyxridd.lib.core.api.plugin.SimplePlugin;
import com.fyxridd.lib.show.chat.config.DelayChatConfig;
import com.fyxridd.lib.show.chat.config.ShowConfig;
import com.fyxridd.lib.show.chat.func.ShowCmd;
import com.fyxridd.lib.show.chat.manager.DelayChatManager;
import com.fyxridd.lib.show.chat.manager.ShowEventManager;
import com.fyxridd.lib.show.chat.manager.ShowListManager;
import com.fyxridd.lib.show.chat.manager.ShowManager;

public class ShowPlugin extends SimplePlugin {
    public static ShowPlugin instance;

    private ShowManager showManager;
    private ShowEventManager showEventManager;
    private ShowListManager showListManager;
    private DelayChatManager delayChatManager;
    private ShowCmd showCmd;
    
    //启动插件
    @Override
    public void onEnable() {
        instance = this;

        //注册配置
        ConfigApi.register(ShowPlugin.instance.pn, ShowConfig.class);
        ConfigApi.register(ShowPlugin.instance.pn, DelayChatConfig.class);

        showManager = new ShowManager();
        showEventManager = new ShowEventManager();
        showListManager = new ShowListManager();
        delayChatManager = new DelayChatManager();
        showCmd = new ShowCmd();

        super.onEnable();
    }

    public ShowManager getShowManager() {
        return showManager;
    }

    public ShowEventManager getShowEventManager() {
        return showEventManager;
    }

    public ShowListManager getShowListManager() {
        return showListManager;
    }

    public DelayChatManager getDelayChatManager() {
        return delayChatManager;
    }
}