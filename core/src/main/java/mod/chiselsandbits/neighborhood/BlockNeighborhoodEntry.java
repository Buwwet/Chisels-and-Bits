package mod.chiselsandbits.neighborhood;

import mod.chiselsandbits.api.blockinformation.IBlockInformation;
import mod.chiselsandbits.api.multistate.accessor.IAreaAccessor;
import mod.chiselsandbits.api.multistate.accessor.identifier.IAreaShapeIdentifier;
import mod.chiselsandbits.blockinformation.BlockInformation;

import java.util.Objects;

public final class BlockNeighborhoodEntry
{
    private final IBlockInformation blockInformation;
    private final IAreaAccessor    accessor;
    private final IAreaShapeIdentifier identifier;

    public BlockNeighborhoodEntry(final IBlockInformation blockInformation, final IAreaAccessor accessor)
    {
        this.blockInformation = blockInformation;
        this.accessor = accessor;
        this.identifier = this.accessor.createNewShapeIdentifier();
    }

    public BlockNeighborhoodEntry(final IBlockInformation blockInformation)
    {
        this.blockInformation = blockInformation;
        this.accessor = null;
        this.identifier = IAreaShapeIdentifier.DUMMY;
    }

    @Override
    public int hashCode()
    {
        int result = blockInformation != null ? blockInformation.hashCode() : 0;
        result = 31 * result + (identifier != null ? identifier.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof final BlockNeighborhoodEntry that))
        {
            return false;
        }

        if (!Objects.equals(blockInformation, that.blockInformation))
        {
            return false;
        }
        return Objects.equals(identifier, that.identifier);
    }

    public IBlockInformation getBlockInformation()
    {
        return blockInformation;
    }

    public IAreaAccessor getAccessor()
    {
        return accessor;
    }

    public IAreaShapeIdentifier getIdentifier()
    {
        return identifier;
    }
}
