package mod.chiselsandbits.inventory.bit;

import mod.chiselsandbits.api.blockinformation.IBlockInformation;
import mod.chiselsandbits.blockinformation.BlockInformation;
import mod.chiselsandbits.api.inventory.bit.IBitInventory;

import java.util.Collections;
import java.util.Map;

public class IllegalBitInventory implements IBitInventory
{
    /**
     * Checks if it is possible to extract a given amount of bits with the given blockstate from the the current inventory.
     *
     * @param blockInformation The blockstate.
     * @param count      The amount of bits to extract.
     * @return {@code true} when extraction is possible.
     */
    @Override
    public boolean canExtract(
      final IBlockInformation blockInformation,
      final int count)
    {
        return false;
    }

    /**
     * Returns the maximal amount of bits with a given blockstate which can be extracted of a given blockstate.
     *
     * @param blockState The blockstate in question.
     * @return The amount of bits that can be extracted with a given blockstate.
     */
    @Override
    public int getMaxExtractAmount(final IBlockInformation blockState)
    {
        return 0;
    }

    /**
     * Extracts a given amount of bits with the given blockstate from the the current inventory.
     *
     * @param blockState The blockstate.
     * @param count      The amount of bits to extract.
     * @throws IllegalArgumentException when extraction is not possible.
     */
    @Override
    public void extract(
      final IBlockInformation blockState,
      final int count) throws IllegalArgumentException
    {

    }

    /**
     * Checks if it is possible to insert a given amount of bits with the given blockstate from the the current inventory.
     *
     * @param blockState The blockstate.
     * @param count      The amount of bits to insert.
     * @return {@code true} when insertion is possible.
     */
    @Override
    public boolean canInsert(final IBlockInformation blockState, final int count)
    {
        return false;
    }

    /**
     * Returns the maximal amount of bits with a given blockstate which can be inserted of a given blockstate.
     *
     * @param blockState The blockstate in question.
     * @return The amount of bits that can be inserted with a given blockstate.
     */
    @Override
    public int getMaxInsertAmount(final IBlockInformation blockState)
    {
        return 0;
    }

    /**
     * Inserts a given amount of bits with the given blockstate from the the current inventory.
     *
     * @param blockState The blockstate.
     * @param count      The amount of bits to insert.
     * @throws IllegalArgumentException when insertion is not possible.
     */
    @Override
    public void insert(final IBlockInformation blockState, final int count) throws IllegalArgumentException
    {
    }

    @Override
    public boolean isEmpty()
    {
        return true;
    }

    @Override
    public Map<IBlockInformation, Integer> getContainedStates()
    {
        return Collections.emptyMap();
    }
}
