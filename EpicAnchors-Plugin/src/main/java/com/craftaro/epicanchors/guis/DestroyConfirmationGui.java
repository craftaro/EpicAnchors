package com.craftaro.epicanchors.guis;

import com.craftaro.core.gui.Gui;
import com.craftaro.core.gui.GuiUtils;
import com.craftaro.core.gui.methods.Closable;
import com.craftaro.core.third_party.net.kyori.adventure.text.Component;
import com.craftaro.core.utils.TextUtils;
import com.craftaro.epicanchors.EpicAnchors;
import com.craftaro.epicanchors.api.Anchor;
import com.craftaro.epicanchors.files.Settings;
import com.craftaro.epicanchors.utils.Callback;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;

public class DestroyConfirmationGui extends Gui {
    private final EpicAnchors plugin;
    private final Anchor anchor;

    private Callback<Boolean> handler;

    public DestroyConfirmationGui(EpicAnchors plugin, Anchor anchor, Callback<Boolean> callback) {
        this.plugin = plugin;
        this.anchor = anchor;

        this.handler = (ex, result) -> {
            this.handler = null;
            this.close();

            callback.accept(ex, result);
        };

        this.setRows(3);
        this.setTitle(plugin.getLocale().getMessage("interface.anchor.title").getMessage());

        constructGUI();
        AnchorGui.runPreparedGuiTask(this.plugin, this, this.anchor);

        Closable currClosable = this.closer;
        this.closer = event -> {
            currClosable.onClose(event);

            if (this.handler != null) {
                this.handler.accept(null, false);
            }
        };
    }

    private void constructGUI() {
        AnchorGui.prepareGui(this.plugin, this, this.anchor);

        Component cancelLore = this.plugin.getLocale().getMessage("interface.button.cancelDestroyLore").getMessage();
        Component confirmLore = this.plugin.getLocale().getMessage("interface.button." +
                        (Settings.ALLOW_ANCHOR_BREAKING.getBoolean() ? "confirmDestroyLore" : "confirmDestroyLoreNoDrops"))
                .getMessage();

        setButton(11, GuiUtils.createButtonItem(XMaterial.RED_TERRACOTTA,
                        this.plugin.getLocale().getMessage("interface.button.cancelDestroy").getMessage(),
                        cancelLore.equals(Component.empty()) ? new Component[0] : new Component[]{cancelLore}),
                event -> this.handler.accept(null, false));

        setButton(15, GuiUtils.createButtonItem(XMaterial.GREEN_TERRACOTTA,
                        this.plugin.getLocale().getMessage("interface.button.confirmDestroy").getMessage(),
                        cancelLore.equals(Component.empty()) ? new Component[0] : new Component[]{confirmLore}),
                event -> this.handler.accept(null, true));
    }
}
