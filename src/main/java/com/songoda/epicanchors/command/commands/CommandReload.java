package com.songoda.epicanchors.command.commands;

import com.songoda.epicanchors.EpicAnchors;
import com.songoda.epicanchors.command.AbstractCommand;
import com.songoda.epicanchors.utils.Methods;
import org.bukkit.command.CommandSender;

public class CommandReload extends AbstractCommand {

    public CommandReload(AbstractCommand parent) {
        super("reload", parent, false);
    }

    @Override
    protected ReturnType runCommand(EpicAnchors instance, CommandSender sender, String... args) {
        instance.reload();
        instance.getLocale().getMessage("&7Configuration and Language files reloaded.").sendPrefixedMessage(sender);
        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "epicanchors.admin";
    }

    @Override
    public String getSyntax() {
        return "/ea reload";
    }

    @Override
    public String getDescription() {
        return "Reload the Configuration and Language files.";
    }
}

