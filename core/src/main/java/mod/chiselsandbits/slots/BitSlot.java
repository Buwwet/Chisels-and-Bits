package mod.chiselsandbits.slots;

import mod.chiselsandbits.item.bit.BitItem;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BitSlot extends Slot
{
    public BitSlot(
      final Container inventoryIn,
      final int index,
      final int xPosition,
      final int yPosition)
    {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(
      final @NotNull ItemStack stack)
    {
        return !stack.isEmpty() && stack.getItem() instanceof BitItem;
    }
}
