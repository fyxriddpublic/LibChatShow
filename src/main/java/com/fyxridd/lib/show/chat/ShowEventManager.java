package com.fyxridd.lib.show.chat;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.projectiles.ProjectileSource;

import com.fyxridd.lib.core.api.ConfigApi;
import com.fyxridd.lib.core.api.event.PlayerChatEvent;
import com.fyxridd.lib.core.api.event.RealDamageEvent;
import com.fyxridd.lib.core.api.event.ReloadConfigEvent;
import com.fyxridd.lib.core.api.fancymessage.FancyMessage;

public class ShowEventManager implements Listener {
    private static boolean inCancelChat;
    private static int deadLoopLevel = 5;//循环最大层次,超过则判定为死循环
    private static boolean cancelInteract, cancelAnimation, cancelAttack, cancelChat, cancelShoot;
    private static int maxBackPage = 10;
    private static int line;
    private static FancyMessage add;
    private static FancyMessage operateTipMenu;
    private static FancyMessage operateTipEmpty;
    private static FancyMessage pageControl, listControl;
    
    public ShowEventManager() {
        //注册事件
        Bukkit.getPluginManager().registerEvents(this, ShowPlugin.instance);
    }

    @EventHandler(priority= EventPriority.LOW)
    public void onReloadConfig(ReloadConfigEvent e) {
        if (e.getPlugin().equals(ShowPlugin.pn)) loadConfig();
    }
    
    @EventHandler(priority= EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityShootBow(EntityShootBowEvent e) {
        if (cancelShoot && e.getEntity() instanceof Player) exit((Player) e.getEntity(), false);
    }

    @EventHandler(priority= EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (cancelInteract) exit(e.getPlayer(), false);
    }

    @EventHandler(priority= EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerAnimation(PlayerAnimationEvent e) {
        if (cancelAnimation) exit(e.getPlayer(), false);
    }

    @EventHandler(priority= EventPriority.LOW, ignoreCancelled = false)
    public void onPlayerChat(PlayerChatEvent e) {
        if (cancelChat) exit(e.getP(), false);
        else if (inCancelChat && !e.isCancelled() && isInPage(e.getP())) e.setCancelled(true);
    }

    @EventHandler(priority= EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent e) {
        exit(e.getPlayer(), false);
    }

    @EventHandler(priority= EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent e) {
        exit(e.getEntity(), false);
    }

    @EventHandler(priority= EventPriority.LOWEST)
    public void onRealDamage(RealDamageEvent e) {
        if (cancelAttack) {
            EntityDamageByEntityEvent event = e.getEntityDamageByEntityEvent();

            //受攻击者
            if (event.getEntity() instanceof Player) {
                ShowManager.exit((Player) event.getEntity(), false, true);
            }

            //攻击者
            Player damager = null;
            if (event.getDamager() instanceof Player) damager = (Player) event.getDamager();
            else if (event.getDamager() instanceof Projectile) {
                ProjectileSource ps = ((Projectile) event.getDamager()).getShooter();
                if (ps instanceof Player) damager = (Player) ps;
            }
            if (damager != null) ShowManager.exit(damager, false, true);
        }
    }
    
    private void loadConfig() {
        YamlConfiguration config = ConfigApi.getConfig(ShowPlugin.pn);

        //inCancelChat
        inCancelChat = config.getBoolean("show.cancel.chatCancel");

        //deadLoopLevel
        deadLoopLevel = config.getInt("show.deadLoopLevel");
        if (deadLoopLevel < 1) {
            deadLoopLevel = 1;
            ConfigApi.log(ShowPlugin.pn, "show.deadLoopLevel < 1");
        }
        //cancelInteract,cancelAnimation,cancelChat
        cancelInteract = config.getBoolean("show.cancel.interact");
        cancelAnimation = config.getBoolean("show.cancel.animation");
        cancelAttack = config.getBoolean("show.cancel.attack");
        cancelChat = config.getBoolean("show.cancel.chat");
        cancelShoot = config.getBoolean("show.cancel.shoot");
        //maxBackPage
        maxBackPage = config.getInt("show.maxBackPage");
        if (maxBackPage < 0) {
            maxBackPage = 0;
            ConfigApi.log(ShowPlugin.pn, "show.maxBackPage < 0");
        }
        //line
        line = config.getInt("show.line");
        if (line < 1) {
            line = 1;
            ConfigApi.log(ShowPlugin.pn, "show.line < 1");
        }
        //add
        add = get(730);
        //operateTipMenu
        operateTipMenu = get(710);
        //operateTipEmpty
        operateTipEmpty = get(715);
        //pageControl
        pageControl = get(720);
        //listControl
        listControl = get(725);
    }
}
