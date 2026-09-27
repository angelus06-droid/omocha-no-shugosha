package uut.entity.render.model.layer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import uut.entity.EntityToyTechnoDemon;
import uut.entity.render.RenderToyTechnoDemon;

public class LayerToyTechnoDemonCollar implements LayerRenderer<EntityToyTechnoDemon> {
    private static final ResourceLocation COLLAR_TEXTURE = new ResourceLocation("uut:textures/model/techno_demon/toy_technodemon_collar.png");
    private final RenderToyTechnoDemon tankRenderer;

    public LayerToyTechnoDemonCollar(RenderToyTechnoDemon rendererIn) {
        this.tankRenderer = rendererIn;
    }

    @Override
    public void doRenderLayer(EntityToyTechnoDemon entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (entitylivingbaseIn.isTamed() && !entitylivingbaseIn.isInvisible()) {
            this.tankRenderer.bindTexture(COLLAR_TEXTURE);

            EnumDyeColor enumdyecolor = entitylivingbaseIn.getCollarColor();
            float[] afloat = enumdyecolor.getColorComponentValues();

            GlStateManager.color(afloat[0], afloat[1], afloat[2]);

            this.tankRenderer.getMainModel().render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }
}