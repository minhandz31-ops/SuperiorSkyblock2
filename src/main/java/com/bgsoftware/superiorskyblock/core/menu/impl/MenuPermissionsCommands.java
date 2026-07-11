package com.bgsoftware.superiorskyblock.core.menu.impl;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandPrivilege;
import com.bgsoftware.superiorskyblock.api.island.PermissionNode;
import com.bgsoftware.superiorskyblock.api.island.PlayerRole;
import com.bgsoftware.superiorskyblock.api.menu.Menu;
import com.bgsoftware.superiorskyblock.api.menu.button.MenuTemplateButton;
import com.bgsoftware.superiorskyblock.api.menu.layout.MenuLayout;
import com.bgsoftware.superiorskyblock.api.menu.view.MenuView;
import com.bgsoftware.superiorskyblock.api.menu.view.ViewArgs;
import com.bgsoftware.superiorskyblock.api.world.GameSound;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.GameSoundImpl;
import com.bgsoftware.superiorskyblock.core.events.plugin.PluginEventsFactory;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.core.io.MenuParserImpl;
import com.bgsoftware.superiorskyblock.core.itemstack.ItemBuilder;
import com.bgsoftware.superiorskyblock.core.menu.AbstractMenu;
import com.bgsoftware.superiorskyblock.core.menu.MenuIdentifiers;
import com.bgsoftware.superiorskyblock.core.menu.MenuParseResult;
import com.bgsoftware.superiorskyblock.core.menu.MenuPatternSlots;
import com.bgsoftware.superiorskyblock.core.menu.TemplateItem;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractMenuTemplateButton;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractMenuViewButton;
import com.bgsoftware.superiorskyblock.core.menu.button.MenuTemplateButtonImpl;
import com.bgsoftware.superiorskyblock.core.menu.button.impl.PermissionCategoryButton;
import com.bgsoftware.superiorskyblock.core.menu.view.AbstractMenuView;
import com.bgsoftware.superiorskyblock.core.menu.view.args.IslandPlayerViewArgs;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.island.privilege.IslandPrivileges;
import com.bgsoftware.superiorskyblock.island.role.SPlayerRole;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Book 2 sub-menu - command &amp; management permissions.
 * <p>
 * A 27-slot chest menu that operates in two modes:
 * <ul>
 *     <li><b>Root mode</b> ({@code category == null}) - displays 3 paper items, one per
 *         {@link PermissionCategory}. Clicking a paper reopens this menu with the
 *         category set.</li>
 *     <li><b>Category mode</b> ({@code category != null}) - displays the permissions
 *         belonging to the selected category as toggle buttons. All toggles require
 *         the permissible player to be Member or higher.</li>
 * </ul>
 * Hidden permissions (default for Coop) are never shown in this menu.
 */
public class MenuPermissionsCommands extends AbstractMenu<MenuPermissionsCommands.View, MenuPermissionsCommands.Args> {

    private static final List<IslandPrivilege> ISLAND_MANAGEMENT_PRIVILEGES = Collections.unmodifiableList(Arrays.asList(
            IslandPrivileges.INVITE_MEMBER,
            IslandPrivileges.KICK_MEMBER,
            IslandPrivileges.BAN_MEMBER,
            IslandPrivileges.EXPEL_PLAYERS,
            IslandPrivileges.COOP_MEMBER,
            IslandPrivileges.UNCOOP_MEMBER,
            IslandPrivileges.PROMOTE_MEMBERS,
            IslandPrivileges.DEMOTE_MEMBERS
    ));

    private static final List<IslandPrivilege> ISLAND_SETTINGS_PRIVILEGES = Collections.unmodifiableList(Arrays.asList(
            IslandPrivileges.CHANGE_NAME,
            IslandPrivileges.SET_HOME,
            IslandPrivileges.CLOSE_ISLAND,
            IslandPrivileges.OPEN_ISLAND,
            IslandPrivileges.CLOSE_BYPASS,
            IslandPrivileges.SET_DISCORD,
            IslandPrivileges.DISCORD_SHOW,
            IslandPrivileges.RATINGS_SHOW
    ));

    private static final List<IslandPrivilege> RED_FLAG_PRIVILEGES = Collections.unmodifiableList(Arrays.asList(
            IslandPrivileges.DISBAND_ISLAND,
            IslandPrivileges.SET_ROLE,
            IslandPrivileges.SET_PERMISSION,
            IslandPrivileges.SET_SETTINGS,
            IslandPrivileges.ALL
    ));

    private MenuPermissionsCommands(MenuParseResult<View> parseResult) {
        super(MenuIdentifiers.MENU_PERMISSIONS_COMMANDS, parseResult);
    }

    @Override
    protected View createViewInternal(SuperiorPlayer superiorPlayer, Args args,
                                      @Nullable MenuView<?, ?> previousMenuView) {
        return new View(superiorPlayer, previousMenuView, this, args);
    }

