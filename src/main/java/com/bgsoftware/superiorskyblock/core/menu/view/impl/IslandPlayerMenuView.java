package com.bgsoftware.superiorskyblock.core.menu.view.impl;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.menu.Menu;
import com.bgsoftware.superiorskyblock.api.menu.view.MenuView;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.menu.view.AbstractMenuView;
import com.bgsoftware.superiorskyblock.core.menu.view.IIslandMenuView;
import com.bgsoftware.superiorskyblock.core.menu.view.IPlayerMenuView;
import com.bgsoftware.superiorskyblock.core.menu.view.args.IslandPlayerViewArgs;

public class IslandPlayerMenuView extends AbstractMenuView<IslandPlayerMenuView, IslandPlayerViewArgs>
        implements IIslandMenuView, IPlayerMenuView {

    private final Island island;
    private final SuperiorPlayer permissiblePlayer;

    public IslandPlayerMenuView(SuperiorPlayer inventoryViewer, @Nullable MenuView<?, ?> previousMenuView,
                                Menu<IslandPlayerMenuView, IslandPlayerViewArgs> menu, IslandPlayerViewArgs args) {
        super(inventoryViewer, previousMenuView, menu);
        this.island = args.getIsland();
        this.permissiblePlayer = args.getPermissiblePlayer();
    }

    @Override
    public Island getIsland() {
        return island;
    }

    @Override
    public SuperiorPlayer getSuperiorPlayer() {
        return permissiblePlayer;
    }

    @Override
    public String replaceTitle(String title) {
        return title.replace("{}", permissiblePlayer.getName());
    }

}
