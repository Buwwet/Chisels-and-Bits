package mod.chiselsandbits.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexSorting;
import mod.chiselsandbits.api.client.render.preview.placement.PlacementPreviewRenderMode;
import mod.chiselsandbits.api.placement.PlacementResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.List;

public class ChiseledBlockGhostRenderer
{
    private static final ChiseledBlockGhostRenderer INSTANCE = new ChiseledBlockGhostRenderer();

    private static final BufferBuilderTransparent BUFFER = new BufferBuilderTransparent();

    public static ChiseledBlockGhostRenderer getInstance()
    {
        return INSTANCE;
    }

    private ChiseledBlockGhostRenderer()
    {
    }

    public void renderGhost(
            final PoseStack poseStack,
            final ItemStack renderStack,
            final Vec3 targetedRenderPos,
            final PlacementResult placementResult,
            final PlacementPreviewRenderMode success,
            final PlacementPreviewRenderMode failure,
            final boolean ignoreDepth)
    {
        poseStack.pushPose();

        // Offset/scale by an unnoticeable amount to prevent z-fighting
        final Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        poseStack.translate(
          targetedRenderPos.x - camera.x - 0.000125,
          targetedRenderPos.y - camera.y + 0.000125,
          targetedRenderPos.z - camera.z - 0.000125
        );
        poseStack.scale(1.001F, 1.001F, 1.001F);

        final Vector4f color = placementResult.getColor();
        final boolean renderColoredGhost = (placementResult.isSuccess() && success.isColoredGhost())
                || (!placementResult.isSuccess() && failure.isColoredGhost());

        BUFFER.setAlphaPercentage(color.w());
        final BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(renderStack, null, null, 0);

        if (!renderColoredGhost || !ignoreDepth)
            renderGhost(poseStack, renderStack, model, renderColoredGhost, color, false);

        if (ignoreDepth)
            renderGhost(poseStack, renderStack, model, renderColoredGhost, color, true);

        poseStack.popPose();
    }

    private void renderGhost(
            final PoseStack poseStack,
            final ItemStack renderStack,
            final BakedModel model,
            final boolean renderColoredGhost,
            final Vector4f color,
            final boolean ignoreDepth)
    {
        final RenderType renderType;
        if (renderColoredGhost)
        {
            renderType = ignoreDepth
                    ? ModRenderTypes.GHOST_BLOCK_COLORED_PREVIEW_ALWAYS.get()
                    : ModRenderTypes.GHOST_BLOCK_COLORED_PREVIEW.get();
        }
        else
        {
            renderType = ignoreDepth
                    ? ModRenderTypes.GHOST_BLOCK_PREVIEW_GREATER.get()
                    : ModRenderTypes.GHOST_BLOCK_PREVIEW.get();
        }
        BUFFER.begin(renderType.mode(), renderType.format());
        if (renderColoredGhost)
        {
            renderModelLists(
              model,
              poseStack,
              BUFFER,
              color
            );
        }
        else
        {
            Minecraft.getInstance().getItemRenderer().renderModelLists(
              model,
              renderStack,
              15728880,
              OverlayTexture.NO_OVERLAY,
              poseStack,
              BUFFER
            );
        }
        renderType.end(BUFFER, RenderSystem.getVertexSorting());
    }

    private static final float[] DIRECTIONAL_BRIGHTNESS = {0.5f, 1f, 0.7f, 0.7f, 0.6f, 0.6f};

    private static Vector3f[] getShadedColors(final Vector4f color)
    {
        // Directionally shade the color by the amount MC normally does
        return Arrays.stream(Direction.values())
                .map(direction ->
                {
                    final float brightness = DIRECTIONAL_BRIGHTNESS[direction.get3DDataValue()];
                    return new Vector3f(
                            color.x() * brightness,
                            color.y() * brightness,
                            color.z() * brightness);
                }).toArray(Vector3f[]::new);
    }

    private static Vector3f[] getNormals(final PoseStack.Pose pose)
    {
        // Transform the normal vector of each direction by the pose's normal matrix
        return Arrays.stream(Direction.values())
                .map(direction ->
                {
                    final Vec3i faceNormal = direction.getNormal();
                    final Vector3f normal = new Vector3f(faceNormal.getX(), faceNormal.getY(), faceNormal.getZ());
                    normal.mul(pose.normal());
                    return normal;
                }).toArray(Vector3f[]::new);
    }

