package com.fyxridd.lib.show.chat.manager;

import com.fyxridd.lib.core.api.config.ConfigApi;

import com.fyxridd.lib.core.api.config.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.projectiles.ProjectileSource;

import com.fyxridd.lib.core.api.event.PlayerChatEvent;
import com.fyxridd.lib.core.api.event.PlayerTipEvent;
import com.fyxridd.lib.core.api.event.RealDamageEvent;
import com.fyxridd.lib.show.chat.ShowPlugin;
import com.fyxridd.lib.show.chat.api.ShowApi;
import com.fyxridd.lib.show.chat.config.ShowConfig;

public class ShowEventManager {
    private ShowConfig config;
    
    public ShowEventManager() {
        //添加配置监听
        ConfigApi.addListener(ShowPlugin.instance.pn, ShowConfig.class, new Setter<ShowConfig>(){
            @Override
            public void set(ShowConfig value) {
                config = value;
            }
        });
        
        //注册事件
        {
            //玩家提示事件
            Bukkit.getPluginManager().registerEvent(PlayerTipEvent.class, ShowPlugin.instance, EventPriority.NORMAL, new EventExecutor() {
                @Override
                public void execute(Listener listener, Event e) throws EventException {
                    PlayerTipEvent event = (PlayerTipEvent) e;
                    if (ShowApi.isInPage(event.getP())) {
                        if (event.isForce()) {//强制显示
                            //取消事件
                            event.setCancelled(true);
                            //使用自己的处理方法
                            ShowApi.tip(event.getP(), event.getMsgs(), true);
                        }else event.setCancelled(true);
                    }
                }
            }, ShowPlugin.instance, true);
            
            //玩家聊天
            Bukkit.getPluginManager().registerEvent(PlayerChatEvent.class, ShowPlugin.instance, EventPriority.LOW, new EventExecutor() {
                @Override
                public void execute(Listener listener, Event e) throws EventException {
                    PlayerChatEvent event = (PlayerChatEvent) e;
                    if (config.isCancelChat()) ShowApi.exit(event.getP(), false);
                    else if (config.isInCancelChat() && ShowApi.isInPage(event.getP())) event.setCancelled(true);
                }
            }, ShowPlugin.instance, true);

            //玩家射箭
            Bukkit.getPluginManager().registerEvent(EntityShootBowEvent.class, ShowPlugin.instance, EventPriority.HIGHEST, new EventExecutor() {
                @Override
                public void execute(Listener listener, Event e) throws EventException {
                    EntityShootBowEvent event = (EntityShootBowEvent) e;
                    if (config.isCancelShoot() && event.getEntity() instanceof Player) ShowApi.exit((Player) event.getEntity(), false);
                }
            }, ShowPlugin.instance, true);

            //玩家接触
            Bukkit.getPluginManager().registerEvent(PlayerInteractEvent.class, ShowPlugin.instance, EventPriority.HIGHEST, new EventExecutor() {
                @Override
                public void execute(Listener listener, Event e) throws EventException {
                    PlayerInteractEvent event = (PlayerInteractEvent) e;
                    if (config.isCancelInteract()) ShowApi.exit(event.getPlayer(), false);
                }
            }, ShowPlugin.instance, true);

            //玩家挥手
            Bukkit.getPluginManager().registerEvent(PlayerAnimationEvent.class, ShowPlugin.instance, EventPriority.HIGHEST, new EventExecutor() {
                @Override
                public void execute(Listener listener, Event e) throws EventException {
                    PlayerAnimationEvent event = (PlayerAnimationEvent) e;
                    if (config.isCancelAnimation()) ShowApi.exit(event.getPlayer(), false);
                }
            }, ShowPlugin.instance, true);

            //真实伤害
            Bukkit.getPluginManager().registerEvent(RealDamageEvent.class, ShowPlugin.instance, EventPriority.LOWEST, new EventExecutor() {
                @Override
                public void execute(Listener listener, Event e) throws EventException {
                    RealDamageEvent event = (RealDamageEvent) e;
                    if (config.isCancelAttack()) {
                        EntityDamageByEntityEvent entityDamageByEntityEvent = event.getEntityDamageByEntityEvent();

                        //受攻击者
                        if (entityDamageByEntityEvent.getEntity() instanceof Player) {
                            ShowApi.exit((Player) entityDamageByEntityEvent.getEntity(), false, true);
                        }

                        //攻击者
                        Player damager = null;
                        if (entityDamageByEntityEvent.getDamager() instanceof Player) damager = (Player) entityDamageByEntityEvent.getDamager();
                        else if (entityDamageByEntityEvent.getDamager() instanceof Projectile) {
                            ProjectileSource ps = ((Projectile) entityDamageByEntityEvent.getDamager()).getShooter();
                            if (ps instanceof Player) damager = (Player) ps;
                        }
                        if (damager != null) ShowApi.exit(damager, false, true);
                    }
                }
            }, ShowPlugin.instance);
            
            //玩家死亡
            Bukkit.getPluginManager().registerEvent(PlayerDeathEvent.class, ShowPlugin.instance, EventPriority.LOWEST, new EventExecutor() {
                @Override
                public void execute(Listener listener, Event e) throws EventException {
                    PlayerDeathEvent event = (PlayerDeathEvent) e;
                    ShowApi.exit(event.getEntity(), false);
                }
            }, ShowPlugin.instance);
            //玩家退出
            Bukkit.getPluginManager().registerEvent(PlayerQuitEvent.class, ShowPlugin.instance, EventPriority.LOWEST, new EventExecutor() {
                @Override
                public void execute(Listener listener, Event e) throws EventException {
                    PlayerQuitEvent event = (PlayerQuitEvent) e;
                    ShowApi.exit(event.getPlayer(), false);
                }
            }, ShowPlugin.instance);
        }
    }
}
