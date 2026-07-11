package com.bgsoftware.superiorskyblock.core.menu.button.impl;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandPrivilege;
import com.bgsoftware.superiorskyblock.api.island.PermissionNode;
import com.bgsoftware.superiorskyblock.api.island.PlayerRole;
import com.bgsoftware.superiorskyblock.api.menu.button.MenuTemplateButton;
import com.bgsoftware.superiorskyblock.api.world.GameSound;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.GameSoundImpl;
import com.bgsoftware.superiorskyblock.core.events.plugin.PluginEventsFactory;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.core.itemstack.ItemBuilder;
import com.bgsoftware.superiorskyblock.core.menu.TemplateItem;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractMenuTemplateButton;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractMenuViewButton;
import com.bgsoftware.superiorskyblock.core.menu.button.MenuTemplateButtonImpl;
import com.bgsoftware.superiorskyblock.core.menu.view.impl.IslandPlayerMenuView;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.island.role.SPlayerRole;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Button for permission pairs (and single permissions) inside the new permissions menus.
 * <p>
 * Left-click toggles the {@code leftPermission}.
 * Right-click toggles the {@code rightPrivilege}, if one is set.
 * <p>
 * When {@code requireMemberRole} is {@code true} (Book 2 / Commands menu), the toggle
 * is blocked if the permissible player's role weight is below the default role (Member).
 * <p>
 * The button item is taken from the menu layout (the {@code items.<char>} section in the
 * yaml). The following placeholders can be used in the name/lore:
 * <ul>
 *     <li>{@code {left-name}} - capitalised name of the left privilege</li>
 *     <li>{@code {left-state}} - {@code &aENABLED} or {@code &cDISABLED}</li>
 *     <li>{@code {right-name}} - capitalised name of the right privilege</li>
 *     <li>{@code {right-state}} - {@code &aENABLED} or {@code &cDISABLED}</li>
 * </ul>
 */
public class PermissionTogglePairButton extends AbstractMenuViewButton<IslandPlayerMenuView> {

    private PermissionTogglePairButton(AbstractMenuTemplateButton<IslandPlayerMenuView> templateButton,
                                       IslandPlayerMenuView menuView) {
        super(templateButton, menuView);
    }

    @Override
    public Template getTemplate() {
        return (Template) super.getTemplate();
    }

    @Override
    public ItemStack createViewItem() {
        Template template = getTemplate();
        Island island = menuView.getIsland();
        SuperiorPlayer permissiblePlayer = menuView.getSuperiorPlayer();

        IslandPrivilege leftPrivilege = template.leftPrivilege;
        IslandPrivilege rightPrivilege = template.rightPrivilege;

        // If permissiblePlayer is null (e.g. editing by role, not player), use Guest role as fallback
        boolean leftEnabled = false;
        boolean rightEnabled = false;
        if (leftPrivilege != null) {
            if (permissiblePlayer != null) {
                leftEnabled = island.getPermissionNode(permissiblePlayer).hasPermission(leftPrivilege);
            } else {
                leftEnabled = false; // default disabled when no player context
            }
        }
        if (rightPrivilege != null) {
            if (permissiblePlayer != null) {
                rightEnabled = island.getPermissionNode(permissiblePlayer).hasPermission(rightPrivilege);
            } else {
                rightEnabled = false;
            }
        }

        TemplateItem buttonTemplateItem = template.getButtonTemplateItem();
        if (buttonTemplateItem == null)
            return new ItemStack(Material.AIR);

        ItemBuilder builder = buttonTemplateItem.getBuilder();

        if (leftPrivilege != null) {
            builder.replaceAll("{left-name}", Formatters.CAPITALIZED_FORMATTER.format(leftPrivilege.getName()));
            builder.replaceAll("{left-state}", leftEnabled ? "&aENABLED" : "&cDISABLED");
        }

        if (rightPrivilege != null) {
            builder.replaceAll("{right-name}", Formatters.CAPITALIZED_FORMATTER.format(rightPrivilege.getName()));
            builder.replaceAll("{right-state}", rightEnabled ? "&aENABLED" : "&cDISABLED");
        }

        return builder.build(menuView.getInventoryViewer());
    }

