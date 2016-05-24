package com.fyxridd.lib.show.chat.config;

import com.fyxridd.lib.core.api.config.basic.Path;
import com.fyxridd.lib.core.api.config.convert.ConfigConvert;
import com.fyxridd.lib.core.api.config.limit.Min;
import com.fyxridd.lib.core.api.lang.LangConverter;
import com.fyxridd.lib.core.api.lang.LangGetter;

public class ShowConfig {
    @Path("lang")
    @ConfigConvert(LangConverter.class)
    private LangGetter lang;

    @Path("show.deadLoopLevel")
    @Min(1)
    private int deadLoopLevel;
    @Path("show.cancel.interact")
    private boolean cancelInteract;
    @Path("show.cancel.animation")
    private boolean cancelAnimation;
    @Path("show.cancel.attack")
    private boolean cancelAttack;
    @Path("show.cancel.chat")
    private boolean cancelChat;
    @Path("show.cancel.shoot")
    private boolean cancelShoot;
    @Path("show.maxBackPage")
    @Min(0)
    private int maxBackPage = 10;
    @Path("show.line")
    @Min(1)
    private int line;
    @Path("show.cancel.chatCancel")
    private boolean inCancelChat;

    @Path("show.fancymessage.add")
    private int add;
    @Path("show.fancymessage.operateTipMenu")
    private int operateTipMenu;
    @Path("show.fancymessage.operateTipEmpty")
    private int operateTipEmpty;
    @Path("show.fancymessage.pageControl")
    private int pageControl;
    @Path("show.fancymessage.listControl")
    private int listControl;
    
    public LangGetter getLang() {
        return lang;
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
