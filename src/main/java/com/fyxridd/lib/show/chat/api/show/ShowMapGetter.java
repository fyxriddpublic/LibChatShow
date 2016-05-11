package com.fyxridd.lib.show.chat.api.show;

import java.util.Map;

public interface ShowMapGetter {
    /**
     * 获取映射的键值映射表
     * @param name 玩家名
     * @param arg 变量(可包含空格)
     * @return 键值映射表
     */
    Map<String, Object> handle(String name, String arg);
}
