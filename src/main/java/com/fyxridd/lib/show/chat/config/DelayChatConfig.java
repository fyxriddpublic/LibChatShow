package com.fyxridd.lib.show.chat.config;

import com.fyxridd.lib.config.api.basic.Path;
import com.fyxridd.lib.config.api.convert.PrimeConvert;
import com.fyxridd.lib.config.api.limit.Min;
import com.fyxridd.lib.core.api.UtilApi;
import com.fyxridd.lib.core.api.fancymessage.FancyMessage;
import com.fyxridd.lib.core.fancymessage.FancyMessageImpl;

@Path("delayChat")
public class DelayChatConfig {
    private class PrefixConverter implements PrimeConvert.PrimeConverter<String, FancyMessage> {
        @Override
        public FancyMessage convert(String plugin, String from) {
            if (from == null || from.isEmpty()) return null;
            return new FancyMessageImpl(UtilApi.convert(from));
        }
    }

    @Path("maxSaves")
    @Min(0)
    private int maxSaves;

    @Path("interval")
    @Min(1)
    private int interval;

    //可为null
    @Path("prefix")
    @PrimeConvert(value = PrefixConverter.class, primeType = PrimeConvert.PrimeType.String)
    private FancyMessage prefix;

    public int getMaxSaves() {
        return maxSaves;
    }

    public int getInterval() {
        return interval;
    }

    public FancyMessage getPrefix() {
        return prefix;
    }
}
