package com.fyxridd.lib.show.chat.condition;

import com.fyxridd.lib.show.chat.api.condition.Condition;
import com.fyxridd.lib.show.chat.api.condition.Convertable;

/**
 * 字符串包含
 */
public class StringHasCondition implements Condition, Convertable{
    /**
     * 操作符
     */
    private static enum Operate {
        Has("has"),
        Not("not")
        ;
        
        private String symbol;
        
        private Operate(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }

        @Override
        public String toString() {
            return symbol;
        }
        
        /**
         * @return 不存在返回null
         */
        public static Operate loadFromSymbol(String symbol) {
            if (symbol.equalsIgnoreCase(Operate.Has.getSymbol())) return Operate.Has;
            else if (symbol.equalsIgnoreCase(Operate.Not.getSymbol())) return Operate.Not;
            else return null;
        }
    }
    
    private boolean sensitive;
    private Operate operate;
    private String from;
    private String to;

    public StringHasCondition() {}

    public StringHasCondition(boolean sensitive, Operate operate, String from, String to) {
        this.sensitive = sensitive;
        this.operate = operate;
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean check() {
        String from,to;
        if (!sensitive) {
            from = this.from.toLowerCase();
            to = this.to.toLowerCase();
        } else {
            from = this.from;
            to = this.to;
        }
        if (operate == Operate.Has) return from.contains(to);
        else if (operate == Operate.Not) return !from.contains(to);
        else throw new IllegalArgumentException("operate is "+operate);
    }

    @Override
    public StringHasCondition clone(){
        return new StringHasCondition(sensitive, operate, from, to);
    }

    @Override
    public void convert(String from, Object to) {
        this.from = this.from.replace(from, to.toString());
        this.to = this.to.replace(from, to.toString());
    }

    /**
     * 从条件字符串读取条件
     * @param condition 条件字符串
     * @return 条件,异常返回null
     */
    public static StringHasCondition loadFromString(String condition) {
        //sensitive
        boolean sensitive;
        if (condition.charAt(0) == 'y') sensitive = true;
        else if (condition.charAt(0) == 'n') sensitive = false;
        else return null;
        //has
        int pos1 = 1;
        int pos2 = condition.indexOf(39, pos1+1);
        int pos3 = pos2+4;
        int pos4 = condition.length()-1;
        Operate operate = Operate.loadFromSymbol(condition.substring(pos2+1, pos3));
        if (operate == null) return null;
        //from,to
        String from;
        if (pos2-pos1 < 1) return null;
        if (pos2-pos1 == 1) from = "";
        else from = condition.substring(pos1 + 1, pos2);
        String to;
        if (pos4-pos3 < 1) return null;
        if (pos4-pos3 == 1) to = "";
        else to = condition.substring(pos3 + 1, pos4);
        //返回
        return new StringHasCondition(sensitive, operate, from, to);
    }

    /**
     * 保存到字符串
     */
    public static String saveToString(StringHasCondition condition) {
        String sensitive = condition.sensitive?"y":"n";
        return sensitive+"'"+condition.from+"'"+condition.operate.getSymbol()+"'"+condition.to+"'";
    }
}
