package com.walrusone.skywarsreloaded.api;

import com.walrusone.skywarsreloaded.commands.*;

@SuppressWarnings("unused")
public interface SWRCommandAPI {
    MainCmdManager getMainCommandManager();
    KitCmdManager getKitCommandManager();
    MapCmdManager getMapCommandManager();
    PartyCmdManager getPartyCommandManager();
}