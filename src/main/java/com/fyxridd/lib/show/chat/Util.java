package com.fyxridd.lib.show.chat;

import com.fyxridd.lib.core.api.MessageApi;
import com.fyxridd.lib.core.api.fancymessage.FancyMessage;
import com.fyxridd.lib.core.api.fancymessage.FancyMessagePart;
import com.fyxridd.lib.core.api.hashList.HashList;
import com.fyxridd.lib.show.chat.api.condition.Condition;
import com.fyxridd.lib.show.chat.condition.MathCompareCondition;
import com.fyxridd.lib.show.chat.condition.StringCompareCondition;
import com.fyxridd.lib.show.chat.condition.StringHasCondition;
import com.fyxridd.lib.show.chat.fancymessage.FancyMessagePartExtra;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class Util {
    private static final String CON_PREFIX_MATH_COMPARE = "m";
    private static final String CON_PREFIX_STRING_COMPARE = "s";
    private static final String CON_PREFIX_STRING_HAS = "c";

    /**
     * @see com.fyxridd.lib.show.chat.api.ShowApi#load(String, ConfigurationSection)
     */
    public static FancyMessage load(String msg, ConfigurationSection config) throws Exception {
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
}
