package mod.chiselsandbits.inventory.bit;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class IItemHandlerBitInventory extends AbstractBitInventory
{

    private final IItemHandler itemHandler;

    public IItemHandlerBitInventory(final IItemHandler itemHandler) {this.itemHandler = itemHandler;}

    /**
     * Gets a copy of the stack that is in the given slot.
     *
     * @param index The index of the slot to read.
     * @return A copy of the stack in the slot.
     */
    @Override
    protected ItemStack getItem(final int index)
    {
        return itemHandler.getStackInSlot(index).copy();
    }

    /**
     * The size of the inventory.
     *
     * @return The size of the inventory.
     */
    @Override
    protected int getInventorySize()
    {
        return itemHandler.getSlots();
    }

    /**
     * Sets the slot with the given index with the given stack.
     *
     * @param index The index of the slot.
     * @param stack The stack to insert.
     */
    @Override
    protected void setSlotContents(final int index, final ItemStack stack)
    {
        itemHandler.extractItem(index, Integer.MAX_VALUE, false);
        
        if (!itemHandler.insertItem(index, stack, false).isEmpty()) {
            throw new IllegalStateException("Failed to insert stack.");
        }
    }

    @Override
    public boolean isEmpty()
    {
        for (int i = 0; i < itemHandler.getSlots(); i++)
        {
            if (!itemHandler.getStackInSlot(i).isEmpty())
                return false;
        }

        return true;
    }
}
