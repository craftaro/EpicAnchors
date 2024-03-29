package com.craftaro.epicanchors.commands.sub;

import com.craftaro.core.commands.AbstractCommand;
import com.craftaro.epicanchors.EpicAnchors;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class ReloadCommand extends AbstractCommand {
    private final EpicAnchors plugin;

    public ReloadCommand(EpicAnchors plugin) {
        super(CommandType.CONSOLE_OK, false, "reload");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        this.plugin.reloadConfig();
        this.plugin.getLocale().getMessage("&7Configuration and Language files reloaded.").sendPrefixedMessage(sender);

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return Collections.emptyList();
    }

    @Override
    public String getPermissionNode() {
        return "EpicAnchors.cmd.reload";
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
