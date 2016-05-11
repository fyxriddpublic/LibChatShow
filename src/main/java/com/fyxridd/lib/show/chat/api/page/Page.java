package com.fyxridd.lib.show.chat.api.page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 页面配置
 */
public interface Page {
    boolean isEnable();

    void setEnable(boolean enable);

    int getPageMax();

    boolean isRefresh();

    List<PageContext> getPageList();

    void setPageMax(int pageMax);

    void setRefresh(boolean refresh);

    String getPer();

    void setPer(String per);

    boolean isFillEmpty();

    void setFillEmpty(boolean fillEmpty);

    boolean isHandleTip();

    void setHandleTip(boolean handleTip);

    boolean isRecord();

    void setRecord(boolean record);

    String getPlugin();

    String getPage();

    int getListSize();

    void setListSize(int listSize);

    Map<Integer, LineContext> getLines();

    void setLines(Map<Integer, LineContext> lines);

    ListInfo getListInfo();

    HashMap<String, MapInfo> getMaps();
}