    public void refreshViews(Island island) {
        refreshViews(view -> view.getIsland().equals(island));
    }

    /**
     * Get the privilege at the given slot index for a category.
     *
     * @param category  The selected category. {@code null} is treated as no category (returns {@code null}).
     * @param slotIndex The 0-based slot index within the category layout.
     * @return The privilege at that slot, or {@code null} if there is no privilege at that slot.
     */
    @Nullable
    public static IslandPrivilege getPrivilegeForSlot(@Nullable PermissionCategory category, int slotIndex) {
        if (category == null || slotIndex < 0)
            return null;

        List<IslandPrivilege> privileges;
        switch (category) {
            case ISLAND_MANAGEMENT:
                privileges = ISLAND_MANAGEMENT_PRIVILEGES;
                break;
            case ISLAND_SETTINGS:
                privileges = ISLAND_SETTINGS_PRIVILEGES;
                break;
            case RED_FLAG:
                privileges = RED_FLAG_PRIVILEGES;
                break;
            default:
                return null;
        }

        return slotIndex < privileges.size() ? privileges.get(slotIndex) : null;
    }

    @Nullable
    public static MenuPermissionsCommands createInstance() {
        MenuParseResult<View> menuParseResult = MenuParserImpl.getInstance().loadMenu("permissions-commands.yml",
                MenuPermissionsCommands::convertOldGUI);

        if (menuParseResult == null) {
            return null;
        }

        MenuPatternSlots menuPatternSlots = menuParseResult.getPatternSlots();
        YamlConfiguration cfg = menuParseResult.getConfig();
        MenuLayout.Builder<View> patternBuilder = menuParseResult.getLayoutBuilder();

        // Category paper items (only displayed when no category is selected).
        patternBuilder.mapButtons(MenuParserImpl.getInstance().parseButtonSlots(cfg, "category-management", menuPatternSlots),
                new PermissionCategoryButton.Builder().setCategory(PermissionCategory.ISLAND_MANAGEMENT));
        patternBuilder.mapButtons(MenuParserImpl.getInstance().parseButtonSlots(cfg, "category-settings", menuPatternSlots),
                new PermissionCategoryButton.Builder().setCategory(PermissionCategory.ISLAND_SETTINGS));
        patternBuilder.mapButtons(MenuParserImpl.getInstance().parseButtonSlots(cfg, "category-red-flag", menuPatternSlots),
                new PermissionCategoryButton.Builder().setCategory(PermissionCategory.RED_FLAG));

        // Permission toggle slots (only displayed when a category is selected).
        // Each slot is mapped to a PermissionSlotButton that knows its slot index. The button
        // dynamically resolves the privilege to display based on the view's category.
        List<Integer> permissionSlots = MenuParserImpl.getInstance().parseButtonSlots(cfg, "permission-slots", menuPatternSlots);
        for (int i = 0; i < permissionSlots.size(); i++) {
            patternBuilder.mapButton(permissionSlots.get(i),
                    new PermissionSlotButton.Builder().setSlotIndex(i));
        }

        return new MenuPermissionsCommands(menuParseResult);
    }

    private static boolean convertOldGUI(SuperiorSkyblockPlugin plugin, YamlConfiguration newMenu) {
        File oldFile = new File(plugin.getDataFolder(), "guis/permissions-gui.yml");

        if (!oldFile.exists())
            return false;

        return false;
    }

    public enum PermissionCategory {
        ISLAND_MANAGEMENT,
        ISLAND_SETTINGS,
        RED_FLAG
    }

    public static class Args implements ViewArgs {

        private final Island island;
        private final SuperiorPlayer permissiblePlayer;
        @Nullable
        private final PermissionCategory category;

        public Args(Island island, SuperiorPlayer permissiblePlayer, @Nullable PermissionCategory category) {
            this.island = island;
            this.permissiblePlayer = permissiblePlayer;
            this.category = category;
        }

        // Convenience constructor used when reopening from the main permissions menu (root mode).
        public Args(Island island, SuperiorPlayer permissiblePlayer) {
            this(island, permissiblePlayer, null);
        }

        public Island getIsland() {
            return island;
        }

        public SuperiorPlayer getPermissiblePlayer() {
            return permissiblePlayer;
        }

        @Nullable
        public PermissionCategory getCategory() {
            return category;
        }

    }

    public static class View extends AbstractMenuView<View, Args> {

        private final Island island;
        private final SuperiorPlayer permissiblePlayer;
        @Nullable
        private final PermissionCategory category;

        View(SuperiorPlayer inventoryViewer, @Nullable MenuView<?, ?> previousMenuView,
             Menu<View, Args> menu, Args args) {
            super(inventoryViewer, previousMenuView, menu);
            this.island = args.getIsland();
            this.permissiblePlayer = args.getPermissiblePlayer();
            this.category = args.getCategory();
        }

        public Island getIsland() {
            return island;
        }

        public SuperiorPlayer getSuperiorPlayer() {
            return permissiblePlayer;
        }

