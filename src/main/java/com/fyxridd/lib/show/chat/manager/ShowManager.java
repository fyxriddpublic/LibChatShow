package com.fyxridd.lib.show.chat.manager;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.fyxridd.lib.core.api.CoreApi;
import com.fyxridd.lib.core.api.MessageApi;
import com.fyxridd.lib.core.api.PerApi;
import com.fyxridd.lib.core.api.UtilApi;
import com.fyxridd.lib.core.api.config.ConfigApi;
import com.fyxridd.lib.core.api.fancymessage.FancyMessage;
import com.fyxridd.lib.core.api.fancymessage.FancyMessagePart;
import com.fyxridd.lib.core.config.ConfigManager;
import com.fyxridd.lib.params.api.ParamsConverter;
import com.fyxridd.lib.params.api.ParamsFactory;
import com.fyxridd.lib.params.api.Session;
import com.fyxridd.lib.show.chat.ShowPlugin;
import com.fyxridd.lib.show.chat.api.ShowApi;
import com.fyxridd.lib.show.chat.api.event.PlayerPageExitEvent;
import com.fyxridd.lib.show.chat.api.fancymessage.Conditional;
import com.fyxridd.lib.show.chat.api.fancymessage.Itemable;
import com.fyxridd.lib.show.chat.api.page.*;
import com.fyxridd.lib.show.chat.api.show.PlayerContext;
import com.fyxridd.lib.show.chat.api.show.Refresh;
import com.fyxridd.lib.show.chat.api.show.ShowList;
import com.fyxridd.lib.show.chat.config.ShowConfig;
import com.fyxridd.lib.show.chat.fancymessage.FancyMessagePartExtra;
import com.fyxridd.lib.show.chat.impl.PageImpl;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
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
    /**
     * 默认pageNow的值
     */
    private static final int DEFAULT_PAGE_NOW = 1;
    /**
     * 默认listNow的值
     */
    private static final int DEFAULT_LIST_NOW = 1;

    private ShowConfig config;

    /**
     * 插件名 页面名 页面
     */
    private Map<String, Map<String, Page>> pageHash = new HashMap<>();

    /**
     * 玩家 玩家页面上下文
     */
    private Map<Player, PlayerContext> playerContextHash = new HashMap<>();
    /**
     * 玩家 返回页面保存列表
     */
    private Map<Player, List<PlayerContext>> backHash = new HashMap<>();
    /**
     * 玩家 提示
     */
    private Map<Player, List<FancyMessage>> tipHash = new HashMap<>();
    /**
     * 正在重新显示的中的玩家列表(用来防止显示死循环),玩家 调用层数
     */
    private Map<Player, Integer> reShowHash = new HashMap<>();

    public ShowManager() {
        //添加配置监听
        ConfigApi.addListener(ShowPlugin.instance.pn, ShowConfig.class, new ConfigManager.Setter<ShowConfig>() {
            @Override
            public void set(ShowConfig value) {
                config = value;
            }
        });
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
        pageHash.put(plugin, new HashMap<String, Page>());
        //读取
        File dir = new File(CoreApi.pluginPath, plugin+File.separator+"show");
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files!= null) {
                for (File file:files) {
                    if (file.isFile() && file.getName().endsWith(".yml") && !file.getName().endsWith("_description.yml")) {
                        register(plugin, file.getName().substring(0, file.getName().length()-4));
                    }
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
            Page page = load(plugin, name);
            if (!pageHash.containsKey(plugin)) pageHash.put(plugin, new HashMap<String, Page>());
            pageHash.get(plugin).put(name, page);
        } catch (Exception e) {
            //TODO
        }
    }

    /**
     * @see com.fyxridd.lib.core.api.ShowApi#show(com.fyxridd.lib.core.api.inter.ShowInterface, Object, Player, String, String, com.fyxridd.lib.core.api.inter.ShowList, HashMap, int, int, List, List, HashMap)
     */
    public void show(Refresh refresh, Object obj, Player p, String plugin, String pageName, ShowList list, Map<String, Object> data, List<FancyMessage> front, List<FancyMessage> behind) {
        show(refresh, obj, p, plugin, pageName, list, data, DEFAULT_PAGE_NOW, DEFAULT_LIST_NOW, front, behind, null);
    }

    /**
     * @see com.fyxridd.lib.core.api.ShowApi#show(com.fyxridd.lib.core.api.inter.ShowInterface, Object, Player, String, String, com.fyxridd.lib.core.api.inter.ShowList, HashMap, int, int, List, List, HashMap)
     */
    public void show(Refresh refresh, Object obj, Player p, String plugin, String pageName, ShowList<Object> list, Map<String, Object> data, int pageNow, int listNow, List<FancyMessage> front, List<FancyMessage> behind) {
        show(refresh, obj, p, plugin, pageName, list, data, pageNow, listNow, front, behind, null);
    }

    /**
     * @see com.fyxridd.lib.core.api.ShowApi#show(com.fyxridd.lib.core.api.inter.ShowInterface, Object, Player, String, String, com.fyxridd.lib.core.api.inter.ShowList, HashMap, int, int, List, List, HashMap)
     */
    public void show(Refresh refresh, Object obj, Player p, String plugin, String pageName, ShowList<Object> list, Map<String, Object> data, List<FancyMessage> front, List<FancyMessage> behind, Map<String, ItemStack> itemHash) {
        show(refresh, obj, p, plugin, pageName, list, data, DEFAULT_PAGE_NOW, DEFAULT_LIST_NOW, front, behind, itemHash);
    }

    /**
     * @see com.fyxridd.lib.core.api.ShowApi#show(com.fyxridd.lib.core.api.inter.ShowInterface, Object, Player, String, String, com.fyxridd.lib.core.api.inter.ShowList, HashMap, int, int, List, List, HashMap)
     */
    public void show(Refresh refresh, Object obj, Player p, String plugin, String pageName, ShowList<Object> list, Map<String, Object> data, int pageNow, int listNow, List<FancyMessage> front, List<FancyMessage> behind, Map<String, ItemStack> itemHash) {
        String name = p.getName();
        //注意!此方法内不能调用tip()方法,而应该用setTip()代替,否则会出现死循环
        try {
            //玩家页面上下文
            PlayerContext pc = playerContextHash.get(p);
            //读取页面
            Page page = null;
            //读取是否成功
            boolean read;
            try {
                page = pageHash.get(plugin).get(pageName);
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
                    pc.getList() == list &&
                    pc.getData() == data &&
                    pc.getPageNow() == pageNow &&
                    pc.getListNow() == listNow) {//是当前页面上下文在调用
                    playerContextHash.remove(p);
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
                FancyMessage msg = getMsg(name, page.getLines(), line);
                if (msg == null) continue;
                resultPage.add(msg.clone());
            }

            //列表控制
            int listSize = page.getListSize();
            int listMax = 0;
            int getSize = 0;
            if (list == null && page.getListInfo() != null) list = ShowApi.getShowList(page.getListInfo().getPlugin(), page.getListInfo().getKey(), p.getName(), page.getListInfo().getArg());
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
            Map<String, String> strDefaults = new HashMap<String, String>();
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
                    playerContextHash.put(p, pc);
                }else if (!pc.getPlugin().equals(plugin) ||
                        !pc.getPageName().equals(pageName)) {//显示的页面与原来不同,保存返回页面
                    List<PlayerContext> backList = backHash.get(p);
                    if (backList == null) {
                        backList = new ArrayList<PlayerContext>();
                        backHash.put(p, backList);
                    }
                    backList.add(pc.clone());
                    if (backList.size() > config.getMaxBackPage()) backList.remove(0);
                }
                //更新玩家页面上下文信息
                pc.setRefresh(refresh);
                pc.setObj(obj);
                pc.setP(p);
                pc.setPlugin(plugin);
                pc.setPageName(pageName);
                pc.setListSize(listSize);
                pc.setList(list);
                pc.setData(data);
                pc.setPageNow(pageNow);
                pc.setListNow(listNow);
                pc.setFront(front);
                pc.setBehind(behind);
                pc.setItemHash(itemHash);
            }

            //补空行
            if (page.isFillEmpty()) {
                int empty = config.getLine()-(page.isHandleTip()?2:0)-resultPage.size()-(front != null?front.size():0)-(behind != null?behind.size():0);
                if (empty > 0) {
                    FancyMessage msg = get(name, config.getAdd());
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
                MessageApi.sendChatPacket(p, get(name, config.getOperateTipMenu()));
                List<FancyMessage> tipList = tipHash.get(p);
                if (tipList != null) {
                    for (FancyMessage tip: tipList) MessageApi.sendChatPacket(p, tip);
                }else MessageApi.sendChatPacket(p, get(name, config.getOperateTipEmpty()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            CoreApi.debug(e.getMessage());
            playerContextHash.remove(p);
            setTip(p, get(name, 655));
        }
    }

    /**
     * @see com.fyxridd.lib.core.api.ShowApi#getMsg(LinkedHashMap, int)
     */
    public FancyMessage getMsg(String player, Map<Integer, LineContext> lines, int line) {
        try {
            if (line == -1) return get(player, config.getPageControl());
            else if (line == -2) return get(player, config.getListControl());
            else return lines.get(line).getMsg();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @see com.fyxridd.lib.core.api.ShowApi#reShow(com.fyxridd.lib.core.api.inter.PlayerContext, boolean)
     */
    public void reShow(PlayerContext pc) {
        reShow(pc, false);
    }

    /**
     * @see ShowApi#reShow(com.fyxridd.lib.core.api.inter.PlayerContext, boolean)
     */
    public void reShow(PlayerContext pc, boolean noRefresh) {
        if (pc == null) return;
        Page page = getPage(pc.getPlugin(), pc.getPageName());
        if (page == null) return;
        Player p = pc.getP();
        if (p == null) return;
        //防止死循环
        if (!reShowHash.containsKey(p)) reShowHash.put(p, 1);
        else reShowHash.put(p, reShowHash.get(p)+1);
        if (reShowHash.get(p) > config.getDeadLoopLevel()) {//判定为死循环
            CoreApi.debug("Dead Loop Checked!!!Auto remove!");
            reShowHash.remove(p);//去除
            ShowApi.exit(pc.getP(), false);
            return;
        }
        //显示
        if (!noRefresh && page.isRefresh() && pc.getRefresh() != null) {//刷新
            pc.getRefresh().refresh(pc);
        }else {//不刷新
            show(pc.getRefresh(), pc.getObj(), pc.getP(), pc.getPlugin(), pc.getPageName(), pc.getList(), pc.getData(), pc.getPageNow(),
                    pc.getListNow(), pc.getFront(), pc.getBehind(), pc.getItemHash());
        }
        //显示正常结束,去除
        reShowHash.remove(p);
    }

    /**
     * @see com.fyxridd.lib.core.api.ShowApi#back(Player)
     */
    public void back(Player p) {
        List<PlayerContext> backList = backHash.get(p);
        if (backList == null || backList.isEmpty()) {//退出页面
            ShowApi.exit(p, true);
            return;
        }
        //返回
        PlayerContext backContext = backList.remove(backList.size()-1);
        playerContextHash.put(p, backContext);
        List<FancyMessage> tipList = new ArrayList<FancyMessage>();
        tipList.add(get(p.getName(), 660));
        tipHash.put(p, tipList);
        reShow(backContext);
    }

    /**
     * @see com.fyxridd.lib.core.api.ShowApi#exit(Player, boolean)
     */
    public void exit(Player p, boolean tip) {
        exit(p, tip, true);
    }

    /**
     * @see com.fyxridd.lib.core.api.ShowApi#exit(Player, boolean, boolean)
     */
    public void exit(Player p, boolean tip, boolean successTip) {
        //缓存
        reShowHash.remove(p);
        backHash.remove(p);
        tipHash.remove(p);
        //当前没有查看的页面
        if (playerContextHash.remove(p) == null) {
            if(tip) tip(p, get(p.getName(), 665), true);
            return;
        }
        //提示
        if (successTip) tip(p, get(p.getName(), 675), true);
        //发出退出页面事件
        Bukkit.getPluginManager().callEvent(new PlayerPageExitEvent(p));
    }

    /**
     * @see com.fyxridd.lib.core.api.ShowApi#tip(Player, List, boolean)
     */
    public void tip(Player p, String msg, boolean force) {
        if (msg == null) return;
        tip(p, MessageApi.convert(msg), force);
    }

    /**
     * @see com.fyxridd.lib.core.api.ShowApi#tip(Player, List, boolean)
     */
    public void tip(Player p, FancyMessage msg, boolean force) {
        List<FancyMessage> tipList = new ArrayList<>();
        tipList.add(msg);
        tip(p, tipList, force);
    }

    /**
     * @see com.fyxridd.lib.core.api.ShowApi#tip(Player, List, boolean)
     */
    public void tip(Player p, List<FancyMessage> msgList, boolean force) {
        if (msgList == null) return;
        PlayerContext pc = playerContextHash.get(p);
        if (pc == null) {
            for (FancyMessage tip:msgList) MessageApi.sendChatPacket(p, tip);
        }else if (force){
            tipHash.put(p, msgList);
            reShow(pc);
        }
    }

    /**
     * @see com.fyxridd.lib.core.api.ShowApi#setTip(Player, List)
     */
    public void setTip(Player p, FancyMessage tip) {
        List<FancyMessage> list = new ArrayList<FancyMessage>();
        list.add(tip);
        setTip(p, list);
    }

    /**
     * @see com.fyxridd.lib.core.api.ShowApi#setTip(Player, List)
     */
    public void setTip(Player p, List<FancyMessage> tip) {
        tipHash.put(p, tip);
    }

    /**
     * @see ShowApi#clearTip(Player, boolean)
     */
    public void clearTip(Player p, boolean show) {
        tipHash.remove(p);
        if (show) {
            //玩家开启界面的情况下才进行重新显示
            PlayerContext pc = playerContextHash.get(p);
            if (pc != null) reShow(pc);
        }
    }

    /**
     * @see com.fyxridd.lib.core.api.ShowApi#hasRegistered(String, String)
     */
    public boolean hasRegistered(String plugin, String page) {
        if (plugin == null || page == null) return false;
        return pageHash.containsKey(plugin) && pageHash.get(plugin).containsKey(page);
    }

    /**
     * @see com.fyxridd.lib.core.api.ShowApi#getPage(String, String)
     */
    public Page getPage(String plugin, String page) {
        if (plugin == null || page == null) return null;
        if (hasRegistered(plugin, page)) return pageHash.get(plugin).get(page);
        else return null;
    }

    /**
     * @see ShowApi#load(String, String)
     */
    public Page load(String plugin, String page) throws Exception {
        String path = CoreApi.pluginPath+ File.separator+plugin+File.separator+"show"+File.separator+page+".yml";
        File file = new File(path);
        file.getParentFile().mkdirs();
        file.createNewFile();
        return load(plugin, page, file);
    }

    public Page load(String plugin, String page, File file) throws Exception {
        return load(plugin, page, UtilApi.loadConfigByUTF8(file));
    }

    /**
     * @see com.fyxridd.lib.core.api.ShowApi#load(String, String, YamlConfiguration)
     */
    public Page load(String plugin, String page, ConfigurationSection config) throws Exception {
        //enable
        boolean enable = config.getBoolean("enable", true);
        //pageMax
        int pageMax = config.getInt("pageMax", 0);
        if (pageMax < 0) {
            throw new Exception("pageMax must >= 0");
        }
        //listSize
        int listSize = config.getInt("listSize", 0);
        if (listSize < 0) {
            throw new Exception("listSize must >= 0");
        }
        //refresh
        boolean refresh = config.getBoolean("refresh", false);
        //per
        String per = config.getString("per");
        //fillEmpty
        boolean fillEmpty = config.getBoolean("fillEmpty", true);
        //handleTip
        boolean handleTip = config.getBoolean("handleTip", true);
        //record
        boolean record = config.getBoolean("record", true);
        //ListInfo
        ListInfo listInfo;
        {
            String listPlugin = config.getString("list.plugin");
            String listKey = config.getString("list.key");
            String listArg = config.getString("list.arg");
            if (listPlugin == null || listPlugin.isEmpty()) listInfo = null;
            else listInfo = new ListInfo(listPlugin, listKey, listArg);
        }
        //ParamsFactory
        ParamsFactory paramsFactory = new ParamsConverter().convert(plugin, config.getConfigurationSection("params"));
        //lines
        LinkedHashMap<Integer, LineContext> lines = new LinkedHashMap<>();
        for (String key: config.getValues(false).keySet()) {//遍历所有的show-xxx
            try {
                if (key.startsWith("show-")) {
                    int num = Integer.parseInt(key.substring(5));
                    String msg = UtilApi.convert(config.getString("show-" + num, null));
                    if (msg == null) {//show-xxx为null
                        throw new Exception("msg is null!");
                    }
                    FancyMessage line = MessageApi.load(msg, (ConfigurationSection) config.get("info-" + num));
                    if (line == null) {//info-xxx配置错误
                        throw new Exception("msg error!");
                    }
                    LineContext lc = new LineContext(num, line);
                    lines.put(num, lc);
                }
            } catch (Exception e) {
                throw new Exception("load '"+key+"' error: "+e.getMessage(), e);
            }
        }
        //pageList
        List<PageContext> pageList = new ArrayList<>();
        for (int index = 1;index<=pageMax;index++) {
            try {
                String[] ss = config.getString("page-"+index, "").split(" ");
                List<Integer> list = new ArrayList<Integer>();
                for (String check:ss) list.add(Integer.parseInt(check));
                pageList.add(new PageContext(index, list));
            } catch (Exception e) {
                throw new Exception("load page "+index+" error: "+e.getMessage(), e);
            }
        }
        return new PageImpl(plugin, page, enable, pageMax, listSize, refresh, per, fillEmpty, handleTip, record, listInfo, paramsFactory, pageList, lines);
    }

    /**
     * @see com.fyxridd.lib.core.api.ShowApi#isInPage(Player)
     */
    public boolean isInPage(Player p) {
        return playerContextHash.containsKey(p);
    }

    /**
     * @see ShowApi#getPlayerContext(Player)
     */
    public PlayerContext getPlayerContext(Player p) {
        return playerContextHash.get(p);
    }

    private FancyMessage get(String player, int id, Object... args) {
        return config.getLang().get(player, id, args);
    }
}
