package com.fyxridd.lib.show.chat.fancymessage;

import com.fyxridd.lib.core.api.fancymessage.FancyMessagePart;
import com.fyxridd.lib.core.api.hashList.HashList;
import com.fyxridd.lib.core.api.hashList.HashListImpl;
import com.fyxridd.lib.show.chat.api.condition.Condition;
import com.fyxridd.lib.show.chat.api.fancymessage.Conditional;
import com.fyxridd.lib.show.chat.api.fancymessage.Itemable;
import com.fyxridd.lib.show.chat.condition.MathCompareCondition;

import org.bukkit.ChatColor;

import java.util.Map;
import java.util.Map.Entry;

/**
 * 提供额外的功能
 */
public class FancyMessagePartExtra extends FancyMessagePart implements Itemable, Conditional {
    //列表符列表,格式'数字.属性'或'数字.方法()'或'数字.方法(name)'
    private HashList<String> listFix;

    //条件检测式
    private MathCompareCondition conExp;
    //条件变量列表
    private Map<String, Condition> conParams;
    
    /**
     * 绑定的悬浮物品名
     */
    private String item;

    public FancyMessagePartExtra(String text) {
        super(text);
    }

    public FancyMessagePartExtra(String text, ChatColor color, ChatColor[] styles, String clickActionName, String clickActionData, String hoverActionName, String hoverActionData, boolean hasFix, boolean updateFlag, HashList<String> listFix, MathCompareCondition conExp, Map<String, Condition> conParams, String item) {
        super(text, color, styles, clickActionName, clickActionData, hoverActionName, hoverActionData, hasFix, updateFlag);
        this.listFix = listFix;
        this.conExp = conExp;
        this.conParams = conParams;
        this.item = item;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
        updateFlag = true;
    }

    public Map<String, Condition> getConParams() {
        return conParams;
    }

    public void setConParams(Map<String, Condition> conParams) {
        this.conParams = conParams;
        updateFlag = true;
    }

    public HashList<String> getListFix() {
        return listFix;
    }

    @Override
    public FancyMessagePartExtra clone() {
        //listFix
        HashList<String> listFix;
        if (this.listFix != null) {
            listFix = new HashListImpl<>();
            for (String s:this.listFix) listFix.add(s);
        }else listFix = null;
        //styles
        ChatColor[] styles;
        if (this.getStyles() != null) styles = this.getStyles().clone();
        else styles = null;
        //新建
        return new FancyMessagePartExtra(text, color, styles, getClickActionName(), getClickActionData(), getHoverActionName(), getHoverActionData(), hasFix, updateFlag, listFix, conExp, conParams, item);
    }

    /**
     *  可以合并的额外条件:
     *     必须另外一个也是FancyMessagePartExtra
     *     listFix都为空
     */
    @Override
    public boolean isSame(FancyMessagePart mp) {
        if (!(mp instanceof FancyMessagePartExtra) || !super.isSame(mp)) return false;
        FancyMessagePartExtra mpe = (FancyMessagePartExtra) mp;
        //listFix
        if ((listFix == null || listFix.isEmpty()) && (mpe.listFix == null || mpe.listFix.isEmpty())) return true;
        return false;
    }

    @Override
    public boolean isEmpty() {
        if (!super.isEmpty()) return false;
        if (listFix == null || listFix.isEmpty()) return true;
        return false;
    }

    @Override
    public String getHoverItem() {
        return item;
    }

    @Override
    public boolean checkCondition() {
        if (conExp == null) return true;
        
        //可包含变量，转换
        if (conParams != null) {
            for (Entry<String, Condition> entry:conParams.entrySet()) {
                conExp.convertVariable(entry.getKey(), entry.getValue().check());
            }
        }
        
        //检测
        return conExp.check();
    }

    @Override
    public void convert(Map<String, Object> replace) {
        if (replace == null) return;

        super.convert(replace);

        //使用前先更新
        if (!update()) return;

        for (String key:replace.keySet()){
            //name,show
            String name = "{"+key+"}";
            Object value = replace.get(key);
            String show;
            if (value == null) show = "";
            else show = String.valueOf(value);
            //con
            if (conParams != null) {
                for (Condition condition:conParams.values()) {
                    if (condition instanceof com.fyxridd.lib.show.chat.api.condition.Convertable) {
                        com.fyxridd.lib.show.chat.api.condition.Convertable convertable = (com.fyxridd.lib.show.chat.api.condition.Convertable) condition;
                        convertable.convert(name, show);
                    }
                }
            }
            //item
            if (item != null) item = item.replace(name, show);
        }
    }

    /**
     * 检测更新hasFix与listFix
     */
    protected boolean update() {
        //不需要更新
        if (!updateFlag) return hasFix;
        //标记改变
        updateFlag = false;

        //更新

        //listFix
        this.listFix = new HashListImpl<>();
        if (conParams != null) {
            for (Condition condition:conParams.values()) checkAdd(condition.toString());
        }
        checkAdd(text);
        checkAdd(item);
        checkAdd(clickActionData);
        checkAdd(hoverActionData);

        //hasFix
        hasFix = !listFix.isEmpty();
        if (!hasFix && (" " + text + item + clickActionData + hoverActionData +" ").split(HAS_FIX).length > 1) hasFix = true;
        if (!hasFix && conParams != null) {
            for (Condition condition:conParams.values()) {
                if (condition.toString().split(HAS_FIX).length > 1) {
                    hasFix = true;
                    break;
                }
            }
        }

        return hasFix;
    }

    /**
     * 检测添加列表Fix
     * @param s 检测的字符串,可能包含{数字.属性}或{数字.方法名()}或{数字.方法名(name)},可为null
     */
    private void checkAdd(String s) {
        if (s == null) return;
        boolean start = false;
        int startIndex = 0;
        char[] cc = s.toCharArray();
        for (int i=0;i<cc.length;i++) {
            char c = cc[i];
            if (c == '{') {
                start = true;
                startIndex = i;
            }else if (c == '}') {
                if (start) {
                    int endIndex = i;
                    if (endIndex - startIndex > 2) {//合格内容长度必须>=2
                        String check = s.substring(startIndex + 1, endIndex);//check为'数字.属性'或'数字.方法名()'或'数字.方法名(name)'
                        int index = check.indexOf('.');
                        if (index != -1) {//包含.
                            String[] ss = check.split("\\.");
                            if (ss.length == 2) {
                                try {
                                    Integer.parseInt(ss[0]);
                                } catch (NumberFormatException e) {
                                    continue;
                                }
                                listFix.add(check);
                            }
                        }
                    }
                    //重置
                    start = false;
                    startIndex = 0;
                }
            }
        }
    }
}
