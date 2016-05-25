package com.fyxridd.lib.show.chat.manager;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.fyxridd.lib.core.api.CoreApi;
import com.fyxridd.lib.core.api.MessageApi;
import com.fyxridd.lib.core.api.PerApi;
import com.fyxridd.lib.core.api.config.ConfigApi;
import com.fyxridd.lib.core.api.config.Setter;
import com.fyxridd.lib.core.api.fancymessage.FancyMessage;
import com.fyxridd.lib.core.api.fancymessage.FancyMessagePart;
import com.fyxridd.lib.func.api.FuncApi;
import com.fyxridd.lib.params.api.Session;
import com.fyxridd.lib.show.chat.ShowPlugin;
import com.fyxridd.lib.show.chat.api.ShowApi;
import com.fyxridd.lib.show.chat.api.event.PlayerPageExitEvent;
import com.fyxridd.lib.show.chat.api.fancymessage.Conditional;
import com.fyxridd.lib.show.chat.api.fancymessage.Itemable;
import com.fyxridd.lib.show.chat.api.page.LineContext;
import com.fyxridd.lib.show.chat.api.page.Page;
import com.fyxridd.lib.show.chat.api.show.PlayerContext;
import com.fyxridd.lib.show.chat.api.show.Refresh;
import com.fyxridd.lib.show.chat.api.show.ShowList;
import com.fyxridd.lib.show.chat.config.LangConfig;
import com.fyxridd.lib.show.chat.config.ShowConfig;
import com.fyxridd.lib.show.chat.fancymessage.FancyMessagePartExtra;
import com.fyxridd.lib.show.chat.func.ShowCmd;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 显示管理
 */
public class ShowManager {
    private static final String CHAT = "chat";
    /**
     * 默认pageNow的值
     */
    private static final int DEFAULT_PAGE_NOW = 1;
    /**
     * 默认listNow的值
     */
    private static final int DEFAULT_LIST_NOW = 1;

    private LangConfig langConfig;
    private ShowConfig showConfig;

    /**
     * 插件名 页面名 页面
     */
    private Map<String, Map<String, Page>> pages = new HashMap<>();

    /**
     * 玩家 玩家页面上下文
     */
    private Map<Player, PlayerContext> playerContexts = new HashMap<>();
    /**
     * 玩家 返回页面保存列表
     */
    private Map<Player, List<PlayerContext>> backs = new HashMap<>();
    /**
     * 玩家 提示
     */
    private Map<Player, List<FancyMessage>> tips = new HashMap<>();
    /**
     * 正在重新显示的中的玩家列表(用来防止显示死循环),玩家 调用层数
     */
    private Map<Player, Integer> reShows = new HashMap<>();

