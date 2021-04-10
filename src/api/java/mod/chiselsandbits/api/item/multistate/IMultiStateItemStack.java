package mod.chiselsandbits.api.item.multistate;

import mod.chiselsandbits.api.multistate.accessor.IAreaAccessor;
import mod.chiselsandbits.api.multistate.mutator.IAreaMutator;
import mod.chiselsandbits.api.util.IPacketBufferSerializable;
import net.minecraft.block.AirBlock;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

/**
 * The itemstack sensitive version of the multistate item.
 */
public interface IMultiStateItemStack extends IAreaAccessor,
                                                IAreaMutator,
                                                INBTSerializable<CompoundNBT>,
                                                IPacketBufferSerializable
{
    /**
     * The statistics of the itemstack.
     *
     * @return The statistics.
     */
    IStatistics getStatistics();

    /**
     * Converts this multistack itemstack data to an actual use able itemstack.
     *
     * @return The itemstack with the data of this multistate itemstack.
     */
    ItemStack toItemStack();
}
