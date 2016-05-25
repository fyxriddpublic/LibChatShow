package com.fyxridd.lib.show.chat.api.page;

import java.util.List;
import java.util.Map;

import com.fyxridd.lib.params.api.ParamsFactory;

/**
 * 页面配置
 */
public interface Page {
    String getPlugin();

    String getPage();

    boolean isEnable();

    String getPer();

    boolean isFillEmpty();

    boolean isHandleTip();

    boolean isRecord();

    int getPageMax();

    ListInfo getListInfo();

    ParamsFactory getParamsFactory();
    
    List<PageContext> getPageList();

    Map<Integer, LineContext> getLines();
}
