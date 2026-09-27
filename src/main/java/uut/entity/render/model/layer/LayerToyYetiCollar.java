package uut.entity.render.model.layer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import uut.entity.EntityToyYeti;
import uut.entity.render.RenderToyYeti;

public class LayerToyYetiCollar implements LayerRenderer<EntityToyYeti> {
    private static final ResourceLocation COLLAR_TEXTURE = new ResourceLocation("uut:textures/model/yeti/toy_yeti_collar.png");
    private final RenderToyYeti yetiRenderer;

    public LayerToyYetiCollar(RenderToyYeti rendererIn) {
        this.yetiRenderer = rendererIn;
    }

    @Override
    public void doRenderLayer(EntityToyYeti entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (entitylivingbaseIn.isTamed() && !entitylivingbaseIn.isInvisible()) {
            this.yetiRenderer.bindTexture(COLLAR_TEXTURE);

            EnumDyeColor enumdyecolor = entitylivingbaseIn.getCollarColor();
            float[] afloat = enumdyecolor.getColorComponentValues();

            GlStateManager.color(afloat[0], afloat[1], afloat[2]);

            this.yetiRenderer.getMainModel().render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }
}