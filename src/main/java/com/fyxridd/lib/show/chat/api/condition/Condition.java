package com.fyxridd.lib.show.chat.api.condition;

/**
 * 条件接口
 */
public interface Condition extends Cloneable{
    /**
     * 检测条件是否满足
     * @return 是否满足
     */
    public boolean check();
}
