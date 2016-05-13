package com.fyxridd.lib.show.chat.api;

import com.fyxridd.lib.core.api.fancymessage.FancyMessage;
import com.fyxridd.lib.show.chat.ShowManager;
import com.fyxridd.lib.show.chat.ShowPlugin;

import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;

public class ShowApi {
    /**
     * @param msg 文字内容
     * @param config 可为null
     * @return 不为null
     */
    public static FancyMessage load(String msg, ConfigurationSection config) throws Exception {
        return ShowManager.load(msg, config);
    }

    /**
     * 转换变量(如果无法转换则不会转换)
     * @see com.fyxridd.lib.show.chat.api.fancymessage.Convertable#convert(Object...)
     * @param msg 信息
     * @param replace 变量
     */
    public static void convert(FancyMessage msg, Object... replace) {
        ShowPlugin.instance.getShowManager().convert(msg, replace);
    }

    /**
     * 转换单个变量(如果无法转换则不会转换)
     * @see com.fyxridd.lib.show.chat.api.fancymessage.Convertable#convert(String, Object)
     */
    public static void convert(FancyMessage msg, String from, String to) {
        ShowPlugin.instance.getShowManager().convert(msg, from, to);
    }

    /**
     * 转换变量(如果无法转换则不会转换)
     * @see com.fyxridd.lib.show.chat.api.fancymessage.Convertable#convert(HashMap)
     */
    public static void convert(FancyMessage msg, HashMap<String, Object> replace) {
        ShowPlugin.instance.getShowManager().convert(msg, replace);
    }
}
