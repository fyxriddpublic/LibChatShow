package com.fyxridd.lib.show.chat.config;

import com.fyxridd.lib.core.api.config.basic.Path;
import com.fyxridd.lib.core.api.config.limit.Min;

@Path("show")
public class ShowConfig {
    @Path("chatFuncPrefix")
    private String chatFuncPrefix;
    @Path("deadLoopLevel")
    @Min(1)
    private int deadLoopLevel;
    @Path("cancel.interact")
    private boolean cancelInteract;
    @Path("cancel.animation")
    private boolean cancelAnimation;
    @Path("cancel.attack")
    private boolean cancelAttack;
    @Path("cancel.chat")
    private boolean cancelChat;
    @Path("cancel.shoot")
    private boolean cancelShoot;
    @Path("maxBackPage")
    @Min(0)
    private int maxBackPage = 10;
    @Path("line")
    @Min(1)
    private int line;
    @Path("cancel.chatCancel")
    private boolean inCancelChat;

    @Path("fancymessage.add")
    private int add;
    @Path("fancymessage.operateTipMenu")
    private int operateTipMenu;
    @Path("fancymessage.operateTipEmpty")
    private int operateTipEmpty;
    @Path("fancymessage.pageControl")
    private int pageControl;
    @Path("fancymessage.listControl")
    private int listControl;

    public String getChatFuncPrefix() {
        return chatFuncPrefix;
    }

    public int getDeadLoopLevel() {
        return deadLoopLevel;
    }

    public boolean isCancelInteract() {
        return cancelInteract;
    }

    public boolean isCancelAnimation() {
        return cancelAnimation;
    }

    public boolean isCancelAttack() {
        return cancelAttack;
    }

    public boolean isCancelChat() {
        return cancelChat;
    }

    public boolean isCancelShoot() {
        return cancelShoot;
    }

    public boolean isInCancelChat() {
        return inCancelChat;
    }

    public int getMaxBackPage() {
        return maxBackPage;
    }

    public int getLine() {
        return line;
    }

    public int getAdd() {
        return add;
    }

    public int getOperateTipMenu() {
        return operateTipMenu;
    }

    public int getOperateTipEmpty() {
        return operateTipEmpty;
    }

    public int getPageControl() {
        return pageControl;
    }

    public int getListControl() {
        return listControl;
    }
}
