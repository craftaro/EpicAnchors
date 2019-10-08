package com.songoda.epicanchors.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.epicanchors.EpicAnchors;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
            instance.getLocale().newMessage("&cThat is not a player...").sendMessage(sender);
            return ReturnType.SYNTAX_ERROR;
        }

        ItemStack itemStack = instance.makeAnchorItem(Integer.parseInt(args[1]) * 20 * 60 * 60);

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
        return null;
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
