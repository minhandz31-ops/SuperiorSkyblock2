package com.bgsoftware.superiorskyblock.api.hooks;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandFlag;
import com.bgsoftware.superiorskyblock.api.island.IslandPrivilege;
import com.bgsoftware.superiorskyblock.api.island.PlayerRole;
import com.bgsoftware.superiorskyblock.api.menu.ISuperiorMenu;
import com.bgsoftware.superiorskyblock.api.menu.MenuIslandCreationConfig;
import com.bgsoftware.superiorskyblock.api.missions.MissionCategory;
import com.bgsoftware.superiorskyblock.api.schematic.Schematic;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;

public interface MenusProvider {

    /**
     * Initialize the menus.
     */
    void initializeMenus();

    /**
     * Open the border-color menu.
     * Used to change the color of the world border for a player.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     */
    void openBorderColor(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu);

    /**
     * Open the confirm-ban menu.
     * Used to confirm a ban of an island member.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param targetIsland The island to ban the player from.
     * @param bannedPlayer The player that will be banned.
     */
    void openConfirmBan(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Island targetIsland, SuperiorPlayer bannedPlayer);

    /**
     * Open the confirm-disband menu.
     * Used to confirm disband of an island.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param targetIsland The island to disband.
     */
    void openConfirmDisband(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Island targetIsland);

    /**
     * Open the confirm-kick menu.
     * Used to confirm a kick of an island member.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param targetIsland The island to kick the player from.
     * @param kickedPlayer The player that will be kicked.
     */
    void openConfirmKick(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Island targetIsland, SuperiorPlayer kickedPlayer);

    /**
     * Open the confirm-leave menu.
     * Used to confirm leaving of an island.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     */
    void openConfirmLeave(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu);

    /**
     * Open the confirm-transfer menu.
     * Used to confirm the transfer of an island.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param targetIsland The island to ban the player from.
     * @param newOwner The player that will be banned.
     */
    void openConfirmTransfer(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Island targetIsland, SuperiorPlayer newOwner);

    /**
     * Open the control-panel menu.
     * Used when opening the control panel of an island.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param targetIsland The island to open the control panel of.
     */
    void openControlPanel(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Island targetIsland);

    /**
     * Open the coops menu.
     * Used when opening the coops menu of an island.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param targetIsland The island to get coop-members from.
     */
    void openCoops(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Island targetIsland);

    /**
     * Refresh the coops-menu for a specific island.
     *
     * @param island The island to refresh the menus for.
     */
    void refreshCoops(Island island);

    /**
     * Open the island-banned-players menu.
     * Used when running the /is ban command.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param targetIsland The island to open the menu for.
     */
    void openIslandBannedPlayers(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Island targetIsland);

    /**
     * Refresh the banned-players menu for a specific island.
     *
     * @param island The island to refresh the menus for.
     */
    void refreshIslandBannedPlayers(Island island);

    /**
     * Open the island-chests menu.
     * Used to open the shared chests menu of an island.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param targetIsland The island to open the shared-chests menu for.
     */
    void openIslandChest(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Island targetIsland);

    /**
     * Refresh the island-chests menu for a specific island.
     *
     * @param island The island to refresh the menus for.
     */
    void refreshIslandChest(Island island);

    /**
     * Get island creation config for specific schematic.
     *
     * @param schematic The schematic to get the creation config for.
     */
    MenuIslandCreationConfig getIslandCreationConfig(Schematic schematic);

    /**
     * Open the islands-creation menu.
     * Used when creating a new island.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param islandName   The desired name of the new island.
     */
    void openIslandCreation(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, String islandName);

    /**
     * Open the rate-menu.
     * Used when giving a rating for an island.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param targetIsland The island to give a rating.
     */
    void openIslandRate(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Island targetIsland);

    /**
     * Open the ratings-menu.
     * Used when checking given ratings of an island.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param targetIsland The island to get ratings from.
     */
    void openIslandRatings(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Island targetIsland);

    /**
     * Refresh the ratings-menu for a specific island.
     *
     * @param island The island to refresh the menus for.
     */
    void refreshIslandRatings(Island island);

