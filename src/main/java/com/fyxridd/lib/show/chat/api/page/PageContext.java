package com.fyxridd.lib.show.chat.api.page;

import java.util.List;

/**
 * 页面上下文
 */
public class PageContext {
    //页面编号,>=1
    private int num;
    //页面内容,行号
    private List<Integer> content;

    public PageContext(int num, List<Integer> content) {
        this.num = num;
        this.content = content;
    }

    public int getNum() {
        return num;
    }

    public List<Integer> getContent() {
        return content;
    }
}
