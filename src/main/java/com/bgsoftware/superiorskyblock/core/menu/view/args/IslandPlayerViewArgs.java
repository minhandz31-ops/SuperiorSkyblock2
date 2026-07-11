package com.bgsoftware.superiorskyblock.core.menu.view.args;

import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.menu.view.ViewArgs;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;

public class IslandPlayerViewArgs implements ViewArgs {

    private final Island island;
    private final SuperiorPlayer permissiblePlayer;

    public IslandPlayerViewArgs(Island island, SuperiorPlayer permissiblePlayer) {
        this.island = island;
        this.permissiblePlayer = permissiblePlayer;
    }

    public Island getIsland() {
        return island;
    }

    public SuperiorPlayer getPermissiblePlayer() {
        return permissiblePlayer;
    }

}
