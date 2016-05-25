package com.fyxridd.lib.show.chat.api.show;

import com.fyxridd.lib.core.api.fancymessage.FancyMessage;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

/**
 * 玩家页面上下文(当前成功正在查看的)<br>
 * (其它类可以调用get方法获取,但不能修改值,防止影响页面返回功能)
 */
public class PlayerContext {
    private Object obj;//功能自定义的额外保存数据
    private Refresh refresh;//回调类,可为null
    private Player p;//玩家
    private String plugin;//插件名
    private String pageName;//页面名
    private int listSize;//列表分页大小
    private int pageNow;//当前页
    private int listNow;//列表当前页
    private int listMax;//(玩家当前看到的)列表最大页
    private Map<String, ItemStack> itemHash;//名称-物品的映射表

    private List<FancyMessage> front, behind;//附加显示的行列表

    public PlayerContext() {
    }

    public PlayerContext(Object obj, Refresh refresh, Player p, String plugin, String pageName, int listSize,
                         int pageNow, int listNow, int listMax,
                         List<FancyMessage> front, List<FancyMessage> behind, Map<String, ItemStack> itemHash) {
        this.obj = obj;
        this.refresh = refresh;
        this.p = p;
        this.plugin = plugin;
        this.pageName = pageName;
        this.listSize = listSize;
        this.pageNow = pageNow;
        this.listNow = listNow;
        this.listMax = listMax;
        this.front = front;
        this.behind = behind;
        this.itemHash = itemHash;
    }

    public Object getObj() {
        return obj;
    }

    public Refresh getRefresh() {
        return refresh;
    }

    public Player getP() {
        return p;
    }

    public String getPlugin() {
        return plugin;
    }

    public String getPageName() {
        return pageName;
    }

    public int getListSize() {
        return listSize;
    }

    public int getPageNow() {
        return pageNow;
    }

    public int getListNow() {
        return listNow;
    }

    public int getListMax() {
        return listMax;
    }

    public Map<String, ItemStack> getItemHash() {
        return itemHash;
    }

    public List<FancyMessage> getFront() {
        return front;
    }

    public List<FancyMessage> getBehind() {
        return behind;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public void setRefresh(Refresh refresh) {
        this.refresh = refresh;
    }

    public void setP(Player p) {
        this.p = p;
    }

    public void setPlugin(String plugin) {
        this.plugin = plugin;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public void setListSize(int listSize) {
        this.listSize = listSize;
    }

    public void setPageNow(int pageNow) {
        this.pageNow = pageNow;
    }

    public void setListNow(int listNow) {
        this.listNow = listNow;
    }

    public void setListMax(int listMax) {
        this.listMax = listMax;
    }

    public void setItemHash(Map<String, ItemStack> itemHash) {
        this.itemHash = itemHash;
    }

    public void setFront(List<FancyMessage> front) {
        this.front = front;
    }

    public void setBehind(List<FancyMessage> behind) {
        this.behind = behind;
    }

    @Override
    public PlayerContext clone() {
        return new PlayerContext(obj, refresh, p, plugin, pageName, listSize, pageNow, listNow, listMax, front, behind, itemHash);
    }
}
