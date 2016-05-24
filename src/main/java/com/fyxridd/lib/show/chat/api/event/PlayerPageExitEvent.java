package com.fyxridd.lib.show.chat.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * 玩家退出页面事件
 */
public class PlayerPageExitEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private Player p;

	public PlayerPageExitEvent(Player p) {
        super();
        this.p = p;
    }

    public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}

    public Player getP() {
        return p;
    }
}