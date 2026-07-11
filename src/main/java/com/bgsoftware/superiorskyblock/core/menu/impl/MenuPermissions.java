package com.bgsoftware.superiorskyblock.core.menu.impl;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.menu.layout.MenuLayout;
import com.bgsoftware.superiorskyblock.api.menu.view.MenuView;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.io.MenuParserImpl;
import com.bgsoftware.superiorskyblock.core.menu.AbstractMenu;
import com.bgsoftware.superiorskyblock.core.menu.MenuIdentifiers;
import com.bgsoftware.superiorskyblock.core.menu.MenuParseResult;
import com.bgsoftware.superiorskyblock.core.menu.MenuPatternSlots;
import com.bgsoftware.superiorskyblock.core.menu.button.impl.PermissionBookButton;
import com.bgsoftware.superiorskyblock.core.menu.view.impl.IslandPlayerMenuView;
import com.bgsoftware.superiorskyblock.core.menu.view.args.IslandPlayerViewArgs;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/**
 * Main permissions menu (Book 1 selection).
 * <p>
 * A simple 27-slot chest menu containing two enchanted books:
 * <ul>
 *     <li>Slot 12 - {@code Physical Permissions} (opens {@link MenuPermissionsPhysical})</li>
 *     <li>Slot 14 - {@code Command &amp; Management} (opens {@link MenuPermissionsCommands})</li>
 * </ul>
 * The rest of the slots are filled with black stained-glass panes.
 */
public class MenuPermissions extends AbstractMenu<IslandPlayerMenuView, IslandPlayerViewArgs> {

    private MenuPermissions(MenuParseResult<IslandPlayerMenuView> parseResult) {
        super(MenuIdentifiers.MENU_PERMISSIONS, parseResult);
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
    public static MenuPermissions createInstance() {
        MenuParseResult<IslandPlayerMenuView> menuParseResult = MenuParserImpl.getInstance().loadMenu("permissions.yml",
                MenuPermissions::convertOldGUI);

        if (menuParseResult == null) {
            return null;
        }

        MenuPatternSlots menuPatternSlots = menuParseResult.getPatternSlots();
        YamlConfiguration cfg = menuParseResult.getConfig();
        MenuLayout.Builder<IslandPlayerMenuView> patternBuilder = menuParseResult.getLayoutBuilder();

        patternBuilder.mapButtons(MenuParserImpl.getInstance().parseButtonSlots(cfg, "physical-book", menuPatternSlots),
                new PermissionBookButton.Builder().setTargetMenu(PermissionBookButton.TargetMenu.PHYSICAL));
        patternBuilder.mapButtons(MenuParserImpl.getInstance().parseButtonSlots(cfg, "commands-book", menuPatternSlots),
                new PermissionBookButton.Builder().setTargetMenu(PermissionBookButton.TargetMenu.COMMANDS));

        return new MenuPermissions(menuParseResult);
    }

    private static boolean convertOldGUI(SuperiorSkyblockPlugin plugin, YamlConfiguration newMenu) {
        File oldFile = new File(plugin.getDataFolder(), "guis/permissions-gui.yml");

        if (!oldFile.exists())
            return false;

        // Old GUI files are no longer supported - we keep the new 27-slot 2-book layout.
        return false;
    }

}
