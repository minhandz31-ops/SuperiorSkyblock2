package com.bgsoftware.superiorskyblock.commands.admin;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.commands.CommandsMap;

public class AdminCommandsMap extends CommandsMap {

    public AdminCommandsMap(SuperiorSkyblockPlugin plugin) {
        super(plugin);
    }

    @Override
    public void loadDefaultCommands() {
        clearCommands();

        registerCommand(new CmdAdminAdd());
        if (plugin.getSettings().isCoopMembers())
            registerCommand(new CmdAdminAddCoopLimit());
        registerCommand(new CmdAdminAddDisbands());
        registerCommand(new CmdAdminAddSize());
        registerCommand(new CmdAdminAddTeamLimit());
        registerCommand(new CmdAdminBypass());
        registerCommand(new CmdAdminChest());
        registerCommand(new CmdAdminClose());
        registerCommand(new CmdAdminCmdAll());
        registerCommand(new CmdAdminCount());
        registerCommand(new CmdAdminData());
        registerCommand(new CmdAdminDebug());
        registerCommand(new CmdAdminDemote());
        registerCommand(new CmdAdminDisband());
        registerCommand(new CmdAdminFly());
        registerCommand(new CmdAdminIgnore());
        registerCommand(new CmdAdminJoin());
        registerCommand(new CmdAdminKick());
        registerCommand(new CmdAdminModules());
        registerCommand(new CmdAdminMsg());
        registerCommand(new CmdAdminMsgAll());
        registerCommand(new CmdAdminName());
        registerCommand(new CmdAdminOpen());
        registerCommand(new CmdAdminOpenMenu());
        registerCommand(new CmdAdminPromote());
        registerCommand(new CmdAdminPurge());
        registerCommand(new CmdAdminReload());
        registerCommand(new CmdAdminRemoveRatings());
        registerCommand(new CmdAdminResetPermissions());
        registerCommand(new CmdAdminResetSettings());
        registerCommand(new CmdAdminResetWorld());
        registerCommand(new CmdAdminSchematic());
        registerCommand(new CmdAdminSetBlockAmount());
        registerCommand(new CmdAdminSetChestRow());
        if (plugin.getSettings().isCoopMembers())
            registerCommand(new CmdAdminSetCoopLimit());
        registerCommand(new CmdAdminSetDisbands());
        registerCommand(new CmdAdminSetLeader());
        registerCommand(new CmdAdminSetPermission());
        registerCommand(new CmdAdminSetRate());
        registerCommand(new CmdAdminSetRoleLimit());
        registerCommand(new CmdAdminSetSettings());
        registerCommand(new CmdAdminSetSize());
        registerCommand(new CmdAdminSetSpawn());
        registerCommand(new CmdAdminSetTeamLimit());
        registerCommand(new CmdAdminSettings());
        registerCommand(new CmdAdminShow());
        registerCommand(new CmdAdminSpawn());
        registerCommand(new CmdAdminSpy());
        registerCommand(new CmdAdminStats());
        registerCommand(new CmdAdminTeleport());
        registerCommand(new CmdAdminTitle());
        registerCommand(new CmdAdminTitleAll());
        registerCommand(new CmdAdminUnignore());
        registerCommand(new CmdAdminUnlockWorld());
    }

}
