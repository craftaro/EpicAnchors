package com.songoda.epicanchors.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.core.configuration.editor.PluginConfigGui;
import com.songoda.core.gui.GuiManager;
import com.songoda.epicanchors.EpicAnchors;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandSettings extends AbstractCommand {

    final EpicAnchors instance;
    final GuiManager guiManager;

    public CommandSettings(EpicAnchors instance, GuiManager manager) {
        super(true, "settings");
        this.instance = instance;
        this.guiManager = manager;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        guiManager.showGUI((Player) sender, new PluginConfigGui(instance));
        return AbstractCommand.ReturnType.SUCCESS;
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
        return "/ea settings";
    }

    @Override
    public String getDescription() {
        return "Edit the EpicAnchors Settings.";
    }
}
