package mod.chiselsandbits.config;

import com.communi.suggestu.scena.core.config.ConfigurationType;
import com.communi.suggestu.scena.core.config.IConfigurationBuilder;
import com.communi.suggestu.scena.core.config.IConfigurationManager;
import mod.chiselsandbits.api.config.IServerConfiguration;
import mod.chiselsandbits.api.multistate.StateEntrySize;
import mod.chiselsandbits.api.util.constants.Constants;

import java.util.function.Supplier;

public class ServerConfiguration implements IServerConfiguration
{
    
    private final Supplier<Boolean> blockListRandomTickingBlocks;
    private final Supplier<Boolean> compatibilityMode;
    private final Supplier<Integer>        bagStackSize;
    private final Supplier<StateEntrySize> bitSize;
    private final Supplier<Integer>        changeTrackerSize;
    private final Supplier<Boolean>        deleteExcessBits;
    private final Supplier<Double> lightFactorMultiplier;

    public ServerConfiguration() {
        final IConfigurationBuilder builder = IConfigurationManager.getInstance().createBuilder(
          ConfigurationType.SYNCED, Constants.MOD_ID + "-server"
        );

        blockListRandomTickingBlocks = builder.defineBoolean("balancing.blacklist-random-ticking-blocks", false);
        compatibilityMode = builder.defineBoolean("balancing.enable-compatibility-mode", false);
        bagStackSize = builder.defineInteger("balancing.bit-bag-stack-size", 512, 64, Integer.MAX_VALUE);
        bitSize = builder.defineEnum("style.bit-size", StateEntrySize.ONE_SIXTEENTH);
        changeTrackerSize = builder.defineInteger("balancing.change-tracker-size", 20, 10, 40);
        deleteExcessBits = builder.defineBoolean("balancing.delete-excess-bits", true);
        lightFactorMultiplier = builder.defineDouble("balancing.light-factor-multiplier", 1,0, 4096);

        builder.setup();
    }

    @Override
    public Supplier<Boolean> getBlackListRandomTickingBlocks()
    {
        return blockListRandomTickingBlocks;
    }

    @Override
    public Supplier<Boolean> getCompatabilityMode()
    {
        return compatibilityMode;
    }

    @Override
    public Supplier<Integer> getBagStackSize()
    {
        return bagStackSize;
    }

    @Override
    public Supplier<StateEntrySize> getBitSize()
    {
        return bitSize;
    }

    @Override
    public Supplier<Integer> getChangeTrackerSize()
    {
        return changeTrackerSize;
    }

    @Override
    public Supplier<Boolean> getDeleteExcessBits()
    {
        return deleteExcessBits;
    }

    @Override
    public Supplier<Double> getLightFactorMultiplier()
    {
        return lightFactorMultiplier;
    }
}
