package com.songoda.epicanchors.command.commands;

import com.songoda.epicanchors.EpicAnchors;
import com.songoda.epicanchors.command.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandGive extends AbstractCommand {

    public CommandGive(AbstractCommand abstractCommand) {
        super("give", abstractCommand, false);
    }

    @Override
    protected ReturnType runCommand(EpicAnchors instance, CommandSender sender, String... args) {
        if (args.length != 3) return ReturnType.SYNTAX_ERROR;

        if (Bukkit.getPlayer(args[1]) == null && !args[1].trim().toLowerCase().equals("all")) {
            instance.getLocale().newMessage("&cThat is not a player...").sendMessage(sender);
            return ReturnType.SYNTAX_ERROR;
        }

        ItemStack itemStack = instance.makAnchorItem(Integer.parseInt(args[2]) * 20 * 60 * 60);

        if (!args[1].trim().toLowerCase().equals("all")) {
            Player player = Bukkit.getOfflinePlayer(args[1]).getPlayer();
            player.getInventory().addItem(itemStack);
            instance.getLocale().getMessage("command.give.success").sendPrefixedMessage(player);
        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.getInventory().addItem(itemStack);
                instance.getLocale().getMessage("command.give.success").sendPrefixedMessage(player);
            }
        }
        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "epicanchors.admin";
    }

    @Override
    public String getSyntax() {
        return "/ea give <player/all> <amount in hours>";
    }

    @Override
    public String getDescription() {
        return "Gives an operator the ability to spawn a ChunkAnchor of his or her choice.";
    }
}
