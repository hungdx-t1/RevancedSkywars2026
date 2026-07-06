package com.walrusone.skywarsreloaded.api.command;

import com.walrusone.skywarsreloaded.commands.BaseCmd;

@SuppressWarnings("unused")
public interface SWRCmdManagerAPI {
    void registerCommand(BaseCmd commandIn);
    void unregisterCommand(BaseCmd commandIn);
    BaseCmd getSubCommand(String name);
}
