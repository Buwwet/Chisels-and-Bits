package mod.chiselsandbits.events;

import mod.chiselsandbits.api.item.leftclick.ILeftClickControllingItem;
import mod.chiselsandbits.api.item.leftclick.LeftClickProcessingState;
import mod.chiselsandbits.api.util.constants.Constants;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class LeftClickEventHandler
{

    @SubscribeEvent
    public static void onPlayerInteractLeftClickBlock(final PlayerInteractEvent.LeftClickBlock event)
    {
        final ItemStack itemStack = event.getItemStack();
        if (itemStack.getItem() instanceof ILeftClickControllingItem) {
            final ILeftClickControllingItem leftClickControllingItem = (ILeftClickControllingItem) itemStack.getItem();
            final LeftClickProcessingState processingState = leftClickControllingItem.handleLeftClickProcessing(
              event.getPlayer(),
              event.getHand(),
              event.getPos(),
              event.getFace(),
              new LeftClickProcessingState(
                event.isCanceled(),
                event.getUseItem()
              )
            );


            if (processingState.shouldCancel())
            {
                event.setCanceled(true);
            }

            event.setUseItem(processingState.getNextState());
        }
    }
}
