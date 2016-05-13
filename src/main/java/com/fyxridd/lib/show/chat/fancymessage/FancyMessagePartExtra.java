package com.fyxridd.lib.show.chat.fancymessage;

import com.fyxridd.lib.core.api.fancymessage.FancyMessagePart;
import com.fyxridd.lib.core.api.hashList.HashList;
import com.fyxridd.lib.core.api.hashList.HashListImpl;
import com.fyxridd.lib.show.chat.api.condition.Condition;
import com.fyxridd.lib.show.chat.api.fancymessage.Conditional;
import com.fyxridd.lib.show.chat.api.fancymessage.Convertable;
import com.fyxridd.lib.show.chat.api.fancymessage.Itemable;
import com.fyxridd.lib.show.chat.condition.MathCompareCondition;

import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 提供额外的功能
 */
public class FancyMessagePartExtra extends FancyMessagePart implements Convertable, Itemable, Conditional {
    private static final String HAS_FIX = "\\{[A-Za-z0-9_]+\\}";
    
    //是否有替换符/列表符
    private boolean hasFix;
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

    //优化策略
    //使用前是否需要进行update()
    //什么时候变为true?当text,clickActionData,hoverActionData等可能包含替换符/列表符的地方改变时变为true
    private boolean updateFlag;

    public FancyMessagePartExtra(String text) {
        super(text);
        updateFlag = true;
    }

    public FancyMessagePartExtra(String text, ChatColor color, ChatColor[] styles, String clickActionName, String clickActionData, String hoverActionName, String hoverActionData, boolean hasFix, HashList<String> listFix, MathCompareCondition conExp, Map<String, Condition> conParams, String item) {
        super(text, color, styles, clickActionName, clickActionData, hoverActionName, hoverActionData);
        this.hasFix = hasFix;
        this.listFix = listFix;
        this.conExp = conExp;
        this.conParams = conParams;
        this.item = item;
    }

    @Override
    public void setText(String text) {
        super.setText(text);
        updateFlag = true;
    }

    @Override
    public void setClickActionData(String clickActionData) {
        super.setClickActionData(clickActionData);
        updateFlag = true;
    }

    @Override
    public void setHoverActionData(String hoverActionData) {
        super.setHoverActionData(hoverActionData);
        updateFlag = true;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
        updateFlag = true;
    }

    public HashList<String> getListFix() {
        return listFix;
    }

    @Override
    public FancyMessagePart clone() {
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
        return new FancyMessagePartExtra(text, color, styles, getClickActionName(), getClickActionData(), getHoverActionName(), getHoverActionData(), hasFix, listFix, conExp, conParams, item);
    }

    /**
     *  可以合并的额外条件:
     *     必须另外一个也是FancyMessagePartExtra
     *     hasFix相同
     *     listFix都为空
     */
    @Override
    public boolean isSame(FancyMessagePart mp) {
        if (!(mp instanceof FancyMessagePartExtra) || !super.isSame(mp)) return false;
        FancyMessagePartExtra mpe = (FancyMessagePartExtra) mp;
        //hasFix
        if ((hasFix && !mpe.hasFix) || (!hasFix && mpe.hasFix)) return false;
        //listFix
        if ((listFix == null || listFix.isEmpty()) && (mpe.listFix == null || mpe.listFix.isEmpty())) return true;
        return false;
    }

    @Override
    public boolean isEmpty() {
        if (!super.isEmpty()) return false;
        if (!hasFix && (listFix == null || listFix.isEmpty())) return true;
        return false;
    }

    @Override
    public void convert(Object... replace) {
        //使用前先更新
        if (!update()) return;

        if (replace.length == 0) return;
        HashMap<String, Object> hash = new HashMap<>();
        for (int i=0;i<replace.length;i++) {
            hash.put(""+i, replace[i]);
        }
        convert(hash);
    }

    @Override
    public void convert(String from, Object to) {
        //使用前先更新
        if (!update()) return;

        HashMap<String, Object> hash = new HashMap<>();
        hash.put(from, to);
        convert(hash);
    }

    @Override
    public void convert(HashMap<String, Object> replace) {
        //使用前先更新
        if (!update()) return;

        if (replace == null) return;
        for (String key:replace.keySet()){
            //name,show
            String name = "{"+key+"}";
            Object value = replace.get(key);
            String show;
            if (value == null) show = "";
            else show = String.valueOf(value);
            //text
            text = text.replace(name, show);
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
            //click
            if (clickActionData != null) clickActionData = clickActionData.replace(name, show);
            //hover
            if (hoverActionData != null) hoverActionData = hoverActionData.replace(name, show);
        }
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

    /**
     * 检测更新hasFix与listFix
     * @return 更新后是否有替换符/列表符
     */
    private boolean update() {
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
