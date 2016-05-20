package com.fyxridd.lib.show.chat.api;

import com.fyxridd.lib.core.api.fancymessage.FancyMessage;

import com.fyxridd.lib.show.chat.ShowPlugin;
import com.fyxridd.lib.show.chat.Util;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ShowApi {
    /**
     * 其它聊天插件不能自行给玩家发送聊天信息或调用MessageApi.send方法,而要调用此方法,否则不会有延时显示聊天信息的功能
     * @param p 玩家,可为null(null时无效果)
     * @param msg 聊天信息,可为null(null时无效果)
     * @param force 是否强制显示
     */
    public static void addChat(Player p, FancyMessage msg, boolean force) {
        ShowPlugin.instance.getDelayChatManager().addChat(p, msg, force);
    }

    /**
     * @param msg 文字内容
     * @param config 可为null
     * @return 不为null
     */
    public static FancyMessage load(String msg, ConfigurationSection config) throws Exception {
        return Util.load(msg, config);
    }
}
