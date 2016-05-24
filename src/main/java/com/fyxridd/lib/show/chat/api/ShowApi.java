package com.fyxridd.lib.show.chat.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fyxridd.lib.core.api.MessageApi;
import com.fyxridd.lib.core.api.fancymessage.FancyMessage;
import com.fyxridd.lib.show.chat.ShowPlugin;
import com.fyxridd.lib.show.chat.Util;
import com.fyxridd.lib.show.chat.api.page.LineContext;
import com.fyxridd.lib.show.chat.api.page.Page;
import com.fyxridd.lib.show.chat.api.show.PlayerContext;
import com.fyxridd.lib.show.chat.api.show.Refresh;
import com.fyxridd.lib.show.chat.api.show.ShowList;
import com.fyxridd.lib.show.chat.api.show.ShowListGetter;
import com.fyxridd.lib.show.chat.impl.ShowListImpl;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShowApi {
    /**
     * 其它聊天插件不能自行给玩家发送聊天信息或调用MessageApi.send方法,而要调用此方法,否则不会有延时显示聊天信息的功能
     * @param p 玩家,可为null(null时无效果)
     * @param msg 聊天信息,可为null(null时无效果)
     * @param force 是否强制显示
     */
    public static void addChat(Player p, FancyMessage msg, boolean force) {
        ShowPlugin.instance.getDelayChatManager().addChat(p, msg, force);
    }

    /**
     * @param msg 文字内容
     * @param config 可为null
     * @return 不为null
     */
    public static FancyMessage loadFancyMessage(String msg, ConfigurationSection config) throws Exception {
        return Util.loadFancyMessage(msg, config);
    }

    /**
     * 注册显示列表
     * @param plugin 插件
     * @param key 列表名
     * @param showListGetter 列表获取器
     */
    public static void register(String plugin, String key, ShowListGetter showListGetter) {
        ShowPlugin.instance.getShowListManager().register(plugin, key, showListGetter);
    }

    /**
     * 注册插件的所有页面<br>
     * 会从plugins/plugin/show文件夹里读取所有的页面信息<br>
     *     会重新注册指定插件的所有页面<br>
     *      (即使读取的页面为null也会加入注册)
     * @param plugin 插件名,不为null
     */
    public static void register(String plugin) {
        ShowPlugin.instance.getShowManager().register(plugin);
    }

    /**
     * 注册插件的单个页面<br>
     * 会从plugins/plugin/show/name.yml里读取页面信息<br>
     *      可重新注册,会覆盖旧的信息<br>
     *      (即使读取的页面为null也会加入注册)
     * @param plugin 插件名,不为null
     * @param name 页面名,不为null
     */
    public static void register(String plugin, String name) {
        ShowPlugin.instance.getShowManager().register(plugin, name);
    }

    /**
     * @see #show(Refresh, Object, org.bukkit.entity.Player, String, String, ShowList, java.util.Map, int, int, java.util.List, java.util.List, java.util.Map)
     */
    public static void show(Refresh refresh, Object obj, Player p, String plugin, String pageName,
                            ShowList list, Map<String, Object> data, List<FancyMessage> front, List<FancyMessage> behind) {
        ShowPlugin.instance.getShowManager().show(refresh, obj, p, plugin, pageName, list, data, front, behind);
    }

    /**
     * @see #show(Refresh, Object, org.bukkit.entity.Player, String, String, ShowList, java.util.Map, int, int, java.util.List, java.util.List, java.util.Map)
     */
    public static void show(Refresh refresh, Object obj, Player p, String plugin, String pageName,
                            ShowList<Object> list, Map<String, Object> data, int pageNow, int listNow,
                            List<FancyMessage> front, List<FancyMessage> behind) {
        ShowPlugin.instance.getShowManager().show(refresh, obj, p, plugin, pageName, list, data, pageNow, listNow, front, behind);
    }

    /**
     * @see #show(Refresh, Object, org.bukkit.entity.Player, String, String, ShowList, java.util.Map, int, int, java.util.List, java.util.List, java.util.Map)
     */
    public static void show(Refresh refresh, Object obj, Player p, String plugin, String pageName,
                            ShowList<Object> list, Map<String, Object> data,
                            List<FancyMessage> front, List<FancyMessage> behind, Map<String, ItemStack> itemHash) {
        ShowPlugin.instance.getShowManager().show(refresh, obj, p, plugin, pageName, list, data, front, behind, itemHash);
    }

    /**
     * 显示页面
     * @param refresh 回调类,用来页面跳转(刷新),null时页面跳转时不刷新
     * @param obj 功能自定义的额外保存数据,可为null
     * @param p 玩家,不为null
     * @param plugin 插件名,不为null
     * @param pageName 页面名,不为null
     * @param list 列表,可为null
     * @param data 名称-值的映射表,可为null
     * @param pageNow 当前页,>0
     * @param listNow 列表当前页,>0
     * @param front 前面额外附加的行列表,可为null
     * @param behind 后面额外附加的行列表,可为null
     * @param itemHash 物品信息替换,可为null
     */
    public static void show(Refresh refresh, Object obj, Player p, String plugin, String pageName,
                            ShowList<Object> list, Map<String, Object> data, int pageNow, int listNow,
                            List<FancyMessage> front, List<FancyMessage> behind, Map<String, ItemStack> itemHash) {
        ShowPlugin.instance.getShowManager().show(refresh, obj, p, plugin, pageName, list, data, pageNow, listNow, front, behind, itemHash);
    }

    /**
     * @see #tip(org.bukkit.entity.Player, java.util.List, boolean)
     */
    public static void tip(Player p, String msg, boolean force) {
        if (msg == null) return;
        ShowPlugin.instance.getShowManager().tip(p, MessageApi.convert(msg), force);
    }

    /**
     * @see #tip(org.bukkit.entity.Player, java.util.List, boolean)
     */
    public static void tip(Player p, FancyMessage msg, boolean force) {
        List<FancyMessage> tipList = new ArrayList<>();
        tipList.add(msg);
        ShowPlugin.instance.getShowManager().tip(p, tipList, force);
    }

    /**
     * 各种操作对玩家进行提示的时候适合调用此方法<br>
     * 会自动根据玩家是否正在查看页面而改变显示方式<br>
     * 可以防止因玩家查看页面而显示不了,也可以让提示玩家的时候不用考虑玩家的显示状态<br>
     * 适合必须让玩家看到提示时调用
     * @param p 玩家,不为null
     * @param msgList 提示信息列表s,可为null
     * @param force false表示玩家在显示界面时不提示,true表示不管玩家有没显示界面都提示
     */
    public static void tip(Player p, List<FancyMessage> msgList, boolean force) {
        ShowPlugin.instance.getShowManager().tip(p, msgList, force);
    }

    /**
     * 由行号获取行
     * @param lines 行列表
     * @param line 行号,-1表示页面控制行,-2表示列表控制行
     * @return 异常返回null
     */
    public static FancyMessage getMsg(String player, Map<Integer, LineContext> lines, int line) {
        return ShowPlugin.instance.getShowManager().getMsg(player, lines, line);
    }

    /**
     * @see #reShow(PlayerContext, boolean)
     */
    public static void reShow(PlayerContext pc) {
        ShowPlugin.instance.getShowManager().reShow(pc);
    }

    /**
     * 页面跳转,重新显示<br>
     * 以下情况下需要调用:<br>
     *     - 操作提示改变<br>
     *     - 分页控制:当前页改变<br>
     *     - 列表控制:列表当前页改变<br>
     *     - 其它功能自行设置的刷新操作
     * @param pc 玩家页面上下文,null时不显示
     * @param noRefresh 是否禁止刷新(一般在重新显示页面方法出错时设为true)
     */
    public static void reShow(PlayerContext pc, boolean noRefresh) {
        ShowPlugin.instance.getShowManager().reShow(pc, noRefresh);
    }

    /**
     * @see #setTip(org.bukkit.entity.Player, java.util.List)
     */
    public static void setTip(Player p, FancyMessage tip) {
        ShowPlugin.instance.getShowManager().setTip(p, tip);
    }

    /**
     * 设置提示信息
     * @param p 玩家,不为null
     * @param tip 提示列表,可为null
     */
    public static void setTip(Player p, List<FancyMessage> tip) {
        ShowPlugin.instance.getShowManager().setTip(p, tip);
    }

    /**
     * 检测指定插件的页面是否有注册
     * @param plugin 插件名,可为null
     * @param page 页面名,可为null
     * @return 是否有注册
     */
    public static boolean hasRegistered(String plugin, String page) {
        return ShowPlugin.instance.getShowManager().hasRegistered(plugin, page);
    }

    /**
     * 获取插件注册的页面<br>
     * 注意: 如果指定插件的指定页面被注册,这个页面可能仍未生成或读取异常,因此会返回null<br>
     * 也就是说返回null不代表指定插件的页面没有注册<br>
     * 当然,没有注册的情况下也是返回null
     * @param plugin 插件名,可为null
     * @param page 页面名,可为null
     * @return 页面,异常返回null
     */
    public static Page getPage(String plugin, String page) {
        return ShowPlugin.instance.getShowManager().getPage(plugin, page);
    }

    /**
     * 玩家请求返回上一页
     * @param p 玩家,不为null
     */
    public static void back(Player p) {
        ShowPlugin.instance.getShowManager().back(p);
    }

    /**
     * @see #exit(org.bukkit.entity.Player, boolean, boolean)
     */
    public static void exit(Player p, boolean tip) {
        ShowPlugin.instance.getShowManager().exit(p, tip);
    }

    /**
     * 玩家请求退出页面
     * @param p 玩家,不为null
     * @param tip 当前没有查看的页面时是否提示
     * @param successTip 退出页面成功时是否提示
     */
    public static void exit(Player p, boolean tip, boolean successTip) {
        ShowPlugin.instance.getShowManager().exit(p, tip, successTip);
    }

    /**
     * 获取玩家是否正在查看界面
     * @param p 玩家,不为null
     * @return 是否正在查看界面
     */
    public static boolean isInPage(Player p) {
        return ShowPlugin.instance.getShowManager().isInPage(p);
    }

    /**
     * 清除玩家的提示
     * @param p 玩家,不为null
     * @param show 是否重新显示
     */
    public static void clearTip(Player p, boolean show) {
        ShowPlugin.instance.getShowManager().clearTip(p, show);
    }

    /**
     * 如果列表可能包含1w个以上的元素,请尽量不要使用Collection类型
     * @param type 传入的列表类型,0指List类型,1指Object[]类型,2指Collection类型(效率最低),3指HashList类型
     * @param list 列表,可为null
     */
    public static ShowList getShowList(int type, Object list) {
        return new ShowListImpl(type, list);
    }

    /**
     * @see #getShowList(int, Object)
     */
    public static ShowList getShowList(int type, Object list, Class classType) {
        return new ShowListImpl(type, list, classType);
    }

    /**
     * 获取显示列表
     * @param plugin 插件名
     * @param key 列表名
     * @param name 玩家名
     * @param arg 变量(可包含空格)
     * @return 显示列表,异常返回null
     */
    public static ShowList getShowList(String plugin, String key, String name, String arg) {
        return ShowPlugin.instance.getShowListManager().getShowList(plugin, key, name, arg);
    }
    
    /**
     * 从plugins/plugin/show/page.yml里读取页面信息
     * @param plugin 插件名,不为null
     * @param page 页面名,不为null
     * @return 页面,异常返回null
     */
    public static Page loadPage(String plugin, String page) throws Exception {
       return Util.loadPage(plugin, page);
    }

    /**
     * 读取页面信息
     * @param plugin 插件名
     * @param page 页面名
     * @param config 页面信息保存的yml文件,不为null
     * @return 页面信息,异常返回null
     */
    public static Page loadPage(String plugin, String page, ConfigurationSection config) throws Exception {
        return Util.loadPage(plugin, page, config);
    }

    /**
     * 获取玩家页面上下文
     * @return 不存在返回null
     */
    public static PlayerContext getPlayerContext(Player p) {
        return ShowPlugin.instance.getShowManager().getPlayerContext(p);
    }
}
