package com.fyxridd.lib.show.chat;

import com.fyxridd.lib.core.CoreMain;
import com.fyxridd.lib.core.api.ConfigApi;
import com.fyxridd.lib.core.api.CoreApi;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class ShowPlugin extends JavaPlugin {
    public static ShowPlugin instance;

    //插件名
    public static String pn;
    //插件jar文件
    public static File file;
    //插件数据文件夹路径
    public static String dataPath;
    //插件版本
    public static String ver;

    @Override
    public void onLoad() {
        instance = this;
        pn = getName();
        file = getFile();
        dataPath = CoreApi.pluginPath + File.separator + pn;
        ver = CoreApi.getPluginVersion(getFile());

        //生成文件
        ConfigApi.generateFiles(getFile(), pn);
    }

    //启动插件
    @Override
    public void onEnable() {
        new CoreMain();

        //成功启动
        CoreApi.sendConsoleMessage(FormatApi.get(pn, 25, pn, ver).getText());
    }

    //停止插件
    @Override
    public void onDisable() {
        //显示插件成功停止信息
        CoreApi.sendConsoleMessage(FormatApi.get(pn, 30, pn, ver).getText());
    }
}