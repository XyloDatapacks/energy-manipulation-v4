package xylo_datapacks.energy_manipulation.entity.shape;

import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import xylo_datapacks.energy_manipulation.item.EnergyManipulationItems;
import xylo_datapacks.energy_manipulation.utils.DataComponentsUtils;

public class ShapeDisplayElement extends ItemDisplayElement {
    boolean skipFirstTickInterpolation = true;
    
    public ShapeDisplayElement(String name) {
        ItemStack displayStack = new ItemStack(EnergyManipulationItems.SHAPE_DISPLAY);
        DataComponentsUtils.setCustomModelDataString(displayStack, name);
        super(displayStack);

        this.setItemDisplayContext(ItemDisplayContext.FIXED);
        this.setScale(new Vector3f(0.f, 0.f, 0.f));
        this.setTranslation(new Vector3f(0f, 0.0f, 0f));
    }

    @Override
    public void tick() {
        if (skipFirstTickInterpolation) {
            this.setInterpolationDuration(0);
            this.setTeleportDuration(0);
            this.setScale(new Vector3f(1.f, 1.f, 1.f));

            skipFirstTickInterpolation = false;
        }
        else {
            this.setInterpolationDuration(1);
            this.setTeleportDuration(1);
        }
        
        super.tick();
    }
}
