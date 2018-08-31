package com.songoda.epicanchors.command.commands;

import com.songoda.arconix.api.methods.formatting.TextComponent;
import com.songoda.epicanchors.command.AbstractCommand;
import com.songoda.epicanchors.EpicAnchorsPlugin;
import org.bukkit.command.CommandSender;

public class CommandReload extends AbstractCommand {

    public CommandReload(AbstractCommand parent) {
        super("reload", "epicanchors.admin", parent);
    }

    @Override
    protected boolean runCommand(EpicAnchorsPlugin instance, CommandSender sender, String... args) {
        instance.reload();
        sender.sendMessage(TextComponent.formatText(instance.references.getPrefix() + "&7Configuration and Language files reloaded."));
        return false;
    }
}
