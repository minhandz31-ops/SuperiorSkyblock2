package com.bgsoftware.superiorskyblock.core.menu;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.menu.view.ViewArgs;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuBorderColor;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuConfirmBan;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuConfirmDisband;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuConfirmKick;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuConfirmLeave;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuConfirmTransfer;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuControlPanel;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuCoops;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuIslandBannedPlayers;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuIslandChest;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuIslandCreation;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuIslandSettings;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuIslandMembers;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuIslandRate;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuIslandRatings;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuIslandUpgrades;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuMemberManage;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuMemberRole;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuMissions;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuMissionsCategory;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuPermissions;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuPermissionsCommands;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuPermissionsPhysical;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuPlayerLanguage;
import com.bgsoftware.superiorskyblock.core.menu.impl.internal.MenuBlank;
import com.bgsoftware.superiorskyblock.core.menu.impl.internal.MenuConfigEditor;
import com.bgsoftware.superiorskyblock.core.menu.view.AbstractMenuView;

public class Menus {

    private static final SuperiorSkyblockPlugin plugin = SuperiorSkyblockPlugin.getPlugin();

    public static final MenuBlank MENU_BLANK = MenuBlank.createInstance();
    public static final MenuConfigEditor MENU_CONFIG_EDITOR = MenuConfigEditor.createInstance();

    public static MenuBorderColor MENU_BORDER_COLOR;
    public static MenuConfirmBan MENU_CONFIRM_BAN;
    public static MenuConfirmDisband MENU_CONFIRM_DISBAND;
    public static MenuConfirmKick MENU_CONFIRM_KICK;
    public static MenuConfirmLeave MENU_CONFIRM_LEAVE;
    public static MenuConfirmTransfer MENU_CONFIRM_TRANSFER;
    public static MenuControlPanel MENU_CONTROL_PANEL;
    public static MenuCoops MENU_COOPS;
    public static MenuIslandBannedPlayers MENU_ISLAND_BANNED_PLAYERS;
    public static MenuIslandChest MENU_ISLAND_CHEST;
    public static MenuIslandCreation MENU_ISLAND_CREATION;
    public static MenuIslandSettings MENU_ISLAND_FLAGS;
    public static MenuIslandMembers MENU_ISLAND_MEMBERS;
    public static MenuIslandRate MENU_ISLAND_RATE;
    public static MenuIslandRatings MENU_ISLAND_RATINGS;
    public static MenuIslandUpgrades MENU_ISLAND_UPGRADES;
    public static MenuMemberManage MENU_MEMBER_MANAGE;
    public static MenuMemberRole MENU_MEMBER_ROLE;
    public static MenuMissions MENU_MISSIONS;
    public static MenuMissionsCategory MENU_MISSIONS_CATEGORY;
    public static MenuPermissions MENU_PERMISSIONS;
    public static MenuPermissionsPhysical MENU_PERMISSIONS_PHYSICAL;
    public static MenuPermissionsCommands MENU_PERMISSIONS_COMMANDS;
    public static MenuPlayerLanguage MENU_PLAYER_LANGUAGE;

    private Menus() {

    }

    public static void registerMenus() {
        // We register the internal menus
        createMenu(MENU_BLANK);
        createMenu(MENU_CONFIG_EDITOR);
        // Load menus from files
        MENU_BORDER_COLOR = createMenu(MenuBorderColor.createInstance());
        MENU_CONFIRM_BAN = createMenu(MenuConfirmBan.createInstance());
        MENU_CONFIRM_DISBAND = createMenu(MenuConfirmDisband.createInstance());
        MENU_CONFIRM_KICK = createMenu(MenuConfirmKick.createInstance());
        MENU_CONFIRM_LEAVE = createMenu(MenuConfirmLeave.createInstance());
        MENU_CONFIRM_TRANSFER = createMenu(MenuConfirmTransfer.createInstance());
        MENU_CONTROL_PANEL = createMenu(MenuControlPanel.createInstance());
        MENU_COOPS = createMenu(MenuCoops.createInstance());
        MENU_ISLAND_BANNED_PLAYERS = createMenu(MenuIslandBannedPlayers.createInstance());
        MENU_ISLAND_CHEST = createMenu(MenuIslandChest.createInstance());
        MENU_ISLAND_CREATION = createMenu(MenuIslandCreation.createInstance());
        MENU_ISLAND_FLAGS = createMenu(MenuIslandSettings.createInstance());
        MENU_ISLAND_MEMBERS = createMenu(MenuIslandMembers.createInstance());
        MENU_ISLAND_RATE = createMenu(MenuIslandRate.createInstance());
        MENU_ISLAND_RATINGS = createMenu(MenuIslandRatings.createInstance());
        MENU_ISLAND_UPGRADES = createMenu(MenuIslandUpgrades.createInstance());
        MENU_MEMBER_MANAGE = createMenu(MenuMemberManage.createInstance());
        MENU_MEMBER_ROLE = createMenu(MenuMemberRole.createInstance());
        MENU_MISSIONS = createMenu(MenuMissions.createInstance());
        MENU_MISSIONS_CATEGORY = createMenu(MenuMissionsCategory.createInstance());
        MENU_PERMISSIONS = createMenu(MenuPermissions.createInstance());
        MENU_PERMISSIONS_PHYSICAL = createMenu(MenuPermissionsPhysical.createInstance());
        MENU_PERMISSIONS_COMMANDS = createMenu(MenuPermissionsCommands.createInstance());
        MENU_PLAYER_LANGUAGE = createMenu(MenuPlayerLanguage.createInstance());
    }

    private static <M extends AbstractMenu<V, A>, V extends AbstractMenuView<V, A>, A extends ViewArgs> M createMenu(M menu) {
        if (menu == null)
            throw new IllegalStateException("Menu could not be initialized.");

        plugin.getMenus().registerMenu(menu);
        return menu;
    }

}
