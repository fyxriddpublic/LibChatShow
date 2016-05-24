package com.fyxridd.lib.show.chat.func;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fyxridd.lib.core.api.config.ConfigApi;
import com.fyxridd.lib.core.api.fancymessage.FancyMessage;
import com.fyxridd.lib.core.config.ConfigManager;
import com.fyxridd.lib.func.api.func.Default;
import com.fyxridd.lib.func.api.func.Func;
import com.fyxridd.lib.func.api.func.FuncType;
import com.fyxridd.lib.show.chat.ShowPlugin;
import com.fyxridd.lib.show.chat.api.ShowApi;
import com.fyxridd.lib.show.chat.api.page.Page;
import com.fyxridd.lib.show.chat.api.show.PlayerContext;
import com.fyxridd.lib.show.chat.api.show.Refresh;
import com.fyxridd.lib.show.chat.api.show.ShowList;
import com.fyxridd.lib.show.chat.config.ShowConfig;

@FuncType("cmd")
public class ShowCmd {
    private ShowConfig config;
    
    public ShowCmd() {
        //添加配置监听
        ConfigApi.addListener(ShowPlugin.instance.pn, ShowConfig.class, new ConfigManager.Setter<ShowConfig>() {
            @Override
            public void set(ShowConfig value) {
                config = value;
            }
        });
    }
    
    /**
     * 返回上一页
     */
    @Func("back")
    public void back(CommandSender sender) {
        if (!(sender instanceof Player)) return;
        Player p = (Player) sender;
        
        //当前没有查看的页面
        PlayerContext pc = ShowApi.getPlayerContext(p);
        if (pc == null) {
            ShowApi.tip(p, get(p.getName(), 665), true);
            return;
        }
        
        ShowApi.back(p);
    }

    /**
     * 退出页面
     */
    @Func("exit")
    public void exit(CommandSender sender) {
        if (!(sender instanceof Player)) return;
        Player p = (Player) sender;

        //当前没有查看的页面
        PlayerContext pc = ShowApi.getPlayerContext(p);
        if (pc == null) {
            ShowApi.tip(p, get(p.getName(), 665), true);
            return;
        }
        
        ShowApi.exit(p, true);
    }

    /**
     * 页面前一页
     */
    @Func("prePage")
    public void prePage(CommandSender sender) {
        if (!(sender instanceof Player)) return;
        Player p = (Player) sender;

        //当前没有查看的页面
        PlayerContext pc = ShowApi.getPlayerContext(p);
        if (pc == null) {
            ShowApi.tip(p, get(p.getName(), 665), true);
            return;
        }

        toPage(p, pc.getPageNow() - 1, false);
    }

    /**
     * 页面后一页
     */
    @Func("nextPage")
    public void nextPage(CommandSender sender) {
        if (!(sender instanceof Player)) return;
        Player p = (Player) sender;

        //当前没有查看的页面
        PlayerContext pc = ShowApi.getPlayerContext(p);
        if (pc == null) {
            ShowApi.tip(p, get(p.getName(), 665), true);
            return;
        }

        toPage(p, pc.getPageNow() + 1, false);
    }

    /**
     * 页面第一页
     */
    @Func("firstPage")
    public void firstPage(CommandSender sender) {
        if (!(sender instanceof Player)) return;
        Player p = (Player) sender;

        //当前没有查看的页面
        PlayerContext pc = ShowApi.getPlayerContext(p);
        if (pc == null) {
            ShowApi.tip(p, get(p.getName(), 665), true);
            return;
        }

        toPage(p, 1, false);
    }

    /**
     * 页面最后一页
     */
    @Func("lastPage")
    public void lastPage(CommandSender sender) {
        if (!(sender instanceof Player)) return;
        Player p = (Player) sender;

        //当前没有查看的页面
        PlayerContext pc = ShowApi.getPlayerContext(p);
        if (pc == null) {
            ShowApi.tip(p, get(p.getName(), 665), true);
            return;
        }
        
        Page page = ShowApi.getPage(pc.getPlugin(), pc.getPageName());
        if (page == null) return;//异常
        toPage(p, page.getPageMax(), false);
    }

    /**
     * 前往页面指定页
     */
    @Func("toPage")
    public void toPage(CommandSender sender, @Default("1") int page) {
        if (!(sender instanceof Player)) return;
        Player p = (Player) sender;

        //当前没有查看的页面
        PlayerContext pc = ShowApi.getPlayerContext(p);
        if (pc == null) {
            ShowApi.tip(p, get(p.getName(), 665), true);
            return;
        }
        
        toPage(p, page, true);
    }

    /**
     * 列表前一页
     */
    @Func("preList")
    public void preList(CommandSender sender) {
        if (!(sender instanceof Player)) return;
        Player p = (Player) sender;

        //当前没有查看的页面
        PlayerContext pc = ShowApi.getPlayerContext(p);
        if (pc == null) {
            ShowApi.tip(p, get(p.getName(), 665), true);
            return;
        }

        toListPage(p, pc.getListNow() - 1, false);
    }

