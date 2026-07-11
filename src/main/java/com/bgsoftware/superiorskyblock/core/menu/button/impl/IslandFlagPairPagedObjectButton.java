package com.bgsoftware.superiorskyblock.core.menu.button.impl;

import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandFlag;
import com.bgsoftware.superiorskyblock.api.menu.button.MenuTemplateButton;
import com.bgsoftware.superiorskyblock.api.menu.button.PagedMenuTemplateButton;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.GameSoundImpl;
import com.bgsoftware.superiorskyblock.core.events.plugin.PluginEventsFactory;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractPagedMenuButton;
import com.bgsoftware.superiorskyblock.core.menu.button.PagedMenuTemplateButtonImpl;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuIslandFlags;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Button that handles a PAIR of island flags.
 * Left-click toggles the first flag (leftFlag).
 * Right-click toggles the second flag (rightFlag).
 * Bumping again disables the active flag.
 */
public class IslandFlagPairPagedObjectButton extends AbstractPagedMenuButton<MenuIslandFlags.View, MenuIslandFlags.IslandFlagInfo> {

    private IslandFlagPairPagedObjectButton(MenuTemplateButton<MenuIslandFlags.View> templateButton, MenuIslandFlags.View menuView) {
        super(templateButton, menuView);
    }

    @Override
    public void onButtonClick(InventoryClickEvent clickEvent) {
        SuperiorPlayer inventoryViewer = menuView.getInventoryViewer();
        Island island = menuView.getIsland();
        IslandFlag islandFlag = pagedObject.getIslandFlag();

        if (islandFlag == null)
            return;

        // Left-click: toggle leftFlag (first flag of the pair)
        // Right-click: toggle rightFlag (second flag of the pair)
        boolean isLeftClick = clickEvent.isLeftClick();

        // The pair logic: islandFlagName is stored as "leftFlag|rightFlag"
        // We parse it and toggle the appropriate one
        String flagName = pagedObject.getIslandFlagName();
        String[] flags = flagName.split("\\|");

        IslandFlag targetFlag;
        if (isLeftClick && flags.length >= 1) {
            targetFlag = IslandFlag.getByName(flags[0]);
        } else if (!isLeftClick && flags.length >= 2) {
            targetFlag = IslandFlag.getByName(flags[1]);
        } else {
            targetFlag = islandFlag;
        }

        if (targetFlag == null)
            return;

        if (island.hasSettingsEnabled(targetFlag)) {
            if (!PluginEventsFactory.callIslandDisableFlagEvent(island, inventoryViewer, targetFlag))
                return;
            island.disableSettings(targetFlag);
        } else {
            if (!PluginEventsFactory.callIslandEnableFlagEvent(island, inventoryViewer, targetFlag))
                return;
            island.enableSettings(targetFlag);
        }

        GameSoundImpl.playSound(clickEvent.getWhoClicked(), pagedObject.getClickSound());
        Message.UPDATED_SETTINGS.send(inventoryViewer, Formatters.CAPITALIZED_FORMATTER.format(targetFlag.getName()));
        menuView.refreshView();
    }

    @Override
    public ItemStack modifyViewItem(ItemStack buttonItem) {
        SuperiorPlayer inventoryViewer = menuView.getInventoryViewer();
        Island island = menuView.getIsland();

        // Check if ANY flag in the pair is enabled
        String flagName = pagedObject.getIslandFlagName();
        String[] flags = flagName.split("\\|");
        boolean anyEnabled = false;
        for (String f : flags) {
            IslandFlag flag = IslandFlag.getByName(f);
            if (flag != null && island.hasSettingsEnabled(flag)) {
                anyEnabled = true;
                break;
            }
        }

        return anyEnabled ?
                pagedObject.getEnabledIslandFlagItem().build(inventoryViewer) :
                pagedObject.getDisabledIslandFlagItem().build(inventoryViewer);
    }

    public static class Builder extends PagedMenuTemplateButtonImpl.AbstractBuilder<MenuIslandFlags.View, MenuIslandFlags.IslandFlagInfo> {

        @Override
        public PagedMenuTemplateButton<MenuIslandFlags.View, MenuIslandFlags.IslandFlagInfo> build() {
            return new PagedMenuTemplateButtonImpl<>(buttonItem, clickSound, commands, requiredPermission,
                    lackPermissionSound, nullItem, getButtonIndex(), IslandFlagPairPagedObjectButton.class,
                    IslandFlagPairPagedObjectButton::new);
        }

    }

}
