package com.fyxridd.lib.show.chat.api.page;

/**
 * 显示列表
 */
public class ListInfo {
    private String plugin, key, arg;

    public ListInfo(String plugin, String key, String arg) {
        this.plugin = plugin;
        this.key = key;
        this.arg = arg;
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