    /**
     * Open the member-manage menu.
     * Used when managing an island member.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param islandMember The island member to manage.
     */
    void openMemberManage(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, SuperiorPlayer islandMember);

    /**
     * Destroy the member-manage menus for a specific island member.
     *
     * @param islandMember The island member to close menus of.
     */
    void destroyMemberManage(SuperiorPlayer islandMember);

    /**
     * Used to open the member-role menu.
     * Used when changing a role of an island member.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param islandMember The island member to change role for.
     */
    void openMemberRole(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, SuperiorPlayer islandMember);

    /**
     * Destroy the member-role menus for a specific island member.
     *
     * @param islandMember The island member to close menus of.
     */
    void destroyMemberRole(SuperiorPlayer islandMember);

    /**
     * Open the members-menu.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param targetIsland The island to check the members of.
     */
    void openMembers(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Island targetIsland);

    /**
     * Refresh the members-menu for a specific island.
     *
     * @param island The island to refresh the menus for.
     */
    void refreshMembers(Island island);

    /**
     * Open the missions-menu.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     */
    void openMissions(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu);

    /**
     * Open the missions-menu of a specific category.
     *
     * @param targetPlayer    The player to open the menu for.
     * @param previousMenu    The previous menu that was opened, if exists.
     * @param missionCategory The category to get missions from.
     */
    void openMissionsCategory(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, MissionCategory missionCategory);

    /**
     * Refresh the missions-menu for a specific category.
     *
     * @param missionCategory The category to refresh the menus for.
     */
    void refreshMissionsCategory(MissionCategory missionCategory);

    /**
     * Open the permissions-menu.
     * Used when changing island-permissions of a player on an island.
     *
     * @param targetPlayer      The player to open the menu for.
     * @param previousMenu      The previous menu that was opened, if exists.
     * @param targetIsland      The island to change permissions in.
     * @param permissiblePlayer The player to change permissions for.
     */
    void openPermissions(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu,
                         Island targetIsland, SuperiorPlayer permissiblePlayer);

    /**
     * Open the permissions-menu.
     * Used when changing island-permissions of an island-role on an island.
     *
     * @param targetPlayer    The player to open the menu for.
     * @param previousMenu    The previous menu that was opened, if exists.
     * @param targetIsland    The island to change permissions in.
     * @param permissibleRole The island-role to change permissions for.
     */
    void openPermissions(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu,
                         Island targetIsland, PlayerRole permissibleRole);

    /**
     * Refresh the permissions-menu for a specific island.
     *
     * @param island The island to refresh the menus for.
     */
    void refreshPermissions(Island island);

    /**
     * Refresh the permissions-menu of a player for a specific island.
     *
     * @param island            The island to refresh the menus for.
     * @param permissiblePlayer The player to change permissions.
     */
    void refreshPermissions(Island island, SuperiorPlayer permissiblePlayer);

    /**
     * Refresh the permissions-menu of an island role for a specific island.
     *
     * @param island          The island to refresh the menus for.
     * @param permissibleRole The island role to change permissions for.
     */
    void refreshPermissions(Island island, PlayerRole permissibleRole);

    /**
     * Update the island permission in the menu.
     *
     * @param islandPrivilege The permission to update.
     */
    void updatePermission(IslandPrivilege islandPrivilege);

    /**
     * Open the player-language menu.
     * Used when a player changes his language.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     */
    void openPlayerLanguage(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu);

    /**
     * Open the island-settings menu.
     * Used when changing island-settings.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param targetIsland The island to change settings for.
     */
    void openSettings(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Island targetIsland);

    /**
     * Refresh the island-settings menu for a specific island.
     *
     * @param island The island to refresh the menus for.
     */
    void refreshSettings(Island island);

    /**
     * Update the island settings in the menu.
     *
     * @param islandFlag The settings to update.
     */
    void updateSettings(IslandFlag islandFlag);

    /**
     * Open the upgrades-menu.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param targetIsland The island to get upgrade levels from.
     */
    void openUpgrades(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Island targetIsland);

    /**
     * Refresh the upgrades-menu for a specific island.
     *
     * @param island The island to refresh the menus for.
     */
    void refreshUpgrades(Island island);

}
