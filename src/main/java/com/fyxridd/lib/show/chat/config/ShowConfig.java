package com.fyxridd.lib.show.chat.config;

import com.fyxridd.lib.config.api.basic.Path;
import com.fyxridd.lib.config.api.convert.ConfigConvert;
import com.fyxridd.lib.lang.api.LangConverter;
import com.fyxridd.lib.lang.api.LangGetter;

public class ShowConfig {
    @Path("lang")
    @ConfigConvert(LangConverter.class)
    private LangGetter lang;

    public LangGetter getLang() {
        return lang;
    }
}
