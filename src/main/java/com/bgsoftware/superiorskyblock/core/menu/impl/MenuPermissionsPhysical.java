package com.bgsoftware.superiorskyblock.core.menu.impl;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandPrivilege;
import com.bgsoftware.superiorskyblock.api.menu.layout.MenuLayout;
import com.bgsoftware.superiorskyblock.api.menu.view.MenuView;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.io.MenuParserImpl;
import com.bgsoftware.superiorskyblock.core.menu.AbstractMenu;
import com.bgsoftware.superiorskyblock.core.menu.MenuIdentifiers;
import com.bgsoftware.superiorskyblock.core.menu.MenuParseResult;
import com.bgsoftware.superiorskyblock.core.menu.MenuPatternSlots;
import com.bgsoftware.superiorskyblock.core.menu.button.impl.PermissionTogglePairButton;
import com.bgsoftware.superiorskyblock.core.menu.view.impl.IslandPlayerMenuView;
import com.bgsoftware.superiorskyblock.core.menu.view.args.IslandPlayerViewArgs;
import com.bgsoftware.superiorskyblock.island.privilege.IslandPrivileges;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/**
 * Book 1 sub-menu - physical permission categories.
 * <p>
 * A 27-slot chest menu containing 4 paper items that act as toggle buttons
 * for permission pairs:
 * <ul>
 *     <li>Break     - {@code BREAK} (left) and {@code SPAWNER_BREAK} (right)</li>
 *     <li>Build     - {@code BUILD} (left only)</li>
 *     <li>Chest     - {@code CHEST_ACCESS} (left) and {@code ISLAND_CHEST} (right)</li>
 *     <li>Drop/Pickup - {@code DROP_ITEMS} (left) and {@code PICKUP_DROPS} (right)</li>
 * </ul>
 * The remaining slots are filled with black stained-glass panes.
 */
public class MenuPermissionsPhysical extends AbstractMenu<IslandPlayerMenuView, IslandPlayerViewArgs> {

    private MenuPermissionsPhysical(MenuParseResult<IslandPlayerMenuView> parseResult) {
        super(MenuIdentifiers.MENU_PERMISSIONS_PHYSICAL, parseResult);
    }

    @Override
    protected IslandPlayerMenuView createViewInternal(SuperiorPlayer superiorPlayer, IslandPlayerViewArgs args,
                                                      @Nullable MenuView<?, ?> previousMenuView) {
        return new IslandPlayerMenuView(superiorPlayer, previousMenuView, this, args);
    }

    public void refreshViews(Island island) {
        refreshViews(view -> view.getIsland().equals(island));
    }

    @Nullable
    public static MenuPermissionsPhysical createInstance() {
        // Ensure the CHEST_ACCESS privilege is registered - it is normally registered
        // lazily by the interactables configuration, but we need it for the chest toggle.
        try {
            IslandPrivilege.register("CHEST_ACCESS");
        } catch (IllegalStateException ignored) {
            // Already registered.
        }

        MenuParseResult<IslandPlayerMenuView> menuParseResult = MenuParserImpl.getInstance().loadMenu("permissions-physical.yml",
                MenuPermissionsPhysical::convertOldGUI);

        if (menuParseResult == null) {
            return null;
        }

        MenuPatternSlots menuPatternSlots = menuParseResult.getPatternSlots();
        YamlConfiguration cfg = menuParseResult.getConfig();
        MenuLayout.Builder<IslandPlayerMenuView> patternBuilder = menuParseResult.getLayoutBuilder();

        // Break - BREAK (left) + SPAWNER_BREAK (right)
        patternBuilder.mapButtons(MenuParserImpl.getInstance().parseButtonSlots(cfg, "break", menuPatternSlots),
                new PermissionTogglePairButton.Builder()
                        .setLeftPrivilege(IslandPrivileges.BREAK)
                        .setRightPrivilege(IslandPrivileges.SPAWNER_BREAK));

        // Build - BUILD (left only)
        patternBuilder.mapButtons(MenuParserImpl.getInstance().parseButtonSlots(cfg, "build", menuPatternSlots),
                new PermissionTogglePairButton.Builder()
                        .setLeftPrivilege(IslandPrivileges.BUILD));

        // Chest - CHEST_ACCESS (left) + ISLAND_CHEST (right)
        patternBuilder.mapButtons(MenuParserImpl.getInstance().parseButtonSlots(cfg, "chest", menuPatternSlots),
                new PermissionTogglePairButton.Builder()
                        .setLeftPrivilege(IslandPrivilege.getByName("CHEST_ACCESS"))
                        .setRightPrivilege(IslandPrivileges.ISLAND_CHEST));

        // Drop/Pickup - DROP_ITEMS (left) + PICKUP_DROPS (right)
        patternBuilder.mapButtons(MenuParserImpl.getInstance().parseButtonSlots(cfg, "drop-pickup", menuPatternSlots),
                new PermissionTogglePairButton.Builder()
                        .setLeftPrivilege(IslandPrivileges.DROP_ITEMS)
                        .setRightPrivilege(IslandPrivileges.PICKUP_DROPS));

        return new MenuPermissionsPhysical(menuParseResult);
    }

    private static boolean convertOldGUI(SuperiorSkyblockPlugin plugin, YamlConfiguration newMenu) {
        File oldFile = new File(plugin.getDataFolder(), "guis/permissions-gui.yml");

        if (!oldFile.exists())
            return false;

        return false;
    }

}
