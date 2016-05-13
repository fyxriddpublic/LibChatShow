package com.fyxridd.lib.show.chat.api;

import com.fyxridd.lib.core.api.fancymessage.FancyMessage;
import com.fyxridd.lib.show.chat.ShowPlugin;

import org.bukkit.configuration.ConfigurationSection;

public class ShowApi {
    /**
     * @param msg 文字内容
     * @param config 可为null
     * @return 不为null
     */
    public static FancyMessage load(String msg, ConfigurationSection config) throws Exception {
        return ShowPlugin.instance.getShowManager().load(msg, config);
    }
}
