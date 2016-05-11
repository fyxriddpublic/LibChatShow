package com.fyxridd.lib.show.chat;

import com.fyxridd.lib.show.chat.api.show.ShowMapGetter;

import java.util.HashMap;
import java.util.Map;

/**
 * 键值获取器管理类
 */
public class ShowMapManager {
    //插件名 键名 键值获取器
    private HashMap<String, HashMap<String, ShowMapGetter>> getters = new HashMap<>();

    public void register(String plugin, String key, ShowMapGetter showMapGetter) {
        if (!getters.containsKey(plugin)) getters.put(plugin, new HashMap<String, ShowMapGetter>());
        getters.get(plugin).put(key, showMapGetter);
    }

    /**
     * 获取键值
     * @param plugin 插件名
     * @param key 键名
     * @param name 玩家名
     * @param arg 变量(可包含空格)
     * @return 键值映射表,异常返回null
     */
    public Map<String, Object> getShowMap(String plugin, String key, String name, String arg) {
        try {
            return getters.get(plugin).get(key).handle(name, arg);
        } catch (Exception e) {
            return null;
        }
    }
}
