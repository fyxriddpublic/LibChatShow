package com.fyxridd.lib.show.chat.api.page;

import java.util.List;

/**
 * 页面上下文
 */
public class PageContext {
    //页面编号,>=1
    private int num;
    //页面描述,如"5 10 15"
    private String text;
    //页面内容,行号
    private List<Integer> content;

    public PageContext(int num, String text, List<Integer> content) {
        this.num = num;
        this.text = text;
        this.content = content;
    }

    public int getNum() {
        return num;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Integer> getContent() {
        return content;
    }
}
