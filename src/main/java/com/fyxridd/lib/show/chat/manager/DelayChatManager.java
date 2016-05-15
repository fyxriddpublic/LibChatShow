package com.fyxridd.lib.show.chat.manager;

import com.fyxridd.lib.config.api.ConfigApi;
import com.fyxridd.lib.config.manager.ConfigManager;
import com.fyxridd.lib.core.api.CoreApi;
import com.fyxridd.lib.core.api.event.PlayerChatBroadcastEvent;
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

    private HashMap<Player, Queue<FancyMessage>> delayChats;

    public DelayChatManager() {
        //注册配置
        ConfigApi.register(ShowPlugin.instance.pn, DelayChatConfig.class);
        //添加配置监听
        ConfigApi.addListener(ShowPlugin.instance.pn, DelayChatConfig.class, new ConfigManager.Setter<DelayChatConfig>() {
            @Override
            public void set(DelayChatConfig value) {
                config = value;
            }
        });
        //监听聊天广播事件
        Bukkit.getPluginManager().registerEvent(PlayerChatBroadcastEvent.class, ShowPlugin.instance, EventPriority.NORMAL, new EventExecutor() {
            @Override
            public void execute(Listener listener, Event e) throws EventException {
                PlayerChatBroadcastEvent event = (PlayerChatBroadcastEvent) e;
                //添加聊天
                addChat(event.getP(), event.getMsg(), false);
                //取消事件
                event.setCancelled(true);
            }
        }, ShowPlugin.instance, true);
        //延时聊天
        Bukkit.getScheduler().scheduleSyncDelayedTask(ShowPlugin.instance, showTask, config.getInterval());
    }

    @EventHandler(priority= EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent e) {
        delayChats.remove(e.getPlayer());
    }

    /**
     * @see CoreApi#addChat(Player, com.fyxridd.lib.core.api.inter.FancyMessage, boolean)
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
