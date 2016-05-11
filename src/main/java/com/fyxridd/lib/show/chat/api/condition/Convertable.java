package com.fyxridd.lib.show.chat.api.condition;

/**
 * 可转换变量的
 * (可被Condition的实现类实现)
 */
public interface Convertable {
    /**
     * 格式转换
     * @param from 名(注意名称中包含{})
     * @param to 值
     */
    void convert(String from, Object to);
    
    /**
     * @return 需要将所有的变量都暴露到字符串中，这样可以检测是否存在变量
     */
    String getConvertedStr();
}
