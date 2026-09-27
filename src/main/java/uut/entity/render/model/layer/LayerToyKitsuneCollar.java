package uut.entity.render.model.layer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import uut.entity.EntityToyKitsune;
import uut.entity.render.RenderToyKitsune;

public class LayerToyKitsuneCollar implements LayerRenderer<EntityToyKitsune> {
    private static final ResourceLocation COLLAR_TEXTURE = new ResourceLocation("uut:textures/model/kitsune/toy_kitsune_collar.png");
    private final RenderToyKitsune kitsuneRenderer;

    public LayerToyKitsuneCollar(RenderToyKitsune rendererIn) {
        this.kitsuneRenderer = rendererIn;
    }

    @Override
    public void doRenderLayer(EntityToyKitsune entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (entitylivingbaseIn.isTamed() && !entitylivingbaseIn.isInvisible()) {
            this.kitsuneRenderer.bindTexture(COLLAR_TEXTURE);

            EnumDyeColor enumdyecolor = entitylivingbaseIn.getCollarColor();
            float[] afloat = enumdyecolor.getColorComponentValues();

            GlStateManager.color(afloat[0], afloat[1], afloat[2]);

            this.kitsuneRenderer.getMainModel().render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }
}