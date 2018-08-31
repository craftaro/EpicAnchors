package com.songoda.epicanchors.command.commands;

import com.songoda.arconix.api.methods.formatting.TextComponent;
import com.songoda.epicanchors.command.AbstractCommand;
import com.songoda.epicanchors.EpicAnchorsPlugin;
import org.bukkit.command.CommandSender;

public class CommandEpicAnchors extends AbstractCommand {

    public CommandEpicAnchors() {
        super("EpicAnchors", null, null);
    }

    @Override
    protected boolean runCommand(EpicAnchorsPlugin instance, CommandSender sender, String... args) {
        sender.sendMessage("");
        sender.sendMessage(TextComponent.formatText("&f>>&m------------&6&l EpicAnchors Help &f&m------------&f<<"));
        sender.sendMessage(TextComponent.formatText("              &7Version " + instance.getDescription().getVersion() + " Created by &5&l&oBrianna"));

        sender.sendMessage(TextComponent.formatText("&6/EpicAnchors&7 - Displays this page."));
        if (sender.hasPermission("epicanchors.admin")) {
            sender.sendMessage(TextComponent.formatText("&6/eca reload &7Reload the Configuration and Language files."));
            sender.sendMessage(TextComponent.formatText("&6/eca give <player> <amount in hours> &7 - Gives an operator the ability to spawn a ChunkAnchor of his or her choice."));
        }
        sender.sendMessage("");

        return false;
    }
}
