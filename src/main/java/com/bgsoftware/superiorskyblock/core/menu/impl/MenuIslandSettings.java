package com.bgsoftware.superiorskyblock.core.menu.impl;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandFlag;
import com.bgsoftware.superiorskyblock.api.menu.layout.MenuLayout;
import com.bgsoftware.superiorskyblock.api.menu.view.MenuView;
import com.bgsoftware.superiorskyblock.api.world.GameSound;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.io.MenuParserImpl;
import com.bgsoftware.superiorskyblock.core.menu.AbstractMenu;
import com.bgsoftware.superiorskyblock.core.menu.MenuIdentifiers;
import com.bgsoftware.superiorskyblock.core.menu.MenuParseResult;
import com.bgsoftware.superiorskyblock.core.menu.MenuPatternSlots;
import com.bgsoftware.superiorskyblock.core.menu.TemplateItem;
import com.bgsoftware.superiorskyblock.core.menu.button.impl.IslandFlagPairButton;
import com.bgsoftware.superiorskyblock.core.menu.converter.MenuConverter;
import com.bgsoftware.superiorskyblock.core.menu.layout.AbstractMenuLayout;
import com.bgsoftware.superiorskyblock.core.menu.view.IIslandMenuView;
import com.bgsoftware.superiorskyblock.core.menu.view.impl.IslandMenuView;
import com.bgsoftware.superiorskyblock.core.menu.view.args.IslandViewArgs;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/**
 * New Island Settings menu - 27-slot chest with 7 pair-flag buttons.
 * Replaces old paged MenuIslandFlags.
 *
 * Each button handles a pair of flags (left-click = flag1, right-click = flag2).
 * Layout defined in settings.yml with fixed pattern, not paged.
 */
public class MenuIslandSettings extends AbstractMenu<IslandMenuView, IslandViewArgs> {

    private MenuIslandSettings(MenuParseResult<IslandMenuView> parseResult) {
        super(MenuIdentifiers.MENU_ISLAND_FLAGS, parseResult);
    }

    @Override
    protected IslandMenuView createViewInternal(SuperiorPlayer superiorPlayer, IslandViewArgs args,
                                      @Nullable MenuView<?, ?> previousMenuView) {
        return new IslandMenuView(superiorPlayer, previousMenuView, this, args);
    }

    public void refreshViews(Island island) {
        refreshViews(view -> view.getIsland().equals(island));
    }

    @Nullable
    public static MenuIslandSettings createInstance() {
        MenuParseResult<IslandMenuView> menuParseResult = MenuParserImpl.getInstance().loadMenu("settings.yml",
                MenuIslandSettings::convertOldGUI);

        if (menuParseResult == null) {
            return null;
        }

        MenuPatternSlots menuPatternSlots = menuParseResult.getPatternSlots();
        YamlConfiguration cfg = menuParseResult.getConfig();
        MenuLayout.Builder<IslandMenuView> patternBuilder = menuParseResult.getLayoutBuilder();

        // Load each pair-flag button from the settings section
        ConfigurationSection settingsSection = cfg.getConfigurationSection("settings");
        if (settingsSection != null) {
            for (String pairKey : settingsSection.getKeys(false)) {
                ConfigurationSection flagSection = settingsSection.getConfigurationSection(pairKey);
                if (flagSection == null || !flagSection.getBoolean("display-menu", true))
                    continue;

                // pairKey format: "LEFT_FLAG|RIGHT_FLAG"
                String[] flags = pairKey.split("\\|");
                IslandFlag leftFlag = flags.length >= 1 ? IslandFlag.getByName(flags[0]) : null;
                IslandFlag rightFlag = flags.length >= 2 ? IslandFlag.getByName(flags[1]) : null;

                TemplateItem enabledItem = MenuParserImpl.getInstance().getItemStack("menus/settings.yml",
                        flagSection.getConfigurationSection("settings-enabled"));
                TemplateItem disabledItem = MenuParserImpl.getInstance().getItemStack("menus/settings.yml",
                        flagSection.getConfigurationSection("settings-disabled"));
                GameSound clickSound = MenuParserImpl.getInstance().getSound(flagSection.getConfigurationSection("sound"));

                // Find slot for this pair - use the 'slots' char from pattern
                // All 7 pairs share the same slot char '@', they're mapped sequentially
                // Actually we need individual slots - use the pair index as slot position
                // The pattern has 7 '@' chars in slots 11-17
                // We map each pair to its corresponding slot
                IslandFlagPairButton.Builder builder = new IslandFlagPairButton.Builder()
                        .setLeftFlag(leftFlag)
                        .setRightFlag(rightFlag)
                        .setEnabledItem(enabledItem)
                        .setDisabledItem(disabledItem)
                        .setClickSound(clickSound);

                // Map to slots - the pattern char '@' appears 7 times (slots 11-17)
                // getMenuButtonSlots returns them in order
                patternBuilder.mapButtons(
                        MenuParserImpl.getInstance().parseButtonSlots(cfg, "slots", menuPatternSlots),
                        builder);
            }
        }

        return new MenuIslandSettings(menuParseResult);
    }

    private static boolean convertOldGUI(SuperiorSkyblockPlugin plugin, YamlConfiguration newMenu) {
        File oldFile = new File(plugin.getDataFolder(), "guis/settings-gui.yml");
        return oldFile.exists();
    }

}
