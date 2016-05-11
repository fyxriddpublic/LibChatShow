package com.fyxridd.lib.show.chat.api.show;

public interface ShowListGetter {
    /**
     * 获取显示列表
     * @param name 玩家名
     * @param arg 变量(可包含空格)
     * @return 显示列表
     */
    ShowList handle(String name, String arg);
}
