package com.fyxridd.lib.show.chat.manager;

import com.fyxridd.lib.show.chat.api.ShowApi;
import com.fyxridd.lib.show.chat.api.show.ShowList;
import com.fyxridd.lib.show.chat.api.show.ShowListGetter;

import java.util.HashMap;
import java.util.Map;

/**
 * 列表获取器管理类
 */
public class ShowListManager {
    //插件名 列表名 列表获取器
    private Map<String, Map<String, ShowListGetter<?>>> handleHash = new HashMap<>();

    /**
     * @see com.fyxridd.lib.core.api.ShowApi#register(String, String, com.fyxridd.lib.core.api.inter.ShowListGetter)
     */
    public void register(String plugin, String key, ShowListGetter<?> showListGetter) {
        if (!handleHash.containsKey(plugin)) handleHash.put(plugin, new HashMap<String, ShowListGetter<?>>());
        handleHash.get(plugin).put(key, showListGetter);
    }

    /**
     * @see ShowApi#getShowList(String, String, String, String)
     */
    public ShowList getShowList(String plugin, String key, String name, String arg) {
        try {
            return handleHash.get(plugin).get(key).handle(name, arg);
        } catch (Exception e) {
            return null;
        }
    }
}
