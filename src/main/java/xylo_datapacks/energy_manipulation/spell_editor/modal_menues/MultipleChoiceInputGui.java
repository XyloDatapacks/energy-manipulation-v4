package xylo_datapacks.energy_manipulation.spell_editor.modal_menues;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.SimpleGuiElement;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import xylo_datapacks.energy_manipulation.font.EnergyManipulationFonts;
import xylo_datapacks.energy_manipulation.glyph.GlyphInstance;
import xylo_datapacks.energy_manipulation.glyph.GlyphsRegistry;
import xylo_datapacks.energy_manipulation.glyph.value_type.GlyphValue;
import xylo_datapacks.energy_manipulation.spell_editor.SpellEditor;
import xylo_datapacks.energy_manipulation.spell_editor.SpellEditorButtonsRegistry;
import xylo_datapacks.energy_manipulation.spell_editor.SpellEditorGui;
import xylo_datapacks.energy_manipulation.spell_editor.SpellEditorGuiUtils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

public class MultipleChoiceInputGui extends SimpleGui {
    static final int PAGE_SIZE = 9*3 - 1;
    protected final SpellEditor editor;
    protected final int editorPage;
    protected final GlyphInstance instance;
    protected final GlyphValue initialValue;
    protected final Function<MultipleChoiceInputGui, List<SimpleGuiElement>> optionsSupplier;

    public MultipleChoiceInputGui(ServerPlayer player, SpellEditor editor, int editorPage, GlyphInstance instance, Function<MultipleChoiceInputGui, List<SimpleGuiElement>> optionsSupplier) {
        super(MenuType.GENERIC_9x3, player, false);

        this.editor = editor;
        this.editorPage = editorPage;
        this.instance = instance;
        this.initialValue = GlyphsRegistry.RAW_VALUE_GLYPH.getPayloadValue(instance).get();
        this.optionsSupplier = optionsSupplier;

        this.setTitle(getTitleText());
        this.setupToolbar();
        this.displaySelector();
    }

    public SpellEditor getSpellEditor() {
        return editor;
    }

    public GlyphInstance getGlyphInstance() {
        return instance;
    }

    protected MutableComponent getTitleText() {
        return Component.empty()
                .append(Component.literal("\uF102\uE002\uF002\uF202").setStyle(Style.EMPTY.withFont(EnergyManipulationFonts.SPELL_BOOK_GUI).withColor(0xFFFFFF)))
                .append(Component.literal("Spell Book").setStyle(Style.EMPTY.withFont(FontDescription.DEFAULT)));
    }

    protected void setupToolbar() {
        this.setSlot(26, new GuiElementBuilder(SpellEditorButtonsRegistry.CANCEL_BUTTON.get())
                .setName(Component.literal("Cancel"))
                .hideTooltip()
                .setCallback(clickType -> {
                    GlyphsRegistry.RAW_VALUE_GLYPH.setPayloadValue(instance, initialValue);
                    goBackToEditor();
                })
                .build());
    }

    public void goBackToEditor() {
        SpellEditorGui newEditorGui = new SpellEditorGui(player, editor, editorPage, false);
        newEditorGui.open();
    }

    protected void displaySelector() {
        AtomicInteger currentSlot = new AtomicInteger(0);
        optionsSupplier.apply(this).forEach(option -> {
            safeAddGlyphGuiElement(currentSlot, () -> Optional.of(option));
        });

        // Clear remaining slots.
        while (!isOutOfGlyphsDrawingSpace(currentSlot)) {
            clearSlot(currentSlot.getAndIncrement() % PAGE_SIZE);
        }
    }

    public boolean isOutOfGlyphsDrawingSpace(AtomicInteger currentSlot) {
        return SpellEditorGuiUtils.isOutOfGlyphsDrawingSpace(PAGE_SIZE, 0, currentSlot);
    }

    public void safeAddGlyphGuiElement(AtomicInteger currentSlot, Supplier<Optional<SimpleGuiElement>> supplier) {
        SpellEditorGuiUtils.safeAddGlyphGuiElement(this, PAGE_SIZE, 0, currentSlot, supplier);
    }
}
