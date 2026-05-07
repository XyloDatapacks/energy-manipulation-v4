package xylo_datapacks.energy_manipulation.spell_editor;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Items;

public class SpellEditorGui extends SimpleGui {
    private final SpellEditor editor;

    public SpellEditorGui(ServerPlayer player, SpellEditor editor) {
        super(MenuType.GENERIC_9x6, player, false);
        this.editor = editor;

        this.setTitle(Component.literal("Spell Editor"));
        this.setupSlots();
    }

    private void setupSlots() {
        this.setSlot(50, new GuiElementBuilder(Items.RED_WOOL)
                .setName(Component.literal("Cancel"))
                .setCallback(clickType -> {
                    player.sendSystemMessage(Component.literal("Clicked cancel"));
                })
                .build());

        this.setSlot(51, new GuiElementBuilder(Items.GREEN_WOOL)
                .setName(Component.literal("Confirm"))
                .setCallback(clickType -> {
                    player.sendSystemMessage(Component.literal("Clicked confirm"));
                })
                .build());
        
        this.setSlot(52, new GuiElementBuilder(Items.SPECTRAL_ARROW)
                .setName(Component.literal("Previous Page"))
                .setCallback(clickType -> {
                    player.sendSystemMessage(Component.literal("Clicked previous page"));
                })
                .build());

        this.setSlot(53, new GuiElementBuilder(Items.SPECTRAL_ARROW)
                .setName(Component.literal("Next Page"))
                .setCallback(clickType -> {
                    player.sendSystemMessage(Component.literal("Clicked next page"));
                })
                .build());
    }

    @Override
    public void onManualClose() {
        super.onManualClose();
        onClose();
    }

    @Override
    public void onPlayerClose(boolean success) {
        super.onPlayerClose(success);
        onClose();
    }
    
    public void onClose() {
       
        
    }
}