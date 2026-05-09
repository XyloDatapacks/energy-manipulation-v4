package xylo_datapacks.energy_manipulation.spell_editor;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class SpellEditorGuiSlot extends Slot {
    public Consumer<ItemStack> onItemStackChangedCallback;
    
    public SpellEditorGuiSlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        onItemStackChangedCallback.accept(getItem());
    }
}
