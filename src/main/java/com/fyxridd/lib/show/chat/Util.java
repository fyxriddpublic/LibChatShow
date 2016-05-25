package com.fyxridd.lib.show.chat;

import com.fyxridd.lib.core.api.CoreApi;
import com.fyxridd.lib.core.api.MessageApi;
import com.fyxridd.lib.core.api.UtilApi;
import com.fyxridd.lib.core.api.fancymessage.FancyMessage;
import com.fyxridd.lib.core.api.fancymessage.FancyMessagePart;
import com.fyxridd.lib.core.api.hashList.HashList;
import com.fyxridd.lib.params.api.ParamsConverter;
import com.fyxridd.lib.params.api.ParamsFactory;
import com.fyxridd.lib.show.chat.api.ShowApi;
import com.fyxridd.lib.show.chat.api.condition.Condition;
import com.fyxridd.lib.show.chat.api.page.LineContext;
import com.fyxridd.lib.show.chat.api.page.ListInfo;
import com.fyxridd.lib.show.chat.api.page.Page;
import com.fyxridd.lib.show.chat.api.page.PageContext;
import com.fyxridd.lib.show.chat.condition.MathCompareCondition;
import com.fyxridd.lib.show.chat.condition.StringCompareCondition;
import com.fyxridd.lib.show.chat.condition.StringHasCondition;
import com.fyxridd.lib.show.chat.fancymessage.FancyMessagePartExtra;
import com.fyxridd.lib.show.chat.impl.PageImpl;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.*;

public class Util {
    private static final String CON_PREFIX_MATH_COMPARE = "m";
    private static final String CON_PREFIX_STRING_COMPARE = "s";
    private static final String CON_PREFIX_STRING_HAS = "c";

    /**
     * @see com.fyxridd.lib.show.chat.api.ShowApi#loadFancyMessage(String, ConfigurationSection)
     */
    public static FancyMessage loadFancyMessage(String msg, ConfigurationSection config) throws Exception {
        try {
            FancyMessage result = MessageApi.load(msg, config);
            Map<Integer, FancyMessagePart> map = result.getMessageParts();
            for (int index=0;index<map.size();index++) {
                boolean hasFix = false;
                HashList<String> listFix = null;
                MathCompareCondition conExp;
                Map<String, Condition> conParams;
                String item;
                boolean updateFlag = true;
                //读取
                {
                    ConfigurationSection directConfig = (ConfigurationSection) config.get(""+index);

                    //conExp
                    {
                        conExp = MathCompareCondition.loadFromString(directConfig.getString("con.exp"));
                    }
                    //conParams
                    {
                        conParams = new HashMap<String, Condition>();
                        ConfigurationSection conParamsConfig = (ConfigurationSection) directConfig.get("con.params");
                        if (conParamsConfig != null) {
                            for (Map.Entry<String, Object> entry:conParamsConfig.getValues(true).entrySet()) {
                                String paramName = entry.getKey();
                                try {
                                    String paramValue = (String) entry.getValue();
                                    Condition condition;
                                    if (paramName.startsWith(CON_PREFIX_MATH_COMPARE)) {
                                        condition = MathCompareCondition.loadFromString(paramValue);
                                    }else if (paramName.startsWith(CON_PREFIX_STRING_COMPARE)) {
                                        condition = StringCompareCondition.loadFromString(paramValue);
                                    }else if (paramName.startsWith(CON_PREFIX_STRING_HAS)) {
                                        condition = StringHasCondition.loadFromString(paramValue);
                                    }else throw new Exception("prefix error!");
                                    conParams.put(paramName, condition);
                                } catch (Exception e) {
                                    throw new Exception("load param '"+paramName+"' error: "+e.getMessage(), e);
                                }
                            }
                        }
                    }
                    //item
                    {
                        item = directConfig.getString("item");
                    }
                }
                //装配
                FancyMessagePart mp = map.get(index);
                FancyMessagePartExtra mpe = new FancyMessagePartExtra(mp.getText(), mp.getColor(), mp.getStyles(), mp.getClickActionName(), mp.getClickActionData(), mp.getHoverActionName(), mp.getHoverActionData(), hasFix, updateFlag, listFix, conExp, conParams, item);
                //更新
                map.put(index, mpe);
            }
            return result;
        } catch (Exception e) {
            throw new Exception("FancyMessage load error: "+e.getMessage(), e);
        }
    }

    /**
     * @see ShowApi#loadPage(String, String)
     */
    public static Page loadPage(String plugin, String page) throws Exception {
        String path = CoreApi.pluginPath+ File.separator+plugin+File.separator+"show"+File.separator+page+".yml";
        File file = new File(path);
        file.getParentFile().mkdirs();
        file.createNewFile();
        return loadPage(plugin, page, file);
    }

    public static Page loadPage(String plugin, String page, File file) throws Exception {
        return loadPage(plugin, page, UtilApi.loadConfigByUTF8(file));
    }

    /**
     * @see ShowApi#loadPage(String, String, ConfigurationSection)
     */
    public static Page loadPage(String plugin, String page, ConfigurationSection config) throws Exception {
        //enable
        boolean enable = config.getBoolean("enable", true);
        //pageMax
        int pageMax = config.getInt("pageMax", 1);
        if (pageMax < 0) {
            throw new Exception("pageMax must >= 0");
        }
        //refresh
        boolean refresh = config.getBoolean("refresh", true);
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
            int listSize = config.getInt("list.size", 0);
            if (listSize < 0) throw new Exception("listSize must >= 0");
            if (listPlugin == null || listPlugin.isEmpty()) listInfo = null;
            else listInfo = new ListInfo(listPlugin, listKey, listArg, listSize);
        }
        //ParamsFactory
        ParamsFactory paramsFactory = new ParamsConverter().convert(plugin, config.getConfigurationSection("params"));
        //lines
        Map<Integer, LineContext> lines = new LinkedHashMap<>();
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
                List<Integer> list = new ArrayList<>();
                for (String check:ss) list.add(Integer.parseInt(check));
                pageList.add(new PageContext(index, list));
            } catch (Exception e) {
                throw new Exception("load page "+index+" error: "+e.getMessage(), e);
            }
        }
        return new PageImpl(plugin, page, enable, pageMax, refresh, per, fillEmpty, handleTip, record, listInfo, paramsFactory, pageList, lines);
    }
}
