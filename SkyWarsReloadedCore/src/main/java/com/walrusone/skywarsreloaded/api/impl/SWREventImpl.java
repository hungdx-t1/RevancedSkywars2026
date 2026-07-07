package com.walrusone.skywarsreloaded.api.impl;

import com.walrusone.skywarsreloaded.api.SWREventAPI;
import com.walrusone.skywarsreloaded.api.SkywarsReloadedAPI;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class SWREventImpl implements SWREventAPI {
    private final SkywarsReloadedAPI swrAPI;

    public SWREventImpl(SkywarsReloadedAPI swrAPIIn) {
        this.swrAPI = swrAPIIn;
    }
}