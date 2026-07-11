package com.bgsoftware.superiorskyblock.core.menu.button.impl;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.menu.button.MenuTemplateButton;
import com.bgsoftware.superiorskyblock.api.world.GameSound;
import com.bgsoftware.superiorskyblock.core.menu.Menus;
import com.bgsoftware.superiorskyblock.core.menu.TemplateItem;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractMenuTemplateButton;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractMenuViewButton;
import com.bgsoftware.superiorskyblock.core.menu.button.MenuTemplateButtonImpl;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuPermissionsCommands;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

/**
 * Button for the 3 category paper-items inside the {@code MenuPermissionsCommands} root view.
 * <p>
 * Clicking a category reopens {@code MenuPermissionsCommands} with the active category set,
 * which displays the permissions belonging to that category as toggle buttons.
 */
public class PermissionCategoryButton extends AbstractMenuViewButton<MenuPermissionsCommands.View> {

    private PermissionCategoryButton(AbstractMenuTemplateButton<MenuPermissionsCommands.View> templateButton,
                                     MenuPermissionsCommands.View menuView) {
        super(templateButton, menuView);
    }

    @Override
    public Template getTemplate() {
        return (Template) super.getTemplate();
    }

    @Override
    public ItemStack createViewItem() {
        // Hide category buttons when a category is already selected.
        if (menuView.getCategory() != null)
            return new ItemStack(Material.AIR);

        return super.createViewItem();
    }

    @Override
    public void onButtonClick(InventoryClickEvent clickEvent) {
        menuView.setPreviousMove(false);
        MenuPermissionsCommands.Args args = new MenuPermissionsCommands.Args(
                menuView.getIsland(), menuView.getSuperiorPlayer(), getTemplate().category);
        Menus.MENU_PERMISSIONS_COMMANDS.createView(menuView.getInventoryViewer(), args, menuView);
    }

    public static class Builder extends AbstractMenuTemplateButton.AbstractBuilder<MenuPermissionsCommands.View> {

        private MenuPermissionsCommands.PermissionCategory category;

        public Builder setCategory(MenuPermissionsCommands.PermissionCategory category) {
            this.category = category;
            return this;
        }

        @Override
        public MenuTemplateButton<MenuPermissionsCommands.View> build() {
            return new Template(buttonItem, clickSound, commands, requiredPermission, lackPermissionSound, category);
        }

    }

    public static class Template extends MenuTemplateButtonImpl<MenuPermissionsCommands.View> {

        private final MenuPermissionsCommands.PermissionCategory category;

        Template(@Nullable TemplateItem buttonItem, @Nullable GameSound clickSound, @Nullable List<String> commands,
                 @Nullable String requiredPermission, @Nullable GameSound lackPermissionSound,
                 MenuPermissionsCommands.PermissionCategory category) {
            super(buttonItem, clickSound, commands, requiredPermission, lackPermissionSound,
                    PermissionCategoryButton.class, PermissionCategoryButton::new);
            this.category = Objects.requireNonNull(category, "category cannot be null");
        }

    }

}
