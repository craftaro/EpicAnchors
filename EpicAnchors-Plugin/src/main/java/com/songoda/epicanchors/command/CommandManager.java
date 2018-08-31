package com.songoda.epicanchors.command;

import com.songoda.arconix.api.methods.formatting.TextComponent;
import com.songoda.epicanchors.EpicAnchorsPlugin;
import com.songoda.epicanchors.command.commands.CommandEpicAnchors;
import com.songoda.epicanchors.command.commands.CommandGive;
import com.songoda.epicanchors.command.commands.CommandReload;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class CommandManager implements CommandExecutor {

    private EpicAnchorsPlugin instance;

    private List<AbstractCommand> commands = new ArrayList<>();

    public CommandManager(EpicAnchorsPlugin instance) {
        this.instance = instance;

        AbstractCommand commandEpicAnchors = addCommand(new CommandEpicAnchors());

        addCommand(new CommandGive(commandEpicAnchors));
        addCommand(new CommandReload(commandEpicAnchors));
    }

    private AbstractCommand addCommand(AbstractCommand abstractCommand) {
        commands.add(abstractCommand);
        return abstractCommand;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        for (AbstractCommand abstractCommand : commands) {
            if (abstractCommand.getCommand().equalsIgnoreCase(command.getName())) {
                if (strings.length == 0) {
                    processPerms(abstractCommand, commandSender, strings);
                    return true;
                }
            } else if (strings.length != 0 && abstractCommand.getParent() != null && abstractCommand.getParent().getCommand().equalsIgnoreCase(command.getName())) {
                String cmd = strings[0];
                if (cmd.equalsIgnoreCase(abstractCommand.getCommand())) {
                    processPerms(abstractCommand, commandSender, strings);
                    return true;
                }
            }
        }
        commandSender.sendMessage(instance.references.getPrefix() + TextComponent.formatText("&7The command you entered does not exist or is spelt incorrectly."));
        return true;
    }

    private void processPerms(AbstractCommand command, CommandSender sender, String[] strings) {
        if (command.getPermissionNode() == null || sender.hasPermission(command.getPermissionNode())) {
            command.runCommand(instance, sender, strings);
            return;
        }
        sender.sendMessage(instance.references.getPrefix() + instance.getLocale().getMessage("event.general.nopermission"));

    }
}
