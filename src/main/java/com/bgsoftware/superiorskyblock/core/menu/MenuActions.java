package com.bgsoftware.superiorskyblock.core.menu;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.menu.MenuIslandCreationConfig;
import com.bgsoftware.superiorskyblock.api.menu.view.MenuView;
import com.bgsoftware.superiorskyblock.api.schematic.Schematic;
import com.bgsoftware.superiorskyblock.api.world.Dimension;
import com.bgsoftware.superiorskyblock.api.wrappers.BlockOffset;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.GameSoundImpl;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.core.threads.BukkitExecutor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Locale;

public class MenuActions {

    private static final SuperiorSkyblockPlugin plugin = SuperiorSkyblockPlugin.getPlugin();

    public static void simulateIslandCreationClick(SuperiorPlayer clickedPlayer, String islandName,
                                                   MenuIslandCreationConfig creationConfig, boolean isPreviewMode,
                                                   @Nullable MenuView<?, ?> menuView) {
        Schematic schematic = creationConfig.getSchematic();

        Player whoClicked = clickedPlayer.asPlayer();

        GameSoundImpl.playSound(whoClicked, creationConfig.getSound());

        creationConfig.getCommands().forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                command.replace("%player%", clickedPlayer.getName())));

        Message.ISLAND_CREATE_PROCCESS_REQUEST.send(clickedPlayer);

        if (menuView != null)
            menuView.closeView();

        Dimension dimension = plugin.getSettings().getWorlds().getDefaultWorldDimension();
        boolean offset = creationConfig.shouldOffsetIslandValue() ||
                plugin.getSettings().getWorlds().getDimensionConfig(dimension).isSchematicOffset();

        BlockOffset spawnOffset = creationConfig.getSpawnOffset();

        plugin.getGrid().createIsland(clickedPlayer, schematic.getName(),
                islandName, offset, spawnOffset);
    }

    private MenuActions() {

    }

}
