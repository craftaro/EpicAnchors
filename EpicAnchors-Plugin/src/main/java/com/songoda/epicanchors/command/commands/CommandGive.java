package com.songoda.epicanchors.command.commands;

import com.songoda.arconix.api.methods.formatting.TextComponent;
import com.songoda.epicanchors.command.AbstractCommand;
import com.songoda.epicanchors.EpicAnchorsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandGive extends AbstractCommand {

    public CommandGive(AbstractCommand abstractCommand) {
        super("give", "epicanchors.admin", abstractCommand);
    }

    @Override
    protected boolean runCommand(EpicAnchorsPlugin instance, CommandSender sender, String... args) {
        if (args.length != 3) return true;

        if (Bukkit.getPlayer(args[1]) == null && !args[1].trim().toLowerCase().equals("all")) {
            sender.sendMessage("Not a player...");
            return true;
        }

        ItemStack itemStack = instance.makeAnchorItem(Integer.parseInt(args[2]) * 20 * 60 * 60);

        if (!args[1].trim().toLowerCase().equals("all")) {
            Player player = Bukkit.getOfflinePlayer(args[1]).getPlayer();
            player.getInventory().addItem(itemStack);
            player.sendMessage(TextComponent.formatText(instance.getLocale().getMessage("command.give.success")));
        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.getInventory().addItem(itemStack);
                player.sendMessage(TextComponent.formatText(instance.getLocale().getMessage("command.give.success")));
            }
        }
        return true;
    }
}
