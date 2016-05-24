package com.fyxridd.lib.show.chat.manager;

import com.fyxridd.lib.core.api.config.ConfigApi;
import com.fyxridd.lib.core.api.config.Setter;
import com.fyxridd.lib.core.api.event.PlayerChatReceiveEvent;
import com.fyxridd.lib.core.api.fancymessage.FancyMessage;
import com.fyxridd.lib.show.chat.ShowPlugin;
import com.fyxridd.lib.show.chat.api.ShowApi;
import com.fyxridd.lib.show.chat.config.DelayChatConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.EventExecutor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 延时聊天
 */
public class DelayChatManager {
    private class ShowTask implements Runnable {
        public void run() {
            Iterator<Map.Entry<Player, Queue<FancyMessage>>> it = delayChats.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Player, Queue<FancyMessage>> entry = it.next();
                if (!ShowApi.isInPage(entry.getKey())) {
                    Queue<FancyMessage> queue = entry.getValue();
                    if (!queue.isEmpty()) ShowApi.tip(entry.getKey(), queue.poll(), true);
                    //删除空聊天信息的
                    if (queue.isEmpty()) it.remove();
                }
            }

            //下个循环
            Bukkit.getScheduler().scheduleSyncDelayedTask(ShowPlugin.instance, showTask, config.getInterval());
        }
    }


    private final ShowTask showTask = new ShowTask();

    //配置

    private DelayChatConfig config;

    //缓存

    private HashMap<Player, Queue<FancyMessage>> delayChats = new HashMap<>();

    public DelayChatManager() {
        //添加配置监听
        ConfigApi.addListener(ShowPlugin.instance.pn, DelayChatConfig.class, new Setter<DelayChatConfig>() {
            @Override
            public void set(DelayChatConfig value) {
                config = value;
                //清空延时信息(因为maxSaves更新了)
                delayChats.clear();
            }
        });
        //监听玩家退出事件
        Bukkit.getPluginManager().registerEvent(PlayerQuitEvent.class, ShowPlugin.instance, EventPriority.LOW, new EventExecutor() {
            @Override
            public void execute(Listener listener, Event e) throws EventException {
                delayChats.remove(((PlayerQuitEvent) e).getPlayer());
            }
        }, ShowPlugin.instance);
        //监听聊天接收事件
        Bukkit.getPluginManager().registerEvent(PlayerChatReceiveEvent.class, ShowPlugin.instance, EventPriority.NORMAL, new EventExecutor() {
            @Override
            public void execute(Listener listener, Event e) throws EventException {
                PlayerChatReceiveEvent event = (PlayerChatReceiveEvent) e;
                //添加聊天
                addChat(event.getP(), event.getMsg(), false);
                //取消事件
                event.setCancelled(true);
            }
        }, ShowPlugin.instance, true);
        //延时聊天
        Bukkit.getScheduler().scheduleSyncDelayedTask(ShowPlugin.instance, showTask, config.getInterval());
    }

    /**
     * @see ShowApi#addChat(Player, FancyMessage, boolean)
     */
    public void addChat(Player p, FancyMessage msg, boolean force) {
        if (p == null || msg == null) return;
        FancyMessage msgCopy = msg.clone();

        if (force || !ShowApi.isInPage(p)) ShowApi.tip(p, msgCopy, true);
        else {
            //延时信息添加前缀
            if (config.getPrefix() != null) msgCopy.combine(config.getPrefix(), true);
            //添加到延时显示队列
            Queue<FancyMessage> queue = delayChats.get(p);
            if (queue == null) {
                queue = new ArrayBlockingQueue<>(config.getMaxSaves(), false);
                delayChats.put(p, queue);
            }
            while (queue.size() >= config.getMaxSaves()) queue.poll();
            queue.offer(msgCopy);
        }
    }
}