    @Override
    public void onButtonClick(InventoryClickEvent clickEvent) {
        Template template = getTemplate();
        Island island = menuView.getIsland();
        SuperiorPlayer inventoryViewer = menuView.getInventoryViewer();
        SuperiorPlayer permissiblePlayer = menuView.getSuperiorPlayer();

        IslandPrivilege targetPrivilege;
        if (clickEvent.getClick() != null && clickEvent.getClick().isRightClick()) {
            targetPrivilege = template.rightPrivilege;
        } else {
            targetPrivilege = template.leftPrivilege;
        }

        if (targetPrivilege == null)
            return;

        // Book 2 (commands) requires the target to be a Member or higher.
        if (template.requireMemberRole) {
            PlayerRole permissibleRole = permissiblePlayer.getPlayerRole();
            if (permissibleRole == null || permissibleRole.getWeight() < SPlayerRole.defaultRole().getWeight()) {
                Message.LACK_CHANGE_PERMISSION.send(inventoryViewer);
                GameSoundImpl.playSound(clickEvent.getWhoClicked(), template.getLackPermissionSound());
                return;
            }
        }

        PermissionNode permissionNode = island.getPermissionNode(permissiblePlayer);
        boolean currentValue = permissionNode.hasPermission(targetPrivilege);

        if (!PluginEventsFactory.callIslandChangePlayerPrivilegeEvent(island, inventoryViewer,
                permissiblePlayer, !currentValue))
            return;

        island.setPermission(permissiblePlayer, targetPrivilege, !currentValue);

        Message.UPDATED_PERMISSION.send(inventoryViewer,
                Formatters.CAPITALIZED_FORMATTER.format(targetPrivilege.getName()));

        GameSoundImpl.playSound(clickEvent.getWhoClicked(), template.getClickSound());

        List<String> accessCommands = template.getClickCommands();
        if (accessCommands != null && !accessCommands.isEmpty()) {
            accessCommands.forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    command.replace("%player%", inventoryViewer.getName())));
        }

        menuView.refreshView();
    }

    public static class Builder extends AbstractMenuTemplateButton.AbstractBuilder<IslandPlayerMenuView> {

        private IslandPrivilege leftPrivilege;
        private IslandPrivilege rightPrivilege;
        private boolean requireMemberRole;

        public Builder setLeftPrivilege(IslandPrivilege leftPrivilege) {
            this.leftPrivilege = leftPrivilege;
            return this;
        }

        public Builder setRightPrivilege(@Nullable IslandPrivilege rightPrivilege) {
            this.rightPrivilege = rightPrivilege;
            return this;
        }

        public Builder setRequireMemberRole(boolean requireMemberRole) {
            this.requireMemberRole = requireMemberRole;
            return this;
        }

        @Override
        public MenuTemplateButton<IslandPlayerMenuView> build() {
            return new Template(buttonItem, clickSound, commands, requiredPermission, lackPermissionSound,
                    leftPrivilege, rightPrivilege, requireMemberRole);
        }

    }

    public static class Template extends MenuTemplateButtonImpl<IslandPlayerMenuView> {

        private final IslandPrivilege leftPrivilege;
        @Nullable
        private final IslandPrivilege rightPrivilege;
        private final boolean requireMemberRole;

        Template(@Nullable TemplateItem buttonItem, @Nullable GameSound clickSound, @Nullable List<String> commands,
                 @Nullable String requiredPermission, @Nullable GameSound lackPermissionSound,
                 IslandPrivilege leftPrivilege, @Nullable IslandPrivilege rightPrivilege,
                 boolean requireMemberRole) {
            super(buttonItem, clickSound, commands, requiredPermission, lackPermissionSound,
                    PermissionTogglePairButton.class, PermissionTogglePairButton::new);
            this.leftPrivilege = Objects.requireNonNull(leftPrivilege, "leftPrivilege cannot be null");
            this.rightPrivilege = rightPrivilege;
            this.requireMemberRole = requireMemberRole;
        }

    }

}
