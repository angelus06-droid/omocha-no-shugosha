package uut.entity.render.model.layer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import uut.entity.EntityToyTinyDragoness;
import uut.entity.render.RenderToyTinyDragoness;

public class LayerToyTinyDragonessCollar implements LayerRenderer<EntityToyTinyDragoness> {
    private static final ResourceLocation COLLAR_TEXTURE = new ResourceLocation("uut:textures/model/tiny_dragoness/toy_tinydragoness_collar.png");
    private final RenderToyTinyDragoness tankRenderer;

    public LayerToyTinyDragonessCollar(RenderToyTinyDragoness rendererIn) {
        this.tankRenderer = rendererIn;
    }

    @Override
    public void doRenderLayer(EntityToyTinyDragoness entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
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