package com.songoda.epicanchors.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.epicanchors.EpicAnchors;
import com.songoda.epicanchors.utils.Methods;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandGive extends AbstractCommand {

    final EpicAnchors instance;

    public CommandGive(EpicAnchors instance) {
        super(false, "give");
        this.instance = instance;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if (args.length != 2) return ReturnType.SYNTAX_ERROR;


        Player target = Bukkit.getPlayer(args[0]);
        if (target == null && !args[0].trim().toLowerCase().equals("all")) {
            instance.getLocale().newMessage("&cThat is not a player...").sendPrefixedMessage(sender);
            return ReturnType.SYNTAX_ERROR;
        }



        ItemStack itemStack;

        if (Methods.isInt(args[1])) {
            itemStack = (Integer.parseInt(args[1]) <= 0) ? instance.makeAnchorItem(-99) : instance.makeAnchorItem(Integer.parseInt(args[1]) * 20 * 60 * 60);
        } else if (args[1].toLowerCase().equals("infinite")) {
            itemStack = instance.makeAnchorItem(-99);
        } else {
            instance.getLocale().newMessage("&cYou can only use whole numbers...").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        if (target != null) {
            target.getInventory().addItem(itemStack);
            instance.getLocale().getMessage("command.give.success").sendPrefixedMessage(target);
        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.getInventory().addItem(itemStack);
                instance.getLocale().getMessage("command.give.success").sendPrefixedMessage(player);
            }
        }

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender commandSender, String... strings) {
        if (strings.length == 1) {
            List<String> players = new ArrayList<>();
            players.add("all");
            for (Player player : Bukkit.getOnlinePlayers()) {
                players.add(player.getName());
            }
            return players;
        } else if (strings.length == 2) {
            return Arrays.asList("1", "2", "3", "4", "5");
        }
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "epicanchors.admin";
    }

    @Override
    public String getSyntax() {
        return "/ea give <player/all> <amount in hours / infinite>";
    }

    @Override
    public String getDescription() {
        return "Gives an operator the ability to spawn a ChunkAnchor of his or her choice.";
    }
}