    public ShowManager() {
        //添加配置监听
        ConfigApi.addListener(ShowPlugin.instance.pn, LangConfig.class, new Setter<LangConfig>() {
            @Override
            public void set(LangConfig value) {
                langConfig = value;
            }
        });
        ConfigApi.addListener(ShowPlugin.instance.pn, ShowConfig.class, new Setter<ShowConfig>() {
            @Override
            public void set(ShowConfig value) {
                showConfig = value;
            }
        });
        //注册功能类型
        FuncApi.registerTypeHook(CHAT, showConfig.getChatFuncPrefix());
        //注册功能
        FuncApi.register(ShowPlugin.instance.pn, new ShowCmd());
        //监听限制聊天包
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(ShowPlugin.instance, PacketType.Play.Server.CHAT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (ShowApi.isInPage(event.getPlayer())) event.setCancelled(true);
            }
        });
    }

    /**
     * @see ShowApi#register(String)
     */
    public void register(String plugin) {
        //重新注册插件的所有页面
        pages.put(plugin, new HashMap<String, Page>());
        //读取
        File dir = new File(CoreApi.pluginPath, plugin+File.separator+"show");
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files!= null) {
                for (File file:files) {
                    if (file.isFile() && file.getName().endsWith(".yml") && !file.getName().equalsIgnoreCase("Description.yml")) register(plugin, file.getName().substring(0, file.getName().length()-4));
                }
            }
        }
    }

    /**
     * @see ShowApi#register(String, String)
     */
    public void register(String plugin, String name) {
        //读取页面信息
        try {
            Page page = ShowApi.loadPage(plugin, name);
            if (!pages.containsKey(plugin)) pages.put(plugin, new HashMap<String, Page>());
            pages.get(plugin).put(name, page);
        } catch (Exception e) {
            //todo
        }
    }

    /**
     * @see ShowApi#show(Refresh, Object, Player, String, String, int, int, List, List, Map)
     */
    public void show(Refresh refresh, Object obj, Player p, String plugin, String pageName, List<FancyMessage> front, List<FancyMessage> behind) {
        show(refresh, obj, p, plugin, pageName, DEFAULT_PAGE_NOW, DEFAULT_LIST_NOW, front, behind, null);
    }

    /**
     * @see ShowApi#show(Refresh, Object, Player, String, String, int, int, List, List, Map)
     */
    public void show(Refresh refresh, Object obj, Player p, String plugin, String pageName, int pageNow, int listNow, List<FancyMessage> front, List<FancyMessage> behind) {
        show(refresh, obj, p, plugin, pageName, pageNow, listNow, front, behind, null);
    }

    /**
     * @see ShowApi#show(Refresh, Object, Player, String, String, int, int, List, List, Map)
     */
    public void show(Refresh refresh, Object obj, Player p, String plugin, String pageName, List<FancyMessage> front, List<FancyMessage> behind, Map<String, ItemStack> itemHash) {
        show(refresh, obj, p, plugin, pageName, DEFAULT_PAGE_NOW, DEFAULT_LIST_NOW, front, behind, itemHash);
    }

    /**
     * @see ShowApi#show(Refresh, Object, Player, String, String, int, int, List, List, Map)
     */
    public void show(Refresh refresh, Object obj, Player p, String plugin, String pageName, int pageNow, int listNow, List<FancyMessage> front, List<FancyMessage> behind, Map<String, ItemStack> itemHash) {
        String name = p.getName();
        //注意!此方法内不能调用tip()方法,而应该用setTip()代替,否则会出现死循环
        try {
            //玩家页面上下文
            PlayerContext pc = playerContexts.get(p);
            //读取页面
            Page page = null;
            //读取是否成功
            boolean read;
            try {
                page = pages.get(plugin).get(pageName);
                read = true;
            } catch (Exception e) {
                read = false;
            }
            if (!read || page == null) { //页面异常,未知页面
                if (pc != null &&
                    pc.getRefresh() == refresh &&
                    pc.getObj() == obj &&
                    pc.getP() == p &&
                    pc.getPlugin().equals(plugin) &&
                    pc.getPageName().equals(pageName) &&
                    pc.getPageNow() == pageNow &&
                    pc.getListNow() == listNow) {//是当前页面上下文在调用
                    playerContexts.remove(p);
                }else setTip(p, get(name, 645));
                return;
            }

            //页面未生效
            if (!page.isEnable()) {
                setTip(p, get(name, 740));
                return;
            }

            //权限检测
            if (!PerApi.checkHasPer(p.getName(), page.getPer())) return;

            //页面控制
            int pageMax = page.getPageMax();//最大页
            //指定页面不存在
            if (pageNow < 1 || pageNow > pageMax) {
                if (pageNow < 1) pageNow = 1;
                else if (pageNow > pageMax) pageNow = pageMax;
                setTip(p, get(name, 650));
            }
            List<Integer> showPage = page.getPageList().get(pageNow-1).getContent();//显示的页面
            List<FancyMessage> resultPage = new ArrayList<>();//结果页面,复制版
            for (int line: showPage) {
                FancyMessage msg = ShowApi.getMsg(name, page.getLines(), line);
                if (msg == null) continue;
                resultPage.add(msg.clone());
            }

            //列表控制
            int listSize = page.getListInfo() != null?page.getListInfo().getSize():0;
            int listMax = 0;
            int getSize = 0;
            ShowList<Object> list = null;
            if (page.getListInfo() != null) list = ShowApi.getShowList(page.getListInfo().getPlugin(), page.getListInfo().getKey(), p.getName(), page.getListInfo().getArg());
            if (list != null) {
                listMax = list.getMaxPage(listSize);//列表最大页
                List showList = list.getPage(listSize, listNow);
                getSize = showList.size();
                for (FancyMessage msg : resultPage) {
                    for (FancyMessagePart mp : msg.getMessageParts().values()) {
                        if (mp instanceof FancyMessagePartExtra) {
                            FancyMessagePartExtra mpe = (FancyMessagePartExtra) mp;
                            if (mpe.getListFix() != null) {
                                Map<String, Object> replace = new HashMap<>();
                                for (String fix : mpe.getListFix()) {
                                    try {
                                        int num;
                                        String value;
                                        String[] ss = fix.split("\\.");
                                        num = Integer.parseInt(ss[0]);
                                        if (fix.charAt(fix.length() - 1) == ')') {//调用方法
                                            if (num >= 0 && num < getSize) {
                                                String methodName = ss[1];
                                                Object o = showList.get(num);
                                                if (methodName.charAt(methodName.length()-2) == '(') {
                                                    methodName = methodName.substring(0, methodName.length()-2);
                                                    Method method;
                                                    if (list.getClassType() == null) method = o.getClass().getDeclaredMethod(methodName);
                                                    else method = list.getClassType().getDeclaredMethod(methodName);
                                                    boolean accessible = method.isAccessible();
                                                    method.setAccessible(true);
                                                    value = String.valueOf(method.invoke(o));
                                                    method.setAccessible(accessible);
                                                }else {
                                                    methodName = methodName.substring(0, methodName.length()-6);
                                                    Method method;
                                                    if (list.getClassType() == null) method = o.getClass().getDeclaredMethod(methodName, String.class);
                                                    else method = list.getClassType().getDeclaredMethod(methodName, String.class);
                                                    boolean accessible = method.isAccessible();
                                                    method.setAccessible(true);
                                                    value = String.valueOf(method.invoke(o, p.getName()));
                                                    method.setAccessible(accessible);
                                                }
                                            } else value = "";
                                        } else {//调用属性
                                            if (num >= 0 && num < getSize) {
                                                Object o = showList.get(num);
                                                Field field;
                                                if (list.getClassType() == null) field = o.getClass().getDeclaredField(ss[1]);
                                                else field = list.getClassType().getDeclaredField(ss[1]);
                                                boolean accessible = field.isAccessible();
                                                field.setAccessible(true);
                                                value = String.valueOf(field.get(o));
                                                field.setAccessible(accessible);
                                            } else value = "";
                                        }
                                        //添加替换
                                        replace.put(fix, value);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        CoreApi.debug(e.getMessage());
                                    }
                                }
                                mpe.convert(replace);
                            }
                        }
                    }
                }
            }

            //提示变量
            Map<String, String> strDefaults = new HashMap<>();
            strDefaults.put("name", p.getName());
            strDefaults.put("displayName", p.getDisplayName());
            strDefaults.put("pageNow", ""+pageNow);
            strDefaults.put("pageMax", ""+pageMax);
            strDefaults.put("listSize", ""+listSize);
            strDefaults.put("listNow", ""+listNow);
            strDefaults.put("listMax", ""+listMax);
            strDefaults.put("getSize", ""+getSize);
            Map<String, Object> replace = new HashMap<>();
            replace.putAll(strDefaults);
            Session session = page.getParamsFactory().openSession(null, strDefaults, null);
            for (String strName:page.getParamsFactory().getStrNames()) replace.put(strName, session.getStr(strName));
            for (FancyMessage msg:resultPage) MessageApi.convert(msg, replace);

            //物品信息替换
            if (itemHash != null) {
                for (FancyMessage msg:resultPage) {
                    for (FancyMessagePart mp : msg.getMessageParts().values()) {
                        if (mp instanceof Itemable) {
                            Itemable itemable = (Itemable) mp;
                            String item = itemable.getHoverItem();
                            if (item != null) {
                                ItemStack is = itemHash.get(item);
                                if (is != null) {
                                    mp.setHoverActionName("show_item");
                                    mp.setHoverActionData(MessageApi.getHoverActionData(is));
                                }
                            }
                        }
                    }
                }
            }

            //条件显示
            for (FancyMessage msg:resultPage) {
                Iterator<FancyMessagePart> it = msg.getMessageParts().values().iterator();
                while (it.hasNext()) {
                    FancyMessagePart mp = it.next();
                    if (mp instanceof Conditional) {
                        Conditional conditional = (Conditional) mp;
                        if (!conditional.checkCondition()) it.remove();
                    }
                }
            }

            //成功

            //更新玩家页面上下文
            if (page.isRecord()) {
                if (pc == null) {
                    pc = new PlayerContext();
                    playerContexts.put(p, pc);
                }else if (!pc.getPlugin().equals(plugin) ||
                        !pc.getPageName().equals(pageName)) {//显示的页面与原来不同,保存返回页面
                    List<PlayerContext> backList = backs.get(p);
                    if (backList == null) {
                        backList = new ArrayList<>();
                        backs.put(p, backList);
                    }
                    backList.add(pc.clone());
                    if (backList.size() > showConfig.getMaxBackPage()) backList.remove(0);
                }
                //更新玩家页面上下文信息
                pc.setRefresh(refresh);
                pc.setObj(obj);
                pc.setP(p);
                pc.setPlugin(plugin);
                pc.setPageName(pageName);
                pc.setListSize(listSize);
                pc.setPageNow(pageNow);
                pc.setListNow(listNow);
                pc.setListMax(listMax);
                pc.setFront(front);
                pc.setBehind(behind);
                pc.setItemHash(itemHash);
            }

            //补空行
            if (page.isFillEmpty()) {
                int empty = showConfig.getLine()-(page.isHandleTip()?2:0)-resultPage.size()-(front != null?front.size():0)-(behind != null?behind.size():0);
                if (empty > 0) {
                    FancyMessage msg = get(name, showConfig.getAdd());
                    for (int i=0;i<empty;i++) MessageApi.sendChatPacket(p, msg);
                }
            }
            //显示页面
            if (front != null) {
                for (FancyMessage msg: front) MessageApi.sendChatPacket(p, msg);
            }
            for (FancyMessage msg:resultPage) MessageApi.sendChatPacket(p, msg);
            if (behind != null) {
                for (FancyMessage msg: behind) MessageApi.sendChatPacket(p, msg);
            }
            //显示页面尾部操作提示
            if (page.isHandleTip()) {
                MessageApi.sendChatPacket(p, get(name, showConfig.getOperateTipMenu()));
                List<FancyMessage> tipList = tips.get(p);
                if (tipList != null) {
                    for (FancyMessage tip: tipList) MessageApi.sendChatPacket(p, tip);
                }else MessageApi.sendChatPacket(p, get(name, showConfig.getOperateTipEmpty()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            CoreApi.debug(e.getMessage());
            playerContexts.remove(p);
            setTip(p, get(name, 655));
        }
    }

    /**
     * @see ShowApi#getMsg(String, Map, int)
     */
    public FancyMessage getMsg(String player, Map<Integer, LineContext> lines, int line) {
        try {
            if (line == -1) return get(player, showConfig.getPageControl());
            else if (line == -2) return get(player, showConfig.getListControl());
            else return lines.get(line).getMsg();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @see ShowApi#reShow(PlayerContext)
     */
    public void reShow(PlayerContext pc) {
        if (pc == null) return;
        Page page = getPage(pc.getPlugin(), pc.getPageName());
        if (page == null) return;
        Player p = pc.getP();
        if (p == null) return;
        //防止死循环
        if (!reShows.containsKey(p)) reShows.put(p, 1);
        else reShows.put(p, reShows.get(p)+1);
        if (reShows.get(p) > showConfig.getDeadLoopLevel()) {//判定为死循环
            CoreApi.debug("Dead Loop Checked!!!Auto remove!");
            reShows.remove(p);//去除
            ShowApi.exit(pc.getP(), false);
            return;
        }
        //显示
        pc.getRefresh().refresh(pc);
        //显示正常结束,去除
        reShows.remove(p);
    }

    /**
     * @see ShowApi#back(Player)
     */
    public void back(Player p) {
        List<PlayerContext> backList = backs.get(p);
        if (backList == null || backList.isEmpty()) {//退出页面
            ShowApi.exit(p, true);
            return;
        }
        //返回
        PlayerContext backContext = backList.remove(backList.size()-1);
        playerContexts.put(p, backContext);
        List<FancyMessage> tipList = new ArrayList<>();
        tipList.add(get(p.getName(), 660));
        tips.put(p, tipList);
        reShow(backContext);
    }

    /**
     * @see ShowApi#exit(Player, boolean)
     */
    public void exit(Player p, boolean tip) {
        exit(p, tip, true);
    }

    /**
     * @see ShowApi#exit(Player, boolean, boolean)
     */
    public void exit(Player p, boolean tip, boolean successTip) {
        //缓存
        reShows.remove(p);
        backs.remove(p);
        tips.remove(p);
        //当前没有查看的页面
        if (playerContexts.remove(p) == null) {
            if(tip) MessageApi.send(p, get(p.getName(), 665), true);
            return;
        }
        //提示
        if (successTip) MessageApi.send(p, get(p.getName(), 675), true);
        //发出退出页面事件
        Bukkit.getPluginManager().callEvent(new PlayerPageExitEvent(p));
    }

    /**
     * @see ShowApi#setTip(Player, List)
     */
    public void setTip(Player p, FancyMessage tip) {
        List<FancyMessage> list = new ArrayList<>();
        list.add(tip);
        setTip(p, list);
    }

    /**
     * @see ShowApi#setTip(Player, List)
     */
    public void setTip(Player p, List<FancyMessage> tip) {
        tips.put(p, tip);
    }

    /**
     * @see ShowApi#clearTip(Player, boolean)
     */
    public void clearTip(Player p, boolean show) {
        tips.remove(p);
        if (show) {
            //玩家开启界面的情况下才进行重新显示
            PlayerContext pc = playerContexts.get(p);
            if (pc != null) reShow(pc);
        }
    }

    /**
     * @see ShowApi#hasRegistered(String, String)
     */
    public boolean hasRegistered(String plugin, String page) {
        if (plugin == null || page == null) return false;
        return pages.containsKey(plugin) && pages.get(plugin).containsKey(page);
    }

    /**
     * @see ShowApi#getPage(String, String)
     */
    public Page getPage(String plugin, String page) {
        if (plugin == null || page == null) return null;
        if (hasRegistered(plugin, page)) return pages.get(plugin).get(page);
        else return null;
    }

    /**
     * @see ShowApi#isInPage(Player)
     */
    public boolean isInPage(Player p) {
        return playerContexts.containsKey(p);
    }

    /**
     * @see ShowApi#getPlayerContext(Player)
     */
    public PlayerContext getPlayerContext(Player p) {
        return playerContexts.get(p);
    }

    private FancyMessage get(String player, int id, Object... args) {
        return langConfig.getLang().get(player, id, args);
    }
}
