package com.bgsoftware.superiorskyblock.external.menus;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.hooks.MenusProvider;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandChest;
import com.bgsoftware.superiorskyblock.api.island.IslandFlag;
import com.bgsoftware.superiorskyblock.api.island.IslandPrivilege;
import com.bgsoftware.superiorskyblock.api.island.PlayerRole;
import com.bgsoftware.superiorskyblock.api.menu.ISuperiorMenu;
import com.bgsoftware.superiorskyblock.api.menu.MenuIslandCreationConfig;
import com.bgsoftware.superiorskyblock.api.menu.button.MenuTemplateButton;
import com.bgsoftware.superiorskyblock.api.missions.MissionCategory;
import com.bgsoftware.superiorskyblock.api.schematic.Schematic;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.errors.ManagerLoadException;
import com.bgsoftware.superiorskyblock.core.io.Files;
import com.bgsoftware.superiorskyblock.core.logging.Log;
import com.bgsoftware.superiorskyblock.core.menu.MenuActions;
import com.bgsoftware.superiorskyblock.core.menu.MenuConfig;
import com.bgsoftware.superiorskyblock.core.menu.Menus;
import com.bgsoftware.superiorskyblock.core.menu.button.impl.IslandCreationButton;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuConfirmBan;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuConfirmKick;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuConfirmTransfer;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuIslandCreation;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuPermissions;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuMissionsCategory;
import com.bgsoftware.superiorskyblock.core.menu.impl.internal.MenuCustom;
import com.bgsoftware.superiorskyblock.core.menu.view.args.EmptyViewArgs;
import com.bgsoftware.superiorskyblock.core.menu.view.args.IslandViewArgs;
import com.bgsoftware.superiorskyblock.core.menu.view.args.IslandPlayerViewArgs;
import com.bgsoftware.superiorskyblock.core.menu.view.args.PlayerViewArgs;
import com.bgsoftware.superiorskyblock.island.privilege.IslandPrivileges;
import com.google.common.base.Preconditions;

