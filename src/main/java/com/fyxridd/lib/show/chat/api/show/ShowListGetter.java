package com.fyxridd.lib.show.chat.api.show;

public interface ShowListGetter<T> {
    /**
     * 获取显示列表
     * @param name 玩家名
     * @param arg 变量(可包含空格)
     * @return 显示列表
     */
    ShowList<T> handle(String name, String arg);
}
