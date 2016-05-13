package com.fyxridd.lib.show.chat.api;

import com.fyxridd.lib.core.api.fancymessage.FancyMessage;

import com.fyxridd.lib.show.chat.Util;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;

public class ShowApi {
    /**
     * 转换变量(如果无法转换则不会转换)
     * @see com.fyxridd.lib.show.chat.api.fancymessage.Convertable#convert(Object...)
     * @param msg 信息
     * @param replace 变量
     */
    public static void convert(FancyMessage msg, Object... replace) {
        Util.convert(msg, replace);
    }

    /**
     * 转换单个变量(如果无法转换则不会转换)
     * @see com.fyxridd.lib.show.chat.api.fancymessage.Convertable#convert(String, Object)
     */
    public static void convert(FancyMessage msg, String from, String to) {
        Util.convert(msg, from, to);
    }

    /**
     * 转换变量(如果无法转换则不会转换)
     * @see com.fyxridd.lib.show.chat.api.fancymessage.Convertable#convert(HashMap)
     */
    public static void convert(FancyMessage msg, HashMap<String, Object> replace) {
        Util.convert(msg, replace);
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