    /**
     * 列表后一页
     */
    @Func("nextList")
    public void nextList(CommandSender sender) {
        if (!(sender instanceof Player)) return;
        Player p = (Player) sender;

        //当前没有查看的页面
        PlayerContext pc = ShowApi.getPlayerContext(p);
        if (pc == null) {
            ShowApi.tip(p, get(p.getName(), 665), true);
            return;
        }

        toListPage(p, pc.getListNow() + 1, false);
    }

    /**
     * 列表第一页
     */
    @Func("firstList")
    public void firstList(CommandSender sender) {
        if (!(sender instanceof Player)) return;
        Player p = (Player) sender;

        //当前没有查看的页面
        PlayerContext pc = ShowApi.getPlayerContext(p);
        if (pc == null) {
            ShowApi.tip(p, get(p.getName(), 665), true);
            return;
        }

        toListPage(p, 1, false);
    }

    /**
     * 列表最后一页
     */
    @Func("lastList")
    public void lastList(CommandSender sender) {
        if (!(sender instanceof Player)) return;
        Player p = (Player) sender;

        //当前没有查看的页面
        PlayerContext pc = ShowApi.getPlayerContext(p);
        if (pc == null) {
            ShowApi.tip(p, get(p.getName(), 665), true);
            return;
        }

        int listMax;
        if (pc.getList() == null) listMax = 0;
        else listMax = pc.getList().getMaxPage(pc.getListSize());
        toListPage(p, listMax, false);
    }

    /**
     * 前往列表指定页
     */
    @Func("toList")
    public void toList(CommandSender sender, @Default("1") int page) {
        if (!(sender instanceof Player)) return;
        Player p = (Player) sender;

        //当前没有查看的页面
        PlayerContext pc = ShowApi.getPlayerContext(p);
        if (pc == null) {
            ShowApi.tip(p, get(p.getName(), 665), true);
            return;
        }
        
        toListPage(p, page, true);
    }

    /**
     * 显示自定义页面
     */
    @Func("customPage")
    public void customPage(CommandSender sender, String pageName) {
        if (!(sender instanceof Player)) return;
        Player p = (Player) sender;
        
        ShowApi.show(new Refresh() {
            @Override
            public void refresh(PlayerContext pc) {
                ShowApi.show(pc.getRefresh(), pc.getObj(), pc.getP(), pc.getPlugin(), pc.getPageName(), pc.getList(), pc.getData(), pc.getPageNow(), pc.getListNow(), pc.getFront(), pc.getBehind(), pc.getItemHash());
            }
        }, pageName, p, ShowPlugin.instance.pn, pageName, null, null, 1, 1, null, null, null);
    }

    /**
     * 前往指定页
     * @param p 玩家
     * @param page 页面号
     * @param tip 成功时是否提示(失败时必然提示)
     */
    private void toPage(Player p, int page, boolean tip) {
        PlayerContext pc = ShowApi.getPlayerContext(p);
        //当前没有查看的页面
        if (pc == null) {
            ShowApi.tip(p, get(p.getName(), 665), true);
            return;
        }
        Page pa = ShowApi.getPage(pc.getPlugin(), pc.getPageName());
        if (pa == null) return;//异常
        if (page < 1 || page > pa.getPageMax()) {//页面超出范围
            ShowApi.tip(p, get(p.getName(), 685), true);
            return;
        }
        if (page == pc.getPageNow()) {//已经处于这一页了
            ShowApi.tip(p, get(p.getName(), 695), true);
            return;
        }
        //成功
        if (tip) {
            List<FancyMessage> list = new ArrayList<FancyMessage>();
            list.add(get(p.getName(), 700, page));
            ShowApi.setTip(p, list);
        }
        pc.setPageNow(page);
        ShowApi.reShow(pc);
    }

    /**
     * 前往列表指定页
     * @param p 玩家
     * @param page 列表页面号
     * @param tip 成功时是否提示(失败时必然提示)
     */
    private void toListPage(Player p, int page, boolean tip) {
        PlayerContext pc = ShowApi.getPlayerContext(p);
        //当前没有查看的页面
        if (pc == null) {
            ShowApi.tip(p, get(p.getName(), 665), true);
            return;
        }
        ShowList list = pc.getList();
        if (list == null) {//页面没有列表
            ShowApi.tip(p, get(p.getName(), 670), true);
            return;
        }
        int listMax = list.getMaxPage(pc.getListSize());//列表最大页
        if (page < 1 || page > listMax) {//页面超出范围
            ShowApi.tip(p, get(p.getName(), 685), true);
            return;
        }
        if (page == pc.getListNow()) {//已经处于这一页了
            ShowApi.tip(p, get(p.getName(), 695), true);
            return;
        }
        //成功
        if (tip) {
            List<FancyMessage> result = new ArrayList<FancyMessage>();
            result.add(get(p.getName(), 705, page));
            ShowApi.setTip(p, result);
        }
        pc.setListNow(page);
        ShowApi.reShow(pc);
    }

    private FancyMessage get(String player, int id, Object... args) {
        return config.getLang().get(player, id, args);
    }
}