    /**
     * Optimized version of ItemRenderer#renderModelLists that ignores textures, and renders a model's
     * quads with a single RGBA color shaded by the quads' direction to match MCs similar shading
     */
    private static void renderModelLists(
            final BakedModel model,
            final PoseStack poseStack,
            final VertexConsumer buffer,
            final Vector4f color)
    {
        final RandomSource random = RandomSource.create(42);

        // Setup normals and shaded colors for each direction
        final Vector3f[] normals = getNormals(poseStack.last());
        final Vector3f[] shadedColors = getShadedColors(color);

        // Initialize reusable position vector to avoid needless creation of new ones
        final Vector4f pos = new Vector4f();

        for (final Direction direction : Direction.values())
        {
            // Render outer directional quads
            random.setSeed(42L);
            renderQuadList(poseStack.last().pose(), buffer, model.getQuads(null, direction, random), normals, shadedColors, pos);
        }

        // Render quads of unspecified direction
        random.setSeed(42L);
        renderQuadList(poseStack.last().pose(), buffer, model.getQuads(null, null, random), normals, shadedColors, pos);
    }

    /**
     * Optimized version of ItemRenderer#renderQuadList
     */
    private static void renderQuadList(
            final Matrix4f pose,
            final VertexConsumer buffer,
            final List<BakedQuad> quads,
            final Vector3f[] normals,
            final Vector3f[] shadedColors,
            final Vector4f pos)
    {
        for (final BakedQuad quad : quads)
        {
            putBulkData(
                    buffer,
                    pose,
                    quad,
                    shadedColors[quad.getDirection().ordinal()],
                    normals[quad.getDirection().ordinal()],
                    pos);
        }
    }

    /**
     * Optimized and stripped down version of IForgeVertexConsumer#putBulkData
     */
    private static void putBulkData(
            final VertexConsumer buffer,
            final Matrix4f pose,
            final BakedQuad bakedQuad,
            final Vector3f color,
            final Vector3f normal,
            final Vector4f pos)
    {
        // Get vertex data
        final int[] vertices = bakedQuad.getVertices();
        final int vertexCount = vertices.length / DefaultVertexFormat.BLOCK.getIntegerSize();

        try (final MemoryStack memorystack = MemoryStack.stackPush()) {
            // Setup buffers
            final ByteBuffer bytebuffer = memorystack.malloc(DefaultVertexFormat.BLOCK.getVertexSize());
            final IntBuffer intbuffer = bytebuffer.asIntBuffer();

            for (int v = 0; v < vertexCount; ++v)
            {
                // Add vertex data to the buffer
                ((Buffer) intbuffer).clear();
                intbuffer.put(vertices, v * 8, 8);

                // Extract relative position, then transform it to the position in the world
                pos.set(bytebuffer.getFloat(0),
                        bytebuffer.getFloat(4),
                        bytebuffer.getFloat(8),
                        1f);
                pos.mul(pose);

                buffer.vertex(pos.x(), pos.y(), pos.z())
                      .color(color.x(), color.y(), color.z(), 1f)
                      .normal(normal.x(), normal.y(), normal.z())
                      .endVertex();
            }
        }
    }

    private static class BufferBuilderTransparent extends BufferBuilder
    {
        private float alphaPercentage;

        public BufferBuilderTransparent()
        {
            super(2097152);
        }

        public void setAlphaPercentage(final float alphaPercentage)
        {
            this.alphaPercentage = Mth.clamp(alphaPercentage, 0, 1);
        }

        @Override
        public VertexConsumer color(int red, int green, int blue, int alpha)
        {
            return super.color(red, green, blue, (int) (alpha * alphaPercentage));
        }

        @Override
        public void vertex(float x, float y, float z, float red, float green, float blue, float alpha, float texU,
                           float texV, int overlayUV, int lightmapUV, float normalX, float normalY, float normalZ)
        {
            super.vertex(x, y, z, red, green, blue, alpha * alphaPercentage, texU, texV, overlayUV, lightmapUV, normalX, normalY, normalZ);
        }
    }
}