        @Nullable
        public PermissionCategory getCategory() {
            return category;
        }

        @Override
        public String replaceTitle(String title) {
            return permissiblePlayer != null ? title.replace("{}", permissiblePlayer.getName()) : title;
        }

    }

    /**
     * Button for the permission toggle slots inside the commands sub-menu.
     * <p>
     * The button is bound to a slot index rather than a specific privilege. The privilege
     * is resolved dynamically based on the view's currently selected category and the slot
     * index, using {@link #getPrivilegeForSlot(PermissionCategory, int)}.
     * <p>
     * In root mode (category is {@code null}) the button renders as air.
     */
    private static class PermissionSlotButton extends AbstractMenuViewButton<View> {

        private PermissionSlotButton(AbstractMenuTemplateButton<View> templateButton, View menuView) {
            super(templateButton, menuView);
        }

        @Override
        public Template getTemplate() {
            return (Template) super.getTemplate();
        }

        @Override
        public ItemStack createViewItem() {
            PermissionCategory category = menuView.getCategory();
            if (category == null)
                return new ItemStack(Material.AIR);

            IslandPrivilege privilege = getPrivilegeForSlot(category, getTemplate().slotIndex);
            if (privilege == null)
                return new ItemStack(Material.AIR);

            Island island = menuView.getIsland();
            SuperiorPlayer permissiblePlayer = menuView.getSuperiorPlayer();

            boolean enabled = island.getPermissionNode(permissiblePlayer).hasPermission(privilege);

            TemplateItem buttonTemplateItem = getTemplate().getButtonTemplateItem();
            if (buttonTemplateItem == null)
                return new ItemStack(Material.AIR);

            ItemBuilder builder = buttonTemplateItem.getBuilder();
            builder.replaceAll("{name}", Formatters.CAPITALIZED_FORMATTER.format(privilege.getName()));
            builder.replaceAll("{state}", enabled ? "&aENABLED" : "&cDISABLED");

            return builder.build(menuView.getInventoryViewer());
        }

        @Override
        public void onButtonClick(InventoryClickEvent clickEvent) {
            PermissionCategory category = menuView.getCategory();
            if (category == null)
                return;

            IslandPrivilege privilege = getPrivilegeForSlot(category, getTemplate().slotIndex);
            if (privilege == null)
                return;

            Island island = menuView.getIsland();
            SuperiorPlayer inventoryViewer = menuView.getInventoryViewer();
            SuperiorPlayer permissiblePlayer = menuView.getSuperiorPlayer();

            // Book 2 (commands) requires the target to be a Member or higher.
            PlayerRole permissibleRole = permissiblePlayer.getPlayerRole();
            if (permissibleRole == null || permissibleRole.getWeight() < SPlayerRole.defaultRole().getWeight()) {
                Message.LACK_CHANGE_PERMISSION.send(inventoryViewer);
                GameSoundImpl.playSound(clickEvent.getWhoClicked(), getTemplate().getLackPermissionSound());
                return;
            }

            PermissionNode permissionNode = island.getPermissionNode(permissiblePlayer);
            boolean currentValue = permissionNode.hasPermission(privilege);

            if (!PluginEventsFactory.callIslandChangePlayerPrivilegeEvent(island, inventoryViewer,
                    permissiblePlayer, !currentValue))
                return;

            island.setPermission(permissiblePlayer, privilege, !currentValue);

            Message.UPDATED_PERMISSION.send(inventoryViewer,
                    Formatters.CAPITALIZED_FORMATTER.format(privilege.getName()));

            GameSoundImpl.playSound(clickEvent.getWhoClicked(), getTemplate().getClickSound());

            List<String> accessCommands = getTemplate().getClickCommands();
            if (accessCommands != null && !accessCommands.isEmpty()) {
                accessCommands.forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        command.replace("%player%", inventoryViewer.getName())));
            }

            menuView.refreshView();
        }

        public static class Builder extends AbstractMenuTemplateButton.AbstractBuilder<View> {

            private int slotIndex = -1;

            public Builder setSlotIndex(int slotIndex) {
                this.slotIndex = slotIndex;
                return this;
            }

            @Override
            public MenuTemplateButton<View> build() {
                return new Template(buttonItem, clickSound, commands, requiredPermission, lackPermissionSound, slotIndex);
            }

        }

        public static class Template extends MenuTemplateButtonImpl<View> {

            private final int slotIndex;

            Template(@Nullable TemplateItem buttonItem, @Nullable GameSound clickSound, @Nullable List<String> commands,
                     @Nullable String requiredPermission, @Nullable GameSound lackPermissionSound, int slotIndex) {
                super(buttonItem, clickSound, commands, requiredPermission, lackPermissionSound,
                        PermissionSlotButton.class, PermissionSlotButton::new);
                this.slotIndex = slotIndex;
                if (slotIndex < 0)
                    throw new IllegalArgumentException("slotIndex cannot be negative.");
            }

        }

    }

}
