package com.fyxridd.lib.show.chat.api.page;

/**
 * 额外键值获取信息
 */
public class MapInfo {
    private String keyName;
    private String plugin, key, arg;

    public MapInfo(String keyName, String plugin, String key, String arg) {
        this.keyName = keyName;
        this.plugin = plugin;
        this.key = key;
        this.arg = arg;
    }

    public String getKeyName() {
        return keyName;
    }

    public String getPlugin() {
        return plugin;
    }

    public String getKey() {
        return key;
    }

    public String getArg() {
        return arg;
    }
}
