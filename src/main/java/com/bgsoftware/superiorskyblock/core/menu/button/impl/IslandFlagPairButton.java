package com.bgsoftware.superiorskyblock.core.menu.button.impl;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandFlag;
import com.bgsoftware.superiorskyblock.api.menu.button.MenuTemplateButton;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.GameSoundImpl;
import com.bgsoftware.superiorskyblock.core.events.plugin.PluginEventsFactory;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.core.menu.TemplateItem;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractMenuTemplateButton;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractMenuViewButton;
import com.bgsoftware.superiorskyblock.core.menu.button.MenuTemplateButtonImpl;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuIslandSettings;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.core.menu.view.impl.IslandMenuView;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Button for pair-flags in the new MenuIslandSettings.
 * Left-click toggles leftFlag, Right-click toggles rightFlag.
 */
public class IslandFlagPairButton extends AbstractMenuViewButton<IslandMenuView> {

    private IslandFlagPairButton(AbstractMenuTemplateButton<IslandMenuView> templateButton,
                                 IslandMenuView menuView) {
        super(templateButton, menuView);
    }

    @Override
    public Template getTemplate() {
        return (Template) super.getTemplate();
    }

    @Override
    public void onButtonClick(InventoryClickEvent clickEvent) {
        SuperiorPlayer inventoryViewer = menuView.getInventoryViewer();
        Island island = menuView.getIsland();

        Template template = getTemplate();
        IslandFlag leftFlag = template.leftFlag;
        IslandFlag rightFlag = template.rightFlag;

        boolean isLeftClick = clickEvent.isLeftClick();
        IslandFlag targetFlag = isLeftClick ? leftFlag : rightFlag;

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

        GameSoundImpl.playSound(clickEvent.getWhoClicked(), template.clickSound);
        Message.UPDATED_SETTINGS.send(inventoryViewer, Formatters.CAPITALIZED_FORMATTER.format(targetFlag.getName()));
        menuView.refreshView();
    }

    @Override
    public ItemStack createViewItem() {
        SuperiorPlayer inventoryViewer = menuView.getInventoryViewer();
        Island island = menuView.getIsland();

        Template template = getTemplate();
        IslandFlag leftFlag = template.leftFlag;
        IslandFlag rightFlag = template.rightFlag;

        // Check if ANY flag in the pair is enabled
        boolean anyEnabled = false;
        if (leftFlag != null && island.hasSettingsEnabled(leftFlag))
            anyEnabled = true;
        if (rightFlag != null && island.hasSettingsEnabled(rightFlag))
            anyEnabled = true;

        TemplateItem item = anyEnabled ? template.enabledItem : template.disabledItem;
        return item != null ? item.getBuilder().build(inventoryViewer) : new ItemStack(org.bukkit.Material.AIR);
    }

    public static class Template extends MenuTemplateButtonImpl<IslandMenuView> {

        @Nullable
        private final IslandFlag leftFlag;
        @Nullable
        private final IslandFlag rightFlag;
        @Nullable
        private final TemplateItem enabledItem;
        @Nullable
        private final TemplateItem disabledItem;
        @Nullable
        private final com.bgsoftware.superiorskyblock.api.world.GameSound clickSound;

        Template(@Nullable IslandFlag leftFlag, @Nullable IslandFlag rightFlag,
                 @Nullable TemplateItem enabledItem, @Nullable TemplateItem disabledItem,
                 @Nullable com.bgsoftware.superiorskyblock.api.world.GameSound clickSound,
                 @Nullable TemplateItem buttonItem, @Nullable com.bgsoftware.superiorskyblock.api.world.GameSound clickSoundDefault,
                 java.util.List<String> commands, @Nullable String requiredPermission,
                 @Nullable com.bgsoftware.superiorskyblock.api.world.GameSound lackPermissionSound) {
            super(buttonItem == null ? TemplateItem.AIR : buttonItem, null, null, requiredPermission,
                    lackPermissionSound, IslandFlagPairButton.class, IslandFlagPairButton::new);
            this.leftFlag = leftFlag;
            this.rightFlag = rightFlag;
            this.enabledItem = enabledItem;
            this.disabledItem = disabledItem;
            this.clickSound = clickSound;
        }

        @Nullable
        public IslandFlag getLeftFlag() {
            return leftFlag;
        }

        @Nullable
        public IslandFlag getRightFlag() {
            return rightFlag;
        }

        @Nullable
        public TemplateItem getEnabledItem() {
            return enabledItem;
        }

        @Nullable
        public TemplateItem getDisabledItem() {
            return disabledItem;
        }

        @Nullable
        public com.bgsoftware.superiorskyblock.api.world.GameSound getClickSound() {
            return clickSound;
        }

    }

    public static class Builder extends AbstractMenuTemplateButton.AbstractBuilder<IslandMenuView> {

        @Nullable
        private IslandFlag leftFlag;
        @Nullable
        private IslandFlag rightFlag;
        @Nullable
        private TemplateItem enabledItem;
        @Nullable
        private TemplateItem disabledItem;
        @Nullable
        private com.bgsoftware.superiorskyblock.api.world.GameSound clickSound;

        public Builder setLeftFlag(@Nullable IslandFlag leftFlag) {
            this.leftFlag = leftFlag;
            return this;
        }

        public Builder setRightFlag(@Nullable IslandFlag rightFlag) {
            this.rightFlag = rightFlag;
            return this;
        }

        public Builder setEnabledItem(@Nullable TemplateItem enabledItem) {
            this.enabledItem = enabledItem;
            return this;
        }

        public Builder setDisabledItem(@Nullable TemplateItem disabledItem) {
            this.disabledItem = disabledItem;
            return this;
        }

        public Builder setClickSound(@Nullable com.bgsoftware.superiorskyblock.api.world.GameSound clickSound) {
            this.clickSound = clickSound;
            return this;
        }

        @Override
        public MenuTemplateButton<IslandMenuView> build() {
            return new Template(leftFlag, rightFlag, enabledItem, disabledItem, clickSound,
                    buttonItem, clickSound, commands, requiredPermission, lackPermissionSound);
        }

    }

}
