package com.fyxridd.lib.show.chat.api.show;

import com.fyxridd.lib.core.api.fancymessage.FancyMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

/**
 * 玩家页面上下文(当前成功正在查看的)<br>
 * 不提供set方法(其它类可以调用get方法获取,但不能修改值,防止影响页面返回功能)
 */
public class PlayerContext {
    private Object obj;//功能自定义的额外保存数据
    private Refresh refresh;//回调类,可为null
    private Player p;//玩家
    private String plugin;//插件名
    private String pageName;//页面名
    private int listSize;//列表分页大小
    private ShowList<Object> list;//列表
    private HashMap<String, Object> data;//名称-值的映射表
    private int pageNow;//当前页
    private int listNow;//列表当前页
    private HashMap<String, ItemStack> itemHash;//名称-物品的映射表

    private List<FancyMessage> front, behind;//附加显示的行列表

    public PlayerContext() {
    }

    public PlayerContext(Object obj, Refresh refresh, Player p, String plugin, String pageName, int listSize,
                         ShowList<Object> list, HashMap<String, Object> data, int pageNow, int listNow,
                         List<FancyMessage> front, List<FancyMessage> behind, HashMap<String, ItemStack> itemHash) {
        this.obj = obj;
        this.refresh = refresh;
        this.p = p;
        this.plugin = plugin;
        this.pageName = pageName;
        this.listSize = listSize;
        this.list = list;
        this.data = data;
        this.pageNow = pageNow;
        this.listNow = listNow;
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

    public ShowList<Object> getList() {
        return list;
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public int getPageNow() {
        return pageNow;
    }

    public int getListNow() {
        return listNow;
    }

    public HashMap<String, ItemStack> getItemHash() {
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

    public void setList(ShowList<Object> list) {
        this.list = list;
    }

    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }

    public void setPageNow(int pageNow) {
        this.pageNow = pageNow;
    }

    public void setListNow(int listNow) {
        this.listNow = listNow;
    }

    public void setItemHash(HashMap<String, ItemStack> itemHash) {
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
        return new PlayerContext(obj, refresh, p, plugin, pageName, listSize, list, data, pageNow, listNow, front, behind, itemHash);
    }
}