import java.io.File;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MenusProvider_Default implements MenusProvider {

    private final Map<Schematic, MenuIslandCreationConfig> ISLAND_CREATION_CONFIG_CACHE = new IdentityHashMap<>();

    private final SuperiorSkyblockPlugin plugin;

    public MenusProvider_Default(SuperiorSkyblockPlugin plugin) {
        this.plugin = plugin;
    }

    private static void handleExceptions(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception ex) {
            ManagerLoadException handlerError = new ManagerLoadException(ex, ManagerLoadException.ErrorLevel.CONTINUE);
            Log.error(handlerError, "An unexpected error occurred while loading menu:");
        }
    }

    @Override
    public void initializeMenus() {
        File guiFolder = new File(plugin.getDataFolder(), "guis");
        if (guiFolder.exists()) {
            File oldGuisFolder = new File(plugin.getDataFolder(), "old-guis");
            //noinspection ResultOfMethodCallIgnored
            guiFolder.renameTo(oldGuisFolder);
        }

        // We first want to unregister all menus
        plugin.getMenus().unregisterMenus();

        Menus.registerMenus();

        File customMenusFolder = new File(plugin.getDataFolder(), "menus/custom");

        if (!customMenusFolder.exists()) {
            //noinspection ResultOfMethodCallIgnored
            customMenusFolder.mkdirs();
            return;
        }

        for (File menuFile : Files.listFolderFiles(customMenusFolder, false)) {
            handleExceptions(() -> plugin.getMenus().registerMenu(MenuCustom.createInstance(menuFile)));
        }
    }

    @Override
    public void openBorderColor(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Menus.MENU_BORDER_COLOR.createView(targetPlayer, EmptyViewArgs.INSTANCE, previousMenu);
    }

    @Override
    public void openConfirmBan(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Island targetIsland, SuperiorPlayer bannedPlayer) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetIsland, "targetIsland parameter cannot be null.");
        Preconditions.checkNotNull(bannedPlayer, "bannedPlayer parameter cannot be null.");
        Menus.MENU_CONFIRM_BAN.createView(targetPlayer, new MenuConfirmBan.Args(targetIsland, bannedPlayer), previousMenu);
    }

    @Override
    public void openConfirmDisband(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Island targetIsland) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetIsland, "targetIsland parameter cannot be null.");
        Menus.MENU_CONFIRM_DISBAND.createView(targetPlayer, new IslandViewArgs(targetIsland), previousMenu);
    }

    @Override
    public void openConfirmKick(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Island targetIsland, SuperiorPlayer kickedPlayer) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetIsland, "targetIsland parameter cannot be null.");
        Preconditions.checkNotNull(kickedPlayer, "kickedPlayer parameter cannot be null.");
        Menus.MENU_CONFIRM_KICK.createView(targetPlayer, new MenuConfirmKick.Args(targetIsland, kickedPlayer), previousMenu);
    }

    @Override
    public void openConfirmLeave(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Menus.MENU_CONFIRM_LEAVE.createView(targetPlayer, EmptyViewArgs.INSTANCE, previousMenu);
    }

    @Override
    public void openConfirmTransfer(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Island targetIsland, SuperiorPlayer newOwner) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetIsland, "targetIsland parameter cannot be null.");
        Preconditions.checkNotNull(newOwner, "newOwner parameter cannot be null.");
        Menus.MENU_CONFIRM_TRANSFER.createView(targetPlayer, new MenuConfirmTransfer.Args(targetIsland, newOwner), previousMenu);
    }

    @Override
    public void openControlPanel(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Island targetIsland) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetIsland, "targetIsland parameter cannot be null.");
        Menus.MENU_CONTROL_PANEL.createView(targetPlayer, new IslandViewArgs(targetIsland), previousMenu);
    }

    @Override
    public void openCoops(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Island targetIsland) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetIsland, "targetIsland parameter cannot be null.");
        Menus.MENU_COOPS.createView(targetPlayer, new IslandViewArgs(targetIsland), previousMenu);
    }

    @Override
    public void refreshCoops(Island island) {
        Preconditions.checkNotNull(island, "island parameter cannot be null.");
        Menus.MENU_COOPS.refreshViews(island);
    }

    @Override
    public void openIslandBannedPlayers(SuperiorPlayer targetPlayer, ISuperiorMenu previousMenu, Island targetIsland) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetIsland, "targetIsland parameter cannot be null.");
        Menus.MENU_ISLAND_BANNED_PLAYERS.createView(targetPlayer, new IslandViewArgs(targetIsland), previousMenu);
    }

    @Override
    public void refreshIslandBannedPlayers(Island island) {
        Preconditions.checkNotNull(island, "island parameter cannot be null.");
        Menus.MENU_ISLAND_BANNED_PLAYERS.refreshViews(island);
    }

    @Override
    public void openIslandChest(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Island targetIsland) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetIsland, "targetIsland parameter cannot be null.");
        if (Menus.MENU_ISLAND_CHEST.isSkipOneItem()) {
            IslandChest[] islandChest = targetIsland.getChest();
            if (islandChest.length == 1) {
                islandChest[0].openChest(targetPlayer);
                return;
            }
        }

        Menus.MENU_ISLAND_CHEST.createView(targetPlayer, new IslandViewArgs(targetIsland), previousMenu);
    }

    @Override
    public void refreshIslandChest(Island island) {
        Preconditions.checkNotNull(island, "island parameter cannot be null.");
        Menus.MENU_ISLAND_CHEST.refreshViews(island);
    }

    @Override
    public MenuIslandCreationConfig getIslandCreationConfig(Schematic schematic) {
        return ISLAND_CREATION_CONFIG_CACHE.computeIfAbsent(schematic, unused -> {
            for (MenuTemplateButton<MenuIslandCreation.View> button : Menus.MENU_ISLAND_CREATION.getLayout().getButtons()) {
                if (IslandCreationButton.class.equals(button.getViewButtonType()) &&
                        ((IslandCreationButton.Template) button).getSchematic().equals(schematic)) {
                    return ((IslandCreationButton.Template) button).getCreationConfig();
                }
            }

            return new MenuConfig.IslandCreation(schematic, null);
        });
    }

    @Override
    public void openIslandCreation(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, String islandName) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(islandName, "islandName parameter cannot be null.");
        if (Menus.MENU_ISLAND_CREATION.isSkipOneItem()) {
            List<Schematic> schematicButtons = Menus.MENU_ISLAND_CREATION.getLayout().getButtons().stream()
                    .filter(button -> IslandCreationButton.class.equals(button.getViewButtonType()))
                    .map(button -> ((IslandCreationButton.Template) button).getSchematic())
                    .collect(Collectors.toList());

            if (schematicButtons.size() == 1) {
                MenuIslandCreationConfig creationConfig = getIslandCreationConfig(schematicButtons.get(0));
                MenuActions.simulateIslandCreationClick(targetPlayer, islandName, creationConfig, false, targetPlayer.getOpenedView());
                return;
            }
        }

        Menus.MENU_ISLAND_CREATION.createView(targetPlayer, new MenuIslandCreation.Args(islandName), previousMenu);
    }

    @Override
    public void openIslandRate(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Island targetIsland) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetIsland, "targetIsland parameter cannot be null.");
        Menus.MENU_ISLAND_RATE.createView(targetPlayer, new IslandViewArgs(targetIsland), previousMenu);
    }

    @Override
    public void openIslandRatings(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Island targetIsland) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetIsland, "targetIsland parameter cannot be null.");
        Menus.MENU_ISLAND_RATINGS.createView(targetPlayer, new IslandViewArgs(targetIsland), previousMenu);
    }

    @Override
    public void refreshIslandRatings(Island island) {
        Preconditions.checkNotNull(island, "island parameter cannot be null.");
        Menus.MENU_ISLAND_RATINGS.refreshViews(island);
    }

    @Override
    public void openMemberManage(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, SuperiorPlayer islandMember) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(islandMember, "islandMember parameter cannot be null.");
        Menus.MENU_MEMBER_MANAGE.createView(targetPlayer, new PlayerViewArgs(islandMember), previousMenu);
    }

    @Override
    public void destroyMemberManage(SuperiorPlayer islandMember) {
        Preconditions.checkNotNull(islandMember, "islandMember parameter cannot be null.");
        Menus.MENU_MEMBER_MANAGE.closeViews(islandMember);
    }

    @Override
    public void openMemberRole(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, SuperiorPlayer islandMember) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(islandMember, "islandMember parameter cannot be null.");
        Menus.MENU_MEMBER_ROLE.createView(targetPlayer, new PlayerViewArgs(islandMember), previousMenu);
    }

    @Override
    public void destroyMemberRole(SuperiorPlayer islandMember) {
        Preconditions.checkNotNull(islandMember, "islandMember parameter cannot be null.");
        Menus.MENU_MEMBER_ROLE.closeViews(islandMember);
    }

    @Override
    public void openMembers(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Island targetIsland) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetIsland, "targetIsland parameter cannot be null.");
        Menus.MENU_ISLAND_MEMBERS.createView(targetPlayer, new IslandViewArgs(targetIsland), previousMenu);
    }

    @Override
    public void refreshMembers(Island island) {
        Preconditions.checkNotNull(island, "island parameter cannot be null.");
        Menus.MENU_ISLAND_MEMBERS.refreshViews(island);
    }

    @Override
    public void openMissions(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        if (Menus.MENU_MISSIONS.isSkipOneItem()) {
            List<MissionCategory> missionCategories = plugin.getMissions().getMissionCategories();
            if (missionCategories.size() == 1) {
                openMissionsCategory(targetPlayer, previousMenu, missionCategories.get(0));
                return;
            }
        }
        Menus.MENU_MISSIONS.createView(targetPlayer, EmptyViewArgs.INSTANCE, previousMenu);
    }

    @Override
    public void openMissionsCategory(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, MissionCategory missionCategory) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(missionCategory, "missionCategory parameter cannot be null.");
        Menus.MENU_MISSIONS_CATEGORY.createView(targetPlayer, new MenuMissionsCategory.Args(missionCategory), previousMenu);
    }

    @Override
    public void refreshMissionsCategory(MissionCategory missionCategory) {
        Preconditions.checkNotNull(missionCategory, "missionCategory parameter cannot be null.");
        Menus.MENU_MISSIONS_CATEGORY.refreshViews(missionCategory);
    }

    @Override
    public void openPermissions(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Island targetIsland, SuperiorPlayer permissiblePlayer) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetIsland, "targetIsland parameter cannot be null.");
        Preconditions.checkNotNull(permissiblePlayer, "permissiblePlayer parameter cannot be null.");
        Menus.MENU_PERMISSIONS.createView(targetPlayer, new IslandPlayerViewArgs(targetIsland, permissiblePlayer), previousMenu);
    }

    @Override
    public void openPermissions(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Island targetIsland, PlayerRole permissibleRole) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetIsland, "targetIsland parameter cannot be null.");
        Preconditions.checkNotNull(permissibleRole, "permissibleRole parameter cannot be null.");
        Menus.MENU_PERMISSIONS.createView(targetPlayer, new IslandPlayerViewArgs(targetIsland, null), previousMenu);
    }

    @Override
    public void refreshPermissions(Island island) {
        Preconditions.checkNotNull(island, "island parameter cannot be null.");
        Menus.MENU_PERMISSIONS.refreshViews(island);
    }

    @Override
    public void refreshPermissions(Island island, SuperiorPlayer permissiblePlayer) {
        Preconditions.checkNotNull(island, "island parameter cannot be null.");
        Preconditions.checkNotNull(permissiblePlayer, "permissiblePlayer parameter cannot be null.");
        Menus.MENU_PERMISSIONS.refreshViews(island);
    }

    @Override
    public void refreshPermissions(Island island, PlayerRole permissibleRole) {
        Preconditions.checkNotNull(island, "island parameter cannot be null.");
        Preconditions.checkNotNull(permissibleRole, "permissibleRole parameter cannot be null.");
        Menus.MENU_PERMISSIONS.refreshViews(island);
    }

    @Override
    public void updatePermission(IslandPrivilege islandPrivilege) {
        // The default implementation does not care if the island privilege is valid for showing the island
        // privileges in the menu. If the island privilege is not valid at the time of opening the menu, it
        // will show it as it was disabled. This is the responsibility of the server owners to properly
        // configure the menu.
    }

    @Override
    public void openPlayerLanguage(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Menus.MENU_PLAYER_LANGUAGE.createView(targetPlayer, EmptyViewArgs.INSTANCE, previousMenu);
    }

    @Override
    public void openSettings(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Island targetIsland) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetIsland, "targetIsland parameter cannot be null.");
        Menus.MENU_ISLAND_FLAGS.createView(targetPlayer, new IslandViewArgs(targetIsland), previousMenu);
    }

    @Override
    public void refreshSettings(Island island) {
        Preconditions.checkNotNull(island, "island parameter cannot be null.");
        Menus.MENU_ISLAND_FLAGS.refreshViews(island);
    }

    @Override
    public void updateSettings(IslandFlag islandFlag) {
        // The default implementation does not care if the island flag is valid for showing the island flags
        // in the menu. If the island flag is not valid at the time of opening the menu, it will show it as
        // it was disabled. This is the responsibility of the server owners to properly configure the menu.
    }

    @Override
    public void openUpgrades(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Island targetIsland) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetIsland, "targetIsland parameter cannot be null.");
        Menus.MENU_ISLAND_UPGRADES.createView(targetPlayer, new IslandViewArgs(targetIsland), previousMenu);
    }

    @Override
    public void refreshUpgrades(Island island) {
        Preconditions.checkNotNull(island, "island parameter cannot be null.");
        Menus.MENU_ISLAND_UPGRADES.refreshViews(island);
    }

}
