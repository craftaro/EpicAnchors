package com.songoda.epicanchors.command.commands;

import com.songoda.epicanchors.EpicAnchors;
import com.songoda.epicanchors.command.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSettings extends AbstractCommand {

    public CommandSettings(AbstractCommand parent) {
        super("settings", parent, true);
    }

    @Override
    protected ReturnType runCommand(EpicAnchors instance, CommandSender sender, String... args) {
        instance.getSettingsManager().openSettingsManager((Player) sender);
        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "epicanchors.admin";
    }

    @Override
    public String getSyntax() {
        return "/ea settings";
    }

    @Override
    public String getDescription() {
        return "Edit the EpicAnchors Settings.";
    }
}
