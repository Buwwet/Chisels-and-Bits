package mod.chiselsandbits.network.packets;

import mod.chiselsandbits.api.change.IChangeTracker;
import mod.chiselsandbits.api.change.IChangeTrackerManager;
import mod.chiselsandbits.api.change.changes.IllegalChangeAttempt;
import mod.chiselsandbits.api.util.LocalStrings;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public final class RequestChangeTrackerOperationPacket extends ModPacket
{
    private boolean redo;

    public RequestChangeTrackerOperationPacket(FriendlyByteBuf byteBuf)
    {
        this.readPayload(byteBuf);
    }

    public RequestChangeTrackerOperationPacket(final boolean redo)
    {
        this.redo = redo;
    }

    @Override
    public void writePayload(final FriendlyByteBuf buffer)
    {
        buffer.writeBoolean(this.redo);
    }

    @Override
    public void readPayload(final FriendlyByteBuf buffer)
    {
        this.redo = buffer.readBoolean();
    }

    @Override
    public void server(final ServerPlayer playerEntity)
    {
        final IChangeTracker tracker = IChangeTrackerManager.getInstance().getChangeTracker(playerEntity);
        if (redo) {
            if (!tracker.canRedo(playerEntity)) {
                playerEntity.sendSystemMessage(LocalStrings.CanNotRedo.getText().withStyle(ChatFormatting.RED));
                return;
            }

            try
            {
                tracker.redo(playerEntity);
                playerEntity.sendSystemMessage(LocalStrings.RedoSuccessful.getText().withStyle(ChatFormatting.GREEN));
            }
            catch (IllegalChangeAttempt e)
            {
                playerEntity.sendSystemMessage(LocalStrings.CanNotRedo.getText().withStyle(ChatFormatting.RED));
            }

            return;
        }

        if (!tracker.canUndo(playerEntity)) {
            playerEntity.sendSystemMessage(LocalStrings.CanNotUndo.getText().withStyle(ChatFormatting.RED));
            return;
        }

        try
        {
            tracker.undo(playerEntity);
            playerEntity.sendSystemMessage(LocalStrings.UndoSuccessful.getText().withStyle(ChatFormatting.GREEN));
        }
        catch (IllegalChangeAttempt e)
        {
            playerEntity.sendSystemMessage(LocalStrings.CanNotUndo.getText().withStyle(ChatFormatting.RED));
        }
    }
}
