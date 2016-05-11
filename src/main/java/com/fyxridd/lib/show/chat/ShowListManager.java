package com.fyxridd.lib.show.chat;

import com.fyxridd.lib.show.chat.api.show.ShowList;
import com.fyxridd.lib.show.chat.api.show.ShowListGetter;

import java.util.HashMap;

/**
 * 列表获取器管理类
 */
public class ShowListManager {
    //插件名 列表名 列表获取器
    private HashMap<String, HashMap<String, ShowListGetter>> handleHash = new HashMap<>();

    /**
     * @see com.fyxridd.lib.core.api.ShowApi#register(String, String, com.fyxridd.lib.core.api.inter.ShowListGetter)
     */
    public void register(String plugin, String key, ShowListGetter showListGetter) {
        if (!handleHash.containsKey(plugin)) handleHash.put(plugin, new HashMap<String, ShowListGetter>());
        handleHash.get(plugin).put(key, showListGetter);
    }

    /**
     * 获取显示列表
     * @param plugin 插件名
     * @param key 列表名
     * @param name 玩家名
     * @param arg 变量(可包含空格)
     * @return 显示列表,异常返回null
     */
    public ShowList getShowList(String plugin, String key, String name, String arg) {
        try {
            return handleHash.get(plugin).get(key).handle(name, arg);
        } catch (Exception e) {
            return null;
        }
    }
}
