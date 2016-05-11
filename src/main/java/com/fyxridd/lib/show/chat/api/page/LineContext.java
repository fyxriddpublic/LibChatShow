package com.fyxridd.lib.show.chat.api.page;

import com.fyxridd.lib.core.api.fancymessage.FancyMessage;

/**
 * 行上下文
 */
public class LineContext {
    //行号,>=1
    private int num;
    //行信息
    private FancyMessage msg;

    public LineContext(int num, FancyMessage msg) {
        this.num = num;
        this.msg = msg;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public FancyMessage getMsg() {
        return msg;
    }

    public void setMsg(FancyMessage msg) {
        this.msg = msg;
    }

    public String getText() {
        return msg.getText();
    }
}
