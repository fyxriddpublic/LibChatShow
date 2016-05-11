package com.fyxridd.lib.show.chat.api.show;

/**
 * 显示接口<br>
 * 所有调用ShowManager.show(xxx)方法的类必须实现这个接口<br>
 * 用来页面跳转(刷新)时回调
 */
public interface Refresh {
    /**
     * 重新加载页面(刷新)<br>
     * 注意!此方法实现里不能调用ShowApi.tip()而应该用ShowApi.setTip()+ShowApi.reShow(pc, true)代替,否则会产生死循环!
     * @param playerContext 玩家页面上下文
     */
    public void refresh(PlayerContext playerContext);
}
