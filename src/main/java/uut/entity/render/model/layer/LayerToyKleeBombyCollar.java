package uut.entity.render.model.layer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import uut.entity.EntityToyKleeBomby;
import uut.entity.render.RenderToyKleeBomby;

public class LayerToyKleeBombyCollar implements LayerRenderer<EntityToyKleeBomby> {
    private static final ResourceLocation COLLAR_TEXTURE = new ResourceLocation("uut:textures/model/klee_bomby/toy_kleebomby_collar.png");
    private final RenderToyKleeBomby kleebombyRenderer;

    public LayerToyKleeBombyCollar(RenderToyKleeBomby rendererIn) {
        this.kleebombyRenderer = rendererIn;
    }

    @Override
    public void doRenderLayer(EntityToyKleeBomby entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (entitylivingbaseIn.isTamed() && !entitylivingbaseIn.isInvisible()) {
            this.kleebombyRenderer.bindTexture(COLLAR_TEXTURE);

            EnumDyeColor enumdyecolor = entitylivingbaseIn.getCollarColor();
            float[] afloat = enumdyecolor.getColorComponentValues();

            GlStateManager.color(afloat[0], afloat[1], afloat[2]);

            this.kleebombyRenderer.getMainModel().render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }
}