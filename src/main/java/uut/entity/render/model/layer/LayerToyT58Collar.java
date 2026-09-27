package uut.entity.render.model.layer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import uut.entity.EntityToyT58;
import uut.entity.render.RenderToyT58;

public class LayerToyT58Collar implements LayerRenderer<EntityToyT58> {
    private static final ResourceLocation COLLAR_TEXTURE = new ResourceLocation("uut:textures/model/t58/toy_t58_collar.png");
    private final RenderToyT58 t58Renderer;

    public LayerToyT58Collar(RenderToyT58 rendererIn) {
        this.t58Renderer = rendererIn;
    }

    @Override
    public void doRenderLayer(EntityToyT58 entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (entitylivingbaseIn.isTamed() && !entitylivingbaseIn.isInvisible()) {
            this.t58Renderer.bindTexture(COLLAR_TEXTURE);

            EnumDyeColor enumdyecolor = entitylivingbaseIn.getCollarColor();
            float[] afloat = enumdyecolor.getColorComponentValues();

            GlStateManager.color(afloat[0], afloat[1], afloat[2]);

            this.t58Renderer.getMainModel().render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }
}