package com.bgsoftware.superiorskyblock.core.menu.button.impl;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.menu.button.MenuTemplateButton;
import com.bgsoftware.superiorskyblock.api.world.GameSound;
import com.bgsoftware.superiorskyblock.core.menu.Menus;
import com.bgsoftware.superiorskyblock.core.menu.TemplateItem;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractMenuTemplateButton;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractMenuViewButton;
import com.bgsoftware.superiorskyblock.core.menu.button.MenuTemplateButtonImpl;
import com.bgsoftware.superiorskyblock.core.menu.view.args.IslandPlayerViewArgs;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuPermissionsCommands;
import com.bgsoftware.superiorskyblock.core.menu.view.impl.IslandPlayerMenuView;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;
import java.util.Objects;

/**
 * Button for the two enchanted books in the main {@code MenuPermissions}.
 * Clicking the book opens the corresponding sub-menu (Physical or Commands).
 */
public class PermissionBookButton extends AbstractMenuViewButton<IslandPlayerMenuView> {

    private PermissionBookButton(AbstractMenuTemplateButton<IslandPlayerMenuView> templateButton, IslandPlayerMenuView menuView) {
        super(templateButton, menuView);
    }

    @Override
    public Template getTemplate() {
        return (Template) super.getTemplate();
    }

    @Override
    public void onButtonClick(InventoryClickEvent clickEvent) {
        menuView.setPreviousMove(false);
        switch (getTemplate().targetMenu) {
            case PHYSICAL:
                Menus.MENU_PERMISSIONS_PHYSICAL.createView(menuView.getInventoryViewer(), 
                        new IslandPlayerViewArgs(menuView.getIsland(), menuView.getSuperiorPlayer()), menuView);
                break;
            case COMMANDS:
                Menus.MENU_PERMISSIONS_COMMANDS.createView(menuView.getInventoryViewer(),
                        new MenuPermissionsCommands.Args(menuView.getIsland(), menuView.getSuperiorPlayer()), menuView);
                break;
        }
    }

    public enum TargetMenu {
        PHYSICAL,
        COMMANDS
    }

    public static class Builder extends AbstractMenuTemplateButton.AbstractBuilder<IslandPlayerMenuView> {

        private TargetMenu targetMenu;

        public Builder setTargetMenu(TargetMenu targetMenu) {
            this.targetMenu = targetMenu;
            return this;
        }

        @Override
        public MenuTemplateButton<IslandPlayerMenuView> build() {
            return new Template(buttonItem, clickSound, commands, requiredPermission, lackPermissionSound, targetMenu);
        }

    }

    public static class Template extends MenuTemplateButtonImpl<IslandPlayerMenuView> {

        private final TargetMenu targetMenu;

        Template(@Nullable TemplateItem buttonItem, @Nullable GameSound clickSound, @Nullable List<String> commands,
                 @Nullable String requiredPermission, @Nullable GameSound lackPermissionSound, TargetMenu targetMenu) {
            super(buttonItem, clickSound, commands, requiredPermission, lackPermissionSound,
                    PermissionBookButton.class, PermissionBookButton::new);
            this.targetMenu = Objects.requireNonNull(targetMenu, "targetMenu cannot be null");
        }

    }

}
